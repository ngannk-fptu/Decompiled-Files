/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.BinaryUtils
 */
package software.amazon.awssdk.http.auth.aws.internal.signer;

import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.SignerUtils;
import software.amazon.awssdk.utils.BinaryUtils;

@SdkInternalApi
public final class RollingSigner {
    private final byte[] signingKey;
    private final String seedSignature;
    private String previousSignature;

    public RollingSigner(byte[] signingKey, String seedSignature) {
        this.seedSignature = seedSignature;
        this.previousSignature = seedSignature;
        this.signingKey = (byte[])signingKey.clone();
    }

    public String sign(Function<String, String> stringToSignTemplate) {
        String stringToSign = stringToSignTemplate.apply(this.previousSignature);
        byte[] bytes = SignerUtils.computeSignature(stringToSign, this.signingKey);
        this.previousSignature = BinaryUtils.toHex((byte[])bytes);
        return this.previousSignature;
    }

    public void reset() {
        this.previousSignature = this.seedSignature;
    }
}

