package nu.studer.teamcity.buildscan.connection

import jetbrains.buildServer.parameters.ParametersProvider
import jetbrains.buildServer.serverSide.SBuild
import jetbrains.buildServer.serverSide.parameters.types.PasswordsProvider
import spock.lang.Specification

import static DevelocityConnectionConstants.DEVELOCITY_ACCESS_KEY_ENV_VAR

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

    def "returns no elements when env.DEVELOCITY_ACCESS_KEY is not set"() {
        given:
        parametersProvider.get(DEVELOCITY_ACCESS_KEY_ENV_VAR) >> null

        when:
        def passwordParameters = passwordsProvider.getPasswordParameters(sBuild)

        then:
        passwordParameters.isEmpty()
    }

    def "returns env.DEVELOCITY_ACCESS_KEY element when env.DEVELOCITY_ACCESS_KEY is set"() {
        given:
        def value = "develocity.example.com=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
        parametersProvider.get(DEVELOCITY_ACCESS_KEY_ENV_VAR) >> value

        when:
        def passwordParameters = passwordsProvider.getPasswordParameters(sBuild)

        then:
        def keyParams = passwordParameters.findAll { it.name == DEVELOCITY_ACCESS_KEY_ENV_VAR }
        keyParams.size() == 1
        keyParams[0].value == value
    }

}
