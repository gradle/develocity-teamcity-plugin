package com.gradle.develocity.teamcity.connection;

import jetbrains.buildServer.serverSide.Parameter;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SimpleParameter;
import jetbrains.buildServer.serverSide.parameters.types.PasswordsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Strings;

import static com.gradle.develocity.teamcity.connection.DevelocityConnectionConstants.*;

/**
 * This class implements {@link PasswordsProvider} in order to hide the Develocity secrets in the
 * parameters output screen
 */
public final class DevelocityPasswordProvider implements PasswordsProvider {

    @NotNull
    @Override
    public Collection<Parameter> getPasswordParameters(@NotNull SBuild build) {
        Collection<Parameter> passwordParameters = new ArrayList<Parameter>(1);
        maybeAddPasswordParameter(build, DEVELOCITY_ACCESS_KEY_ENV_VAR, passwordParameters);
        maybeAddPasswordParameter(build, GRADLE_ENTERPRISE_ACCESS_KEY_ENV_VAR, passwordParameters);
        maybeAddPasswordParameter(build, GRADLE_PLUGIN_REPOSITORY_USERNAME_ENV_VAR, passwordParameters);
        maybeAddPasswordParameter(build, GRADLE_PLUGIN_REPOSITORY_PASSWORD_ENV_VAR, passwordParameters);

        return passwordParameters;
    }

    private static void maybeAddPasswordParameter(@NotNull SBuild build, String accessKeyEnvVar, Collection<Parameter> passwordParameters) {
        String accessKey = build.getParametersProvider().get(accessKeyEnvVar);
        if (!Strings.isNullOrEmpty(accessKey)) {
            Parameter parameter = new SimpleParameter(accessKeyEnvVar, accessKey);
            passwordParameters.add(parameter);
        }
    }

}
