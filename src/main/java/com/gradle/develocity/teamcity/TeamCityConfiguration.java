package com.gradle.develocity.teamcity;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public final class TeamCityConfiguration {

    public static final String LOG_PARSING_CONFIG_PARAM = "develocityPlugin.log-parsing.enabled";

    public final String fullBuildName;
    public final Map<String, String> params;

    public TeamCityConfiguration(@NotNull String fullBuildName, @NotNull Map<String, String> params) {
        this.fullBuildName = fullBuildName;
        this.params = Collections.unmodifiableMap(params);
    }

}
