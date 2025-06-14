package com.gradle.develocity.teamcity.agent.bootstrapping.restore.process

import spock.lang.Requires
import spock.lang.Specification
import spock.lang.Subject

import java.time.Duration
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean

class ProcessExecutorTest extends Specification {

    @Subject
    def processExecutor = new ProcessExecutor()

    def "returns result when no error occurs"() {
        when:
        def result = processExecutor.execute({ 42 }, longTimeout())

        then:
        result == 42
    }

    def "respects overall timeout"() {
        when:
        processExecutor.execute({ Thread.sleep(100); 42 }, Duration.ofMillis(10))

        then:
        thrown(TimeoutException)
    }

    def "action runs in a daemon thread"() {
        given:
        def isDaemon = new AtomicBoolean(false)

        when:
        processExecutor.execute(
                { isDaemon.set(Thread.currentThread().isDaemon()) },
                longTimeout()
        )

        then:
        isDaemon
    }

    def "wraps exception from action in an execution exception"() {
        when:
        processExecutor.execute(
                { throw new RuntimeException("Boom!")},
                longTimeout()
        )

        then:
        def e = thrown(ExecutionException)
        e.cause instanceof RuntimeException
        e.cause.message == "Boom!"
    }

    def "can be used in try-with-resources"() {
        given:
        def spiedProcessExecutor = Spy(processExecutor)

        when:
        try (def exec = spiedProcessExecutor) {
            exec.execute({}, longTimeout())
        }

        then:
        1 * spiedProcessExecutor.close()
    }

    @Requires({ runningOnLinuxOrMacOS() })
    def "captures non-zero exit code from external process"() {
        when:
        def result = processExecutor.execute(["/bin/sh", "-c", "exit 42"], longTimeout())

        then:
        result.exitCode() == 42
    }

    @Requires({ runningOnLinuxOrMacOS() })
    def "captures stdout from external process"() {
        when:
        def result = processExecutor.execute(["/bin/echo", "Hello"], longTimeout())

        then:
        with (result) {
            exitCode() == 0
            stdout() == "Hello"
            stderr() == ""
        }
    }

    @Requires({ runningOnLinuxOrMacOS() })
    def "captures stderr from external process"() {
        when:
        def result = processExecutor.execute(["/bin/sh", "-c", "echo Error >&2"], longTimeout())

        then:
        with (result) {
            exitCode() == 0
            stdout() == ""
            stderr() == "Error"
        }
    }

    private static Duration longTimeout() {
        Duration.ofSeconds(42)
    }

    private static boolean runningOnLinuxOrMacOS() {
        def os = System.getProperty("os.name").toLowerCase()
        return (os.contains("linux") || os.contains("mac os x"))
    }

}
