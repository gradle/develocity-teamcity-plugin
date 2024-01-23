package com.gradle.develocity.internal.slack;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

final class SlackHttpNotifier {

    private static final Logger LOGGER = Logger.getLogger("jetbrains.buildServer.DEVELOCITY");

    private final URL webhookUrl;
    private final SlackPayloadSerializer payloadSerializer;

    private SlackHttpNotifier(@NotNull URL webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.payloadSerializer = SlackPayloadSerializer.create();
    }

    @NotNull
    static SlackHttpNotifier forWebhook(@NotNull URL webhookUrl) {
        return new SlackHttpNotifier(webhookUrl);
    }

    void notify(@NotNull SlackPayload payload) throws IOException {
        String json = payloadSerializer.toJson(payload);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        URLConnection urlConnection = webhookUrl.openConnection();
        if (!(urlConnection instanceof HttpURLConnection)) {
            throw new IllegalArgumentException("HttpURLConnection expected");
        }

        HttpURLConnection con = (HttpURLConnection) urlConnection;

        con.setInstanceFollowRedirects(true);
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);
        con.setUseCaches(false);

        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.addRequestProperty("Content-type", "application/json");

        // connect and send headers
        con.connect();

        // send payload
        try (InputStream is = new ByteArrayInputStream(bytes); OutputStream os = con.getOutputStream()) {
            copy(is, os);
        }

        // log response code
        int responseCode = con.getResponseCode();
        LOGGER.debug("Invoking Slack webhook returned response code: " + responseCode);
    }

    private static void copy(@NotNull InputStream in, @NotNull OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int len = in.read(buffer);
        while (len != -1) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }
    }

}
