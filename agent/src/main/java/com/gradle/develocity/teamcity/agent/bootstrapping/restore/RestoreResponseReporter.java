package com.gradle.develocity.teamcity.agent.bootstrapping.restore;

import jetbrains.buildServer.agent.BuildRunnerContext;

import java.time.Duration;

import static java.lang.String.format;

interface RestoreResponseReporter {

    void report(BuildRunnerContext runner, RestoreResponse restoreResponse);

    default String toStorageSizeString(long sizeInBytes) {
        final double KB = 1024.0;
        final double MB = KB * 1024;
        final double GB = MB * 1024;

        if (sizeInBytes >= GB) {
            return format("%.1f GiB", sizeInBytes / GB);
        } else if (sizeInBytes >= MB) {
            return format("%.1f MiB", sizeInBytes / MB);
        } else if (sizeInBytes >= KB) {
            return format("%.1f KiB", sizeInBytes / KB);
        } else {
            return sizeInBytes + " B";
        }

    }

    default String toDurationString(Duration duration) {
        final long durationInMillis = duration.toMillis();
        final double SECOND = 1000.0;
        final double MINUTE = 60 * SECOND;
        final double HOUR = 60 * MINUTE;

        if (durationInMillis >= HOUR) {
            return String.format("%.1f h", durationInMillis / HOUR);
        } else if (durationInMillis >= MINUTE) {
            return String.format("%.1f m", durationInMillis / MINUTE);
        } else if (durationInMillis >= SECOND) {
            return String.format("%.1f s", durationInMillis / SECOND);
        } else {
            return durationInMillis + " ms";
        }
    }

    default String toNumberString(long number) {
        return Long.toString(number);
    }

    class ForGradle implements RestoreResponseReporter {

        // The env variables will be used to create custom values via the Gradle init script (`develocity-injection.init.gradle`)
        private static final String BOOTSTRAP_STATE_ENV_VAR_PREFIX = "RESTORE_BOOTSTRAP_STATE_";
        private static final String IMAGE_NAME_ENV_VAR = BOOTSTRAP_STATE_ENV_VAR_PREFIX + "IMAGE_NAME";
        private static final String ARTIFACTS_COUNT_ENV_VAR = BOOTSTRAP_STATE_ENV_VAR_PREFIX + "ARTIFACTS_COUNT";
        private static final String ARTIFACTS_TOTAL_SIZE_ENV_VAR = BOOTSTRAP_STATE_ENV_VAR_PREFIX + "ARTIFACTS_TOTAL_SIZE";
        private static final String ARTIFACTS_DOWNLOAD_DURATION_ENV_VAR = BOOTSTRAP_STATE_ENV_VAR_PREFIX + "ARTIFACTS_DOWNLOAD_DURATION";
        private static final String ARTIFACTS_UNPACK_DURATION_ENV_VAR = BOOTSTRAP_STATE_ENV_VAR_PREFIX + "ARTIFACTS_UNPACK_DURATION";

        @Override
        public void report(BuildRunnerContext runner, RestoreResponse restoreResponse) {
            runner.addEnvironmentVariable(IMAGE_NAME_ENV_VAR, restoreResponse.imageName());
            runner.addEnvironmentVariable(ARTIFACTS_COUNT_ENV_VAR, toNumberString(restoreResponse.artifactCount()));
            runner.addEnvironmentVariable(ARTIFACTS_TOTAL_SIZE_ENV_VAR, toStorageSizeString(restoreResponse.totalArtifactSizeInBytes()));
            runner.addEnvironmentVariable(ARTIFACTS_DOWNLOAD_DURATION_ENV_VAR, toDurationString(restoreResponse.totalArtifactDownloadDuration()));
            runner.addEnvironmentVariable(ARTIFACTS_UNPACK_DURATION_ENV_VAR, toDurationString(restoreResponse.totalArtifactUnpackDuration()));
        }

    }

}
