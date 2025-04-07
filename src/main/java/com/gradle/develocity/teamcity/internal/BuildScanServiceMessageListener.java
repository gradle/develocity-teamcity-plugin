package com.gradle.develocity.teamcity.internal;

import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTranslator;
import jetbrains.buildServer.serverSide.SRunningBuild;
import com.gradle.develocity.teamcity.BuildScanDataStore;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class BuildScanServiceMessageListener implements ServiceMessageTranslator {

    private static final Logger LOGGER = Logger.getLogger("jetbrains.buildServer.BUILDSCAN");

    // values need to be kept in sync with build-scan-init.gradle
    private static final String BUILD_SCAN_SERVICE_MESSAGE_NAME = "com.gradle.develocity.buildScanLifeCycle";
    private static final String BUILD_SCAN_SERVICE_STARTED_MESSAGE_ARGUMENT = "BUILD_STARTED";
    private static final String BUILD_SCAN_SERVICE_URL_MESSAGE_ARGUMENT_PREFIX = "BUILD_SCAN_URL:";

    private final BuildScanDataStore buildScanDataStore;

    public BuildScanServiceMessageListener(@NotNull BuildScanDataStore buildScanDataStore) {
        this.buildScanDataStore = buildScanDataStore;
    }

    @NotNull
    @Override
    public List<BuildMessage1> translate(@NotNull SRunningBuild runningBuild, @NotNull BuildMessage1 originalMessage, @NotNull ServiceMessage serviceMessage) {
        String argument = requireNonNull(serviceMessage.getArgument());
        if (argument.equals(BUILD_SCAN_SERVICE_STARTED_MESSAGE_ARGUMENT)) {
            buildScanDataStore.mark(runningBuild);
        } else if (argument.startsWith(BUILD_SCAN_SERVICE_URL_MESSAGE_ARGUMENT_PREFIX)) {
            buildScanDataStore.store(runningBuild, argument.substring(BUILD_SCAN_SERVICE_URL_MESSAGE_ARGUMENT_PREFIX.length()));
        } else {
            LOGGER.error(String.format("Unknown argument format: '%s' for message service: %s", argument, BUILD_SCAN_SERVICE_MESSAGE_NAME));
        }

        // omit these service messages from the final build log
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public String getServiceMessageName() {
        return BUILD_SCAN_SERVICE_MESSAGE_NAME;
    }

}
