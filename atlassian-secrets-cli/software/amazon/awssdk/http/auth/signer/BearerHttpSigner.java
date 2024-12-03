/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.signer;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.auth.internal.signer.DefaultBearerHttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;
import software.amazon.awssdk.identity.spi.TokenIdentity;

@SdkPublicApi
public interface BearerHttpSigner
extends HttpSigner<TokenIdentity> {
    public static BearerHttpSigner create() {
        return new DefaultBearerHttpSigner();
    }
}

