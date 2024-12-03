/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  okhttp3.MediaType
 *  okhttp3.OkHttpClient
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  okhttp3.Response
 */
package io.micrometer.core.ipc.http;

import io.micrometer.core.ipc.http.HttpSender;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpSender
implements HttpSender {
    private static final MediaType MEDIA_TYPE_APPLICATION_JSON = MediaType.get((String)"application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_TEXT_PLAIN = MediaType.get((String)"text/plain; charset=utf-8");
    private final OkHttpClient client;

    public OkHttpSender(OkHttpClient client) {
        this.client = client;
    }

    public OkHttpSender() {
        this(new OkHttpClient());
    }

    @Override
    public HttpSender.Response send(HttpSender.Request request) throws Throwable {
        Request.Builder requestBuilder = new Request.Builder().url(request.getUrl());
        for (Map.Entry<String, String> requestHeader : request.getRequestHeaders().entrySet()) {
            requestBuilder.addHeader(requestHeader.getKey(), requestHeader.getValue());
        }
        byte[] entity = request.getEntity();
        HttpSender.Method method = request.getMethod();
        String methodValue = method.toString();
        if (entity.length > 0) {
            String contentType = request.getRequestHeaders().get("Content-Type");
            MediaType mediaType = contentType != null ? MediaType.get((String)(contentType + "; charset=utf-8")) : MEDIA_TYPE_APPLICATION_JSON;
            RequestBody body = RequestBody.create((byte[])entity, (MediaType)mediaType);
            requestBuilder.method(methodValue, body);
        } else if (OkHttpSender.requiresRequestBody(method)) {
            RequestBody body = RequestBody.create((byte[])entity, (MediaType)MEDIA_TYPE_TEXT_PLAIN);
            requestBuilder.method(methodValue, body);
        } else {
            requestBuilder.method(methodValue, null);
        }
        Response response = this.client.newCall(requestBuilder.build()).execute();
        return new HttpSender.Response(response.code(), response.body() == null ? null : response.body().string());
    }

    private static boolean requiresRequestBody(HttpSender.Method method) {
        switch (method) {
            case POST: 
            case PUT: {
                return true;
            }
        }
        return false;
    }
}

