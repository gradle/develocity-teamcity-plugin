package com.gradle.develocity.teamcity.agent.bootstrapping;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

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
    public void beforeRunnerStart(@NotNull BuildRunnerContext runner) {
        if (isEnabledForJob(runner)) {
            String manifestPrefix = getManifestPrefix(runner);
            LOG.info(String.format("Restoring bootstrap state for job %s from image with prefix %s", getJobId(runner), manifestPrefix));

            BootstrapStateRestoreService restoreService = new BootstrapStateRestoreService(URI.create("http://edge.url"), "token");
            RestoreResponse restoreResponse = restoreService.restoreFromManifestWithPrefix(manifestPrefix);

            BuildType buildType = BuildType.from(runner);
            switch (buildType) {
                case GRADLE:
                    new RestoreResponseReporter.ForGradle().report(runner, restoreResponse);
                    break;

                case MAVEN:
                    throw new IllegalArgumentException("Unsupported build type: " + buildType.name());
            }
        }
    }

    private static boolean isEnabledForJob(@NotNull BuildRunnerContext runner) {
        String enableRestoreBootstrapValue = runner.getConfigParameters().get(ENABLE_RESTORE_BOOTSTRAP_STATE_CONFIG_PARAM);
        return Boolean.parseBoolean(enableRestoreBootstrapValue);
    }

    private static String getManifestPrefix(@NotNull BuildRunnerContext runner) {
        return runner.getConfigParameters().get(BOOTSTRAP_MANIFEST_PREFIX_CONFIG_PARAM);
    }

    private static String getJobId(@NotNull BuildRunnerContext runner) {
        return runner.getRunnerParameters().get(TEAMCITY_BUILD_ID_RUNNER_PARAM);
    }


    enum BuildType {
        GRADLE,
        MAVEN;

        static BuildType from(BuildRunnerContext ctx) {
            String type = ctx.getRunType();

            if ("gradle-runner".equals(type)) {
                return GRADLE;
            } else if ("maven-runner".equals(type)) {
                return MAVEN;
            } else {
                throw new IllegalArgumentException("Unknown build type: " + type);
            }
        }
    }

}
