/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.identity.spi.Identity
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.http.auth.spi.signer;

import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.auth.spi.internal.signer.DefaultSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.BaseSignRequest;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public interface SignRequest<IdentityT extends Identity>
extends BaseSignRequest<ContentStreamProvider, IdentityT>,
ToCopyableBuilder<Builder<IdentityT>, SignRequest<IdentityT>> {
    public static <IdentityT extends Identity> Builder<IdentityT> builder(IdentityT identity) {
        return DefaultSignRequest.builder(identity);
    }

    public static interface Builder<IdentityT extends Identity>
    extends BaseSignRequest.Builder<Builder<IdentityT>, ContentStreamProvider, IdentityT>,
    CopyableBuilder<Builder<IdentityT>, SignRequest<IdentityT>> {
    }
}

