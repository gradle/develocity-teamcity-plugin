package com.gradle.develocity.teamcity.connection;

@SuppressWarnings("unused")
public final class DevelocityConnectionConstants {

    // Constants defined by the Develocity Connection
    // These are used to correlate data set by the user in the connection dialog to the descriptor parameters available in the Project Feature Descriptor Parameters

    public static final String GRADLE_PLUGIN_REPOSITORY_URL = "gradlePluginRepositoryUrl";
    public static final String GRADLE_PLUGIN_REPOSITORY_USERNAME = "gradlePluginRepositoryUsername";
    public static final String GRADLE_PLUGIN_REPOSITORY_PASSWORD = "gradlePluginRepositoryPassword";
    public static final String GRADLE_PLUGIN_CAPTURE_FILE_FINGERPRINTS = "gradlePluginCaptureFileFingerprints";
    public static final String DEVELOCITY_URL = "develocityUrl";
    public static final String ALLOW_UNTRUSTED_SERVER = "allowUntrustedServzer";
    public static final String DEVELOCITY_PLUGIN_VERSION = "develocityPluginVersion";
    public static final String CCUD_PLUGIN_VERSION = "commonCustomUserDataPluginVersion";
    public static final String DEVELOCITY_EXTENSION_VERSION = "develocityExtensionVersion";
    public static final String CCUD_EXTENSION_VERSION = "commonCustomUserDataExtensionVersion";
    public static final String CUSTOM_DEVELOCITY_EXTENSION_COORDINATES = "customDevelocityExtensionCoordinates";
    public static final String CUSTOM_CCUD_EXTENSION_COORDINATES = "customCommonCustomUserDataExtensionCoordinates";
    public static final String EXTENSION_CAPTURE_FILE_FINGERPRINTS = "extensionCaptureFileFingerprints";
    public static final String INSTRUMENT_COMMAND_LINE_BUILD_STEP = "instrumentCommandLineBuildStep";
    public static final String DEVELOCITY_ACCESS_KEY = "develocityAccessKey";
    public static final String DEVELOCITY_ACCESS_TOKEN_EXPIRY = "develocityAccessTokenExpiry";
    public static final String ENFORCE_DEVELOCITY_URL = "enforceDevelocityUrl";
    public static final String ENABLE_INJECTION = "enableInjection";

    // Constants defined by the BuildScanServiceMessageInjector
    // This connection sets these values as build parameters so that they can be picked up by the BuildScanServiceMessageInjector

    public static final String GRADLE_PLUGIN_REPOSITORY_URL_CONFIG_PARAM = "develocityPlugin.gradle.plugin-repository.url";
    public static final String GRADLE_PLUGIN_REPOSITORY_USERNAME_ENV_VAR = "env.DEVELOCITY_INJECTION_PLUGIN_REPOSITORY_USERNAME";
    public static final String GRADLE_PLUGIN_REPOSITORY_PASSWORD_ENV_VAR = "env.DEVELOCITY_INJECTION_PLUGIN_REPOSITORY_PASSWORD";
    public static final String GRADLE_PLUGIN_CAPTURE_FILE_FINGERPRINTS_CONFIG_PARAM = "develocityPlugin.gradle.plugin.capture-file-fingerprints";
    public static final String DEVELOCITY_URL_CONFIG_PARAM = "develocityPlugin.develocity.url";
    public static final String ALLOW_UNTRUSTED_SERVER_CONFIG_PARAM = "develocityPlugin.develocity.allow-untrusted-server";
    public static final String DEVELOCITY_PLUGIN_VERSION_CONFIG_PARAM = "develocityPlugin.develocity.plugin.version";
    public static final String CCUD_PLUGIN_VERSION_CONFIG_PARAM = "develocityPlugin.ccud.plugin.version";
    public static final String DEVELOCITY_EXTENSION_VERSION_CONFIG_PARAM = "develocityPlugin.develocity.extension.version";
    public static final String CCUD_EXTENSION_VERSION_CONFIG_PARAM = "develocityPlugin.ccud.extension.version";
    public static final String CUSTOM_DEVELOCITY_EXTENSION_COORDINATES_CONFIG_PARAM = "develocityPlugin.develocity.extension.custom.coordinates";
    public static final String CUSTOM_CCUD_EXTENSION_COORDINATES_CONFIG_PARAM = "develocityPlugin.ccud.extension.custom.coordinates";
    public static final String DEVELOCITY_EXTENSION_CAPTURE_FILE_FINGERPRINTS_CONFIG_PARAM = "develocityPlugin.develocity.extension.capture-file-fingerprints";
    public static final String INSTRUMENT_COMMAND_LINE_BUILD_STEP_CONFIG_PARAM = "develocityPlugin.command-line-build-step.enabled";
    public static final String DEVELOCITY_ACCESS_KEY_ENV_VAR = "env.DEVELOCITY_ACCESS_KEY";
    public static final String GRADLE_ENTERPRISE_ACCESS_KEY_ENV_VAR = "env.GRADLE_ENTERPRISE_ACCESS_KEY";
    public static final String ENABLE_INJECTION_CONFIG_PARAM = "develocityPlugin.develocity.injection.enabled";
    public static final String ENFORCE_DEVELOCITY_URL_CONFIG_PARAM = "develocityPlugin.develocity.enforce-url";

    public static final String DEVELOCITY_CONNECTION_PROVIDER = "develocity-connection-provider";

    // The below getters exist so that develocityConnectionDialog.jsp can read these constants using JavaBean conventions

    public String getGradlePluginRepositoryUrl() {
        return GRADLE_PLUGIN_REPOSITORY_URL;
    }

    public String getGradlePluginRepositoryUsername() {
        return GRADLE_PLUGIN_REPOSITORY_USERNAME;
    }

    public String getGradlePluginRepositoryPassword() {
        return GRADLE_PLUGIN_REPOSITORY_PASSWORD;
    }

    public String getGradlePluginCaptureFileFingerprints() {
        return GRADLE_PLUGIN_CAPTURE_FILE_FINGERPRINTS;
    }

    public String getDevelocityUrl() {
        return DEVELOCITY_URL;
    }

    public String getAllowUntrustedServer() {
        return ALLOW_UNTRUSTED_SERVER;
    }

    public String getDevelocityPluginVersion() {
        return DEVELOCITY_PLUGIN_VERSION;
    }

    public String getCommonCustomUserDataPluginVersion() {
        return CCUD_PLUGIN_VERSION;
    }

    public String getDevelocityExtensionVersion() {
        return DEVELOCITY_EXTENSION_VERSION;
    }

    public String getCommonCustomUserDataExtensionVersion() {
        return CCUD_EXTENSION_VERSION;
    }

    public String getCustomDevelocityExtensionCoordinates() {
        return CUSTOM_DEVELOCITY_EXTENSION_COORDINATES;
    }

    public String getCustomCommonCustomUserDataExtensionCoordinates() {
        return CUSTOM_CCUD_EXTENSION_COORDINATES;
    }

    public String getExtensionCaptureFileFingerprints() {
        return EXTENSION_CAPTURE_FILE_FINGERPRINTS;
    }

    public String getInstrumentCommandLineBuildStep() {
        return INSTRUMENT_COMMAND_LINE_BUILD_STEP;
    }

    public String getDevelocityAccessKey() {
        return DEVELOCITY_ACCESS_KEY;
    }

    public String getDevelocityAccessTokenExpiry() {
        return DEVELOCITY_ACCESS_TOKEN_EXPIRY;
    }

    public String getEnforceDevelocityUrl() {
        return ENFORCE_DEVELOCITY_URL;
    }

    public String getEnableInjection() {
        return ENABLE_INJECTION;
    }

}
