package com.gradle.develocity.maven

import com.gradle.develocity.TcPluginConfig
import com.gradle.develocity.maven.testutils.MavenBuildStepConfig
import com.gradle.develocity.maven.testutils.MavenProject

import static org.junit.Assume.assumeTrue

class TeamCityGoalFilteringExtensionApplicationTest extends BaseExtensionApplicationTest {

    def "does not publish build scan for TeamCity specific info goal invocation (#jdkCompatibleMavenVersion)"() {
        assumeTrue jdkCompatibleMavenVersion.isJvmVersionCompatible()
        assumeTrue DEVELOCITY_URL != null

        given:
        def mvnProject = new MavenProject.Configuration().buildIn(checkoutDir)

        and:
        def gePluginConfig = new TcPluginConfig(
            develocityUrl: DEVELOCITY_URL,
            develocityExtensionVersion: DEVELOCITY_EXTENSION_VERSION,
        )

        and:
        def mvnBuildStepConfig = new MavenBuildStepConfig(
            checkoutDir: checkoutDir,
            goals: 'org.jetbrains.maven:info-maven3-plugin:1.0.2:info',
        )

        when:
        def output = run(jdkCompatibleMavenVersion.mavenVersion, mvnProject, gePluginConfig, mvnBuildStepConfig)

        then:
        outputMissesTeamCityServiceMessageBuildStarted(output)
        outputMissesTeamCityServiceMessageBuildScanUrl(output)

        where:
        jdkCompatibleMavenVersion << SUPPORTED_MAVEN_VERSIONS
    }

}
