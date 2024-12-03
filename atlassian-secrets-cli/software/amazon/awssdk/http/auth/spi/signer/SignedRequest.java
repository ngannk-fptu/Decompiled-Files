/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.spi.signer;

import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.auth.spi.internal.signer.DefaultSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.BaseSignedRequest;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public interface SignedRequest
extends BaseSignedRequest<ContentStreamProvider>,
ToCopyableBuilder<Builder, SignedRequest> {
    public static Builder builder() {
        return DefaultSignedRequest.builder();
    }

    public static interface Builder
    extends BaseSignedRequest.Builder<Builder, ContentStreamProvider>,
    CopyableBuilder<Builder, SignedRequest> {
    }
}

