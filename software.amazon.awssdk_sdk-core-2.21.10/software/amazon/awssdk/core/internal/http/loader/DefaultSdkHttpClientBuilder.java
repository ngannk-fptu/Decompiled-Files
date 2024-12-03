/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpClient
 *  software.amazon.awssdk.http.SdkHttpClient$Builder
 *  software.amazon.awssdk.http.SdkHttpService
 *  software.amazon.awssdk.utils.AttributeMap
 */
package software.amazon.awssdk.core.internal.http.loader;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.internal.http.loader.CachingSdkHttpServiceProvider;
import software.amazon.awssdk.core.internal.http.loader.ClasspathSdkHttpServiceProvider;
import software.amazon.awssdk.core.internal.http.loader.SdkHttpServiceProvider;
import software.amazon.awssdk.core.internal.http.loader.SdkHttpServiceProviderChain;
import software.amazon.awssdk.core.internal.http.loader.SystemPropertyHttpServiceProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.SdkHttpService;
import software.amazon.awssdk.utils.AttributeMap;

@SdkInternalApi
public final class DefaultSdkHttpClientBuilder
implements SdkHttpClient.Builder {
    private static final SdkHttpServiceProvider<SdkHttpService> DEFAULT_CHAIN = new CachingSdkHttpServiceProvider<SdkHttpService>(new SdkHttpServiceProviderChain(SystemPropertyHttpServiceProvider.syncProvider(), ClasspathSdkHttpServiceProvider.syncProvider()));

    public SdkHttpClient buildWithDefaults(AttributeMap serviceDefaults) {
        return DEFAULT_CHAIN.loadService().map(SdkHttpService::createHttpClientBuilder).map(f -> f.buildWithDefaults(serviceDefaults)).orElseThrow(() -> SdkClientException.builder().message("Unable to load an HTTP implementation from any provider in the chain. You must declare a dependency on an appropriate HTTP implementation or pass in an SdkHttpClient explicitly to the client builder.").build());
    }
}

