/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.checksums.spi.ChecksumAlgorithm
 *  software.amazon.awssdk.crt.auth.signing.AwsSigner
 *  software.amazon.awssdk.crt.auth.signing.AwsSigningConfig
 *  software.amazon.awssdk.crt.auth.signing.AwsSigningConfig$AwsSignatureType
 *  software.amazon.awssdk.crt.auth.signing.AwsSigningConfig$AwsSignedBodyHeaderType
 *  software.amazon.awssdk.crt.auth.signing.AwsSigningConfig$AwsSigningAlgorithm
 *  software.amazon.awssdk.crt.auth.signing.AwsSigningResult
 *  software.amazon.awssdk.crt.http.HttpRequest
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 *  software.amazon.awssdk.http.auth.spi.signer.AsyncSignRequest
 *  software.amazon.awssdk.http.auth.spi.signer.AsyncSignedRequest
 *  software.amazon.awssdk.http.auth.spi.signer.BaseSignRequest
 *  software.amazon.awssdk.http.auth.spi.signer.SignRequest
 *  software.amazon.awssdk.http.auth.spi.signer.SignedRequest
 *  software.amazon.awssdk.http.auth.spi.signer.SignedRequest$Builder
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 */
package software.amazon.awssdk.http.auth.aws.crt.internal.signer;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.checksums.spi.ChecksumAlgorithm;
import software.amazon.awssdk.crt.auth.signing.AwsSigner;
import software.amazon.awssdk.crt.auth.signing.AwsSigningConfig;
import software.amazon.awssdk.crt.auth.signing.AwsSigningResult;
import software.amazon.awssdk.crt.http.HttpRequest;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.crt.internal.signer.AwsChunkedV4aPayloadSigner;
import software.amazon.awssdk.http.auth.aws.crt.internal.signer.V4aPayloadSigner;
import software.amazon.awssdk.http.auth.aws.crt.internal.signer.V4aProperties;
import software.amazon.awssdk.http.auth.aws.crt.internal.signer.V4aRequestSigningResult;
import software.amazon.awssdk.http.auth.aws.crt.internal.util.CrtHttpRequestConverter;
import software.amazon.awssdk.http.auth.aws.crt.internal.util.CrtUtils;
import software.amazon.awssdk.http.auth.aws.internal.signer.CredentialScope;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.ChecksumUtil;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.CredentialUtils;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.SignerConstant;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4FamilyHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4aHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.RegionSet;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.BaseSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@SdkInternalApi
public final class DefaultAwsCrtV4aHttpSigner
implements AwsV4aHttpSigner {
    private static final int DEFAULT_CHUNK_SIZE_IN_BYTES = 131072;

    private static V4aProperties v4aProperties(BaseSignRequest<?, ? extends AwsCredentialsIdentity> request) {
        Clock signingClock = (Clock)request.requireProperty(SIGNING_CLOCK, (Object)Clock.systemUTC());
        Instant signingInstant = signingClock.instant();
        AwsCredentialsIdentity credentials = CredentialUtils.sanitizeCredentials((AwsCredentialsIdentity)request.identity());
        RegionSet regionSet = (RegionSet)request.requireProperty(REGION_SET);
        String serviceSigningName = (String)request.requireProperty(SERVICE_SIGNING_NAME);
        CredentialScope credentialScope = new CredentialScope(regionSet.asString(), serviceSigningName, signingInstant);
        boolean doubleUrlEncode = (Boolean)request.requireProperty(DOUBLE_URL_ENCODE, (Object)true);
        boolean normalizePath = (Boolean)request.requireProperty(NORMALIZE_PATH, (Object)true);
        return V4aProperties.builder().credentials(credentials).credentialScope(credentialScope).signingClock(signingClock).doubleUrlEncode(doubleUrlEncode).normalizePath(normalizePath).build();
    }

    private static V4aPayloadSigner v4aPayloadSigner(BaseSignRequest<?, ? extends AwsCredentialsIdentity> request, V4aProperties v4aProperties) {
        boolean isPayloadSigning = DefaultAwsCrtV4aHttpSigner.isPayloadSigning(request);
        boolean isChunkEncoding = (Boolean)request.requireProperty(CHUNK_ENCODING_ENABLED, (Object)false);
        boolean isTrailing = request.request().firstMatchingHeader("x-amz-trailer").isPresent();
        boolean isFlexible = request.hasProperty(CHECKSUM_ALGORITHM);
        if (DefaultAwsCrtV4aHttpSigner.useChunkEncoding(isPayloadSigning, isChunkEncoding, isTrailing || isFlexible)) {
            return AwsChunkedV4aPayloadSigner.builder().credentialScope(v4aProperties.getCredentialScope()).chunkSize(131072).checksumAlgorithm((ChecksumAlgorithm)request.property(CHECKSUM_ALGORITHM)).build();
        }
        return V4aPayloadSigner.create();
    }

    private static boolean useChunkEncoding(boolean payloadSigningEnabled, boolean chunkEncodingEnabled, boolean isTrailingOrFlexible) {
        return payloadSigningEnabled && chunkEncodingEnabled || chunkEncodingEnabled && isTrailingOrFlexible;
    }

    private static Duration validateExpirationDuration(Duration expirationDuration) {
        if (expirationDuration.compareTo(SignerConstant.PRESIGN_URL_MAX_EXPIRATION_DURATION) > 0) {
            throw new IllegalArgumentException("Requests that are pre-signed by SigV4 algorithm are valid for at most 7 days. The expiration duration set on the current request [" + expirationDuration + "] has exceeded this limit.");
        }
        return expirationDuration;
    }

    private static AwsSigningConfig signingConfig(BaseSignRequest<?, ? extends AwsCredentialsIdentity> request, V4aProperties v4aProperties) {
        AwsV4FamilyHttpSigner.AuthLocation authLocation = (AwsV4FamilyHttpSigner.AuthLocation)((Object)request.requireProperty(AUTH_LOCATION, (Object)AwsV4FamilyHttpSigner.AuthLocation.HEADER));
        Duration expirationDuration = (Duration)request.property(EXPIRATION_DURATION);
        boolean isPayloadSigning = DefaultAwsCrtV4aHttpSigner.isPayloadSigning(request);
        boolean isChunkEncoding = (Boolean)request.requireProperty(CHUNK_ENCODING_ENABLED, (Object)false);
        boolean isTrailing = request.request().firstMatchingHeader("x-amz-trailer").isPresent();
        boolean isFlexible = request.hasProperty(CHECKSUM_ALGORITHM) && !DefaultAwsCrtV4aHttpSigner.hasChecksumHeader(request);
        AwsSigningConfig signingConfig = new AwsSigningConfig();
        signingConfig.setCredentials(CrtUtils.toCredentials(v4aProperties.getCredentials()));
        signingConfig.setService(v4aProperties.getCredentialScope().getService());
        signingConfig.setRegion(v4aProperties.getCredentialScope().getRegion());
        signingConfig.setAlgorithm(AwsSigningConfig.AwsSigningAlgorithm.SIGV4_ASYMMETRIC);
        signingConfig.setTime(v4aProperties.getCredentialScope().getInstant().toEpochMilli());
        signingConfig.setUseDoubleUriEncode(v4aProperties.shouldDoubleUrlEncode());
        signingConfig.setShouldNormalizeUriPath(v4aProperties.shouldNormalizePath());
        signingConfig.setSignedBodyHeader(AwsSigningConfig.AwsSignedBodyHeaderType.X_AMZ_CONTENT_SHA256);
        switch (authLocation) {
            case HEADER: {
                signingConfig.setSignatureType(AwsSigningConfig.AwsSignatureType.HTTP_REQUEST_VIA_HEADERS);
                if (!request.hasProperty(EXPIRATION_DURATION)) break;
                throw new UnsupportedOperationException(String.format("%s is not supported for %s.", new Object[]{EXPIRATION_DURATION, AwsV4FamilyHttpSigner.AuthLocation.HEADER}));
            }
            case QUERY_STRING: {
                signingConfig.setSignatureType(AwsSigningConfig.AwsSignatureType.HTTP_REQUEST_VIA_QUERY_PARAMS);
                if (!request.hasProperty(EXPIRATION_DURATION)) break;
                signingConfig.setExpirationInSeconds(DefaultAwsCrtV4aHttpSigner.validateExpirationDuration(expirationDuration).getSeconds());
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown auth-location: " + (Object)((Object)authLocation));
            }
        }
        if (isPayloadSigning) {
            DefaultAwsCrtV4aHttpSigner.configurePayloadSigning(signingConfig, isChunkEncoding, isTrailing || isFlexible);
        } else {
            DefaultAwsCrtV4aHttpSigner.configureUnsignedPayload(signingConfig, isChunkEncoding, isTrailing || isFlexible);
        }
        return signingConfig;
    }

    private static boolean isPayloadSigning(BaseSignRequest<?, ? extends AwsCredentialsIdentity> request) {
        boolean isAnonymous = CredentialUtils.isAnonymous((AwsCredentialsIdentity)request.identity());
        boolean isPayloadSigningEnabled = (Boolean)request.requireProperty(PAYLOAD_SIGNING_ENABLED, (Object)true);
        boolean isEncrypted = "https".equals(request.request().protocol());
        return !isAnonymous && (isPayloadSigningEnabled || !isEncrypted);
    }

    private static void configureUnsignedPayload(AwsSigningConfig signingConfig, boolean isChunkEncoding, boolean isTrailingOrFlexible) {
        if (isChunkEncoding && isTrailingOrFlexible) {
            signingConfig.setSignedBodyValue("STREAMING-UNSIGNED-PAYLOAD-TRAILER");
        } else {
            signingConfig.setSignedBodyValue("UNSIGNED-PAYLOAD");
        }
    }

    private static void configurePayloadSigning(AwsSigningConfig signingConfig, boolean isChunkEncoding, boolean isTrailingOrFlexible) {
        if (isChunkEncoding) {
            if (isTrailingOrFlexible) {
                signingConfig.setSignedBodyValue("STREAMING-AWS4-ECDSA-P256-SHA256-PAYLOAD-TRAILER");
            } else {
                signingConfig.setSignedBodyValue("STREAMING-AWS4-ECDSA-P256-SHA256-PAYLOAD");
            }
        }
    }

    private static boolean hasChecksumHeader(BaseSignRequest<?, ? extends AwsCredentialsIdentity> request) {
        ChecksumAlgorithm checksumAlgorithm = (ChecksumAlgorithm)request.property(CHECKSUM_ALGORITHM);
        if (checksumAlgorithm != null) {
            String checksumHeaderName = ChecksumUtil.checksumHeaderName(checksumAlgorithm);
            return request.request().firstMatchingHeader(checksumHeaderName).isPresent();
        }
        return false;
    }

    private static SignedRequest doSign(SignRequest<? extends AwsCredentialsIdentity> request, AwsSigningConfig signingConfig, V4aPayloadSigner payloadSigner) {
        if (CredentialUtils.isAnonymous((AwsCredentialsIdentity)request.identity())) {
            return (SignedRequest)((SignedRequest.Builder)((SignedRequest.Builder)SignedRequest.builder().request(request.request())).payload(request.payload().orElse(null))).build();
        }
        SdkHttpRequest.Builder requestBuilder = (SdkHttpRequest.Builder)request.request().toBuilder();
        payloadSigner.beforeSigning(requestBuilder, request.payload().orElse(null), signingConfig.getSignedBodyValue());
        SdkHttpRequest sanitizedRequest = CrtUtils.sanitizeRequest((SdkHttpRequest)requestBuilder.build());
        HttpRequest crtRequest = CrtHttpRequestConverter.toRequest(sanitizedRequest, request.payload().orElse(null));
        V4aRequestSigningResult requestSigningResult = DefaultAwsCrtV4aHttpSigner.sign((SdkHttpRequest)requestBuilder.build(), crtRequest, signingConfig);
        ContentStreamProvider payload = payloadSigner.sign(request.payload().orElse(null), requestSigningResult);
        return (SignedRequest)((SignedRequest.Builder)((SignedRequest.Builder)SignedRequest.builder().request((SdkHttpRequest)requestSigningResult.getSignedRequest().build())).payload((Object)payload)).build();
    }

    private static V4aRequestSigningResult sign(SdkHttpRequest request, HttpRequest crtRequest, AwsSigningConfig signingConfig) {
        AwsSigningResult signingResult = (AwsSigningResult)CompletableFutureUtils.joinLikeSync((CompletableFuture)AwsSigner.sign((HttpRequest)crtRequest, (AwsSigningConfig)signingConfig));
        return new V4aRequestSigningResult((SdkHttpRequest.Builder)CrtHttpRequestConverter.toRequest(request, signingResult.getSignedRequest()).toBuilder(), signingResult.getSignature(), signingConfig);
    }

    public SignedRequest sign(SignRequest<? extends AwsCredentialsIdentity> request) {
        V4aProperties v4aProperties = DefaultAwsCrtV4aHttpSigner.v4aProperties(request);
        AwsSigningConfig signingConfig = DefaultAwsCrtV4aHttpSigner.signingConfig(request, v4aProperties);
        V4aPayloadSigner payloadSigner = DefaultAwsCrtV4aHttpSigner.v4aPayloadSigner(request, v4aProperties);
        return DefaultAwsCrtV4aHttpSigner.doSign(request, signingConfig, payloadSigner);
    }

    public CompletableFuture<AsyncSignedRequest> signAsync(AsyncSignRequest<? extends AwsCredentialsIdentity> request) {
        throw new UnsupportedOperationException();
    }
}

