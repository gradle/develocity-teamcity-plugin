package com.gradle.develocity.teamcity.agent.bootstrapping.restore;

import java.time.Duration;

public class RestoreResponse {
    private final String imageName;
    private final long artifactsCount;
    private final long totalArtifactSizeInBytes;
    private final Duration wallClockDuration;

    private RestoreResponse(String imageName, long artifactsCount, long totalArtifactSizeInBytes, Duration wallClockDuration) {
        this.imageName = imageName;
        this.artifactsCount = artifactsCount;
        this.totalArtifactSizeInBytes = totalArtifactSizeInBytes;
        this.wallClockDuration = wallClockDuration;
    }

    static RestoreResponse of(String imageName, long artifactCount, long totalArtifactSizeInBytes, Duration wallClockDuration) {
        return new RestoreResponse(imageName, artifactCount, totalArtifactSizeInBytes, wallClockDuration);
    }

    public String imageName() {
        return imageName;
    }

    public long artifactsCount() {
        return artifactsCount;
    }

    public long totalArtifactSizeInBytes() {
        return totalArtifactSizeInBytes;
    }

    public Duration wallClockDuration() {
        return wallClockDuration;
    }

}
