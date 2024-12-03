/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fx3.httpclient.HttpCallback
 *  kotlin.Pair
 *  okhttp3.MediaType
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  okhttp3.Response
 *  okhttp3.ResponseBody
 *  okio.Buffer
 *  okio.BufferedSource
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.featureflag;

import com.atlassian.fx3.httpclient.HttpCallback;
import com.atlassian.migration.agent.okhttp.OKHttpProxyBuilder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import kotlin.Pair;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fx3OkhttpAdapter
implements HttpCallback {
    private OkHttpClient client;
    private static final Logger log = LoggerFactory.getLogger(Fx3OkhttpAdapter.class);

    public Fx3OkhttpAdapter(OKHttpProxyBuilder okHttpProxyBuilder) {
        this.client = Fx3OkhttpAdapter.buildHttpClient(okHttpProxyBuilder);
    }

    private static OkHttpClient buildHttpClient(OKHttpProxyBuilder okHttpProxyBuilder) {
        return okHttpProxyBuilder.getProxyBuilder().connectTimeout(5L, TimeUnit.SECONDS).followRedirects(true).followSslRedirects(true).readTimeout(20L, TimeUnit.SECONDS).build();
    }

    @NotNull
    public Pair<Integer, String> post(@NotNull URL path, @NotNull Map<String, ? extends List<String>> headers, @NotNull String body) {
        RequestBody requestBody = RequestBody.create((String)body, (MediaType)MediaType.parse((String)"application/json"));
        Request.Builder requestBuilder = new Request.Builder().url(path).post(requestBody);
        for (Map.Entry<String, ? extends List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                requestBuilder.addHeader(entry.getKey(), value);
            }
        }
        Request request = requestBuilder.build();
        try {
            Response response = this.client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            assert (responseBody != null);
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            String responseBodyString = buffer.clone().readString(StandardCharsets.UTF_8);
            return new Pair((Object)response.code(), (Object)responseBodyString);
        }
        catch (Exception e) {
            log.error("Error occurred while making post request with message : {} . Returning empty FeatureFlag Set from Fx3 Service", (Object)e.getMessage());
            return new Pair((Object)200, (Object)"{\"featureFlagValues\":[],\"deletedFlags\":[],\"versionData\":\"\"}");
        }
    }
}

