package nu.studer.teamcity.buildscan.connection

import jetbrains.buildServer.parameters.ParametersProvider
import jetbrains.buildServer.serverSide.SBuild
import jetbrains.buildServer.serverSide.parameters.types.PasswordsProvider
import spock.lang.Specification

import static DevelocityConnectionConstants.DEVELOCITY_ACCESS_KEY_ENV_VAR
import static nu.studer.teamcity.buildscan.connection.DevelocityConnectionConstants.GRADLE_ENTERPRISE_ACCESS_KEY_ENV_VAR

class DevelocityPasswordProviderTest extends Specification {

    PasswordsProvider passwordsProvider
    SBuild sBuild
    ParametersProvider parametersProvider

    void setup() {
        passwordsProvider = new DevelocityPasswordProvider()

        sBuild = Stub(SBuild)
        parametersProvider = Stub(ParametersProvider)
        sBuild.getParametersProvider() >> parametersProvider
    }

    def "returns no elements when no access key variables are set"() {
        when:
        def passwordParameters = passwordsProvider.getPasswordParameters(sBuild)

        then:
        passwordParameters.isEmpty()
    }

    def "returns password parameters when access key variables are set"() {
        given:
        def value = "develocity.example.com=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
        parametersProvider.get(DEVELOCITY_ACCESS_KEY_ENV_VAR) >> value
        parametersProvider.get(GRADLE_ENTERPRISE_ACCESS_KEY_ENV_VAR) >> value

        when:
        def passwordParameters = passwordsProvider.getPasswordParameters(sBuild)

        then:
        passwordParameters.collect {
            it.name
        } == [DEVELOCITY_ACCESS_KEY_ENV_VAR, GRADLE_ENTERPRISE_ACCESS_KEY_ENV_VAR]
        passwordParameters.forEach {
            assert it.value == value
        }
    }

}
