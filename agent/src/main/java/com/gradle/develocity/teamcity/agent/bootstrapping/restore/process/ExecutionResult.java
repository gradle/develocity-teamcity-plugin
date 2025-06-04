package com.gradle.develocity.teamcity.agent.bootstrapping.restore.process;

import java.time.Duration;

public class ExecutionResult {
    private final int exitCode;
    private final Duration duration;
    private final String stdout;
    private final String stderr;

    private ExecutionResult(int exitCode, Duration duration, String stdout, String stderr) {
        this.exitCode = exitCode;
        this.duration = duration;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public static ExecutionResult of(int exitCode, Duration duration, String stdout, String stderr) {
        return new ExecutionResult(exitCode, duration, stdout, stderr);
    }

    public int exitCode() {
        return exitCode;
    }

    public Duration duration() {
        return duration;
    }

    public String stdout() {
        return stdout;
    }

    public String stderr() {
        return stderr;
    }

}