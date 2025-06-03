package com.gradle.develocity.teamcity.agent.bootstrapping.restore;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.BuildRunnerContext;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

import static java.lang.String.format;

public class BootstrapStateRestoreService {

    private static final Logger LOG = Logger.getInstance("jetbrains.buildServer.AGENT");

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

    RestoreResponse restoreFromManifestWithPrefix(String manifestPrefix) {
        Optional<JdkProvider.JdkLocation> jdkLocation = jdkProvider.jdkForMajorVersion(21);
        jdkLocation.ifPresent(jdk ->
                LOG.info(format("Using JDK at '%s'", jdk.executablePath().toString()))
        );

        Path dvBootstrapCliTool = cliToolProvider.dvBootstrapCliTool();
        LOG.info(format("Using DV bootstrap cli tool at '%s'", dvBootstrapCliTool.toString()));

        return RestoreResponse.of("key", 0L, 0L, Duration.ZERO, Duration.ZERO);
    }

}
