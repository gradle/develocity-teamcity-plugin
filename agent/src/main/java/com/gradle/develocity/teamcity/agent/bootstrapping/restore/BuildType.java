package com.gradle.develocity.teamcity.agent.bootstrapping.restore;

import jetbrains.buildServer.agent.BuildRunnerContext;

enum BuildType {
    GRADLE,
    MAVEN;

    static BuildType from(BuildRunnerContext context) {
        String type = context.getRunType();

        if ("gradle-runner".equals(type)) {
            return GRADLE;
        } else if ("maven-runner".equals(type)) {
            return MAVEN;
        } else {
            throw new IllegalArgumentException("Unknown build type: " + type);
        }
    }
}
