package com.gradle.develocity.teamcity.agent.servicemessage;

import com.gradle.develocity.agent.maven.adapters.BuildScanApiAdapter;

final class BuildScanServiceMessageSender {

    private static final String BUILD_SCAN_SERVICE_MESSAGE_NAME = "com.gradle.develocity.buildScanLifeCycle";
    private static final String BUILD_SCAN_SERVICE_STARTED_MESSAGE_ARGUMENT = "BUILD_STARTED";
    private static final String BUILD_SCAN_SERVICE_URL_MESSAGE_ARGUMENT_PREFIX = "BUILD_SCAN_URL:";

    static void register(BuildScanApiAdapter buildScan) {
        System.out.println(ServiceMessage.of(
            BUILD_SCAN_SERVICE_MESSAGE_NAME,
            BUILD_SCAN_SERVICE_STARTED_MESSAGE_ARGUMENT
        ));

        buildScan.buildScanPublished(scan ->
            System.out.println(ServiceMessage.of(
                BUILD_SCAN_SERVICE_MESSAGE_NAME,
                BUILD_SCAN_SERVICE_URL_MESSAGE_ARGUMENT_PREFIX + scan.getBuildScanUri().toString()
            ))
        );
    }

    private BuildScanServiceMessageSender() {
    }

}
