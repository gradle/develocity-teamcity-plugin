package com.gradle.develocity.teamcity.agent.bootstrapping.restore;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.Optional;

public class BootstrapStateRestorer extends AgentLifeCycleAdapter {

    private static final Logger LOG = Logger.getInstance("jetbrains.buildServer.AGENT");

    // TeamCity Develocity configuration parameters
    private static final String ENABLE_RESTORE_BOOTSTRAP_STATE_CONFIG_PARAM = "develocityPlugin.enable-restore-bootstrap-state";
    private static final String BOOTSTRAP_MANIFEST_PREFIX_CONFIG_PARAM = "develocityPlugin.bootstrap-manifest-prefix";
    private static final String TEAMCITY_BUILD_ID_RUNNER_PARAM = "teamcity.build.id";

    public BootstrapStateRestorer(@NotNull EventDispatcher<AgentLifeCycleListener> eventDispatcher) {
        eventDispatcher.addListener(this);
    }

    @Override
    public void beforeRunnerStart(@NotNull BuildRunnerContext context) {
        if (isEnabledForJob(context)) {
            String manifestPrefix = getManifestPrefix(context);
            LOG.info(String.format("Restoring bootstrap state for job %s from image with prefix %s", getJobId(context), manifestPrefix));

            BootstrapStateRestoreService restoreService = new BootstrapStateRestoreService(URI.create("http://edge.url"), "token", context);
            Optional<RestoreResponse> maybeRestoreResponse = restoreService.restoreFromManifestWithPrefix(manifestPrefix);

            maybeRestoreResponse.ifPresent(restoreResponse -> {
                BuildType buildType = BuildType.from(context);
                switch (buildType) {
                    case GRADLE:
                        new RestoreResponseReporter.ForGradle().report(context, restoreResponse);
                        break;

                    case MAVEN:
                        throw new IllegalArgumentException("Unsupported build type: " + buildType.name());
                }
            });
        }
    }

    private static boolean isEnabledForJob(BuildRunnerContext context) {
        String enableRestoreBootstrapValue = context.getConfigParameters().get(ENABLE_RESTORE_BOOTSTRAP_STATE_CONFIG_PARAM);
        return Boolean.parseBoolean(enableRestoreBootstrapValue);
    }

    private static String getManifestPrefix(BuildRunnerContext context) {
        return context.getConfigParameters().get(BOOTSTRAP_MANIFEST_PREFIX_CONFIG_PARAM);
    }

    private static String getJobId(BuildRunnerContext context) {
        return context.getRunnerParameters().get(TEAMCITY_BUILD_ID_RUNNER_PARAM);
    }

}
