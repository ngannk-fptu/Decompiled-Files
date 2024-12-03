/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.checksums.Algorithm
 *  software.amazon.awssdk.core.checksums.ChecksumSpecs
 *  software.amazon.awssdk.core.checksums.SdkChecksum
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.core.internal.util.HttpChecksumUtils
 *  software.amazon.awssdk.core.signer.Presigner
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpFullRequest$Builder
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Pair
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.auth.signer.internal;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute;
import software.amazon.awssdk.auth.signer.internal.AbstractAwsSigner;
import software.amazon.awssdk.auth.signer.internal.Aws4SignerRequestParams;
import software.amazon.awssdk.auth.signer.internal.Aws4SignerUtils;
import software.amazon.awssdk.auth.signer.internal.ContentChecksum;
import software.amazon.awssdk.auth.signer.internal.FifoCache;
import software.amazon.awssdk.auth.signer.internal.SignerKey;
import software.amazon.awssdk.auth.signer.internal.SigningAlgorithm;
import software.amazon.awssdk.auth.signer.params.Aws4PresignerParams;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.auth.signer.params.SignerChecksumParams;
import software.amazon.awssdk.core.checksums.Algorithm;
import software.amazon.awssdk.core.checksums.ChecksumSpecs;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.internal.util.HttpChecksumUtils;
import software.amazon.awssdk.core.signer.Presigner;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Pair;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public abstract class AbstractAws4Signer<T extends Aws4SignerParams, U extends Aws4PresignerParams>
extends AbstractAwsSigner
implements Presigner {
    public static final String EMPTY_STRING_SHA256_HEX = BinaryUtils.toHex((byte[])AbstractAws4Signer.hash(""));
    private static final Logger LOG = Logger.loggerFor(Aws4Signer.class);
    private static final int SIGNER_CACHE_MAX_SIZE = 300;
    private static final FifoCache<SignerKey> SIGNER_CACHE = new FifoCache(300);
    private static final List<String> LIST_OF_HEADERS_TO_IGNORE_IN_LOWER_CASE = Arrays.asList("connection", "x-amzn-trace-id", "user-agent", "expect");

    protected SdkHttpFullRequest.Builder doSign(SdkHttpFullRequest request, Aws4SignerRequestParams requestParams, T signingParams) {
        SdkHttpFullRequest.Builder mutableRequest = request.toBuilder();
        SdkChecksum sdkChecksum = this.createSdkChecksumFromParams(signingParams, request);
        String contentHash = this.calculateContentHash(mutableRequest, signingParams, sdkChecksum);
        return this.doSign(mutableRequest.build(), requestParams, signingParams, new ContentChecksum(contentHash, sdkChecksum));
    }

    protected SdkHttpFullRequest.Builder doSign(SdkHttpFullRequest request, Aws4SignerRequestParams requestParams, T signingParams, ContentChecksum contentChecksum) {
        SdkHttpFullRequest.Builder mutableRequest = request.toBuilder();
        AwsCredentials sanitizedCredentials = this.sanitizeCredentials(((Aws4SignerParams)signingParams).awsCredentials());
        if (sanitizedCredentials instanceof AwsSessionCredentials) {
            this.addSessionCredentials(mutableRequest, (AwsSessionCredentials)sanitizedCredentials);
        }
        this.addHostHeader(mutableRequest);
        this.addDateHeader(mutableRequest, requestParams.getFormattedRequestSigningDateTime());
        mutableRequest.firstMatchingHeader("x-amz-content-sha256").filter(h -> h.equals("required")).ifPresent(h -> mutableRequest.putHeader("x-amz-content-sha256", contentChecksum.contentHash()));
        this.putChecksumHeader(((Aws4SignerParams)signingParams).checksumParams(), contentChecksum.contentFlexibleChecksum(), mutableRequest, contentChecksum.contentHash());
        CanonicalRequest canonicalRequest = this.createCanonicalRequest(request, mutableRequest, contentChecksum.contentHash(), ((Aws4SignerParams)signingParams).doubleUrlEncode(), ((Aws4SignerParams)signingParams).normalizePath());
        String canonicalRequestString = canonicalRequest.string();
        String stringToSign = this.createStringToSign(canonicalRequestString, requestParams);
        byte[] signingKey = this.deriveSigningKey(sanitizedCredentials, requestParams);
        byte[] signature = this.computeSignature(stringToSign, signingKey);
        mutableRequest.putHeader("Authorization", this.buildAuthorizationHeader(signature, sanitizedCredentials, requestParams, canonicalRequest));
        this.processRequestPayload(mutableRequest, signature, signingKey, requestParams, signingParams, contentChecksum.contentFlexibleChecksum());
        return mutableRequest;
    }

    protected SdkHttpFullRequest.Builder doPresign(SdkHttpFullRequest request, Aws4SignerRequestParams requestParams, U signingParams) {
        SdkHttpFullRequest.Builder mutableRequest = request.toBuilder();
        long expirationInSeconds = this.getSignatureDurationInSeconds(requestParams, signingParams);
        this.addHostHeader(mutableRequest);
        AwsCredentials sanitizedCredentials = this.sanitizeCredentials(((Aws4SignerParams)signingParams).awsCredentials());
        if (sanitizedCredentials instanceof AwsSessionCredentials) {
            mutableRequest.putRawQueryParameter("X-Amz-Security-Token", ((AwsSessionCredentials)sanitizedCredentials).sessionToken());
        }
        String contentSha256 = this.calculateContentHashPresign(mutableRequest, signingParams);
        CanonicalRequest canonicalRequest = this.createCanonicalRequest(request, mutableRequest, contentSha256, ((Aws4SignerParams)signingParams).doubleUrlEncode(), ((Aws4SignerParams)signingParams).normalizePath());
        this.addPreSignInformationToRequest(mutableRequest, canonicalRequest, sanitizedCredentials, requestParams, expirationInSeconds);
        String string = canonicalRequest.string();
        String stringToSign = this.createStringToSign(string, requestParams);
        byte[] signingKey = this.deriveSigningKey(sanitizedCredentials, requestParams);
        byte[] signature = this.computeSignature(stringToSign, signingKey);
        mutableRequest.putRawQueryParameter("X-Amz-Signature", BinaryUtils.toHex((byte[])signature));
        return mutableRequest;
    }

    @Override
    protected void addSessionCredentials(SdkHttpFullRequest.Builder mutableRequest, AwsSessionCredentials credentials) {
        mutableRequest.putHeader("X-Amz-Security-Token", credentials.sessionToken());
    }

    protected String calculateContentHash(SdkHttpFullRequest.Builder mutableRequest, T signerParams) {
        return this.calculateContentHash(mutableRequest, signerParams, null);
    }

    protected String calculateContentHash(SdkHttpFullRequest.Builder mutableRequest, T signerParams, SdkChecksum contentFlexibleChecksum) {
        InputStream payloadStream = this.getBinaryRequestPayloadStream(mutableRequest.contentStreamProvider());
        return BinaryUtils.toHex((byte[])this.hash(payloadStream, contentFlexibleChecksum));
    }

    protected abstract void processRequestPayload(SdkHttpFullRequest.Builder var1, byte[] var2, byte[] var3, Aws4SignerRequestParams var4, T var5);

    protected abstract void processRequestPayload(SdkHttpFullRequest.Builder var1, byte[] var2, byte[] var3, Aws4SignerRequestParams var4, T var5, SdkChecksum var6);

    protected abstract String calculateContentHashPresign(SdkHttpFullRequest.Builder var1, U var2);

    protected final byte[] deriveSigningKey(AwsCredentials credentials, Aws4SignerRequestParams signerRequestParams) {
        return this.deriveSigningKey(credentials, Instant.ofEpochMilli(signerRequestParams.getRequestSigningDateTimeMilli()), signerRequestParams.getRegionName(), signerRequestParams.getServiceSigningName());
    }

    protected final byte[] deriveSigningKey(AwsCredentials credentials, Instant signingInstant, String region, String service) {
        String cacheKey = this.createSigningCacheKeyName(credentials, region, service);
        SignerKey signerKey = SIGNER_CACHE.get(cacheKey);
        if (signerKey != null && signerKey.isValidForDate(signingInstant)) {
            return signerKey.getSigningKey();
        }
        LOG.trace(() -> "Generating a new signing key as the signing key not available in the cache for the date: " + signingInstant.toEpochMilli());
        byte[] signingKey = this.newSigningKey(credentials, Aws4SignerUtils.formatDateStamp(signingInstant), region, service);
        SIGNER_CACHE.add(cacheKey, new SignerKey(signingInstant, signingKey));
        return signingKey;
    }

    private CanonicalRequest createCanonicalRequest(SdkHttpFullRequest request, SdkHttpFullRequest.Builder requestBuilder, String contentSha256, boolean doubleUrlEncode, boolean normalizePath) {
        return new CanonicalRequest(request, requestBuilder, contentSha256, doubleUrlEncode, normalizePath);
    }

    private String createStringToSign(String canonicalRequest, Aws4SignerRequestParams requestParams) {
        LOG.debug(() -> "AWS4 Canonical Request: " + canonicalRequest);
        String requestHash = BinaryUtils.toHex((byte[])AbstractAws4Signer.hash(canonicalRequest));
        String stringToSign = requestParams.getSigningAlgorithm() + "\n" + requestParams.getFormattedRequestSigningDateTime() + "\n" + requestParams.getScope() + "\n" + requestHash;
        LOG.debug(() -> "AWS4 String to sign: " + stringToSign);
        return stringToSign;
    }

    private String createSigningCacheKeyName(AwsCredentials credentials, String regionName, String serviceName) {
        return credentials.secretAccessKey() + "-" + regionName + "-" + serviceName;
    }

    private byte[] computeSignature(String stringToSign, byte[] signingKey) {
        return this.sign(stringToSign.getBytes(StandardCharsets.UTF_8), signingKey, SigningAlgorithm.HmacSHA256);
    }

    private String buildAuthorizationHeader(byte[] signature, AwsCredentials credentials, Aws4SignerRequestParams signerParams, CanonicalRequest canonicalRequest) {
        String accessKeyId = credentials.accessKeyId();
        String scope = signerParams.getScope();
        StringBuilder stringBuilder = canonicalRequest.signedHeaderStringBuilder();
        String signatureHex = BinaryUtils.toHex((byte[])signature);
        return "AWS4-HMAC-SHA256 Credential=" + accessKeyId + "/" + scope + ", SignedHeaders=" + stringBuilder + ", Signature=" + signatureHex;
    }

    private void addPreSignInformationToRequest(SdkHttpFullRequest.Builder mutableRequest, CanonicalRequest canonicalRequest, AwsCredentials sanitizedCredentials, Aws4SignerRequestParams signerParams, long expirationInSeconds) {
        String signingCredentials = sanitizedCredentials.accessKeyId() + "/" + signerParams.getScope();
        mutableRequest.putRawQueryParameter("X-Amz-Algorithm", "AWS4-HMAC-SHA256");
        mutableRequest.putRawQueryParameter("X-Amz-Date", signerParams.getFormattedRequestSigningDateTime());
        mutableRequest.putRawQueryParameter("X-Amz-SignedHeaders", canonicalRequest.signedHeaderString());
        mutableRequest.putRawQueryParameter("X-Amz-Expires", Long.toString(expirationInSeconds));
        mutableRequest.putRawQueryParameter("X-Amz-Credential", signingCredentials);
    }

    private static boolean isWhiteSpace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\u000b' || ch == '\r' || ch == '\f';
    }

    private void addHostHeader(SdkHttpFullRequest.Builder mutableRequest) {
        StringBuilder hostHeaderBuilder = new StringBuilder(mutableRequest.host());
        if (!SdkHttpUtils.isUsingStandardPort((String)mutableRequest.protocol(), (Integer)mutableRequest.port())) {
            hostHeaderBuilder.append(":").append(mutableRequest.port());
        }
        mutableRequest.putHeader("Host", hostHeaderBuilder.toString());
    }

    private void addDateHeader(SdkHttpFullRequest.Builder mutableRequest, String dateTime) {
        mutableRequest.putHeader("X-Amz-Date", dateTime);
    }

    private long getSignatureDurationInSeconds(Aws4SignerRequestParams requestParams, U signingParams) {
        long expirationInSeconds = ((Aws4PresignerParams)signingParams).expirationTime().map(t -> t.getEpochSecond() - requestParams.getRequestSigningDateTimeMilli() / 1000L).orElse(604800L);
        if (expirationInSeconds > 604800L) {
            throw SdkClientException.builder().message("Requests that are pre-signed by SigV4 algorithm are valid for at most 7 days. The expiration date set on the current request [" + Aws4SignerUtils.formatTimestamp(expirationInSeconds * 1000L) + "] + has exceeded this limit.").build();
        }
        return expirationInSeconds;
    }

    private byte[] newSigningKey(AwsCredentials credentials, String dateStamp, String regionName, String serviceName) {
        byte[] kSecret = ("AWS4" + credentials.secretAccessKey()).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = this.sign(dateStamp, kSecret, SigningAlgorithm.HmacSHA256);
        byte[] kRegion = this.sign(regionName, kDate, SigningAlgorithm.HmacSHA256);
        byte[] kService = this.sign(serviceName, kRegion, SigningAlgorithm.HmacSHA256);
        return this.sign("aws4_request", kService, SigningAlgorithm.HmacSHA256);
    }

    protected <B extends Aws4PresignerParams.Builder> B extractPresignerParams(B builder, ExecutionAttributes executionAttributes) {
        builder = this.extractSignerParams(builder, executionAttributes);
        builder.expirationTime((Instant)executionAttributes.getAttribute(AwsSignerExecutionAttribute.PRESIGNER_EXPIRATION));
        return builder;
    }

    protected <B extends Aws4SignerParams.Builder> B extractSignerParams(B paramsBuilder, ExecutionAttributes executionAttributes) {
        ChecksumSpecs checksumSpecs;
        Boolean normalizePath;
        paramsBuilder.awsCredentials((AwsCredentials)executionAttributes.getAttribute(AwsSignerExecutionAttribute.AWS_CREDENTIALS)).signingName((String)executionAttributes.getAttribute(AwsSignerExecutionAttribute.SERVICE_SIGNING_NAME)).signingRegion((Region)executionAttributes.getAttribute(AwsSignerExecutionAttribute.SIGNING_REGION)).timeOffset((Integer)executionAttributes.getAttribute(AwsSignerExecutionAttribute.TIME_OFFSET)).signingClockOverride((Clock)executionAttributes.getAttribute(AwsSignerExecutionAttribute.SIGNING_CLOCK));
        Boolean doubleUrlEncode = (Boolean)executionAttributes.getAttribute(AwsSignerExecutionAttribute.SIGNER_DOUBLE_URL_ENCODE);
        if (doubleUrlEncode != null) {
            paramsBuilder.doubleUrlEncode(doubleUrlEncode);
        }
        if ((normalizePath = (Boolean)executionAttributes.getAttribute(AwsSignerExecutionAttribute.SIGNER_NORMALIZE_PATH)) != null) {
            paramsBuilder.normalizePath(normalizePath);
        }
        if ((checksumSpecs = (ChecksumSpecs)executionAttributes.getAttribute(SdkExecutionAttribute.RESOLVED_CHECKSUM_SPECS)) != null && checksumSpecs.algorithm() != null) {
            paramsBuilder.checksumParams(this.buildSignerChecksumParams(checksumSpecs));
        }
        return paramsBuilder;
    }

    private void putChecksumHeader(SignerChecksumParams checksumSigner, SdkChecksum sdkChecksum, SdkHttpFullRequest.Builder mutableRequest, String contentHashString) {
        if (checksumSigner != null && sdkChecksum != null && !"UNSIGNED-PAYLOAD".equals(contentHashString) && !"STREAMING-UNSIGNED-PAYLOAD-TRAILER".equals(contentHashString)) {
            if (HttpChecksumUtils.isHttpChecksumPresent((SdkHttpRequest)mutableRequest.build(), (ChecksumSpecs)ChecksumSpecs.builder().headerName(checksumSigner.checksumHeaderName()).build())) {
                LOG.debug(() -> "Checksum already added in header ");
                return;
            }
            String headerChecksum = checksumSigner.checksumHeaderName();
            if (StringUtils.isNotBlank((CharSequence)headerChecksum)) {
                mutableRequest.putHeader(headerChecksum, BinaryUtils.toBase64((byte[])sdkChecksum.getChecksumBytes()));
            }
        }
    }

    private SignerChecksumParams buildSignerChecksumParams(ChecksumSpecs checksumSpecs) {
        return SignerChecksumParams.builder().algorithm(checksumSpecs.algorithm()).isStreamingRequest(checksumSpecs.isRequestStreaming()).checksumHeaderName(checksumSpecs.headerName()).build();
    }

    private SdkChecksum createSdkChecksumFromParams(T signingParams, SdkHttpFullRequest request) {
        boolean isValidChecksumHeader;
        SignerChecksumParams signerChecksumParams = ((Aws4SignerParams)signingParams).checksumParams();
        boolean bl = isValidChecksumHeader = signerChecksumParams != null && StringUtils.isNotBlank((CharSequence)signerChecksumParams.checksumHeaderName());
        if (isValidChecksumHeader && !HttpChecksumUtils.isHttpChecksumPresent((SdkHttpRequest)request, (ChecksumSpecs)ChecksumSpecs.builder().headerName(signerChecksumParams.checksumHeaderName()).build())) {
            return SdkChecksum.forAlgorithm((Algorithm)signerChecksumParams.algorithm());
        }
        return null;
    }

    static final class CanonicalRequest {
        private final SdkHttpFullRequest request;
        private final SdkHttpFullRequest.Builder requestBuilder;
        private final String contentSha256;
        private final boolean doubleUrlEncode;
        private final boolean normalizePath;
        private String canonicalRequestString;
        private StringBuilder signedHeaderStringBuilder;
        private List<Pair<String, List<String>>> canonicalHeaders;
        private String signedHeaderString;

        CanonicalRequest(SdkHttpFullRequest request, SdkHttpFullRequest.Builder requestBuilder, String contentSha256, boolean doubleUrlEncode, boolean normalizePath) {
            this.request = request;
            this.requestBuilder = requestBuilder;
            this.contentSha256 = contentSha256;
            this.doubleUrlEncode = doubleUrlEncode;
            this.normalizePath = normalizePath;
        }

        public String string() {
            if (this.canonicalRequestString == null) {
                StringBuilder canonicalRequest = new StringBuilder(512);
                canonicalRequest.append(this.requestBuilder.method().toString()).append("\n");
                this.addCanonicalizedResourcePath(canonicalRequest, (SdkHttpRequest)this.request, this.doubleUrlEncode, this.normalizePath);
                canonicalRequest.append("\n");
                this.addCanonicalizedQueryString(canonicalRequest, (SdkHttpRequest.Builder)this.requestBuilder);
                canonicalRequest.append("\n");
                this.addCanonicalizedHeaderString(canonicalRequest, this.canonicalHeaders());
                canonicalRequest.append("\n").append((CharSequence)this.signedHeaderStringBuilder()).append("\n").append(this.contentSha256);
                this.canonicalRequestString = canonicalRequest.toString();
            }
            return this.canonicalRequestString;
        }

        private void addCanonicalizedResourcePath(StringBuilder result, SdkHttpRequest request, boolean urlEncode, boolean normalizePath) {
            boolean trimTrailingSlash;
            String path;
            String string = path = normalizePath ? request.getUri().normalize().getRawPath() : request.encodedPath();
            if (StringUtils.isEmpty((CharSequence)path)) {
                result.append("/");
                return;
            }
            if (urlEncode) {
                path = SdkHttpUtils.urlEncodeIgnoreSlashes((String)path);
            }
            if (!path.startsWith("/")) {
                result.append("/");
            }
            result.append(path);
            boolean bl = trimTrailingSlash = normalizePath && path.length() > 1 && !request.encodedPath().endsWith("/") && result.charAt(result.length() - 1) == '/';
            if (trimTrailingSlash) {
                result.setLength(result.length() - 1);
            }
        }

        private void addCanonicalizedQueryString(StringBuilder result, SdkHttpRequest.Builder httpRequest) {
            TreeMap sorted = new TreeMap();
            httpRequest.forEachRawQueryParameter((key, values) -> {
                if (StringUtils.isEmpty((CharSequence)key)) {
                    return;
                }
                String encodedParamName = SdkHttpUtils.urlEncode((String)key);
                ArrayList<String> encodedValues = new ArrayList<String>(values.size());
                for (String value : values) {
                    String encodedValue = SdkHttpUtils.urlEncode((String)value);
                    String signatureFormattedEncodedValue = encodedValue == null ? "" : encodedValue;
                    encodedValues.add(signatureFormattedEncodedValue);
                }
                Collections.sort(encodedValues);
                sorted.put(encodedParamName, encodedValues);
            });
            SdkHttpUtils.flattenQueryParameters((StringBuilder)result, sorted);
        }

        public StringBuilder signedHeaderStringBuilder() {
            if (this.signedHeaderStringBuilder == null) {
                this.signedHeaderStringBuilder = new StringBuilder();
                this.addSignedHeaders(this.signedHeaderStringBuilder, this.canonicalHeaders());
            }
            return this.signedHeaderStringBuilder;
        }

        public String signedHeaderString() {
            if (this.signedHeaderString == null) {
                this.signedHeaderString = this.signedHeaderStringBuilder().toString();
            }
            return this.signedHeaderString;
        }

        private List<Pair<String, List<String>>> canonicalHeaders() {
            if (this.canonicalHeaders == null) {
                this.canonicalHeaders = this.canonicalizeSigningHeaders(this.requestBuilder);
            }
            return this.canonicalHeaders;
        }

        private void addCanonicalizedHeaderString(StringBuilder result, List<Pair<String, List<String>>> canonicalizedHeaders) {
            canonicalizedHeaders.forEach(header -> {
                result.append((String)header.left());
                result.append(":");
                for (String headerValue : (List)header.right()) {
                    this.addAndTrim(result, headerValue);
                    result.append(",");
                }
                result.setLength(result.length() - 1);
                result.append("\n");
            });
        }

        private List<Pair<String, List<String>>> canonicalizeSigningHeaders(SdkHttpFullRequest.Builder headers) {
            ArrayList<Pair<String, List<String>>> result = new ArrayList<Pair<String, List<String>>>(headers.numHeaders());
            headers.forEachHeader((key, value) -> {
                String lowerCaseHeader = StringUtils.lowerCase((String)key);
                if (!LIST_OF_HEADERS_TO_IGNORE_IN_LOWER_CASE.contains(lowerCaseHeader)) {
                    result.add(Pair.of((Object)lowerCaseHeader, (Object)value));
                }
            });
            result.sort(Comparator.comparing(Pair::left));
            return result;
        }

        private void addAndTrim(StringBuilder result, String value) {
            int lengthBefore = result.length();
            boolean isStart = true;
            boolean previousIsWhiteSpace = false;
            for (int i = 0; i < value.length(); ++i) {
                char ch = value.charAt(i);
                if (AbstractAws4Signer.isWhiteSpace(ch)) {
                    if (previousIsWhiteSpace || isStart) continue;
                    result.append(' ');
                    previousIsWhiteSpace = true;
                    continue;
                }
                result.append(ch);
                isStart = false;
                previousIsWhiteSpace = false;
            }
            if (lengthBefore == result.length()) {
                return;
            }
            int lastNonWhitespaceChar = result.length() - 1;
            while (AbstractAws4Signer.isWhiteSpace(result.charAt(lastNonWhitespaceChar))) {
                --lastNonWhitespaceChar;
            }
            result.setLength(lastNonWhitespaceChar + 1);
        }

        private void addSignedHeaders(StringBuilder result, List<Pair<String, List<String>>> canonicalizedHeaders) {
            for (Pair<String, List<String>> header : canonicalizedHeaders) {
                result.append((String)header.left()).append(';');
            }
            if (!canonicalizedHeaders.isEmpty()) {
                result.setLength(result.length() - 1);
            }
        }
    }
}

