package com.gradle.develocity.teamcity.agent.bootstrapping.restore;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.BuildRunnerContext;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

public class BootstrapStateRestoreService {

    private static final Logger LOG = Logger.getInstance("jetbrains.buildServer.AGENT");

    private final URI edgeUrl;
    private final String token;
    private final BuildRunnerContext context;

    BootstrapStateRestoreService(URI edgeUrl, String token, BuildRunnerContext context) {
        this.edgeUrl = edgeUrl;
        this.token = token;
        this.context = context;
    }

    RestoreResponse restoreFromManifestWithPrefix(String manifestPrefix) {
        Optional<JdkProvider.JdkLocation> jdkLocation = new JdkProvider(context).jdkForMajorVersion(21);

        jdkLocation.ifPresent( jdk ->
               LOG.info("Using JDK at " + jdk.executablePath().toString())
        );

        return RestoreResponse.of("key", 0L, 0L, Duration.ZERO, Duration.ZERO);
    }

}
