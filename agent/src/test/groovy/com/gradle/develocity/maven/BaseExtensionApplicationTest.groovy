package com.gradle.develocity.maven

import jetbrains.buildServer.agent.AgentLifeCycleListener
import jetbrains.buildServer.util.EventDispatcher
import com.gradle.develocity.BuildScanServiceMessageInjector
import com.gradle.develocity.ExtensionApplicationListener
import com.gradle.develocity.TcPluginConfig
import com.gradle.develocity.TestBuildRunnerContext
import com.gradle.develocity.maven.testutils.MavenBuildStepConfig
import com.gradle.develocity.maven.testutils.MavenInstaller
import com.gradle.develocity.maven.testutils.MavenProject
import com.gradle.develocity.maven.testutils.MavenRunner
import spock.lang.Specification
import spock.lang.TempDir

class BaseExtensionApplicationTest extends Specification {

    static final List<JdkCompatibleMavenVersion> SUPPORTED_MAVEN_VERSIONS = [
        new JdkCompatibleMavenVersion('3.3.1', 7, 11),
        new JdkCompatibleMavenVersion('3.5.0', 7, 11),
        new JdkCompatibleMavenVersion('3.5.4', 7, 11),
        new JdkCompatibleMavenVersion('3.6.0', 7, 11),
        new JdkCompatibleMavenVersion('3.6.3', 7, 11),
        new JdkCompatibleMavenVersion('3.8.1', 7, 11),
        new JdkCompatibleMavenVersion('3.8.6', 7, 11),
        new JdkCompatibleMavenVersion('3.9.0', 7, 11),
        new JdkCompatibleMavenVersion('3.9.4', 7, 11)
    ]

    static final List<JdkCompatibleMavenVersion> UNSUPPORTED_MAVEN_VERSIONS = [
        new JdkCompatibleMavenVersion('3.0', 5, 11),
        new JdkCompatibleMavenVersion('3.0.5', 5, 11),
        new JdkCompatibleMavenVersion('3.1.0', 5, 11),
        new JdkCompatibleMavenVersion('3.1.1', 5, 11),
        new JdkCompatibleMavenVersion('3.2.1', 6, 11),
        new JdkCompatibleMavenVersion('3.2.5', 6, 11)
    ]

    static final String DEVELOCITY_URL_STR = System.getenv('GRADLE_ENTERPRISE_TEST_INSTANCE')
    static final URI DEVELOCITY_URL = DEVELOCITY_URL_STR ? new URI(DEVELOCITY_URL_STR) : null
    static final String DEVELOCITY_EXTENSION_VERSION = '1.20'
    static final String CCUD_EXTENSION_VERSION = '1.12.5'

    @TempDir
    File checkoutDir

    @TempDir
    File agentTempDir

    @TempDir
    File agentMavenInstallation

    ExtensionApplicationListener extensionApplicationListener

    void setup() {
        extensionApplicationListener = Mock(ExtensionApplicationListener)
    }

    String run(String mvnVersion, MavenProject mvnProject, TcPluginConfig tcPluginConfig) {
        run(mvnVersion, mvnProject, tcPluginConfig, new MavenBuildStepConfig(checkoutDir: checkoutDir))
    }

    String run(String mvnVersion, MavenProject mvnProject, TcPluginConfig tcPluginConfig, MavenBuildStepConfig mvnBuildStepConfig) {
        def injector = new BuildScanServiceMessageInjector(EventDispatcher.create(AgentLifeCycleListener.class), extensionApplicationListener)

        new MavenInstaller(
            version: mvnVersion,
            installationDir: agentMavenInstallation,
        ).installMaven()

        def configParameters = tcPluginConfig.toConfigParameters()
        def runnerParameters = mvnBuildStepConfig.toRunnerParameters()

        def context = new TestBuildRunnerContext('Maven2', agentTempDir, configParameters, runnerParameters, ['maven': agentMavenInstallation.absolutePath], mvnBuildStepConfig.isVirtualContext)
        injector.beforeRunnerStart(context)

        def runner = new MavenRunner(
            installationDir: agentMavenInstallation,
            projectDir: new File(runnerParameters.get('teamcity.build.workingDir') ?: checkoutDir.absolutePath),
            multiModuleProjectDir: mvnProject.dotMvn.parentFile,
            arguments: ("${runnerParameters.get('goals')} ${runnerParameters.get('runnerArgs')}".toString().trim().split(/\s+/))
        )

        if (runnerParameters.containsKey('pomLocation')) {
            runner.arguments += ['-f', new File(runnerParameters.get('teamcity.build.checkoutDir'), runnerParameters.get('pomLocation')).absolutePath]
        }

        return runner.runBuild()
    }

    void outputContainsTeamCityServiceMessageBuildStarted(String output) {
        def serviceMsg = "##teamcity[com.gradle.develocity.buildScanLifeCycle 'BUILD_STARTED']"
        assert output.contains(serviceMsg)
        assert 1 == output.count(serviceMsg)
    }

    void outputMissesTeamCityServiceMessageBuildStarted(String output) {
        def serviceMsg = "##teamcity[com.gradle.develocity.buildScanLifeCycle 'BUILD_STARTED']"
        assert !output.contains(serviceMsg)
    }

    void outputContainsTeamCityServiceMessageBuildScanUrl(String output) {
        def serviceMsg = "##teamcity[com.gradle.develocity.buildScanLifeCycle 'BUILD_SCAN_URL:${DEVELOCITY_URL}/s/"
        assert output.contains(serviceMsg)
        assert 1 == output.count(serviceMsg)
    }

    void outputMissesTeamCityServiceMessageBuildScanUrl(String output) {
        def serviceMsg = "##teamcity[com.gradle.develocity.buildScanLifeCycle 'BUILD_SCAN_URL:${DEVELOCITY_URL}/s/"
        assert !output.contains(serviceMsg)
    }

    void outputContainsBuildSuccess(String output) {
        assert output.contains("[INFO] BUILD SUCCESS")
    }

    static String getRelativePath(File parent, File child) {
        parent.toPath().relativize(child.toPath()).toString()
    }

    static final class JdkCompatibleMavenVersion {

        final String mavenVersion
        private final Integer jdkMin
        private final Integer jdkMax

        JdkCompatibleMavenVersion(String mavenVersion, Integer jdkMin, Integer jdkMax) {
            this.mavenVersion = mavenVersion
            this.jdkMin = jdkMin
            this.jdkMax = jdkMax
        }

        boolean isJvmVersionCompatible() {
            def jvmVersion = getJvmVersion()
            jdkMin <= jvmVersion && jvmVersion <= jdkMax
        }

        private static int getJvmVersion() {
            String version = System.getProperty('java.version')
            if (version.startsWith('1.')) {
                Integer.parseInt(version.substring(2, 3))
            } else {
                Integer.parseInt(version.substring(0, version.indexOf('.')))
            }
        }

        @Override
        String toString() {
            return "JdkCompatibleMavenVersion{" +
                "Maven " + mavenVersion +
                ", JDK " + jdkMin + "-" + jdkMax +
                '}'
        }

    }

}
