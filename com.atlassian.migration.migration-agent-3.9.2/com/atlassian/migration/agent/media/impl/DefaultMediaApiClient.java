/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.core.UriBuilder
 *  okhttp3.MediaType
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  okhttp3.ResponseBody
 *  okio.BufferedSink
 *  okio.Okio
 *  org.codehaus.jackson.JsonNode
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.media.impl;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.media.ClientId;
import com.atlassian.migration.agent.media.CreateFileOptions;
import com.atlassian.migration.agent.media.Entity;
import com.atlassian.migration.agent.media.Etag;
import com.atlassian.migration.agent.media.MediaApiClient;
import com.atlassian.migration.agent.media.Upload;
import com.atlassian.migration.agent.media.exception.ParseMediaDataException;
import com.atlassian.migration.agent.media.impl.CryptoUtils;
import com.atlassian.migration.agent.media.impl.MediaRoutes;
import com.atlassian.migration.agent.okhttp.HttpService;
import com.atlassian.migration.agent.okhttp.MediaTypes;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;

public class DefaultMediaApiClient
implements MediaApiClient {
    private static final String MEDIA_RESPONSE_DATA_SECTION = "data";
    public static final String FILENAME_QUERY_PARAM = "?name=";
    private static final Logger log = ContextLoggerFactory.getLogger(DefaultMediaApiClient.class);
    private final MigrationAgentConfiguration configuration;
    private final HttpService httpService;

    DefaultMediaApiClient(MigrationAgentConfiguration configuration, HttpService httpService) {
        this.configuration = configuration;
        this.httpService = httpService;
    }

    @Override
    @Nonnull
    public ClientId createClient(String title, String description) {
        ImmutableMap body = ImmutableMap.of((Object)"title", (Object)title, (Object)"description", (Object)description);
        Request request = DefaultMediaApiClient.request(MediaRoutes.POST_CLIENT_IDENTITY.getUrl(this.getUrl())).post(RequestBody.create((MediaType)MediaTypes.APPLICATION_JSON_TYPE, (String)Jsons.valueAsString(body))).build();
        return this.callJson(request, ClientId.class);
    }

    @Override
    @Nonnull
    public Etag uploadChunk(final ByteBuffer data, String uploadId, String partNumber) {
        final Etag etag = new Etag(data.remaining(), CryptoUtils.sha1(data));
        Request request = DefaultMediaApiClient.request(MediaRoutes.CHUNK.getUrl(this.getUrl(), etag.toString(), uploadId, partNumber)).put(new RequestBody(){

            public long contentLength() {
                return etag.getLength();
            }

            @Nullable
            public MediaType contentType() {
                return MediaTypes.APPLICATION_STREAM_TYPE;
            }

            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(data);
            }
        }).build();
        this.httpService.call(request);
        return etag;
    }

    @Override
    @Nonnull
    public Optional<Upload> createUpload() {
        String url = UriBuilder.fromPath((String)MediaRoutes.CREATE_UPLOAD.getUrl(this.getUrl())).queryParam("createUpTo", new Object[]{1}).build(new Object[0]).toString();
        Request request = DefaultMediaApiClient.request(url).post(RequestBody.create((MediaType)MediaTypes.APPLICATION_JSON_TYPE, (String)"{}")).build();
        Upload[] uploads = this.callJson(request, Upload[].class);
        if (uploads == null || uploads.length == 0) {
            return Optional.empty();
        }
        return Optional.of(uploads[0]);
    }

    @Override
    public void updateUpload(String uploadId, int offset, List<Etag> etags) {
        String[] chunks = (String[])etags.stream().map(Etag::toString).toArray(String[]::new);
        ImmutableMap body = ImmutableMap.of((Object)"chunks", (Object)chunks, (Object)"offset", (Object)offset);
        Request request = DefaultMediaApiClient.request(MediaRoutes.PUT_UPLOAD_CHUNKS.getUrl(this.getUrl(), uploadId)).put(RequestBody.create((MediaType)MediaTypes.APPLICATION_JSON_TYPE, (String)Jsons.valueAsString(body))).build();
        this.httpService.call(request, (Set<Integer>)ImmutableSet.of((Object)409));
    }

    @Override
    public Entity uploadFile(final InputStream inputStream, String fileName) {
        Entity result;
        RequestBody requestBody = new RequestBody(){

            public MediaType contentType() {
                return MediaTypes.APPLICATION_STREAM_TYPE;
            }

            public long contentLength() throws IOException {
                long contentLen = inputStream.available();
                if (contentLen > 0L) {
                    return contentLen;
                }
                return super.contentLength();
            }

            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeAll(Okio.source((InputStream)inputStream));
            }
        };
        String url = MediaRoutes.POST_FILE_BINARY.getUrl(this.getUrl());
        if (!fileName.isEmpty()) {
            try {
                url = url + FILENAME_QUERY_PARAM + URLEncoder.encode(fileName, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                log.error("Failed to encode file name: {}", (Object)fileName, (Object)e);
                throw new RuntimeException(e);
            }
        }
        Request request = DefaultMediaApiClient.request(url).post(requestBody).build();
        try {
            result = this.callJson(request, Entity.class);
        }
        catch (Throwable ex) {
            log.error("Failed to upload file: {}", (Object)fileName, (Object)ex);
            throw ex;
        }
        return result;
    }

    @Override
    @Nonnull
    public Entity createFileFromUpload(String uploadId, @Nullable String name, @Nullable String mimeType, CreateFileOptions options) {
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("uploadId", uploadId);
        if (name != null) {
            body.put("name", name);
        }
        if (mimeType != null) {
            body.put("mimeType", name);
        }
        body.put("conditions", options);
        Request request = DefaultMediaApiClient.request(MediaRoutes.CREATE_FILE_FROM_UPLOAD.getUrl(this.getUrl())).post(RequestBody.create((MediaType)MediaTypes.APPLICATION_JSON_TYPE, (String)Jsons.valueAsString(body))).build();
        return this.callJson(request, Entity.class);
    }

    @Override
    @Nonnull
    public Entity createFileFromChunks(List<Etag> etags, @Nullable String name, @Nullable String mimeType, CreateFileOptions options) {
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("chunks", etags.stream().map(Etag::toString).toArray(String[]::new));
        if (name != null) {
            body.put("name", name);
        }
        if (mimeType != null) {
            body.put("mimeType", name);
        }
        body.put("conditions", options);
        Request request = DefaultMediaApiClient.request(MediaRoutes.CREATE_FILE_FROM_CHUNKS.getUrl(this.getUrl())).post(RequestBody.create((MediaType)MediaTypes.APPLICATION_JSON_TYPE, (String)Jsons.valueAsString(body))).build();
        return this.callJson(request, Entity.class);
    }

    private String getUrl() {
        return this.configuration.getMediaServiceUrl();
    }

    private static Request.Builder request(String url) {
        return new Request.Builder().addHeader("Accept", "application/json").url(url);
    }

    private <T> T callJson(Request request, Class<T> bodyType) {
        return this.httpService.call(request, this.mediaResponseMapper(bodyType));
    }

    @VisibleForTesting
    <T> Function<ResponseBody, T> mediaResponseMapper(Class<T> bodyType) {
        return responseBody -> {
            try {
                JsonNode root = Jsons.OBJECT_MAPPER.readTree(responseBody.charStream());
                return Jsons.OBJECT_MAPPER.readValue(root.get(MEDIA_RESPONSE_DATA_SECTION), bodyType);
            }
            catch (IOException e) {
                throw new ParseMediaDataException("Failed to parse data section from media response", e);
            }
        };
    }
}

