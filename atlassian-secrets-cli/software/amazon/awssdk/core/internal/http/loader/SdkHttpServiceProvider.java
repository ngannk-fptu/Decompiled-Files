/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.loader;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
interface SdkHttpServiceProvider<T> {
    public Optional<T> loadService();
}

