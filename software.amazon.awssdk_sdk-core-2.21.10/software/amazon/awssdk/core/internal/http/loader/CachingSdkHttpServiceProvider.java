/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.internal.http.loader;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.loader.SdkHttpServiceProvider;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
final class CachingSdkHttpServiceProvider<T>
implements SdkHttpServiceProvider<T> {
    private final SdkHttpServiceProvider<T> delegate;
    private volatile Optional<T> factory;

    CachingSdkHttpServiceProvider(SdkHttpServiceProvider<T> delegate) {
        this.delegate = (SdkHttpServiceProvider)Validate.notNull(delegate, (String)"Delegate service provider cannot be null", (Object[])new Object[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Optional<T> loadService() {
        if (this.factory == null) {
            CachingSdkHttpServiceProvider cachingSdkHttpServiceProvider = this;
            synchronized (cachingSdkHttpServiceProvider) {
                if (this.factory == null) {
                    this.factory = this.delegate.loadService();
                }
            }
        }
        return this.factory;
    }
}

