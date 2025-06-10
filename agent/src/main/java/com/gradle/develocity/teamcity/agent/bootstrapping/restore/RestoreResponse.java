package com.gradle.develocity.teamcity.agent.bootstrapping.restore;

import java.time.Duration;

public class RestoreResponse {
    private final String imageName;
    private final long artifactCount;
    private final long totalArtifactSizeInBytes;
    private final Duration totalArtifactDownloadDuration;
    private final Duration totalArtifactUnpackDuration;

    private RestoreResponse(String imageName, long artifactCount, long totalArtifactSizeInBytes, Duration totalArtifactDownloadDuration, Duration totalArtifactUnpackDuration) {
        this.imageName = imageName;
        this.artifactCount = artifactCount;
        this.totalArtifactSizeInBytes = totalArtifactSizeInBytes;
        this.totalArtifactDownloadDuration = totalArtifactDownloadDuration;
        this.totalArtifactUnpackDuration = totalArtifactUnpackDuration;
    }

    static RestoreResponse of(String imageName, long artifactCount, long totalArtifactSizeInBytes, Duration totalArtifactDownloadDuration, Duration totalArtifactUnpackDuration) {
        return new RestoreResponse(imageName, artifactCount, totalArtifactSizeInBytes, totalArtifactDownloadDuration, totalArtifactUnpackDuration);
    }

    public String imageName() {
        return imageName;
    }

    public long artifactCount() {
        return artifactCount;
    }

    public long totalArtifactSizeInBytes() {
        return totalArtifactSizeInBytes;
    }

    public Duration totalArtifactDownloadDuration() {
        return totalArtifactDownloadDuration;
    }

    public Duration totalArtifactUnpackDuration() {
        return totalArtifactUnpackDuration;
    }
}
