package com.gradle.develocity.teamcity.agent.bootstrapping.restore

import jetbrains.buildServer.agent.BuildRunnerContext
import spock.lang.Specification

import static com.gradle.develocity.teamcity.agent.bootstrapping.restore.OperatingSystem.LINUX
import static com.gradle.develocity.teamcity.agent.bootstrapping.restore.OperatingSystem.MACOS
import static com.gradle.develocity.teamcity.agent.bootstrapping.restore.OperatingSystem.UNKNOWN
import static com.gradle.develocity.teamcity.agent.bootstrapping.restore.OperatingSystem.WINDOWS

class OperatingSystemTest extends Specification {

    def "detects operating system for #osName"() {
        given:
        def ctx = Stub(BuildRunnerContext) {
            getConfigParameters() >> ["teamcity.agent.jvm.os.name": osName]
        }

        expect:
        OperatingSystem.current(ctx) == os

        where:
        osName       || os
        "Linux"      || LINUX
        "Mac OS X"   || MACOS
        "Windows 10" || WINDOWS
        "OpenBSD"    || UNKNOWN
    }

}
