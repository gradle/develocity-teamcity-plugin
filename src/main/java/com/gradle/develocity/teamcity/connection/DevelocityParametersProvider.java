package com.gradle.develocity.teamcity.connection;

import com.gradle.develocity.teamcity.token.DevelocityAccessCredentials;
import com.gradle.develocity.teamcity.token.ShortLivedTokenClient;
import com.gradle.develocity.teamcity.token.ShortLivedTokenClientFactory;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProjectFeatureDescriptor;
import jetbrains.buildServer.serverSide.oauth.OAuthConstants;
import jetbrains.buildServer.serverSide.parameters.BuildParametersProvider;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.gradle.develocity.teamcity.connection.DevelocityConnectionConstants.*;

/**
 * This implementation of {@link BuildParametersProvider} injects configuration parameters and environment variables
 * needed in order to automatically apply Develocity to Gradle and Maven builds, based on the configuration of
 * the connection.
 */
@SuppressWarnings({"DuplicatedCode", "Convert2Diamond"})
public final class DevelocityParametersProvider implements BuildParametersProvider {

    private static final Logger LOGGER = Logger.getLogger("jetbrains.buildServer.BUILDSCAN");

    private final ShortLivedTokenClientFactory shortLivedTokenClientFactory;
    private SBuild previousBuild;
    private Map<String, String> cachedParametersFromPreviousBuild;

    public DevelocityParametersProvider(ShortLivedTokenClientFactory shortLivedTokenClientFactory) {
        this.shortLivedTokenClientFactory = shortLivedTokenClientFactory;
    }

    @NotNull
    @Override
    public Map<String, String> getParameters(@NotNull SBuild build, boolean emulationMode) {
        // Execute only once per build
        if (build != previousBuild) {
            List<Map<String, String>> connections = getAllDevelocityConnections(build);

            // descriptorParameters can contain null values, but TeamCity handles these null parameters as if they were not set
            Map<String, String> params = new HashMap<>();
            for (int i = connections.size() - 1; i >= 0; i--) {
                Map<String, String> connectionParams = connections.get(i);
                String allowUntrustedServer = connectionParams.get(ALLOW_UNTRUSTED_SERVER);
                String token = createShortLivedToken(
                    connectionParams.get(DEVELOCITY_ACCESS_KEY),
                    connectionParams.get(DEVELOCITY_ACCESS_TOKEN_EXPIRY),
                    allowUntrustedServer
                );
                if (token != null) {
                    setParameter(DEVELOCITY_ACCESS_KEY_ENV_VAR, token, params);
                    // Set the legacy access key variable to support legacy GE plugins
                    setParameter(GRADLE_ENTERPRISE_ACCESS_KEY_ENV_VAR, token, params);
                }
                setParameter(GRADLE_PLUGIN_REPOSITORY_URL_CONFIG_PARAM, connectionParams.get(GRADLE_PLUGIN_REPOSITORY_URL), params);
                setParameter(GRADLE_PLUGIN_REPOSITORY_USERNAME_ENV_VAR, connectionParams.get(GRADLE_PLUGIN_REPOSITORY_USERNAME), params);
                setParameter(GRADLE_PLUGIN_REPOSITORY_PASSWORD_ENV_VAR, connectionParams.get(GRADLE_PLUGIN_REPOSITORY_PASSWORD), params);
                setParameter(GRADLE_PLUGIN_CAPTURE_FILE_FINGERPRINTS_CONFIG_PARAM, connectionParams.get(GRADLE_PLUGIN_CAPTURE_FILE_FINGERPRINTS), params);
                setParameter(DEVELOCITY_URL_CONFIG_PARAM, connectionParams.get(DEVELOCITY_URL), params);
                setParameter(ALLOW_UNTRUSTED_SERVER_CONFIG_PARAM, allowUntrustedServer, params);
                setParameter(DEVELOCITY_PLUGIN_VERSION_CONFIG_PARAM, connectionParams.get(DEVELOCITY_PLUGIN_VERSION), params);
                setParameter(CCUD_PLUGIN_VERSION_CONFIG_PARAM, connectionParams.get(CCUD_PLUGIN_VERSION), params);
                setParameter(DEVELOCITY_EXTENSION_VERSION_CONFIG_PARAM, connectionParams.get(DEVELOCITY_EXTENSION_VERSION), params);
                setParameter(CCUD_EXTENSION_VERSION_CONFIG_PARAM, connectionParams.get(CCUD_EXTENSION_VERSION), params);
                setParameter(CUSTOM_DEVELOCITY_EXTENSION_COORDINATES_CONFIG_PARAM, connectionParams.get(CUSTOM_DEVELOCITY_EXTENSION_COORDINATES), params);
                setParameter(CUSTOM_CCUD_EXTENSION_COORDINATES_CONFIG_PARAM, connectionParams.get(CUSTOM_CCUD_EXTENSION_COORDINATES), params);
                setParameter(DEVELOCITY_EXTENSION_CAPTURE_FILE_FINGERPRINTS_CONFIG_PARAM, connectionParams.get(EXTENSION_CAPTURE_FILE_FINGERPRINTS), params);
                setParameter(INSTRUMENT_COMMAND_LINE_BUILD_STEP_CONFIG_PARAM, connectionParams.get(INSTRUMENT_COMMAND_LINE_BUILD_STEP), params);
                setParameter(ENABLE_INJECTION_CONFIG_PARAM, connectionParams.get(ENABLE_INJECTION), params);
                setParameter(ENFORCE_DEVELOCITY_URL_CONFIG_PARAM, connectionParams.get(ENFORCE_DEVELOCITY_URL), params);
            }
            previousBuild = build;
            cachedParametersFromPreviousBuild = params;
            return params;
        }
        return cachedParametersFromPreviousBuild;
    }

    private String createShortLivedToken(String accessKey, String expiry, String allowUntrustedServer) {
        if (accessKey == null) {
            return null;
        }
        if (!DevelocityAccessCredentials.isValid(accessKey)) {
            LOGGER.error("Develocity access key format is not valid");
            return null;
        }
        DevelocityAccessCredentials allKeys = DevelocityAccessCredentials.parse(accessKey);
        if (allKeys.isEmpty()) {
            return null;
        }

        Integer expiryAsInt = expiry != null ? Integer.parseInt(expiry) : null;
        boolean allowUntrusted = Boolean.parseBoolean(allowUntrustedServer);

        ShortLivedTokenClient tokenClient = shortLivedTokenClientFactory.create(allowUntrusted);

        List<DevelocityAccessCredentials.HostnameAccessKey> shortLivedTokens = allKeys.stream()
            .map(k -> tokenClient.get(
                "https://" + k.getHostname(), k, expiryAsInt))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        return shortLivedTokens.isEmpty()
            ? null
            : DevelocityAccessCredentials.of(shortLivedTokens).getRaw();
    }

    private static void setParameter(String key, String value, Map<String, String> params) {
        if (value != null) {
            params.put(key, value);
        }
    }

    @NotNull
    private static List<Map<String, String>> getAllDevelocityConnections(@NotNull SBuild build) {
        SBuildType buildType = build.getBuildType();
        if (buildType == null) {
            return Collections.emptyList();
        }

        List<Map<String, String>> connections = new ArrayList<Map<String, String>>();
        Collection<SProjectFeatureDescriptor> descriptors = buildType.getProject().getAvailableFeaturesOfType(OAuthConstants.FEATURE_TYPE);
        for (SProjectFeatureDescriptor descriptor : descriptors) {
            Map<String, String> parameters = descriptor.getParameters();
            String connectionType = parameters.get(OAuthConstants.OAUTH_TYPE_PARAM);
            if (DEVELOCITY_CONNECTION_PROVIDER.equals(connectionType)) {
                connections.add(parameters);
            }
        }
        return connections;
    }

    @NotNull
    @Override
    public Collection<String> getParametersAvailableOnAgent(@NotNull SBuild build) {
        return Collections.emptyList();
    }

}
