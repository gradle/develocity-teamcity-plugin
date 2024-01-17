package com.gradle.develocity;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.Future;

public interface ExternalIntegration {

    @NotNull
    Optional<Future> handle(@NotNull BuildScanReferences buildScans, @NotNull TeamCityBuildStatus teamCityBuildStatus, @NotNull TeamCityConfiguration teamCityConfiguration);

}
