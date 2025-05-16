package com.gradle.develocity.teamcity.connection;

import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.oauth.OAuthConnectionDescriptor;
import jetbrains.buildServer.serverSide.oauth.OAuthProvider;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.gradle.develocity.teamcity.connection.DevelocityConnectionConstants.*;

public final class DevelocityConnectionProvider extends OAuthProvider {

    private static final Logger LOGGER = Logger.getLogger("jetbrains.buildServer.BUILDSCAN");

    private static final String DEFAULT_PLUGIN_VERSIONS_RESOURCE = "default-plugin-versions.properties";

    private final PluginDescriptor descriptor;

    public DevelocityConnectionProvider(PluginDescriptor pluginDescriptor) {
        this.descriptor = pluginDescriptor;
    }

    @NotNull
    @Override
    public String getType() {
        return DEVELOCITY_CONNECTION_PROVIDER;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Develocity";
    }

    @Nullable
    @Override
    public String getEditParametersUrl() {
        return descriptor.getPluginResourcesPath("develocityConnectionDialog.jsp");
    }

    @NotNull
    @Override
    public String describeConnection(@NotNull OAuthConnectionDescriptor connection) {
        Map<String, String> params = connection.getParameters();

        String description = "Develocity Connection Settings:\n";

        String geUrl = params.get(DEVELOCITY_URL);
        if (geUrl != null) {
            description += String.format("* Develocity Server URL: %s\n", geUrl);
        }

        String allowUntrustedServer = params.get(ALLOW_UNTRUSTED_SERVER);
        if (allowUntrustedServer != null) {
            description += String.format("* Allow Untrusted Server: %s\n", allowUntrustedServer);
        }

        String geAccessKey = params.get(DEVELOCITY_ACCESS_KEY);
        if (geAccessKey != null) {
            description += String.format("* Develocity Access Key: %s\n", "******");
        }

        String accessTokenExpiry = params.get(DEVELOCITY_ACCESS_TOKEN_EXPIRY);
        if (accessTokenExpiry != null) {
            description += String.format("* Develocity Access Token Expiry: %s\n", accessTokenExpiry);
        }

        String enforceGeUrl = params.get(ENFORCE_DEVELOCITY_URL);
        if (enforceGeUrl != null) {
            description += String.format("* Enforce Develocity Server URL: %s\n", enforceGeUrl);
        }

        String enableInjection = params.get(ENABLE_INJECTION);
        if (enableInjection != null) {
            description += String.format("* Enable Develocity auto-injection: %s\n", enableInjection);
        }

        description += "\nGradle Settings:\n";

        String gePluginVersion = params.get(DEVELOCITY_PLUGIN_VERSION);
        if (gePluginVersion != null) {
            description += String.format("* Develocity Gradle Plugin Version: %s\n", gePluginVersion);
        }

        String ccudPluginVersion = params.get(CCUD_PLUGIN_VERSION);
        if (ccudPluginVersion != null) {
            description += String.format("* Common Custom User Data Gradle Plugin Version: %s\n", ccudPluginVersion);
        }

        String gradlePluginRepositoryUrl = params.get(GRADLE_PLUGIN_REPOSITORY_URL);
        if (gradlePluginRepositoryUrl != null) {
            description += String.format("* Gradle Plugin Repository URL: %s\n", gradlePluginRepositoryUrl);
        }

        String gradlePluginCaptureFileFingerprints = params.get(GRADLE_PLUGIN_CAPTURE_FILE_FINGERPRINTS);
        if (gradlePluginCaptureFileFingerprints != null) {
            description += String.format("* Gradle Plugin Capture Task Input Files: %s\n", gradlePluginCaptureFileFingerprints);
        }

        description += "\nMaven Settings:\n";

        String geExtensionVersion = params.get(DEVELOCITY_EXTENSION_VERSION);
        if (geExtensionVersion != null) {
            description += String.format("* Develocity Maven Extension Version: %s\n", geExtensionVersion);
        }

        String ccudExtensionVersion = params.get(CCUD_EXTENSION_VERSION);
        if (ccudExtensionVersion != null) {
            description += String.format("* Common Custom User Data Maven Extension Version: %s\n", ccudExtensionVersion);
        }

        String customGeExtensionCoordinates = params.get(CUSTOM_DEVELOCITY_EXTENSION_COORDINATES);
        if (customGeExtensionCoordinates != null) {
            description += String.format("* Develocity Maven Extension Custom Coordinates: %s\n", customGeExtensionCoordinates);
        }

        String customCcudExtensionCoordinates = params.get(CUSTOM_CCUD_EXTENSION_COORDINATES);
        if (customCcudExtensionCoordinates != null) {
            description += String.format("* Common Custom User Data Maven Extension Custom Coordinates: %s\n", customCcudExtensionCoordinates);
        }

        String extensionCaptureFileFingerprints = params.get(EXTENSION_CAPTURE_FILE_FINGERPRINTS);
        if (extensionCaptureFileFingerprints != null) {
            description += String.format("* Extension Capture Goal Input Files: %s\n", extensionCaptureFileFingerprints);
        }

        description += "\nTeamCity Build Steps Settings:\n";

        String instrumentCommandLineBuildStep = params.get(INSTRUMENT_COMMAND_LINE_BUILD_STEP);
        if (instrumentCommandLineBuildStep != null) {
            description += String.format("* Instrument Command Line Build Steps: %s\n", instrumentCommandLineBuildStep);
        }

        return description;
    }

    @Nullable
    @Override
    public Map<String, String> getDefaultProperties() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_PLUGIN_VERSIONS_RESOURCE);
        if (inputStream == null) {
            return null;
        }

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            LOGGER.warn("Unable to load default plugin versions from " + DEFAULT_PLUGIN_VERSIONS_RESOURCE, e);
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Map<String, String> defaultProperties = new HashMap<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            defaultProperties.put(entry.getKey().toString(), String.valueOf(entry.getValue()));
        }

        return defaultProperties;
    }

    @NotNull
    @Override
    public PropertiesProcessor getPropertiesProcessor() {
        return properties -> {
            List<InvalidProperty> errors = new ArrayList<>();
            String accessKey = properties.get(DEVELOCITY_ACCESS_KEY);
            if (accessKey != null && !DevelocityAccessKeyValidator.isValid(accessKey)) {
                errors.add(new InvalidProperty(DEVELOCITY_ACCESS_KEY, "Invalid access key"));
            }
            String accessTokenExpiry = properties.get(DEVELOCITY_ACCESS_TOKEN_EXPIRY);
            if (accessTokenExpiry != null && !isValid(accessTokenExpiry)) {
                errors.add(new InvalidProperty(DEVELOCITY_ACCESS_TOKEN_EXPIRY, "It should be an integer between 1 and 24"));
            }
            return errors;
        };
    }

    private static boolean isValid(String accessTokenExpiry) {
        try {
            if (accessTokenExpiry != null && !accessTokenExpiry.isEmpty()) {
                int expiry = Integer.parseInt(accessTokenExpiry);
                return expiry > 0 && expiry <= 24;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
