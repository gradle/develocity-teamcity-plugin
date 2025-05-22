package com.gradle.develocity.teamcity.agent.bootstrapping;

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

    // The env variables will be used to create custom values via the Gradle init script (`develocity-injection.init.gradle`)
    private static final String BOOTSTRAP_STATE_ENV_VAR_PREFIX = "RESTORE_BOOTSTRAP_STATE_";
    private static final String ENTRY_KEY_ENV_VAR = BOOTSTRAP_STATE_ENV_VAR_PREFIX + "ENTRY_KEY";
    private static final String ENTRY_SIZE_ENV_VAR = BOOTSTRAP_STATE_ENV_VAR_PREFIX + "ENTRY_SIZE";
    private static final String DOWNLOAD_DURATION_ENV_VAR = BOOTSTRAP_STATE_ENV_VAR_PREFIX + "DOWNLOAD_DURATION";
    private static final String UNPACK_DURATION_ENV_VAR = BOOTSTRAP_STATE_ENV_VAR_PREFIX + "UNPACK_DURATION";

    public BootstrapStateRestorer(@NotNull EventDispatcher<AgentLifeCycleListener> eventDispatcher) {
        eventDispatcher.addListener(this);
    }

    @Override
    public void beforeRunnerStart(@NotNull BuildRunnerContext runner) {
        if (isEnabledForJob(runner)) {
            // TODO: How to make sure that image name is specified, when feature is enabled?
            String bootstrapImageName = getBootstrapImageName(runner);
            LOG.info(String.format("Restoring bootstrap state for job %s from image %s", getJobId(runner), bootstrapImageName));

            runner.addEnvironmentVariable(ENTRY_KEY_ENV_VAR, bootstrapImageName); // Should probably be effective name downloaded
            runner.addEnvironmentVariable(ENTRY_SIZE_ENV_VAR, "42.21 MiB");
            runner.addEnvironmentVariable(DOWNLOAD_DURATION_ENV_VAR, "42s");
            runner.addEnvironmentVariable(UNPACK_DURATION_ENV_VAR, "21s");
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
