package com.gradle.develocity.teamcity.agent.bootstrapping.restore;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.BuildRunnerContext;

import java.util.Locale;
import java.util.Map;

public enum OperatingSystem {

    LINUX,
    MACOS,
    WINDOWS,
    UNKNOWN;

    private static final Logger LOG = Logger.getInstance("jetbrains.buildServer.AGENT");
    private static final String OS_NAME_SYSTEM_PROPERTY = "teamcity.agent.jvm.os.name";

    public static OperatingSystem current(BuildRunnerContext context) {
        Map<String, String> configParameters = context.getConfigParameters();
        String osName = configParameters.get(OS_NAME_SYSTEM_PROPERTY).toLowerCase(Locale.ROOT);

        if (osName.contains("linux")) {
            return LINUX;
        }

        if (osName.contains("mac os x")) {
            return MACOS;
        }

        if (osName.contains("windows")) {
            return WINDOWS;
        }

        return UNKNOWN;
    }
}
