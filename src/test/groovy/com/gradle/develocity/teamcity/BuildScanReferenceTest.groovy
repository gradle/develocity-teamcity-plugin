package com.gradle.develocity.teamcity

import spock.lang.Requires
import spock.lang.Specification
import spock.util.environment.OperatingSystem

class BuildScanReferenceTest extends Specification {

    def "can serialize and deserialize through custom serialization"() {
        given:
        def buildScanReference = new BuildScanReference("myId", "myUrl")

        when:
        def bytes = new ByteArrayOutputStream()
        def outputStream = new ObjectOutputStream(bytes)
        outputStream.writeObject(buildScanReference)

        def inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))
        BuildScanReference result = inputStream.readObject() as BuildScanReference

        then:
        result == buildScanReference
    }

    def "can get url without protocol"() {
        given:
        def buildScanReference = new BuildScanReference("myId", url)

        when:
        def urlWithoutProtocol = buildScanReference.getUrlWithoutProtocol()

        then:
        urlWithoutProtocol == "scans.gradle.com/s/htyg3re5"

        where:
        url << ["http://scans.gradle.com/s/htyg3re5", "https://scans.gradle.com/s/htyg3re5"]
    }

    @Requires(value = { OperatingSystem.current.isLinux() })
    def "can render badge"() {
        given:
        def buildScanReference = new BuildScanReference("myId", "https://scans.gradle.com/s/htyg3re5")
        def reference = BuildScanReference.getResource("Badge.html").text

        when:
        def badge = buildScanReference.publishedBuildScanBadge
        then:
        reference == """\
            <html><body>
            <img src="${badge}">
            </body></html>
            """.stripIndent(true)
    }

}
