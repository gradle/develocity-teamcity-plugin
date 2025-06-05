package com.gradle.develocity.teamcity.agent.bootstrapping.restore.process;

import com.intellij.openapi.diagnostic.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

public class ProcessExecutor implements AutoCloseable {

    private static final Logger LOG = Logger.getInstance("jetbrains.buildServer.AGENT");

    private final ExecutorService executor = Executors.newCachedThreadPool(daemonThreadFactory());
    private final AtomicReference<Process> externalProcess = new AtomicReference<>();

    public ExecutionResult execute(List<String> commandAndArguments, Duration timeout) throws TimeoutException, ExecutionException, InterruptedException {
        Callable<ExecutionResult> action = () -> {
            try {
                Instant startTimestamp = Instant.now();
                externalProcess.set(new ProcessBuilder(commandAndArguments).start());
                Future<String> stdoutFuture = executor.submit(() -> readStream(externalProcess.get().getInputStream()));
                Future<String> stderrFuture = executor.submit(() -> readStream(externalProcess.get().getErrorStream()));

                int exitCode = externalProcess.get().waitFor();
                String stdout = stdoutFuture.get();
                String stderr = stderrFuture.get();
                Instant endTimestamp = Instant.now();

                return ExecutionResult.of(exitCode, Duration.between(startTimestamp, endTimestamp), stdout, stderr);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        };

        return execute(action, timeout);
    }

    <T> T execute(Callable<T> action, Duration timeout) throws TimeoutException, ExecutionException, InterruptedException {
        CompletableFuture<T> result = CompletableFuture.supplyAsync(() -> {
            try {
                return action.call();
            } catch (Throwable e) {
                throw new CompletionException(e);
            }
        }, executor);

        try {
            return result.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            LOG.warn(String.format("Action timed out after %s", timeout));
            result.cancel(true);
            throw e;
        }
    }

    private static String readStream(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(joining(lineSeparator()));
    }

    private static ThreadFactory daemonThreadFactory() {
        return runnable -> {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setDaemon(true);
            return thread;
        };
    }

    @Override
    public void close() {
        Process process = externalProcess.get();
        if (process != null && process.isAlive()) {
            process.destroy();
        }

        executor.shutdown();

        try {
            boolean hasTerminated = executor.awaitTermination(30, TimeUnit.SECONDS);
            if (!hasTerminated) {
                LOG.warn("Termination of executor service has timed out");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
