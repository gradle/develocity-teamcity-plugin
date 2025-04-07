package com.gradle.develocity.teamcity.agent.gradle.testutils

import jetbrains.buildServer.agent.AgentLifeCycleListener
import jetbrains.buildServer.util.EventDispatcher
import com.gradle.develocity.teamcity.agent.BuildScanServiceMessageInjector
import com.gradle.develocity.teamcity.agent.ExtensionApplicationListener
import org.jetbrains.annotations.NotNull

class TestBuildScanServiceMessageInjector extends BuildScanServiceMessageInjector {

    private final File gradleUserHome

    TestBuildScanServiceMessageInjector(File gradleUserHome, @NotNull EventDispatcher<AgentLifeCycleListener> eventDispatcher, @NotNull ExtensionApplicationListener extensionApplicationListener) {
        super(eventDispatcher, extensionApplicationListener)
        this.gradleUserHome = gradleUserHome
    }

    @NotNull
    @Override
    protected File getGradleUserHome() {
        return gradleUserHome
    }

}
