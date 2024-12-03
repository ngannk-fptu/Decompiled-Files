/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 *  software.amazon.awssdk.utils.builder.SdkBuilder
 */
package software.amazon.awssdk.http.async;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.http.async.AsyncExecuteRequest;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.builder.SdkBuilder;

@Immutable
@ThreadSafe
@SdkPublicApi
public interface SdkAsyncHttpClient
extends SdkAutoCloseable {
    public CompletableFuture<Void> execute(AsyncExecuteRequest var1);

    default public String clientName() {
        return "UNKNOWN";
    }

    @FunctionalInterface
    public static interface Builder<T extends Builder<T>>
    extends SdkBuilder<T, SdkAsyncHttpClient> {
        default public SdkAsyncHttpClient build() {
            return this.buildWithDefaults(AttributeMap.empty());
        }

        public SdkAsyncHttpClient buildWithDefaults(AttributeMap var1);
    }
}

