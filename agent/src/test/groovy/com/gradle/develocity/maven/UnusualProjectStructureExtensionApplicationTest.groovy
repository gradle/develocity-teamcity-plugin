package com.gradle.develocity.maven

import com.gradle.develocity.TcPluginConfig
import com.gradle.develocity.maven.testutils.MavenBuildStepConfig
import com.gradle.develocity.maven.testutils.MavenProject

import static org.junit.Assume.assumeTrue

class UnusualProjectStructureExtensionApplicationTest extends BaseExtensionApplicationTest {

    def "publishes build scan when pom is in a subdirectory and extensions.xml is in project root directory (#jdkCompatibleMavenVersion)"() {
        assumeTrue jdkCompatibleMavenVersion.isJvmVersionCompatible()
        assumeTrue DEVELOCITY_URL != null

        given:
        def mvnProject = new MavenProject.Configuration(
            geUrl: DEVELOCITY_URL,
            geExtensionVersion: DEVELOCITY_EXTENSION_VERSION,
            pomDirName: 'subdir'
        ).buildIn(checkoutDir)

        and:
        def gePluginConfig = new TcPluginConfig(
            develocityExtensionVersion: DEVELOCITY_EXTENSION_VERSION,
        )

        and:
        def mvnBuildStepConfig = new MavenBuildStepConfig(
            checkoutDir: checkoutDir,
            pathToPomFile: getRelativePath(checkoutDir, mvnProject.pom)
        )

        when:
        def output = run(jdkCompatibleMavenVersion.mavenVersion, mvnProject, gePluginConfig, mvnBuildStepConfig)

        then:
        outputContainsBuildSuccess(output)

        and:
        outputContainsTeamCityServiceMessageBuildStarted(output)
        outputContainsTeamCityServiceMessageBuildScanUrl(output)

        where:
        jdkCompatibleMavenVersion << SUPPORTED_MAVEN_VERSIONS
    }

    def "publishes build scan when pom is in a subdirectory and subdirectory is specified as pom path and extensions.xml is in project root directory (#jdkCompatibleMavenVersion)"() {
        assumeTrue jdkCompatibleMavenVersion.isJvmVersionCompatible()
        assumeTrue DEVELOCITY_URL != null

        given:
        def mvnProject = new MavenProject.Configuration(
            geUrl: DEVELOCITY_URL,
            geExtensionVersion: DEVELOCITY_EXTENSION_VERSION,
            pomDirName: 'subdir'
        ).buildIn(checkoutDir)

        and:
        def gePluginConfig = new TcPluginConfig(
            develocityExtensionVersion: DEVELOCITY_EXTENSION_VERSION,
        )

        and:
        def mvnBuildStepConfig = new MavenBuildStepConfig(
            checkoutDir: checkoutDir,
            pathToPomFile: getRelativePath(checkoutDir, mvnProject.pom.parentFile)
        )

        when:
        def output = run(jdkCompatibleMavenVersion.mavenVersion, mvnProject, gePluginConfig, mvnBuildStepConfig)

        then:
        outputContainsBuildSuccess(output)

        and:
        outputContainsTeamCityServiceMessageBuildStarted(output)
        outputContainsTeamCityServiceMessageBuildScanUrl(output)

        where:
        jdkCompatibleMavenVersion << SUPPORTED_MAVEN_VERSIONS
    }

    def "publishes build scan when pom is in a subdirectory and extensions.xml is in a higher subdirectory (#jdkCompatibleMavenVersion)"() {
        assumeTrue jdkCompatibleMavenVersion.isJvmVersionCompatible()
        assumeTrue DEVELOCITY_URL != null

        given:
        def mvnProject = new MavenProject.Configuration(
            geUrl: DEVELOCITY_URL,
            geExtensionVersion: DEVELOCITY_EXTENSION_VERSION,
            pomDirName: 'subdir1/subdir2',
            dotMvnParentDirName: 'subdir1',
        ).buildIn(checkoutDir)

        and:
        def gePluginConfig = new TcPluginConfig(
            develocityExtensionVersion: DEVELOCITY_EXTENSION_VERSION,
        )

        and:
        def mvnBuildStepConfig = new MavenBuildStepConfig(
            checkoutDir: checkoutDir,
            pathToPomFile: getRelativePath(checkoutDir, mvnProject.pom)
        )

        when:
        def output = run(jdkCompatibleMavenVersion.mavenVersion, mvnProject, gePluginConfig, mvnBuildStepConfig)

        then:
        outputContainsBuildSuccess(output)

        and:
        outputContainsTeamCityServiceMessageBuildStarted(output)
        outputContainsTeamCityServiceMessageBuildScanUrl(output)

        where:
        jdkCompatibleMavenVersion << SUPPORTED_MAVEN_VERSIONS
    }

}
