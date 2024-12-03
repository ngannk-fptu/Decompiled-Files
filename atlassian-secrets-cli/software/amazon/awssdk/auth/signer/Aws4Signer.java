/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.signer.internal.BaseAws4Signer;

@SdkPublicApi
public final class Aws4Signer
extends BaseAws4Signer {
    private Aws4Signer() {
    }

    public static Aws4Signer create() {
        return new Aws4Signer();
    }
}

