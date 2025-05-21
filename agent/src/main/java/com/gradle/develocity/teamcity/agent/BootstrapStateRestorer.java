package com.gradle.develocity.teamcity.agent;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

public class BootstrapStateRestorer extends AgentLifeCycleAdapter {

    private static final Logger LOG = Logger.getInstance("jetbrains.buildServer.AGENT");

    // TeamCity Develocity configuration parameters

    private static final String ENABLE_RESTORE_BOOTSTRAP_STATE_CONFIG_PARAM = "develocityPlugin.enable-restore-bootstrap-state";
    private static final String BOOTSTRAP_IMAGE_NAME_CONFIG_PARAM = "develocityPlugin.bootstrapImageName";

    private static final String TEAMCITY_BUILD_ID_RUNNER_PARAM = "teamcity.build.id";

    public BootstrapStateRestorer(@NotNull EventDispatcher<AgentLifeCycleListener> eventDispatcher) {
        eventDispatcher.addListener(this);
    }

    @Override
    public void beforeRunnerStart(@NotNull BuildRunnerContext runner) {
        if (isEnabledForJob(runner)) {
            // TODO: How to make sure that image name is specified, when feature is enabled?
            LOG.info(String.format("Restoring bootstrap state for job %s from image %s", getJobId(runner), getBootstrapImageName(runner)));
        }
    }

    @Override
    public void runnerFinished(@NotNull BuildRunnerContext runner, @NotNull BuildFinishedStatus status) {
        // TODO
    }

    private static boolean isEnabledForJob(@NotNull BuildRunnerContext runner) {
        String enableRestoreBootstrapValue = runner.getConfigParameters().get(ENABLE_RESTORE_BOOTSTRAP_STATE_CONFIG_PARAM);
        return Boolean.parseBoolean(enableRestoreBootstrapValue);
    }

    private static String getBootstrapImageName(@NotNull BuildRunnerContext runner) {
        return runner.getConfigParameters().get(BOOTSTRAP_IMAGE_NAME_CONFIG_PARAM);
    }

    private static String getJobId(@NotNull BuildRunnerContext runner) {
        return runner.getRunnerParameters().get(TEAMCITY_BUILD_ID_RUNNER_PARAM);
    }

}
