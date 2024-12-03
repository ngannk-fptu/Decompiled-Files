/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer;

import java.nio.ByteBuffer;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.checksums.spi.ChecksumAlgorithm;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.internal.signer.AwsChunkedV4PayloadSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.Checksummer;
import software.amazon.awssdk.http.auth.aws.internal.signer.CredentialScope;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4PayloadSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4Properties;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4RequestSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4RequestSigningResult;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.ChecksumUtil;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.CredentialUtils;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.OptionalDependencyLoaderUtil;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.SignerConstant;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4FamilyHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.BaseSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;

@SdkInternalApi
public final class DefaultAwsV4HttpSigner
implements AwsV4HttpSigner {
    private static final int DEFAULT_CHUNK_SIZE_IN_BYTES = 131072;

    @Override
    public SignedRequest sign(SignRequest<? extends AwsCredentialsIdentity> request) {
        Checksummer checksummer = DefaultAwsV4HttpSigner.checksummer(request, null);
        V4Properties v4Properties = DefaultAwsV4HttpSigner.v4Properties(request);
        V4RequestSigner v4RequestSigner = DefaultAwsV4HttpSigner.v4RequestSigner(request, v4Properties);
        V4PayloadSigner payloadSigner = DefaultAwsV4HttpSigner.v4PayloadSigner(request, v4Properties);
        return DefaultAwsV4HttpSigner.doSign(request, checksummer, v4RequestSigner, payloadSigner);
    }

    @Override
    public CompletableFuture<AsyncSignedRequest> signAsync(AsyncSignRequest<? extends AwsCredentialsIdentity> request) {
        Checksummer checksummer = DefaultAwsV4HttpSigner.asyncChecksummer(request);
        V4Properties v4Properties = DefaultAwsV4HttpSigner.v4Properties(request);
        V4RequestSigner v4RequestSigner = DefaultAwsV4HttpSigner.v4RequestSigner(request, v4Properties);
        V4PayloadSigner payloadSigner = DefaultAwsV4HttpSigner.v4PayloadAsyncSigner(request, v4Properties);
        return DefaultAwsV4HttpSigner.doSign(request, checksummer, v4RequestSigner, payloadSigner);
    }

    private static V4Properties v4Properties(BaseSignRequest<?, ? extends AwsCredentialsIdentity> request) {
        Clock signingClock = request.requireProperty(SIGNING_CLOCK, Clock.systemUTC());
        Instant signingInstant = signingClock.instant();
        AwsCredentialsIdentity credentials = CredentialUtils.sanitizeCredentials(request.identity());
        String regionName = request.requireProperty(AwsV4HttpSigner.REGION_NAME);
        String serviceSigningName = (String)request.requireProperty(SERVICE_SIGNING_NAME);
        CredentialScope credentialScope = new CredentialScope(regionName, serviceSigningName, signingInstant);
        boolean doubleUrlEncode = request.requireProperty(DOUBLE_URL_ENCODE, true);
        boolean normalizePath = request.requireProperty(NORMALIZE_PATH, true);
        return V4Properties.builder().credentials(credentials).credentialScope(credentialScope).signingClock(signingClock).doubleUrlEncode(doubleUrlEncode).normalizePath(normalizePath).build();
    }

    private static V4RequestSigner v4RequestSigner(BaseSignRequest<?, ? extends AwsCredentialsIdentity> request, V4Properties v4Properties) {
        Function<V4Properties, V4RequestSigner> requestSigner;
        AwsV4FamilyHttpSigner.AuthLocation authLocation = request.requireProperty(AUTH_LOCATION, AwsV4FamilyHttpSigner.AuthLocation.HEADER);
        Duration expirationDuration = (Duration)request.property(EXPIRATION_DURATION);
        boolean isAnonymous = CredentialUtils.isAnonymous(request.identity());
        if (isAnonymous) {
            return V4RequestSigner.anonymous(v4Properties);
        }
        switch (authLocation) {
            case HEADER: {
                if (expirationDuration != null) {
                    throw new UnsupportedOperationException(String.format("%s is not supported for %s.", new Object[]{EXPIRATION_DURATION, AwsV4FamilyHttpSigner.AuthLocation.HEADER}));
                }
                requestSigner = V4RequestSigner::header;
                break;
            }
            case QUERY_STRING: {
                requestSigner = expirationDuration == null ? V4RequestSigner::query : properties -> V4RequestSigner.presigned(properties, DefaultAwsV4HttpSigner.validateExpirationDuration(expirationDuration));
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unsupported authLocation " + (Object)((Object)authLocation));
            }
        }
        return requestSigner.apply(v4Properties);
    }

    private static Checksummer checksummer(BaseSignRequest<?, ? extends AwsCredentialsIdentity> request, Boolean isPayloadSigningOverride) {
        boolean isPayloadSigning = isPayloadSigningOverride != null ? isPayloadSigningOverride : DefaultAwsV4HttpSigner.isPayloadSigning(request);
        boolean isEventStreaming = DefaultAwsV4HttpSigner.isEventStreaming(request.request());
        boolean hasChecksumHeader = DefaultAwsV4HttpSigner.hasChecksumHeader(request);
        boolean isChunkEncoding = request.requireProperty(CHUNK_ENCODING_ENABLED, false);
        boolean isTrailing = request.request().firstMatchingHeader("x-amz-trailer").isPresent();
        boolean isFlexible = request.hasProperty(CHECKSUM_ALGORITHM) && !hasChecksumHeader;
        boolean isAnonymous = CredentialUtils.isAnonymous(request.identity());
        if (isEventStreaming) {
            return Checksummer.forPrecomputed256Checksum("STREAMING-AWS4-HMAC-SHA256-EVENTS");
        }
        if (isPayloadSigning) {
            if (isChunkEncoding) {
                if (isFlexible || isTrailing) {
                    return Checksummer.forPrecomputed256Checksum("STREAMING-AWS4-HMAC-SHA256-PAYLOAD-TRAILER");
                }
                return Checksummer.forPrecomputed256Checksum("STREAMING-AWS4-HMAC-SHA256-PAYLOAD");
            }
            if (isFlexible) {
                return Checksummer.forFlexibleChecksum((ChecksumAlgorithm)request.property(CHECKSUM_ALGORITHM));
            }
            return Checksummer.create();
        }
        if ((isFlexible || isTrailing) && isChunkEncoding) {
            return Checksummer.forPrecomputed256Checksum("STREAMING-UNSIGNED-PAYLOAD-TRAILER");
        }
        if (isFlexible) {
            return Checksummer.forFlexibleChecksum("UNSIGNED-PAYLOAD", (ChecksumAlgorithm)request.property(CHECKSUM_ALGORITHM));
        }
        if (isAnonymous) {
            return Checksummer.forNoOp();
        }
        return Checksummer.forPrecomputed256Checksum("UNSIGNED-PAYLOAD");
    }

    private static Checksummer asyncChecksummer(BaseSignRequest<?, ? extends AwsCredentialsIdentity> request) {
        boolean isHttp = !"https".equals(request.request().protocol());
        boolean isPayloadSigning = DefaultAwsV4HttpSigner.isPayloadSigning(request);
        boolean isChunkEncoding = request.requireProperty(CHUNK_ENCODING_ENABLED, false);
        boolean shouldTreatAsUnsigned = isHttp && isPayloadSigning && isChunkEncoding;
        Boolean overridePayloadSigning = shouldTreatAsUnsigned ? Boolean.valueOf(false) : null;
        return DefaultAwsV4HttpSigner.checksummer(request, overridePayloadSigning);
    }

    private static V4PayloadSigner v4PayloadSigner(SignRequest<? extends AwsCredentialsIdentity> request, V4Properties properties) {
        boolean isFlexible;
        boolean isPayloadSigning = DefaultAwsV4HttpSigner.isPayloadSigning(request);
        boolean isEventStreaming = DefaultAwsV4HttpSigner.isEventStreaming(request.request());
        boolean isChunkEncoding = request.requireProperty(CHUNK_ENCODING_ENABLED, false);
        boolean isTrailing = request.request().firstMatchingHeader("x-amz-trailer").isPresent();
        boolean bl = isFlexible = request.hasProperty(CHECKSUM_ALGORITHM) && !DefaultAwsV4HttpSigner.hasChecksumHeader(request);
        if (isEventStreaming) {
            if (isPayloadSigning) {
                return OptionalDependencyLoaderUtil.getEventStreamV4PayloadSigner(properties.getCredentials(), properties.getCredentialScope(), properties.getSigningClock());
            }
            throw new UnsupportedOperationException("Unsigned payload is not supported with event-streaming.");
        }
        if (DefaultAwsV4HttpSigner.useChunkEncoding(isPayloadSigning, isChunkEncoding, isTrailing || isFlexible)) {
            return AwsChunkedV4PayloadSigner.builder().credentialScope(properties.getCredentialScope()).chunkSize(131072).checksumAlgorithm((ChecksumAlgorithm)request.property(CHECKSUM_ALGORITHM)).build();
        }
        return V4PayloadSigner.create();
    }

    private static V4PayloadSigner v4PayloadAsyncSigner(AsyncSignRequest<? extends AwsCredentialsIdentity> request, V4Properties properties) {
        boolean isPayloadSigning = request.requireProperty(PAYLOAD_SIGNING_ENABLED, true);
        boolean isEventStreaming = DefaultAwsV4HttpSigner.isEventStreaming(request.request());
        boolean isChunkEncoding = request.requireProperty(CHUNK_ENCODING_ENABLED, false);
        if (isEventStreaming) {
            if (isPayloadSigning) {
                return OptionalDependencyLoaderUtil.getEventStreamV4PayloadSigner(properties.getCredentials(), properties.getCredentialScope(), properties.getSigningClock());
            }
            throw new UnsupportedOperationException("Unsigned payload is not supported with event-streaming.");
        }
        if (isChunkEncoding && isPayloadSigning) {
            return V4PayloadSigner.create();
        }
        return V4PayloadSigner.create();
    }

    private static SignedRequest doSign(SignRequest<? extends AwsCredentialsIdentity> request, Checksummer checksummer, V4RequestSigner requestSigner, V4PayloadSigner payloadSigner) {
        SdkHttpRequest.Builder requestBuilder = (SdkHttpRequest.Builder)request.request().toBuilder();
        ContentStreamProvider requestPayload = request.payload().orElse(null);
        checksummer.checksum(requestPayload, requestBuilder);
        payloadSigner.beforeSigning(requestBuilder, requestPayload);
        V4RequestSigningResult requestSigningResult = requestSigner.sign(requestBuilder);
        ContentStreamProvider signedPayload = null;
        if (requestPayload != null) {
            signedPayload = payloadSigner.sign(requestPayload, requestSigningResult);
        }
        return (SignedRequest)((SignedRequest.Builder)((SignedRequest.Builder)SignedRequest.builder().request((SdkHttpRequest)requestSigningResult.getSignedRequest().build())).payload(signedPayload)).build();
    }

    private static CompletableFuture<AsyncSignedRequest> doSign(AsyncSignRequest<? extends AwsCredentialsIdentity> request, Checksummer checksummer, V4RequestSigner requestSigner, V4PayloadSigner payloadSigner) {
        SdkHttpRequest.Builder requestBuilder = (SdkHttpRequest.Builder)request.request().toBuilder();
        return checksummer.checksum((Publisher<ByteBuffer>)request.payload().orElse(null), requestBuilder).thenApply(payload -> {
            V4RequestSigningResult requestSigningResultFuture = requestSigner.sign(requestBuilder);
            return (AsyncSignedRequest)((AsyncSignedRequest.Builder)((AsyncSignedRequest.Builder)AsyncSignedRequest.builder().request((SdkHttpRequest)requestSigningResultFuture.getSignedRequest().build())).payload(payloadSigner.signAsync((Publisher<ByteBuffer>)payload, requestSigningResultFuture))).build();
        });
    }

    private static Duration validateExpirationDuration(Duration expirationDuration) {
        if (!DefaultAwsV4HttpSigner.isBetweenInclusive(Duration.ofSeconds(1L), expirationDuration, SignerConstant.PRESIGN_URL_MAX_EXPIRATION_DURATION)) {
            throw new IllegalArgumentException("Requests that are pre-signed by SigV4 algorithm are valid for at least 1 second and at most 7 days. The expiration duration set on the current request [" + expirationDuration + "] does not meet these bounds.");
        }
        return expirationDuration;
    }

    private static boolean isBetweenInclusive(Duration start, Duration x, Duration end) {
        return start.compareTo(x) <= 0 && x.compareTo(end) <= 0;
    }

    private static boolean isPayloadSigning(BaseSignRequest<?, ? extends AwsCredentialsIdentity> request) {
        boolean isAnonymous = CredentialUtils.isAnonymous(request.identity());
        boolean isPayloadSigningEnabled = request.requireProperty(PAYLOAD_SIGNING_ENABLED, true);
        boolean isEncrypted = "https".equals(request.request().protocol());
        return !isAnonymous && (isPayloadSigningEnabled || !isEncrypted);
    }

    private static boolean isEventStreaming(SdkHttpRequest request) {
        return "application/vnd.amazon.eventstream".equals(request.firstMatchingHeader("Content-Type").orElse(""));
    }

    private static boolean hasChecksumHeader(BaseSignRequest<?, ? extends AwsCredentialsIdentity> request) {
        ChecksumAlgorithm checksumAlgorithm = (ChecksumAlgorithm)request.property(CHECKSUM_ALGORITHM);
        if (checksumAlgorithm != null) {
            String checksumHeaderName = ChecksumUtil.checksumHeaderName(checksumAlgorithm);
            return request.request().firstMatchingHeader(checksumHeaderName).isPresent();
        }
        return false;
    }

    private static boolean useChunkEncoding(boolean payloadSigningEnabled, boolean chunkEncodingEnabled, boolean isTrailingOrFlexible) {
        return payloadSigningEnabled && chunkEncodingEnabled || chunkEncodingEnabled && isTrailingOrFlexible;
    }
}

