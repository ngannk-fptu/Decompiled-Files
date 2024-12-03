/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 *  software.amazon.awssdk.http.SdkHttpService
 *  software.amazon.awssdk.http.async.SdkAsyncHttpService
 *  software.amazon.awssdk.utils.SystemSetting
 */
package software.amazon.awssdk.core.internal.http.loader;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.internal.http.loader.SdkHttpServiceProvider;
import software.amazon.awssdk.core.internal.http.loader.SdkServiceLoader;
import software.amazon.awssdk.http.SdkHttpService;
import software.amazon.awssdk.http.async.SdkAsyncHttpService;
import software.amazon.awssdk.utils.SystemSetting;

@SdkInternalApi
final class ClasspathSdkHttpServiceProvider<T>
implements SdkHttpServiceProvider<T> {
    private final SdkServiceLoader serviceLoader;
    private final SystemSetting implSystemProperty;
    private final Class<T> serviceClass;

    @SdkTestInternalApi
    ClasspathSdkHttpServiceProvider(SdkServiceLoader serviceLoader, SystemSetting implSystemProperty, Class<T> serviceClass) {
        this.serviceLoader = serviceLoader;
        this.implSystemProperty = implSystemProperty;
        this.serviceClass = serviceClass;
    }

    @Override
    public Optional<T> loadService() {
        Iterable iterable = () -> this.serviceLoader.loadServices(this.serviceClass);
        List impls = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
        if (impls.isEmpty()) {
            return Optional.empty();
        }
        if (impls.size() > 1) {
            String implText = impls.stream().map(clazz -> clazz.getClass().getName()).collect(Collectors.joining(",", "[", "]"));
            throw SdkClientException.builder().message(String.format("Multiple HTTP implementations were found on the classpath. To avoid non-deterministic loading implementations, please explicitly provide an HTTP client via the client builders, set the %s system property with the FQCN of the HTTP service to use as the default, or remove all but one HTTP implementation from the classpath.  The multiple implementations found were: %s", this.implSystemProperty.property(), implText)).build();
        }
        return impls.stream().findFirst();
    }

    static SdkHttpServiceProvider<SdkHttpService> syncProvider() {
        return new ClasspathSdkHttpServiceProvider<SdkHttpService>(SdkServiceLoader.INSTANCE, SdkSystemSetting.SYNC_HTTP_SERVICE_IMPL, SdkHttpService.class);
    }

    static SdkHttpServiceProvider<SdkAsyncHttpService> asyncProvider() {
        return new ClasspathSdkHttpServiceProvider<SdkAsyncHttpService>(SdkServiceLoader.INSTANCE, SdkSystemSetting.ASYNC_HTTP_SERVICE_IMPL, SdkAsyncHttpService.class);
    }
}

