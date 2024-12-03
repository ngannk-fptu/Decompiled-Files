/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  okhttp3.ConnectionPool
 *  okhttp3.Interceptor
 *  okhttp3.OkHttpClient
 */
package com.atlassian.migration.agent.media.impl;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.media.MediaClientTokenSupplier;
import com.atlassian.migration.agent.media.MediaFileUploader;
import com.atlassian.migration.agent.media.MediaFileUploaderFactory;
import com.atlassian.migration.agent.media.impl.DefaultMediaApiClient;
import com.atlassian.migration.agent.media.impl.DefaultMediaFileUploader;
import com.atlassian.migration.agent.media.impl.MediaAuthInterceptor;
import com.atlassian.migration.agent.media.impl.RetryingMediaApiClient;
import com.atlassian.migration.agent.okhttp.HttpService;
import com.atlassian.migration.agent.okhttp.OKHttpProxyBuilder;
import com.atlassian.migration.agent.service.impl.UserAgentInterceptor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class DefaultMediaFileUploaderFactory
implements MediaFileUploaderFactory {
    private final MediaClientTokenSupplier mediaClientTokenSupplier;
    private final UserAgentInterceptor userAgentInterceptor;
    private final MigrationAgentConfiguration configuration;
    private final OKHttpProxyBuilder okHttpProxyBuilder;
    private static final ConnectionPool connectionPool = new ConnectionPool(100, 3L, TimeUnit.MINUTES);

    public DefaultMediaFileUploaderFactory(MediaClientTokenSupplier mediaClientTokenSupplier, MigrationAgentConfiguration configuration, UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        this.configuration = configuration;
        this.mediaClientTokenSupplier = mediaClientTokenSupplier;
        this.userAgentInterceptor = userAgentInterceptor;
        this.okHttpProxyBuilder = okHttpProxyBuilder;
    }

    @Override
    @Nonnull
    public MediaFileUploader create(String containerToken) {
        OkHttpClient httpClient = this.buildClient(containerToken);
        HttpService httpService = new HttpService(() -> httpClient);
        return new DefaultMediaFileUploader(new RetryingMediaApiClient(new DefaultMediaApiClient(this.configuration, httpService)));
    }

    private OkHttpClient buildClient(String containerToken) {
        return this.okHttpProxyBuilder.getProxyBuilder().followRedirects(true).followSslRedirects(true).writeTimeout(3L, TimeUnit.MINUTES).connectTimeout(20L, TimeUnit.SECONDS).readTimeout(3L, TimeUnit.MINUTES).addInterceptor((Interceptor)this.userAgentInterceptor).addInterceptor((Interceptor)new MediaAuthInterceptor(() -> this.mediaClientTokenSupplier.getToken(containerToken))).connectionPool(connectionPool).build();
    }
}

