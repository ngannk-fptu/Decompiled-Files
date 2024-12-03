/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.signer.internal.AbstractAwsS3V4Signer;

@SdkPublicApi
public final class AwsS3V4Signer
extends AbstractAwsS3V4Signer {
    private AwsS3V4Signer() {
    }

    public static AwsS3V4Signer create() {
        return new AwsS3V4Signer();
    }
}

