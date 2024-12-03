/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.internal.signer.CredentialScope;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.DigestAlgorithm;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.FifoCache;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.SignerKey;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.SigningAlgorithm;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public final class SignerUtils {
    private static final Logger LOG = Logger.loggerFor(SignerUtils.class);
    private static final FifoCache<SignerKey> SIGNER_CACHE = new FifoCache(300);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.of("UTC"));
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneId.of("UTC"));

    private SignerUtils() {
    }

    public static String formatDate(Instant instant) {
        return DATE_FORMATTER.format(instant);
    }

    public static String formatDateTime(Instant instant) {
        return TIME_FORMATTER.format(instant);
    }

    public static String hashCanonicalRequest(String canonicalRequestString) {
        return BinaryUtils.toHex(SignerUtils.hash(canonicalRequestString));
    }

    public static byte[] deriveSigningKey(AwsCredentialsIdentity credentials, CredentialScope credentialScope) {
        String cacheKey = SignerUtils.createSigningCacheKeyName(credentials, credentialScope.getRegion(), credentialScope.getService());
        SignerKey signerKey = SIGNER_CACHE.get(cacheKey);
        if (signerKey != null && signerKey.isValidForDate(credentialScope.getInstant())) {
            return signerKey.getSigningKey();
        }
        LOG.trace(() -> "Generating a new signing key as the signing key not available in the cache for the date: " + credentialScope.getInstant().toEpochMilli());
        byte[] signingKey = SignerUtils.newSigningKey(credentials, credentialScope.getDate(), credentialScope.getRegion(), credentialScope.getService());
        SIGNER_CACHE.add(cacheKey, new SignerKey(credentialScope.getInstant(), signingKey));
        return signingKey;
    }

    private static String createSigningCacheKeyName(AwsCredentialsIdentity credentials, String regionName, String serviceName) {
        return credentials.secretAccessKey() + "-" + regionName + "-" + serviceName;
    }

    private static byte[] newSigningKey(AwsCredentialsIdentity credentials, String dateStamp, String regionName, String serviceName) {
        byte[] kSecret = ("AWS4" + credentials.secretAccessKey()).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = SignerUtils.sign(dateStamp, kSecret);
        byte[] kRegion = SignerUtils.sign(regionName, kDate);
        byte[] kService = SignerUtils.sign(serviceName, kRegion);
        return SignerUtils.sign("aws4_request", kService);
    }

    public static byte[] sign(String stringData, byte[] key) {
        try {
            byte[] data = stringData.getBytes(StandardCharsets.UTF_8);
            return SignerUtils.sign(data, key, SigningAlgorithm.HMAC_SHA256);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to calculate a request signature: ", e);
        }
    }

    public static byte[] sign(byte[] data, byte[] key, SigningAlgorithm algorithm) {
        try {
            Mac mac = algorithm.getMac();
            mac.init(new SecretKeySpec(key, algorithm.toString()));
            return mac.doFinal(data);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to calculate a request signature: ", e);
        }
    }

    public static byte[] computeSignature(String stringToSign, byte[] signingKey) {
        return SignerUtils.sign(stringToSign.getBytes(StandardCharsets.UTF_8), signingKey, SigningAlgorithm.HMAC_SHA256);
    }

    public static void addHostHeader(SdkHttpRequest.Builder requestBuilder) {
        String host = requestBuilder.host();
        if (!SdkHttpUtils.isUsingStandardPort(requestBuilder.protocol(), requestBuilder.port())) {
            StringBuilder hostHeaderBuilder = new StringBuilder(host);
            hostHeaderBuilder.append(":").append(requestBuilder.port());
            requestBuilder.putHeader("Host", hostHeaderBuilder.toString());
        } else {
            requestBuilder.putHeader("Host", host);
        }
    }

    public static void addDateHeader(SdkHttpRequest.Builder requestBuilder, String dateTime) {
        requestBuilder.putHeader("X-Amz-Date", dateTime);
    }

    public static long moveContentLength(SdkHttpRequest.Builder request, InputStream payload) {
        Optional<String> decodedContentLength = request.firstMatchingHeader("x-amz-decoded-content-length");
        if (!decodedContentLength.isPresent()) {
            String contentLength = request.firstMatchingHeader("Content-Length").orElseGet(() -> String.valueOf(SignerUtils.readAll(payload)));
            request.putHeader("x-amz-decoded-content-length", contentLength).removeHeader("Content-Length");
            return Long.parseLong(contentLength);
        }
        request.removeHeader("Content-Length");
        return Long.parseLong(decodedContentLength.get());
    }

    private static MessageDigest getMessageDigestInstance() {
        return DigestAlgorithm.SHA256.getDigest();
    }

    public static InputStream getBinaryRequestPayloadStream(ContentStreamProvider streamProvider) {
        try {
            if (streamProvider == null) {
                return new ByteArrayInputStream(new byte[0]);
            }
            return streamProvider.newStream();
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to read request payload to sign request: ", e);
        }
    }

    public static byte[] hash(InputStream input) {
        try {
            MessageDigest md = SignerUtils.getMessageDigestInstance();
            byte[] buf = new byte[4096];
            int read = 0;
            while (read >= 0) {
                read = input.read(buf);
                md.update(buf, 0, read);
            }
            return md.digest();
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to compute hash while signing request: ", e);
        }
    }

    public static byte[] hash(byte[] data) {
        try {
            MessageDigest md = SignerUtils.getMessageDigestInstance();
            md.update(data);
            return md.digest();
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to compute hash while signing request: ", e);
        }
    }

    public static byte[] hash(String text) {
        return SignerUtils.hash(text.getBytes(StandardCharsets.UTF_8));
    }

    private static int readAll(InputStream inputStream) {
        try {
            byte[] buffer = new byte[4096];
            int read = 0;
            int offset = 0;
            while (read >= 0) {
                read = inputStream.read(buffer);
                if (read < 0) continue;
                offset += read;
            }
            return offset;
        }
        catch (Exception e) {
            throw new RuntimeException("Could not finish reading stream: ", e);
        }
    }

    public static String getContentHash(SdkHttpRequest.Builder requestBuilder) {
        return requestBuilder.firstMatchingHeader("x-amz-content-sha256").orElseThrow(() -> new IllegalArgumentException("Content hash must be present in the 'x-amz-content-sha256' header!"));
    }
}

