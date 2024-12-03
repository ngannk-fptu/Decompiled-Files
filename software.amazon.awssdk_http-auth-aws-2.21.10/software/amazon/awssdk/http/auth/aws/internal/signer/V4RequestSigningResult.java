/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 */
package software.amazon.awssdk.http.auth.aws.internal.signer;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4CanonicalRequest;

@SdkInternalApi
public final class V4RequestSigningResult {
    private final String contentHash;
    private final byte[] signingKey;
    private final String signature;
    private final V4CanonicalRequest canonicalRequest;
    private final SdkHttpRequest.Builder signedRequest;

    public V4RequestSigningResult(String contentHash, byte[] signingKey, String signature, V4CanonicalRequest canonicalRequest, SdkHttpRequest.Builder signedRequest) {
        this.contentHash = contentHash;
        this.signingKey = (byte[])signingKey.clone();
        this.signature = signature;
        this.canonicalRequest = canonicalRequest;
        this.signedRequest = signedRequest;
    }

    public String getContentHash() {
        return this.contentHash;
    }

    public byte[] getSigningKey() {
        return (byte[])this.signingKey.clone();
    }

    public String getSignature() {
        return this.signature;
    }

    public V4CanonicalRequest getCanonicalRequest() {
        return this.canonicalRequest;
    }

    public SdkHttpRequest.Builder getSignedRequest() {
        return this.signedRequest;
    }
}

