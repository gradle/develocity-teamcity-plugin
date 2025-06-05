package com.gradle.develocity.teamcity.agent.bootstrapping.restore

import jetbrains.buildServer.agent.BuildParametersMap
import jetbrains.buildServer.agent.BuildRunnerContext
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

class JdkProviderTest extends Specification {

    def "uses jdk without architecture qualifier"() {
        given:
        def jdkProvider = new JdkProvider(
                withEnvVariablesInContext([
                        "JDK_21_0",
                        "JDK_21_0_x64",
                        "JDK_21_0_ARM64"
                ]),
                new NoopJavaExecutableFinder("-path")
        )

        when:
        def jdk = jdkProvider.jdkForMajorVersion(21)

        then:
        jdk.present
        with(jdk.get()) {
            envVariableName() == "JDK_21_0"
            executablePath() == Paths.get("JDK_21_0-path")
            majorVersion() == 21
            minorVersion() == 0
        }
    }

    def "fails if there is no version without architecture"() {
        given:
        def jdkProvider = new JdkProvider(
                withEnvVariablesInContext([
                        "JDK_21_0_x64",
                        "JDK_21_0_ARM64"
                ]),
                new NoopJavaExecutableFinder("-path")
        )

        expect:
        !jdkProvider.jdkForMajorVersion(21).present
    }

    def "uses highest possible version"() {
        given:
        def jdkProvider = new JdkProvider(
                withEnvVariablesInContext([
                        "JDK_17_1",
                        "JDK_21_0",
                        "JDK_22_1",
                        "JDK_22_0"
                ]),
                new NoopJavaExecutableFinder("-path")
        )

        when:
        def jdk = jdkProvider.jdkForMajorVersion(21)

        then:
        jdk.present
        with(jdk.get()) {
            majorVersion() == 22
            minorVersion() == 1
        }
    }

    def "fails if no version meets requested major version"() {
        given:
        def jdkProvider = new JdkProvider(
                withEnvVariablesInContext([
                        "JDK_11_0",
                        "JDK_17_0",
                ]),
                new NoopJavaExecutableFinder("-path")
        )

        expect:
        !jdkProvider.jdkForMajorVersion(21).present
    }

    def "handles strange version number for JDK 8"() {
        given:
        def jdkProvider = new JdkProvider(
                withEnvVariablesInContext([
                        "JDK_1_8",
                        "JDK_17_0",
                ]),
                new NoopJavaExecutableFinder("-path")
        )

        when:
        def jdk = jdkProvider.jdkForMajorVersion(17)

        then:
        jdk.present
        with (jdk.get()) {
            majorVersion() == 17
        }
    }

    private BuildRunnerContext withEnvVariablesInContext(List<String> envVars) {
        def buildParametersMap = Stub(BuildParametersMap) {
            getEnvironmentVariables() >> envVars.collectEntries { [(it): it] }
        }

        def ctx = Stub(BuildRunnerContext) {
            getBuildParameters() >> buildParametersMap
        }

    }


    class NoopJavaExecutableFinder implements JdkProvider.JavaExecutableFinder {

        private final String postfix

        NoopJavaExecutableFinder(String postfix) {
            this.postfix = postfix
        }

        @Override
        Optional<Path> executablePathFor(Path jdkBasePath) {
            return Optional.of(Paths.get("${jdkBasePath}${postfix}"))
        }

    }

}
