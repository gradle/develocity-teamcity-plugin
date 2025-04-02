package nu.studer.teamcity.buildscan.connection;

import jetbrains.buildServer.serverSide.Parameter;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SimpleParameter;
import jetbrains.buildServer.serverSide.parameters.types.PasswordsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

import static nu.studer.teamcity.buildscan.connection.DevelocityConnectionConstants.DEVELOCITY_ACCESS_KEY_ENV_VAR;
import static nu.studer.teamcity.buildscan.connection.DevelocityConnectionConstants.GRADLE_ENTERPRISE_ACCESS_KEY_ENV_VAR;

import com.google.common.base.Strings;

/**
 * This class implements {@link PasswordsProvider} in order to hide the env.DEVELOCITY_ACCESS_KEY secret in the
 * parameters output screen
 */
public final class DevelocityPasswordProvider implements PasswordsProvider {

    @NotNull
    @Override
    public Collection<Parameter> getPasswordParameters(@NotNull SBuild build) {
        Collection<Parameter> passwordParameters = new ArrayList<Parameter>(1);
        maybeAddPasswordParameter(build, DEVELOCITY_ACCESS_KEY_ENV_VAR, passwordParameters);
        maybeAddPasswordParameter(build, GRADLE_ENTERPRISE_ACCESS_KEY_ENV_VAR, passwordParameters);

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
