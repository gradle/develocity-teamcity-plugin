package com.gradle.develocity.teamcity.agent.bootstrapping.restore;

import com.gradle.develocity.teamcity.agent.bootstrapping.restore.JdkProvider.JdkLocation;
import com.gradle.develocity.teamcity.agent.bootstrapping.restore.process.ExecutionResult;
import com.gradle.develocity.teamcity.agent.bootstrapping.restore.process.ProcessExecutor;
import com.intellij.openapi.diagnostic.Logger;

import jetbrains.buildServer.agent.BuildProgressLogger;
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

    private final URI develocityUrl;
    private final String token;
    private final JdkProvider jdkProvider;
    private final DevelocityBootstrapCliToolProvider cliToolProvider;
    private final BuildProgressLogger buildLogger;

    BootstrapStateRestoreService(URI develocityUrl, String token, BuildRunnerContext context) {
        this.develocityUrl = develocityUrl;
        this.token = token;
        this.jdkProvider = new JdkProvider(context);
        this.cliToolProvider = new DevelocityBootstrapCliToolProvider(context);
        this.buildLogger = context.getBuild().getBuildLogger();
    }

    Optional<RestoreResponse> restoreFrom(String imageName) {
        Optional<JdkLocation> maybeJdkLocation = jdkProvider.jdkForMajorVersion(21);
        if (!maybeJdkLocation.isPresent()) {
            return Optional.empty();
        }

        Optional<Path> maybeDvBootstrapCliTool = cliToolProvider.dvBootstrapCliTool();
        if (!maybeDvBootstrapCliTool.isPresent()) {
            return Optional.empty();
        }

        JdkLocation jdkLocation = maybeJdkLocation.get();
        String message = format("Using JDK at '%s'", jdkLocation.executablePath().toString());
        log(message);

        Path dvBootstrapCliTool = maybeDvBootstrapCliTool.get();
        log(format("Using DV bootstrap cli tool at '%s'", dvBootstrapCliTool.toString()));

        try (ProcessExecutor executor = new ProcessExecutor()) {
            try {
                List<String> command = Arrays.asList(
                        jdkLocation.executablePath().toString(),
                        "-jar",
                        dvBootstrapCliTool.toString(),
                        "restore"
                );

                ExecutionResult result = executor.execute(command, RESTORE_TIMEOUT);

                log("Got stdout: " + result.stdout());
                log("Got stderr: " + result.stderr());
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
                        "image-42",
                        42L,
                        42424242L,
                        Duration.ofSeconds(42)
                )
        );
    }

    private void log(String message) {
        LOG.info(message);
        buildLogger.message(message);
    }

}
