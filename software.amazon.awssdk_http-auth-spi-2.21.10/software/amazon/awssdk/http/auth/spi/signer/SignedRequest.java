/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.utils.builder.SdkBuilder
 */
package software.amazon.awssdk.http.auth.spi.signer;

import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.auth.spi.internal.signer.DefaultSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.BaseSignedRequest;
import software.amazon.awssdk.utils.builder.SdkBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public interface SignedRequest
extends BaseSignedRequest<ContentStreamProvider> {
    public static Builder builder() {
        return new DefaultSignedRequest.BuilderImpl();
    }

    public static interface Builder
    extends BaseSignedRequest.Builder<Builder, ContentStreamProvider>,
    SdkBuilder<Builder, SignedRequest> {
    }
}

