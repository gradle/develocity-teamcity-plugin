package com.gradle.develocity.teamcity.agent.bootstrapping.restore;

import com.gradle.develocity.teamcity.agent.bootstrapping.restore.JdkProvider.JdkLocation;
import com.gradle.develocity.teamcity.agent.bootstrapping.restore.process.ExecutionResult;
import com.gradle.develocity.teamcity.agent.bootstrapping.restore.process.ProcessExecutor;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.BuildRunnerContext;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.lang.String.format;

public class BootstrapStateRestoreService {

    private static final Logger LOG = Logger.getInstance("jetbrains.buildServer.AGENT");
    private static final Duration RESTORE_TIMEOUT = Duration.ofMinutes(10);

    private final URI edgeUrl;
    private final String token;
    private final JdkProvider jdkProvider;
    private final DevelocityBootstrapCliToolProvider cliToolProvider;

    BootstrapStateRestoreService(URI edgeUrl, String token, BuildRunnerContext context) {
        this.edgeUrl = edgeUrl;
        this.token = token;
        this.jdkProvider = new JdkProvider(context);
        this.cliToolProvider = new DevelocityBootstrapCliToolProvider(context);
    }

    Optional<RestoreResponse> restoreFromManifestWithPrefix(String manifestPrefix) {
        Optional<JdkLocation> maybeJdkLocation = jdkProvider.jdkForMajorVersion(21);
        if (!maybeJdkLocation.isPresent()) {
            return Optional.empty();
        }

        JdkLocation jdkLocation = maybeJdkLocation.get();
        LOG.info(format("Using JDK at '%s'", jdkLocation.executablePath().toString()));

        Path dvBootstrapCliTool = cliToolProvider.dvBootstrapCliTool();
        LOG.info(format("Using DV bootstrap cli tool at '%s'", dvBootstrapCliTool.toString()));

        try(ProcessExecutor executor = new ProcessExecutor()) {
            try {
                List<String> command = Arrays.asList(
                        jdkLocation.executablePath().toString(),
                        "-jar",
                        dvBootstrapCliTool.toString(),
                        "restore"
                );

                ExecutionResult result = executor.execute(command, RESTORE_TIMEOUT);

                LOG.info("Got stdout: " + result.stdout());
                LOG.info("Got stderr: " + result.stderr());
            } catch (ExecutionException e) {
                LOG.error("Executing restore service failed with execution exception", e);
                return Optional.empty();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Optional.empty();
            } catch (TimeoutException e) {
                // TODO: In case of failures, shouldn't we cleanup the mess that was already done?
                //       Otherwise the build might run into issues due to corrupted cache entries.
                //       An idea might be that the restore service unpacks data in a temporary
                //       folder, and only when restoring is completed, we move the data to its
                //       final destination. This could either be done by the restore tool itself
                //       (preferred way) or by the this restore service here.
                LOG.error(format("Executing restore service timed out after %s", RESTORE_TIMEOUT));
                return Optional.empty();
            }
        }

        return Optional.of(
                RestoreResponse.of(
                        "key",
                        0L,
                        0L,
                        Duration.ZERO,
                        Duration.ZERO)
        );
    }

}
