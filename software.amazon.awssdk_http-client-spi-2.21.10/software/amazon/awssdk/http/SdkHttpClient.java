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
package software.amazon.awssdk.http;

import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.http.ExecutableHttpRequest;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.builder.SdkBuilder;

@Immutable
@ThreadSafe
@SdkPublicApi
public interface SdkHttpClient
extends SdkAutoCloseable {
    public ExecutableHttpRequest prepareRequest(HttpExecuteRequest var1);

    default public String clientName() {
        return "UNKNOWN";
    }

    @FunctionalInterface
    public static interface Builder<T extends Builder<T>>
    extends SdkBuilder<T, SdkHttpClient> {
        default public SdkHttpClient build() {
            return this.buildWithDefaults(AttributeMap.empty());
        }

        public SdkHttpClient buildWithDefaults(AttributeMap var1);
    }
}

