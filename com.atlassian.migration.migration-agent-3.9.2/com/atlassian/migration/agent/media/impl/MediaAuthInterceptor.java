/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  okhttp3.Interceptor
 *  okhttp3.Interceptor$Chain
 *  okhttp3.Request
 *  okhttp3.Response
 */
package com.atlassian.migration.agent.media.impl;

import com.atlassian.migration.agent.media.MediaClientToken;
import java.io.IOException;
import java.util.function.Supplier;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class MediaAuthInterceptor
implements Interceptor {
    private static final String CLIENT_ID_HEADER = "X-Client-Id";
    private static final String BEARER = "Bearer ";
    private final Supplier<MediaClientToken> clientTokenSupplier;

    MediaAuthInterceptor(Supplier<MediaClientToken> clientTokenSupplier) {
        this.clientTokenSupplier = clientTokenSupplier;
    }

    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        MediaClientToken token = this.clientTokenSupplier.get();
        request = request.newBuilder().addHeader(CLIENT_ID_HEADER, token.getClientId()).addHeader("Authorization", BEARER + token.getToken()).build();
        return chain.proceed(request);
    }
}

