/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4CanonicalRequest;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4Properties;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4RequestSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4RequestSigningResult;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.SignerUtils;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class DefaultV4RequestSigner
implements V4RequestSigner {
    private static final Logger LOG = Logger.loggerFor(DefaultV4RequestSigner.class);
    private final V4Properties properties;
    private final String contentHash;

    public DefaultV4RequestSigner(V4Properties properties, String contentHash) {
        this.properties = properties;
        this.contentHash = contentHash;
    }

    @Override
    public V4RequestSigningResult sign(SdkHttpRequest.Builder requestBuilder) {
        V4CanonicalRequest canonicalRequest = this.createCanonicalRequest((SdkHttpRequest)requestBuilder.build(), this.contentHash);
        String canonicalRequestHash = SignerUtils.hashCanonicalRequest(canonicalRequest.getCanonicalRequestString());
        String stringToSign = this.createSignString(canonicalRequestHash);
        byte[] signingKey = this.createSigningKey();
        String signature = this.createSignature(stringToSign, signingKey);
        return new V4RequestSigningResult(this.contentHash, signingKey, signature, canonicalRequest, requestBuilder);
    }

    private V4CanonicalRequest createCanonicalRequest(SdkHttpRequest request, String contentHash) {
        return new V4CanonicalRequest(request, contentHash, new V4CanonicalRequest.Options(this.properties.shouldDoubleUrlEncode(), this.properties.shouldNormalizePath()));
    }

    private String createSignString(String canonicalRequestHash) {
        LOG.debug(() -> "AWS4 Canonical Request Hash: " + canonicalRequestHash);
        String stringToSign = "AWS4-HMAC-SHA256\n" + this.properties.getCredentialScope().getDatetime() + "\n" + this.properties.getCredentialScope().scope() + "\n" + canonicalRequestHash;
        LOG.debug(() -> "AWS4 String to sign: " + stringToSign);
        return stringToSign;
    }

    private byte[] createSigningKey() {
        return SignerUtils.deriveSigningKey(this.properties.getCredentials(), this.properties.getCredentialScope());
    }

    private String createSignature(String stringToSign, byte[] signingKey) {
        return BinaryUtils.toHex(SignerUtils.computeSignature(stringToSign, signingKey));
    }
}

