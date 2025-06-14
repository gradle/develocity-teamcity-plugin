package com.gradle.develocity.teamcity.agent.bootstrapping.restore;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.util.FileUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import static java.lang.String.format;

public class DevelocityBootstrapCliToolProvider {

    private static final Logger LOG = Logger.getInstance("jetbrains.buildServer.AGENT");
    // Keep in sync with `agent/build.gradle`
    private static final String DEVELOCITY_BOOTSTRAP_CLI_TOOL_NAME = "develocity-bootstrap-cli";

    private final BuildRunnerContext context;

    DevelocityBootstrapCliToolProvider(BuildRunnerContext context) {
        this.context = context;
    }

    Optional<Path> dvBootstrapCliTool() {
        String toolJarName = format("%s.jar", DEVELOCITY_BOOTSTRAP_CLI_TOOL_NAME);
        File toolJar = new File(agentsTempDirectory(), toolJarName);

        // Copying resources does not throw an exception if the resource does not exist.
        // Instead, only a warning is logged in the agent's log.
        FileUtil.copyResourceIfNotExists(this.getClass(), format("/%s", toolJarName), toolJar);

        return toolJar.exists() ? Optional.of(toolJar.toPath()) : Optional.empty();
    }

    private File agentsTempDirectory() {
        return context.getBuild().getAgentTempDirectory();
    }
}
