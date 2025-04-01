package nu.studer.teamcity.buildscan.agent

class TcPluginConfig {

    URI gradlePluginRepositoryUrl
    URI develocityUrl
    boolean develocityAllowUntrustedServer
    boolean develocityEnforceUrl
    String develocityPluginVersion
    String ccudPluginVersion
    String develocityExtensionVersion
    String ccudExtensionVersion
    String develocityExtensionCustomCoordinates
    String ccudExtensionCustomCoordinates
    boolean enableCommandLineRunner

    // configuration params as they would be set by the user in the TeamCity configuration
    Map<String, String> toConfigParameters() {
        Map<String, String> configProps = [:]
        if (gradlePluginRepositoryUrl) {
            configProps.put 'develocityPlugin.gradle.plugin-repository.url', gradlePluginRepositoryUrl.toString()
        }
        if (develocityUrl) {
            configProps.put 'develocityPlugin.gradle-enterprise.url', develocityUrl.toString()
        }
        if (develocityAllowUntrustedServer) {
            configProps.put 'develocityPlugin.gradle-enterprise.allow-untrusted-server', 'true'
        }
        if (develocityEnforceUrl) {
            configProps.put 'develocityPlugin.gradle-enterprise.enforce-url', 'true'
        }
        if (develocityPluginVersion) {
            configProps.put 'develocityPlugin.gradle-enterprise.plugin.version', develocityPluginVersion
        }
        if (ccudPluginVersion) {
            configProps.put 'develocityPlugin.ccud.plugin.version', ccudPluginVersion
        }
        if (develocityExtensionVersion) {
            configProps.put 'develocityPlugin.gradle-enterprise.extension.version', develocityExtensionVersion
        }
        if (ccudExtensionVersion) {
            configProps.put 'develocityPlugin.ccud.extension.version', ccudExtensionVersion
        }
        if (develocityExtensionCustomCoordinates) {
            configProps.put('develocityPlugin.gradle-enterprise.extension.custom.coordinates', develocityExtensionCustomCoordinates)
        }
        if (ccudExtensionCustomCoordinates) {
            configProps.put('develocityPlugin.ccud.extension.custom.coordinates', ccudExtensionCustomCoordinates)
        }
        if (enableCommandLineRunner) {
            configProps.put 'develocityPlugin.command-line-build-step.enabled', 'true'
        }
        configProps
    }

}
