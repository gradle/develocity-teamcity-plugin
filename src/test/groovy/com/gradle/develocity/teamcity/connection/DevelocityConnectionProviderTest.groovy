package com.gradle.develocity.teamcity.connection

import jetbrains.buildServer.serverSide.oauth.OAuthConnectionDescriptor
import jetbrains.buildServer.serverSide.oauth.OAuthProvider
import jetbrains.buildServer.web.openapi.PluginDescriptor
import spock.lang.Specification
import spock.lang.Unroll

import static DevelocityConnectionConstants.*

@Unroll
class DevelocityConnectionProviderTest extends Specification {

    OAuthProvider connectionProvider

    void setup() {
        connectionProvider = new DevelocityConnectionProvider(Stub(PluginDescriptor))
    }

    def "default version of #key is set"() {
        when:
        def defaultProperties = connectionProvider.getDefaultProperties()

        then:
        defaultProperties.containsKey(key)

        where:
        key << [
                DEVELOCITY_PLUGIN_VERSION,
                CCUD_PLUGIN_VERSION,
                DEVELOCITY_EXTENSION_VERSION,
                CCUD_EXTENSION_VERSION
        ]
    }

    def "description includes value of #parameter"() {
        given:
        OAuthConnectionDescriptor connection = Stub()
        connection.getParameters() >> [(parameter): value]

        when:
        def description = connectionProvider.describeConnection(connection)

        then:
        description.contains("$text: $value")

        where:
        parameter                               | value                           | text
        GRADLE_PLUGIN_REPOSITORY_URL            | 'https://plugins.example.com'   | 'Gradle Plugin Repository URL'
        DEVELOCITY_URL                          | 'https://ge.example.com'        | 'Develocity Server URL'
        ALLOW_UNTRUSTED_SERVER                  | 'true'                          | 'Allow Untrusted Server'
        DEVELOCITY_PLUGIN_VERSION               | '4.0.2'                         | 'Develocity Gradle Plugin Version'
        CCUD_PLUGIN_VERSION                     | '2.3'                           | 'Common Custom User Data Gradle Plugin Version'
        DEVELOCITY_EXTENSION_VERSION            | '2.0.1'                         | 'Develocity Maven Extension Version'
        CCUD_EXTENSION_VERSION                  | '2.0.3'                         | 'Common Custom User Data Maven Extension Version'
        CUSTOM_DEVELOCITY_EXTENSION_COORDINATES | 'com.company:my-ge-extension'   | 'Develocity Maven Extension Custom Coordinates'
        CUSTOM_CCUD_EXTENSION_COORDINATES       | 'com.company:my-ccud-extension' | 'Common Custom User Data Maven Extension Custom Coordinates'
        INSTRUMENT_COMMAND_LINE_BUILD_STEP      | 'true'                          | 'Instrument Command Line Build Steps'
    }

    def "description includes includes placeholder value for access key"() {
        given:
        OAuthConnectionDescriptor connection = Stub()
        connection.getParameters() >> [(DEVELOCITY_ACCESS_KEY): 'server=secret']

        when:
        def description = connectionProvider.describeConnection(connection)

        then:
        description.contains('Develocity Access Key: ******')
    }

    def "returns validation error if access key is invalid"() {
        when:
        def errors = connectionProvider.propertiesProcessor.process([(DEVELOCITY_ACCESS_KEY): accessKey])

        then:
        errors.size() == 1

        and:
        def error = errors.find { it.propertyName == DEVELOCITY_ACCESS_KEY }
        error != null
        error.invalidReason == 'Invalid access key'

        where:
        accessKey << ['', 'secret', '=secret', 'server=']
    }

    def "does not return validation error if access key is valid or absent"() {
        when:
        def errors = connectionProvider.propertiesProcessor.process(accessKey != null ? [(DEVELOCITY_ACCESS_KEY): accessKey] : [:])

        then:
        errors.isEmpty()

        where:
        accessKey << [null, 'server=secret', 'server1,server2=secret1;server3=secret2']
    }

}
