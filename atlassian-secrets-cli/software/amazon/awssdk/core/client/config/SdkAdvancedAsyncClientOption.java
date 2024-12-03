/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.client.config;

import java.util.concurrent.Executor;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.client.config.ClientOption;

@SdkPublicApi
public final class SdkAdvancedAsyncClientOption<T>
extends ClientOption<T> {
    public static final SdkAdvancedAsyncClientOption<Executor> FUTURE_COMPLETION_EXECUTOR = new SdkAdvancedAsyncClientOption<Executor>(Executor.class);

    private SdkAdvancedAsyncClientOption(Class<T> valueClass) {
        super(valueClass);
    }
}

