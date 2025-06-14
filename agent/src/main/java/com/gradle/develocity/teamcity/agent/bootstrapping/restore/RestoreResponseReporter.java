package com.gradle.develocity.teamcity.agent.bootstrapping.restore;

import jetbrains.buildServer.agent.BuildRunnerContext;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

        private static final Pattern ALLOWED_CHARACTERS_PATTERN = Pattern.compile("[^a-zA-Z0-9 .,]");

        private static final String DEVELOCITY_INJECTION_CUSTOM_VALUES_ENV_VAR = "DEVELOCITY_INJECTION_CUSTOM_VALUES";
        private static final String IMAGE_NAME_CUSTOM_VALUE_NAME = "Bootstrap image";
        private static final String ARTIFACTS_COUNT_CUSTOM_VALUE_NAME = "Bootstrap artifacts count";
        private static final String ARTIFACTS_TOTAL_DOWNLOAD_SIZE_CUSTOM_VALUE_NAME = "Bootstrap artifacts total download size";
        private static final String RESTORE_WALL_CLOCK_DURATION_CUSTOM_VALUE_NAME = "Bootstrap restore wall clock duration";

        @Override
        public void report(BuildRunnerContext runner, RestoreResponse restoreResponse) {
            List<String> customValues = Arrays.asList(
                    customValueFor(IMAGE_NAME_CUSTOM_VALUE_NAME, restoreResponse.imageName()),
                    customValueFor(ARTIFACTS_COUNT_CUSTOM_VALUE_NAME, toNumberString(restoreResponse.artifactsCount())),
                    customValueFor(ARTIFACTS_TOTAL_DOWNLOAD_SIZE_CUSTOM_VALUE_NAME, toStorageSizeString(restoreResponse.totalArtifactSizeInBytes())),
                    customValueFor(RESTORE_WALL_CLOCK_DURATION_CUSTOM_VALUE_NAME, toDurationString(restoreResponse.wallClockDuration()))
            );

            String serializedCustomValuePairs = customValues.stream().collect(Collectors.joining(", "));

            runner.addEnvironmentVariable(DEVELOCITY_INJECTION_CUSTOM_VALUES_ENV_VAR, serializedCustomValuePairs);
        }

        private static String customValueFor(String name, String value) {
            return format("'%s': '%s'", cleaned(name), cleaned(value));
        }

        private static String cleaned(String value) {
            Matcher matcher = ALLOWED_CHARACTERS_PATTERN.matcher(value);
            return matcher.replaceAll("");
        }
    }

}
