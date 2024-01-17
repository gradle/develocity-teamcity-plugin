package com.gradle.develocity.maven

import com.gradle.develocity.TcPluginConfig
import com.gradle.develocity.maven.testutils.MavenProject

import static org.junit.Assume.assumeTrue

class UrlConfigurationExtensionApplicationTest extends BaseExtensionApplicationTest {

    def "ignores Develocity URL requested via TC config when Develocity extension is not applied via the classpath (#jdkCompatibleMavenVersion)"() {
        assumeTrue jdkCompatibleMavenVersion.isJvmVersionCompatible()
        assumeTrue GE_URL != null

        given:
        def mvnProject = new MavenProject.Configuration(
            geUrl: GE_URL,
            geExtensionVersion: GE_EXTENSION_VERSION,
        ).buildIn(checkoutDir)

        and:
        def gePluginConfig = new TcPluginConfig(
                develocityUrl: new URI('https://develocity-server.invalid'),
                develocityAllowUntrustedServer: true,
                develocityExtensionVersion: GE_EXTENSION_VERSION,
        )

        when:
        def output = run(jdkCompatibleMavenVersion.mavenVersion, mvnProject, gePluginConfig)

        then:
        0 * extensionApplicationListener.geExtensionApplied(_)
        0 * extensionApplicationListener.ccudExtensionApplied(_)

        and:
        outputContainsTeamCityServiceMessageBuildStarted(output)
        outputContainsTeamCityServiceMessageBuildScanUrl(output)

        where:
        jdkCompatibleMavenVersion << SUPPORTED_MAVEN_VERSIONS
    }

    def "configures Develocity URL requested via TC config when Develocity extension is applied via classpath (#jdkCompatibleMavenVersion)"() {
        assumeTrue jdkCompatibleMavenVersion.isJvmVersionCompatible()
        assumeTrue GE_URL != null

        given:
        def mvnProject = new MavenProject.Configuration(
            geUrl: 'https://develocity-server.invalid',
            geExtensionVersion: null,
        ).buildIn(checkoutDir)

        and:
        def gePluginConfig = new TcPluginConfig(
                develocityUrl: GE_URL,
                develocityAllowUntrustedServer: true,
                develocityExtensionVersion: GE_EXTENSION_VERSION,
        )

        when:
        def output = run(jdkCompatibleMavenVersion.mavenVersion, mvnProject, gePluginConfig)

        then:
        1 * extensionApplicationListener.geExtensionApplied(GE_EXTENSION_VERSION)
        0 * extensionApplicationListener.ccudExtensionApplied(_)

        and:
        outputContainsTeamCityServiceMessageBuildStarted(output)
        outputContainsTeamCityServiceMessageBuildScanUrl(output)

        where:
        jdkCompatibleMavenVersion << SUPPORTED_MAVEN_VERSIONS
    }

    def "enforces Develocity URL and allowUntrustedServer in project if enforce url parameter is enabled (#jdkCompatibleMavenVersion)"() {
        assumeTrue jdkCompatibleMavenVersion.isJvmVersionCompatible()
        assumeTrue GE_URL != null

        given:
        def mvnProject = new MavenProject.Configuration(
                geUrl: new URI('https://develocity-server.invalid'),
                geExtensionVersion: GE_EXTENSION_VERSION,
        ).buildIn(checkoutDir)

        and:
        def gePluginConfig = new TcPluginConfig(
                develocityUrl: GE_URL,
                develocityAllowUntrustedServer: true,
                develocityEnforceUrl: true,
                develocityExtensionVersion: GE_EXTENSION_VERSION,
        )

        when:
        def output = run(jdkCompatibleMavenVersion.mavenVersion, mvnProject, gePluginConfig)

        then:
        0 * extensionApplicationListener.geExtensionApplied(_)
        0 * extensionApplicationListener.ccudExtensionApplied(_)

        and:
        outputContainsTeamCityServiceMessageBuildStarted(output)
        outputContainsTeamCityServiceMessageBuildScanUrl(output)

        where:
        jdkCompatibleMavenVersion << SUPPORTED_MAVEN_VERSIONS
    }

}
