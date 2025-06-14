package com.gradle.develocity.teamcity.internal;

import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.buildLog.LogMessage;
import com.gradle.develocity.teamcity.BuildScanLookup;
import com.gradle.develocity.teamcity.BuildScanReference;
import com.gradle.develocity.teamcity.BuildScanReferences;
import com.gradle.develocity.teamcity.TeamCityConfiguration;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class LogIteratingBuildScanLookup implements BuildScanLookup {

    private static final Logger LOGGER = Logger.getLogger("jetbrains.buildServer.BUILDSCAN");

    private static final Set<String> PUBLISHING_BUILD_PATTERNS = Set.of(
        // Maven
        "[INFO] Publishing build scan...",
        "[INFO] Publishing Build Scan...",
        // Gradle
        "Publishing build scan...",
        "Publishing Build Scan..."
    );

    @Override
    @NotNull
    public BuildScanReferences getBuildScansForBuild(@NotNull SBuild build) {
        // check if log parsing is explicitly enabled via configuration parameter
        String logParsing = build.getParametersProvider().get(TeamCityConfiguration.LOG_PARSING_CONFIG_PARAM);
        boolean iterateLog = Boolean.parseBoolean(logParsing);
        if (!iterateLog) {
            LOGGER.debug(String.format("Log parsing not enabled. Not parsing build log for build scan urls of build id: %s", build.getBuildId()));
            return BuildScanReferences.of();
        }

        LOGGER.debug(String.format("Parsing build log for build scan urls of build id: %s", build.getBuildId()));

        List<BuildScanReference> buildScans = new ArrayList<>();
        boolean foundPublishMessage = false;
        for (Iterator<LogMessage> iterator = build.getBuildLog().getMessagesIterator(); iterator.hasNext(); ) {
            LogMessage message = iterator.next();
            String text = message.getText();
            if (!foundPublishMessage && PUBLISHING_BUILD_PATTERNS.contains(text)) {
                foundPublishMessage = true;
            } else if (foundPublishMessage && Util.isBuildScanUrl(text)) {
                buildScans.add(new BuildScanReference(Util.getBuildScanId(text), Util.getBuildScanUrl(text)));
                foundPublishMessage = false;
            } else {
                foundPublishMessage = false;
            }
        }
        return BuildScanReferences.of(buildScans);
    }

}
