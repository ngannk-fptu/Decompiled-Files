/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  net.jodah.failsafe.Failsafe
 *  net.jodah.failsafe.Policy
 *  net.jodah.failsafe.RetryPolicy
 *  okhttp3.Interceptor
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  org.codehaus.jackson.type.TypeReference
 */
package com.atlassian.migration.agent.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.okhttp.HttpService;
import com.atlassian.migration.agent.okhttp.OKHttpProxyBuilder;
import com.atlassian.migration.agent.okhttp.RetryPolicyBuilder;
import com.atlassian.migration.agent.service.ObjectStorageService;
import com.atlassian.migration.agent.service.impl.UserAgentInterceptor;
import java.util.concurrent.TimeUnit;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.RetryPolicy;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.codehaus.jackson.type.TypeReference;

public class S3ObjectStorageService
implements ObjectStorageService {
    private final HttpService httpService;
    private final RetryPolicy<Object> retryPolicy;

    @VisibleForTesting
    public S3ObjectStorageService(HttpService httpService) {
        this.httpService = httpService;
        this.retryPolicy = RetryPolicyBuilder.s3policy().build();
    }

    public S3ObjectStorageService(UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        this(new HttpService(() -> S3ObjectStorageService.buildHttpClient(userAgentInterceptor, okHttpProxyBuilder)));
    }

    @Override
    public Object download(String url, TypeReference<?> reference) {
        return Failsafe.with(this.retryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.httpService.callJson(this.getRequest(url), reference));
    }

    private Request getRequest(String url) {
        return new Request.Builder().url(url).get().build();
    }

    private static OkHttpClient buildHttpClient(UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        return okHttpProxyBuilder.getProxyBuilder().connectTimeout(5L, TimeUnit.SECONDS).followRedirects(true).followSslRedirects(true).readTimeout(20L, TimeUnit.SECONDS).addInterceptor((Interceptor)userAgentInterceptor).build();
    }
}

