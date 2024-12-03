/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.crt.auth.signing.AwsSigner
 *  software.amazon.awssdk.crt.auth.signing.AwsSigningConfig
 *  software.amazon.awssdk.crt.auth.signing.AwsSigningConfig$AwsSignatureType
 *  software.amazon.awssdk.crt.auth.signing.AwsSigningConfig$AwsSignedBodyHeaderType
 *  software.amazon.awssdk.crt.auth.signing.AwsSigningResult
 *  software.amazon.awssdk.crt.http.HttpHeader
 *  software.amazon.awssdk.crt.http.HttpRequestBodyStream
 */
package software.amazon.awssdk.http.auth.aws.crt.internal.signer;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.crt.auth.signing.AwsSigner;
import software.amazon.awssdk.crt.auth.signing.AwsSigningConfig;
import software.amazon.awssdk.crt.auth.signing.AwsSigningResult;
import software.amazon.awssdk.crt.http.HttpHeader;
import software.amazon.awssdk.crt.http.HttpRequestBodyStream;
import software.amazon.awssdk.http.auth.aws.crt.internal.io.CrtInputStream;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@SdkInternalApi
public final class RollingSigner {
    private final byte[] seedSignature;
    private final AwsSigningConfig signingConfig;
    private byte[] previousSignature;

    public RollingSigner(byte[] seedSignature, AwsSigningConfig signingConfig) {
        this.seedSignature = (byte[])seedSignature.clone();
        this.previousSignature = (byte[])seedSignature.clone();
        this.signingConfig = signingConfig;
    }

    private static byte[] signChunk(byte[] chunkBody, byte[] previousSignature, AwsSigningConfig signingConfig) {
        AwsSigningConfig configCopy = signingConfig.clone();
        configCopy.setSignatureType(AwsSigningConfig.AwsSignatureType.HTTP_REQUEST_CHUNK);
        configCopy.setSignedBodyHeader(AwsSigningConfig.AwsSignedBodyHeaderType.NONE);
        configCopy.setSignedBodyValue(null);
        CrtInputStream crtBody = new CrtInputStream(() -> new ByteArrayInputStream(chunkBody));
        return (byte[])CompletableFutureUtils.joinLikeSync(AwsSigner.signChunk((HttpRequestBodyStream)crtBody, (byte[])previousSignature, (AwsSigningConfig)configCopy));
    }

    private static AwsSigningResult signTrailerHeaders(Map<String, List<String>> headerMap, byte[] previousSignature, AwsSigningConfig signingConfig) {
        List httpHeaderList = headerMap.entrySet().stream().map(entry -> new HttpHeader((String)entry.getKey(), String.join((CharSequence)",", (Iterable)entry.getValue()))).collect(Collectors.toList());
        AwsSigningConfig configCopy = signingConfig.clone();
        configCopy.setSignatureType(AwsSigningConfig.AwsSignatureType.HTTP_REQUEST_TRAILING_HEADERS);
        configCopy.setSignedBodyHeader(AwsSigningConfig.AwsSignedBodyHeaderType.NONE);
        configCopy.setSignedBodyValue(null);
        return (AwsSigningResult)CompletableFutureUtils.joinLikeSync(AwsSigner.sign(httpHeaderList, (byte[])previousSignature, (AwsSigningConfig)configCopy));
    }

    public byte[] sign(byte[] chunkBody) {
        this.previousSignature = RollingSigner.signChunk(chunkBody, this.previousSignature, this.signingConfig);
        return this.previousSignature;
    }

    public byte[] sign(Map<String, List<String>> headerMap) {
        AwsSigningResult result = RollingSigner.signTrailerHeaders(headerMap, this.previousSignature, this.signingConfig);
        this.previousSignature = result != null ? result.getSignature() : new byte[]{};
        return this.previousSignature;
    }

    public void reset() {
        this.previousSignature = this.seedSignature;
    }
}

