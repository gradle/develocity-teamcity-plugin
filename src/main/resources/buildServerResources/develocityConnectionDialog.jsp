<%--suppress XmlPathReference --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/include-internal.jsp" %>

<jsp:useBean id="keys" class="com.gradle.develocity.teamcity.connection.DevelocityConnectionConstants"/>
<jsp:useBean id="project" type="jetbrains.buildServer.serverSide.SProject" scope="request"/>

<tr>
    <td><label for="displayName">Display name:</label><l:star/></td>
    <td>
        <props:textProperty name="displayName" className="longField"/>
        <span class="smallNote">Provide some name to distinguish this connection from others.</span>
    </td>
</tr>

<tr class="groupingTitle">
    <td colspan="2"><strong>Develocity Connection Settings</strong></td>
</tr>

<tr>
    <td><label for="${keys.develocityUrl}">Develocity Server URL:</label></td>
    <td>
        <props:textProperty name="${keys.develocityUrl}" className="longField"/>
        <span class="smallNote">The URL of the Develocity server.</span>
    </td>
</tr>

<tr>
    <td><label for="${keys.allowUntrustedServer}">Allow Untrusted Server:</label></td>
    <td>
        <props:checkboxProperty name="${keys.allowUntrustedServer}"/>
        <span class="smallNote">Whether it is acceptable to communicate with a server with an untrusted SSL certificate.</span>
    </td>
</tr>

<tr>
    <td><label for="${keys.develocityAccessKey}">Develocity Access Key:</label></td>
    <td>
        <props:passwordProperty name="${keys.develocityAccessKey}" className="longField"/>
        <span class="error" id="error_${keys.develocityAccessKey}"></span>
        <span class="smallNote">The access key for authenticating with the Develocity server.</span>
    </td>
</tr>

<tr>
    <td colspan="2">
        <div class="smallNoteAttention">The access key must be in the <b>&lt;server host name&gt;=&lt;access key&gt;</b> format. For more details please refer to the <a href="https://docs.gradle.com/develocity/gradle-plugin/#manual_access_key_configuration" target="_blank">documentation</a>.</div>
    </td>
</tr>

<tr>
    <td><label for="${keys.develocityAccessTokenExpiry}">Develocity short-lived access token expiry:</label></td>
    <td>
        <props:textProperty name="${keys.develocityAccessTokenExpiry}" className="longField"/>
        <span class="error" id="error_${keys.develocityAccessTokenExpiry}"></span>
        <span class="smallNote">The short-lived access tokens expiry in hours. Defaults to 2 hours. For more information, please refer to the <a href="https://docs.gradle.com/enterprise/api-manual/#short_lived_access_tokens" target="_blank">documentation</a>.</span>
    </td>
</tr>

<tr class="groupingTitle">
    <td colspan="2"><strong>Develocity Injection Settings</strong></td>
</tr>

<tr>
    <td><label for="${keys.enableInjection}">Enable Develocity auto-injection:</label></td>
    <td>
        <props:checkboxProperty name="${keys.enableInjection}"/>
        <span class="smallNote">Whether to enable the Develocity auto-injection.</span>
    </td>
</tr>

<tr>
    <td><label for="${keys.enforceDevelocityUrl}">Enforce Develocity Server URL:</label></td>
    <td>
        <props:checkboxProperty name="${keys.enforceDevelocityUrl}"/>
        <span class="smallNote">Whether to enforce the Develocity Server URL configured in this connection over a URL configured in the project's build.</span>
    </td>
</tr>

<tr class="groupingTitle">
    <td colspan="2">Gradle Settings</td>
</tr>

<tr>
    <td><label for="${keys.develocityPluginVersion}">Develocity Gradle Plugin Version:</label></td>
    <td>
        <props:textProperty name="${keys.develocityPluginVersion}" className="longField"/>
        <span class="smallNote">The version of the Develocity Gradle Plugin to apply to Gradle builds.</span>
    </td>
</tr>

<tr>
    <td><label for="${keys.commonCustomUserDataPluginVersion}">Common Custom User Data Gradle Plugin Version:</label></td>
    <td>
        <props:textProperty name="${keys.commonCustomUserDataPluginVersion}" className="longField"/>
        <span class="smallNote">The version of the Common Custom User Data Gradle Plugin to apply to Gradle builds.</span>
    </td>
</tr>

<tr class="advancedSetting">
    <td><label for="${keys.gradlePluginRepositoryUrl}">Gradle Plugin Repository URL:</label></td>
    <td>
        <props:textProperty name="${keys.gradlePluginRepositoryUrl}" className="longField"/>
        <span class="smallNote">The URL of the repository to use when resolving the Develocity and CCUD plugins. Defaults to the Gradle Plugin Portal.</span>

    </td>
</tr>

<tr>
    <td><label for="${keys.gradlePluginRepositoryUsername}">Gradle Plugin Repository Username:</label></td>
    <td>
        <props:textProperty name="${keys.gradlePluginRepositoryUsername}" className="longField"/>
        <span class="error" id="error_${keys.gradlePluginRepositoryUsername}"></span>
        <span class="smallNote">The username to log in to a custom Gradle Plugin repository.</span>
    </td>
</tr>

<tr>
    <td><label for="${keys.gradlePluginRepositoryPassword}">Gradle Plugin Repository Password:</label></td>
    <td>
        <props:passwordProperty name="${keys.gradlePluginRepositoryPassword}" className="longField"/>
        <span class="error" id="error_${keys.gradlePluginRepositoryPassword}"></span>
        <span class="smallNote">The password to log in to a custom Gradle Plugin repository.</span>
    </td>
</tr>

<tr class="advancedSetting">
    <td><label for="${keys.gradlePluginCaptureFileFingerprints}">Capture File Fingerprints:</label></td>
    <td>
        <props:checkboxProperty name="${keys.gradlePluginCaptureFileFingerprints}"/>
        <span class="smallNote">Enables capturing the paths and content hashes of each individual input file.</span>
    </td>
</tr>

<tr class="groupingTitle">
    <td colspan="2">Maven Settings</td>
</tr>

<tr>
    <td><label for="${keys.develocityExtensionVersion}">Develocity Maven Extension Version:</label></td>
    <td>
        <props:textProperty name="${keys.develocityExtensionVersion}" className="longField"/>
        <span class="smallNote">The version of the Develocity Maven Extension to apply to Maven builds.</span>
    </td>
</tr>

<tr>
    <td><label for="${keys.commonCustomUserDataExtensionVersion}">Common Custom User Data Maven Extension Version:</label></td>
    <td>
        <props:textProperty name="${keys.commonCustomUserDataExtensionVersion}" className="longField"/>
        <span class="smallNote">The version of the Common Custom User Data Maven Extension to apply to Maven builds.</span>
    </td>
</tr>

<tr class="advancedSetting">
    <td><label for="${keys.customDevelocityExtensionCoordinates}">Develocity Maven Extension Custom Coordinates:</label></td>
    <td>
        <props:textProperty name="${keys.customDevelocityExtensionCoordinates}" className="longField"/>
        <span class="smallNote">The coordinates of a custom extension that has a transitive dependency on the Develocity Maven Extension.</span>
    </td>
</tr>

<tr class="advancedSetting">
    <td><label for="${keys.customCommonCustomUserDataExtensionCoordinates}">Common Custom User Data Maven Extension Custom Coordinates:</label></td>
    <td>
        <props:textProperty name="${keys.customCommonCustomUserDataExtensionCoordinates}" className="longField"/>
        <span class="smallNote">The coordinates of a custom Common Custom User Data Maven Extension or of a custom extension that has a transitive dependency on it.</span>
    </td>
</tr>

<tr class="advancedSetting">
    <td><label for="${keys.extensionCaptureFileFingerprints}">Capture File Fingerprints::</label></td>
    <td>
        <props:checkboxProperty name="${keys.extensionCaptureFileFingerprints}"/>
        <span class="smallNote">Enables capturing the paths and content hashes of each individual input file.</span>
    </td>
</tr>

<tr class="groupingTitle">
    <td colspan="2">TeamCity Build Steps Settings</td>
</tr>

<tr>
    <td><label for="${keys.instrumentCommandLineBuildStep}">Instrument Command Line Build Steps:</label></td>
    <td>
        <props:checkboxProperty name="${keys.instrumentCommandLineBuildStep}"/>
        <span class="smallNote">Whether to instrument Gradle and Maven builds which utilize the Command Line build steps rather than the Gradle and Maven build steps.</span>
    </td>
</tr>
