package com.gradle.develocity.teamcity.agent.bootstrapping;

import java.time.Duration;

public class RestoreResponse {
    private final String manifestKey;
    private final long artifactCount;
    private final long totalArtifactSizeInBytes;
    private final Duration totalArtifactDownloadDuration;
    private final Duration totalArtifactUnpackDuration;

    private RestoreResponse(String manifestKey, long artifactCount, long totalArtifactSizeInBytes, Duration totalArtifactDownloadDuration, Duration totalArtifactUnpackDuration) {
        this.manifestKey = manifestKey;
        this.artifactCount = artifactCount;
        this.totalArtifactSizeInBytes = totalArtifactSizeInBytes;
        this.totalArtifactDownloadDuration = totalArtifactDownloadDuration;
        this.totalArtifactUnpackDuration = totalArtifactUnpackDuration;
    }

    static RestoreResponse of(String manifestKey, long artifactCount, long totalArtifactSizeInBytes, Duration totalArtifactDownloadDuration, Duration totalArtifactUnpackDuration) {
        return new RestoreResponse(manifestKey, artifactCount, totalArtifactSizeInBytes, totalArtifactDownloadDuration, totalArtifactUnpackDuration);
    }

    public String manifestKey() {
        return manifestKey;
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
