/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.internal.http.loader;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.loader.SdkHttpServiceProvider;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
final class SdkHttpServiceProviderChain<T>
implements SdkHttpServiceProvider<T> {
    private final List<SdkHttpServiceProvider<T>> httpProviders;

    @SafeVarargs
    SdkHttpServiceProviderChain(SdkHttpServiceProvider<T> ... httpProviders) {
        this.httpProviders = Arrays.asList(Validate.notEmpty((Object[])httpProviders, (String)"httpProviders cannot be null or empty", (Object[])new Object[0]));
    }

    @Override
    public Optional<T> loadService() {
        return this.httpProviders.stream().map(SdkHttpServiceProvider::loadService).filter(Optional::isPresent).map(Optional::get).findFirst();
    }
}

