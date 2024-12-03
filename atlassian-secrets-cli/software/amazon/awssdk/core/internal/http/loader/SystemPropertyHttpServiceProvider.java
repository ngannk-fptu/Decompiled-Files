/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.loader;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.internal.http.loader.SdkHttpServiceProvider;
import software.amazon.awssdk.http.SdkHttpService;
import software.amazon.awssdk.http.async.SdkAsyncHttpService;
import software.amazon.awssdk.utils.SystemSetting;

@SdkInternalApi
final class SystemPropertyHttpServiceProvider<T>
implements SdkHttpServiceProvider<T> {
    private final SystemSetting implSetting;
    private final Class<T> serviceClass;

    private SystemPropertyHttpServiceProvider(SystemSetting implSetting, Class<T> serviceClass) {
        this.implSetting = implSetting;
        this.serviceClass = serviceClass;
    }

    @Override
    public Optional<T> loadService() {
        return this.implSetting.getStringValue().map(this::createServiceFromProperty);
    }

    private T createServiceFromProperty(String httpImplFqcn) {
        try {
            return this.serviceClass.cast(Class.forName(httpImplFqcn).newInstance());
        }
        catch (Exception e) {
            throw SdkClientException.builder().message(String.format("Unable to load the HTTP factory implementation from the %s system property. Ensure the class '%s' is present on the classpathand has a no-arg constructor", SdkSystemSetting.SYNC_HTTP_SERVICE_IMPL.property(), httpImplFqcn)).cause(e).build();
        }
    }

    static SystemPropertyHttpServiceProvider<SdkHttpService> syncProvider() {
        return new SystemPropertyHttpServiceProvider<SdkHttpService>(SdkSystemSetting.SYNC_HTTP_SERVICE_IMPL, SdkHttpService.class);
    }

    static SystemPropertyHttpServiceProvider<SdkAsyncHttpService> asyncProvider() {
        return new SystemPropertyHttpServiceProvider<SdkAsyncHttpService>(SdkSystemSetting.ASYNC_HTTP_SERVICE_IMPL, SdkAsyncHttpService.class);
    }
}

