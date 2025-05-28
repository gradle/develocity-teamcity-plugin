package com.gradle.develocity.teamcity.agent.bootstrapping;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gradle.develocity.bootstrap.UniversalCacheBootstrapApi;

import java.net.URI;
import java.time.Duration;

public class BootstrapStateRestoreService {

    private final UniversalCacheBootstrapApi api;
    private final JsonParser jsonParser;

    BootstrapStateRestoreService(URI edgeUrl, String token) {
        this.api = new UniversalCacheBootstrapApi(edgeUrl, token);
        this.jsonParser = new JsonParser();
    }

    RestoreResponse restoreFromManifestWithPrefix(String manifestPrefix) {
        String response = api.restoreFromManifestWithPrefix(manifestPrefix);
        JsonObject root = jsonParser.parse(response).getAsJsonObject();
        JsonObject manifest = root.get("manifest").getAsJsonObject();
        JsonObject artifacts = root.get("artifacts").getAsJsonObject();

        return RestoreResponse.of(
                manifest.get("key").getAsString(),
                artifacts.get("count").getAsLong(),
                artifacts.get("totalSizeInBytes").getAsLong(),
                Duration.ofSeconds(artifacts.get("totalSerialDownloadDurationInSeconds").getAsLong()),
                Duration.ofSeconds(artifacts.get("totalSerialUnpackDurationInSeconds").getAsLong())
        );
    }

}
