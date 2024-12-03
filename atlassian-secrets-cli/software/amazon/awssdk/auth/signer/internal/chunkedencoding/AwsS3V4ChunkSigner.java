/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer.internal.chunkedencoding;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.internal.AbstractAws4Signer;
import software.amazon.awssdk.auth.signer.internal.SigningAlgorithm;
import software.amazon.awssdk.auth.signer.internal.chunkedencoding.AwsChunkSigner;
import software.amazon.awssdk.auth.signer.internal.util.HeaderTransformsHelper;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.utils.BinaryUtils;

@SdkInternalApi
public class AwsS3V4ChunkSigner
implements AwsChunkSigner {
    public static final int SIGNATURE_LENGTH = 64;
    private static final String CHUNK_STRING_TO_SIGN_PREFIX = "AWS4-HMAC-SHA256-PAYLOAD";
    private static final String TRAILING_HEADER_STRING_TO_SIGN_PREFIX = "AWS4-HMAC-SHA256-TRAILER";
    private final String dateTime;
    private final String keyPath;
    private final MessageDigest sha256;
    private final MessageDigest sha256ForTrailer;
    private final Mac hmacSha256;
    private final Mac trailerHmacSha256;

    public AwsS3V4ChunkSigner(byte[] signingKey, String datetime, String keyPath) {
        try {
            this.sha256 = MessageDigest.getInstance("SHA-256");
            this.sha256ForTrailer = MessageDigest.getInstance("SHA-256");
            String signingAlgo = SigningAlgorithm.HmacSHA256.toString();
            this.hmacSha256 = Mac.getInstance(signingAlgo);
            this.hmacSha256.init(new SecretKeySpec(signingKey, signingAlgo));
            this.trailerHmacSha256 = Mac.getInstance(signingAlgo);
            this.trailerHmacSha256.init(new SecretKeySpec(signingKey, signingAlgo));
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
        this.dateTime = datetime;
        this.keyPath = keyPath;
    }

    @Override
    public String signChunk(byte[] chunkData, String previousSignature) {
        String chunkStringToSign = "AWS4-HMAC-SHA256-PAYLOAD\n" + this.dateTime + "\n" + this.keyPath + "\n" + previousSignature + "\n" + AbstractAws4Signer.EMPTY_STRING_SHA256_HEX + "\n" + BinaryUtils.toHex(this.sha256.digest(chunkData));
        try {
            byte[] bytes = this.hmacSha256.doFinal(chunkStringToSign.getBytes(StandardCharsets.UTF_8));
            return BinaryUtils.toHex(bytes);
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to calculate a request signature: " + e.getMessage()).cause(e).build();
        }
    }

    @Override
    public String signChecksumChunk(byte[] calculatedChecksum, String previousSignature, String checksumHeaderForTrailer) {
        Map<String, List<String>> canonicalizeSigningHeaders = HeaderTransformsHelper.canonicalizeSigningHeaders(Collections.singletonMap(checksumHeaderForTrailer, Arrays.asList(BinaryUtils.toBase64(calculatedChecksum))));
        String canonicalizedHeaderString = HeaderTransformsHelper.getCanonicalizedHeaderString(canonicalizeSigningHeaders);
        String chunkStringToSign = "AWS4-HMAC-SHA256-TRAILER\n" + this.dateTime + "\n" + this.keyPath + "\n" + previousSignature + "\n" + BinaryUtils.toHex(this.sha256ForTrailer.digest(canonicalizedHeaderString.getBytes(StandardCharsets.UTF_8)));
        return BinaryUtils.toHex(this.trailerHmacSha256.doFinal(chunkStringToSign.getBytes(StandardCharsets.UTF_8)));
    }

    public static int getSignatureLength() {
        return 64;
    }
}

