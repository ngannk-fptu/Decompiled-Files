/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Named
 *  okhttp3.OkHttpClient
 *  okhttp3.OkHttpClient$Builder
 */
package com.atlassian.migration.agent.okhttp;

import javax.inject.Named;
import okhttp3.OkHttpClient;

@Named
public final class OkHttpClientSingleton {
    private final OkHttpClient client = new OkHttpClient();

    public OkHttpClient.Builder getBuilder() {
        return this.getClient().newBuilder();
    }

    private OkHttpClient getClient() {
        return this.client;
    }
}

