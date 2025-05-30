package com.gradle.develocity.teamcity.agent.maven.testutils

import org.gradle.util.GradleVersion

class VersionUtils {

    private VersionUtils() {
    }

    static boolean isAtLeast(String current, String reference) {
        GradleVersion.version(current) >= GradleVersion.version(reference)
    }
}
