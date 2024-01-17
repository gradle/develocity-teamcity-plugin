package com.gradle.develocity;

import jetbrains.buildServer.serverSide.SBuild;
import org.jetbrains.annotations.NotNull;

public interface BuildScanLookup {

    @NotNull
    BuildScanReferences getBuildScansForBuild(@NotNull SBuild build);

}
