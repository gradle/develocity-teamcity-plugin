package com.gradle.develocity.teamcity;

import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildType;
import org.jetbrains.annotations.NotNull;

public interface BuildScanDisplayArbiter {

    boolean showBuildScanInfo(@NotNull SBuild build);

    boolean hasSupportedRunner(SBuildType buildType);
}
