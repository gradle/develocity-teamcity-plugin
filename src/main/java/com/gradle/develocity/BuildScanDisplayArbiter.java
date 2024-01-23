package com.gradle.develocity;

import jetbrains.buildServer.serverSide.SBuild;
import org.jetbrains.annotations.NotNull;

public interface BuildScanDisplayArbiter {

    boolean showBuildScanInfo(@NotNull SBuild build);

}
