<p>
  <a href="https://github.com/gradle/develocity-teamcity-plugin/actions?query=workflow%3A%22Verify+Build%22"><img src="https://github.com/gradle/develocity-teamcity-plugin/workflows/Verify%20Build/badge.svg"></a>
</p>

develocity-teamcity-plugin
==========================

# Overview

[TeamCity](https://www.jetbrains.com/teamcity/) plugin that integrates with the Build Scan service and more generally with Develocity for Gradle and Maven builds run via TeamCity. Build scans are available as a free service on [scans.gradle.com](https://scans.gradle.com/) and commercially via [Develocity](https://gradle.com).

For each Gradle and Maven build that is run from TeamCity, this plugin exposes the links to the created build scans in the TeamCity UI. The plugin can also be configured to ad-hoc connect Gradle and Maven builds to an existing Develocity instance such that a Build Scan is published each time a build is run from TeamCity.

The plugin is available from the [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/27005-develocity-integration-for-gradle-and-maven-builds).

## Build Scan

Recent Build Scan: https://gradle.com/s/yevfzaz2bvvh4

Find out more about Build Scan for Gradle and Maven at https://scans.gradle.com and https://gradle.com.

# Installation

## Option 1: Conveniently select plugin

1. Go to the plugin list of your TeamCity installation at `<TeamCityInstanceRootUrl>/admin/admin.html?item=plugins` and browse the plugins repository.

2. Select, install, and activate the plugin as described [here](https://www.jetbrains.com/help/teamcity/installing-additional-plugins.html#Installing+a+plugin+from+JetBrains+Plugins+repository).

## Option 2: Manually upload plugin

1. Download the plugin `.zip` file from [https://plugins.jetbrains.com/plugin/27005-develocity-integration-for-gradle-and-maven-builds](https://plugins.jetbrains.com/plugin/27005-develocity-integration-for-gradle-and-maven-builds).

2. Go to the plugin list of your TeamCity installation at `<TeamCityInstanceRootUrl>/admin/admin.html?item=plugins` and click on the link _Upload plugin zip_ to install the previously downloaded plugin `.zip` file.

3. Restart TeamCity.

# Integrations

## Build Scan links surfacing

### Gradle builds

If you use TeamCity's _Gradle_ runner to launch your Gradle builds, there is nothing special to do.

If you use TeamCity's _Command Line_ runner to launch your Gradle builds, you can opt in to enable build scan detection using the `develocityPlugin.command-line-build-step.enabled` configuration parameter.

If the first two mechanisms will not work for your Gradle build configurations, you can still get integration with build scans, but it requires your build logs being parsed for build scan links. In case of huge build logs, this can put a significant toll on the performance of your TeamCity instance. You can enable the parsing of the build logs by setting the `develocityPlugin.log-parsing.enabled` configuration parameter to _true_.

### Maven builds

If you use TeamCity's _Maven_ runner to launch Maven builds, there is nothing special to do.

If you use TeamCity's _Command Line_ runner to launch your Maven builds, you can opt in to enable build scan detection using the `develocityPlugin.command-line-build-step.enabled` configuration parameter.

If the first two mechanisms will not work for your Maven build configurations, you can still get integration with build scans, but it requires your build logs being parsed for build scan links. In case of huge build logs, this can put a significant toll on the performance of your TeamCity instance. You can enable the parsing of the build logs by setting the `develocityPlugin.log-parsing.enabled` configuration parameter to _true_.

## Develocity connectivity

You can ad-hoc connect Gradle and Maven builds to an existing Develocity instance by automatically injecting the [Develocity Gradle plugin](https://docs.gradle.com/develocity/gradle-plugin/) and the [Develocity Maven extension](https://docs.gradle.com/develocity/maven-extension/) when these builds are run via TeamCity's _Gradle_ or _Maven_ runner. If a Gradle or Maven build is run via TeamCity's _Command Line_ runner the auto-injection can be opted in to, too. If a given build is already connected to Develocity, the auto-injection is skipped.

The same auto-injection behavior is available for the [Common Custom User Data Gradle plugin](https://github.com/gradle/common-custom-user-data-gradle-plugin) and the [Common Custom User Data Maven extension](https://github.com/gradle/common-custom-user-data-maven-extension).

The higher in TeamCity's project hierarchy the required configuration parameters are applied, the more widely they apply since the configuration parameters are passed on to all child projects. Child projects can override the configuration parameters of their parent projects and even disable the auto-injection by setting the appropriate configuration parameters to empty values.

For convenience, the configuration parameter values can be defined through a form describing a Develocity connection. Alternatively, the configuration parameter values can be defined as TeamCity configuration parameters.

### Configuration via TeamCity connection

A Develocity connection can be created in the Connections section of the configuration of a given project. In the Add Connection dropdown, select the Develocity connection type.

Fill out the Add Connection dialog with the URL for the Develocity instance, the plugin and extension versions to be applied, and any other fields as needed. Some values, such as the plugin and extension versions, will be pre-populated.

A Develocity connection can be created on any project and is automatically inherited by all its child projects.

<details>

<summary>Click for an example configuration.</summary>

<img width="591" alt="develocity-connection-dialog" src="https://user-images.githubusercontent.com/625514/235716524-ef39e577-07d2-4ceb-9dfd-de515aff3d3d.png">

</details>

### Configuration via TeamCity configuration parameters

As an alternative to the configuration via a TeamCity connection, you can describe the configuration via TeamCity configuration parameters on a given project.

The TeamCity configuration parameters can be set on any project and are automatically inherited by all its child projects.

<details>

<summary>Click for an example configuration.</summary>

#### Gradle Builds

1. In TeamCity, on the build configuration for which you want to apply Develocity, create three configuration parameters:

   - `develocityPlugin.develocity.url` - the URL of the Develocity instance to which to publish build scans
   - `develocityPlugin.develocity.plugin.version` - the version of the [Develocity Gradle plugin](https://docs.gradle.com/develocity/gradle-plugin/) to apply
   - `develocityPlugin.ccud.plugin.version` - the version of the [Common Custom User Data Gradle plugin](https://github.com/gradle/common-custom-user-data-gradle-plugin) to apply (optional)

2. If required, provide additional configuration parameters for your environment (optional):

    - `develocityPlugin.develocity.allow-untrusted-server` - allow communication with an untrusted server; set to _true_ if your Develocity instance is using a self-signed certificate
    - `develocityPlugin.develocity.enforce-url` - enforce the configured Develocity URL over a URL configured in the project's build; set to _true_ to enforce publication of build scans to the configured Develocity URL
    - `develocityPlugin.gradle.plugin-repository.url` - the URL of the repository to use when resolving the GE and CCUD plugins; required if your TeamCity agents are not able to access the Gradle Plugin Portal
    - `develocityPlugin.command-line-build-step.enabled` - enable Develocity integration for _Command Line_ build steps; by default only steps using the _Gradle_ runner are enabled

##### Example Gradle Configuration

<img width="1302" alt="image" src="https://user-images.githubusercontent.com/231070/171748029-590c9f99-9382-41f3-8597-8c049ce328ed.png">

#### Maven Builds

1. In TeamCity, on the build configuration for which you want to integrate Develocity, create three configuration parameters:

   - `develocityPlugin.develocity.url` - the URL of the Develocity instance to which to publish build scans
   - `develocityPlugin.develocity.extension.version` - the version of the [Develocity Maven extension](https://docs.gradle.com/develocity/maven-extension/) to apply
   - `develocityPlugin.ccud.extension.version` - the version of the [Common Custom User Data Maven extension](https://github.com/gradle/common-custom-user-data-maven-extension) to apply (optional)

2. If required, provide additional configuration parameters for your environment (optional):

    - `develocityPlugin.develocity.allow-untrusted-server` - allow communication with an untrusted server; set to _true_ if your Develocity instance is using a self-signed certificate
    - `develocityPlugin.develocity.enforce-url` - enforce the configured Develocity URL over a URL configured in the project's build; set to _true_ to enforce publication of build scans to the configured Develocity URL
    - `develocityPlugin.develocity.extension.custom.coordinates` - the coordinates of a custom extension that has a transitive dependency on the Develocity Maven Extension
    - `develocityPlugin.ccud.extension.custom.coordinates` - the coordinates of a custom Common Custom User Data Maven Extension or of a custom extension that has a transitive dependency on it
    - `develocityPlugin.command-line-build-step.enabled` - enable Develocity integration for _Command Line_ build steps; by default only steps using the _Maven_ runner are enabled

##### Example Maven Configuration

<img width="1298" alt="image" src="https://user-images.githubusercontent.com/231070/171748453-243aa862-c31b-4367-bb90-49b257de1f3f.png">

</details>

## Slack notifications (experimental)

1. In Slack, create a webhook and keep track of the created URL.

2. In TeamCity, on the build configuration for which you want to be notified about published build scans, create a configuration parameter with name `develocityPlugin.slack-webhook.url` and the value being the URL of the webhook created in step #1.

3. Trigger your Gradle builds with build scans enabled.

4. Find a notification about the published build scans in the Slack channel configured in the webhook.

# Compatibility

## Build Scan links surfacing

The version of the Develocity Gradle plugin and the Develocity Maven extension that are applied to a build must meet a minimum version requirement for the link surfacing to work.

| Develocity Teamcity Plugin version | Minimum supported GE Maven extension version | Minimum supported GE Gradle plugin version |
|------------------------------------|----------------------------------------------|--------------------------------------------|
| Next                               | 1.11                                         | 3.0  (or Gradle Build Scan plugin 1.8)     |

## Develocity connectivity

The configured version of the Develocity Gradle plugin and the Develocity Maven extension that get automatically applied by the Develocity TeamCity plugin must be compatible with the version of the Develocity server that the build is connecting to.

### Gradle builds

The compatibility of the specified version of the Develocity Gradle plugin with Develocity can be found [here](https://docs.gradle.com/develocity/compatibility/#develocity_gradle_plugin). The compatibility of the optionally specified version of the Common Custom User Data Gradle plugin with the Develocity Gradle plugin can be found [here](https://github.com/gradle/common-custom-user-data-gradle-plugin#version-compatibility).

### Maven builds

The compatibility of the specified version of the Develocity Maven extension with Develocity can be found [here](https://docs.gradle.com/develocity/compatibility/#maven_extension). The compatibility of the optionally specified version of the Common Custom User Data Maven extension with the Develocity Maven extension can be found [here](https://github.com/gradle/common-custom-user-data-maven-extension#version-compatibility).

For Maven builds, the version of the Develocity Maven extension automatically applied by the TeamCity Build Scan plugin is currently bundled by the plugin and cannot be configured. This will change in a future version of this plugin.

<details>

<summary>Click for an overview of what Maven extension versions are bundled and injected.</summary>

| Develocity Teamcity Plugin version | Injected DV Maven extension version | Injected Common CCUD Maven extension version |
|------------------------------------|-------------------------------------|----------------------------------------------|
| Next                               | 2.0                                 | 2.0.2                                        |

</details>

# Feedback and Contributions

Both feedback and contributions are very welcome.

# Acknowledgements
+ [bigdaz](https://github.com/bigdaz) (several prs related to the Develocity integration)
+ [clayburn](https://github.com/clayburn) (several prs related to the Develocity integration)
+ [marcphilipp](https://github.com/marcphilipp) (pr #53 that adds Gradle 4/5 compatibility to the Develocity integration)
+ [ldaley](https://github.com/ldaley) (pr #53 that adds Gradle 4/5 compatibility to the Develocity integration)
+ [madlexa](https://github.com/madlexa) (pr #47 that adds TeamCity 2022.04 compatibility)
+ [facewindu](https://github.com/facewindu) (pr #21 that includes init script test coverage)
+ [dmitry-treskunov](https://github.com/dmitry-treskunov) (bug report and proposed fix)
+ [pbielicki](https://github.com/pbielicki) (pr #17 that adds a hint to the BuildScanServiceMessageMavenExtension @Component)
+ [jonnybbb](https://github.com/jonnybbb) (pr #14 and #15 that store build scan links under artifacts and clean up legacy data)
+ [davidburstromspotify](https://github.com/davidburstromspotify) (bug report and proposed fix)
+ [guylabs](https://github.com/guylabs) (pr #10 that provides support for the Develocity Gradle plugin)
+ [autonomousapps](https://github.com/autonomousapps) (pr #9 that provides build scans for Maven builds)
+ [mark-vieira](https://github.com/mark-vieira) (pr #6 that provides message service functionality)
+ [pavelsher](https://github.com/pavelsher) (several code pointers)

# License

This plugin is available under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
