/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.crt.auth.signing.AwsSigningConfig
 */
package software.amazon.awssdk.http.auth.aws.crt.internal.signer;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.crt.auth.signing.AwsSigningConfig;
import software.amazon.awssdk.http.SdkHttpRequest;

@SdkInternalApi
public final class V4aRequestSigningResult {
    private final SdkHttpRequest.Builder signedRequest;
    private final byte[] signature;
    private final AwsSigningConfig signingConfig;

    public V4aRequestSigningResult(SdkHttpRequest.Builder signedRequest, byte[] signature, AwsSigningConfig signingConfig) {
        this.signedRequest = signedRequest;
        this.signature = (byte[])signature.clone();
        this.signingConfig = signingConfig;
    }

    public SdkHttpRequest.Builder getSignedRequest() {
        return this.signedRequest;
    }

    public byte[] getSignature() {
        return this.signature;
    }

    public AwsSigningConfig getSigningConfig() {
        return this.signingConfig;
    }
}

