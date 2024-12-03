/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.HttpChecksumConstant;
import software.amazon.awssdk.core.checksums.Algorithm;
import software.amazon.awssdk.core.checksums.ChecksumSpecs;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.internal.signer.SigningMethod;
import software.amazon.awssdk.core.internal.util.HttpChecksumResolver;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.utils.Pair;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public final class HttpChecksumUtils {
    private static final int CHECKSUM_BUFFER_SIZE = 16384;

    private HttpChecksumUtils() {
    }

    public static String httpChecksumHeader(String algorithmName) {
        return String.format("%s-%s", "x-amz-checksum", StringUtils.lowerCase(algorithmName));
    }

    public static boolean isStreamingUnsignedPayload(SdkHttpRequest sdkHttpRequest, ExecutionAttributes executionAttributes, ChecksumSpecs headerChecksumSpecs, boolean isContentStreaming) {
        String protocol;
        SigningMethod signingMethodUsed = executionAttributes.getAttribute(HttpChecksumConstant.SIGNING_METHOD);
        if (HttpChecksumUtils.isHeaderBasedSigningAuth(signingMethodUsed, protocol = sdkHttpRequest.protocol())) {
            return false;
        }
        return HttpChecksumUtils.isUnsignedPayload(signingMethodUsed, protocol, isContentStreaming) && headerChecksumSpecs.isRequestStreaming();
    }

    public static boolean isHeaderBasedSigningAuth(SigningMethod signingMethodUsed, String protocol) {
        switch (signingMethodUsed) {
            case HEADER_BASED_AUTH: {
                return true;
            }
            case PROTOCOL_BASED_UNSIGNED: {
                return "http".equals(protocol);
            }
        }
        return false;
    }

    public static boolean isUnsignedPayload(SigningMethod signingMethod, String protocol, boolean isContentStreaming) {
        switch (signingMethod) {
            case UNSIGNED_PAYLOAD: {
                return true;
            }
            case PROTOCOL_STREAMING_SIGNING_AUTH: {
                return "https".equals(protocol) || !isContentStreaming;
            }
            case PROTOCOL_BASED_UNSIGNED: {
                return "https".equals(protocol);
            }
        }
        return false;
    }

    public static byte[] computeChecksum(InputStream is, Algorithm algorithm) throws IOException {
        SdkChecksum sdkChecksum = SdkChecksum.forAlgorithm(algorithm);
        try (BufferedInputStream bis = new BufferedInputStream(is);){
            int bytesRead;
            byte[] buffer = new byte[16384];
            while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                sdkChecksum.update(buffer, 0, bytesRead);
            }
            byte[] byArray = sdkChecksum.getChecksumBytes();
            return byArray;
        }
    }

    public static Optional<ChecksumSpecs> checksumSpecWithRequestAlgorithm(ExecutionAttributes executionAttributes) {
        ChecksumSpecs resolvedChecksumSpecs = HttpChecksumResolver.getResolvedChecksumSpecs(executionAttributes);
        if (resolvedChecksumSpecs != null && resolvedChecksumSpecs.algorithm() != null) {
            return Optional.of(resolvedChecksumSpecs);
        }
        return Optional.empty();
    }

    public static boolean isHttpChecksumPresent(SdkHttpRequest sdkHttpRequest, ChecksumSpecs checksumSpec) {
        return sdkHttpRequest.firstMatchingHeader(checksumSpec.headerName()).isPresent() || HttpChecksumUtils.isTrailerChecksumPresent(sdkHttpRequest, checksumSpec);
    }

    public static boolean isMd5ChecksumRequired(ExecutionAttributes executionAttributes) {
        ChecksumSpecs resolvedChecksumSpecs = HttpChecksumResolver.getResolvedChecksumSpecs(executionAttributes);
        if (resolvedChecksumSpecs == null) {
            return false;
        }
        return resolvedChecksumSpecs.algorithm() == null && resolvedChecksumSpecs.isRequestChecksumRequired();
    }

    private static boolean isTrailerChecksumPresent(SdkHttpRequest sdkHttpRequest, ChecksumSpecs checksumSpec) {
        Optional<String> trailerBasedChecksum = sdkHttpRequest.firstMatchingHeader("x-amz-trailer");
        if (trailerBasedChecksum.isPresent()) {
            return trailerBasedChecksum.filter(checksum -> checksum.equalsIgnoreCase(checksumSpec.headerName())).isPresent();
        }
        return false;
    }

    public static boolean isTrailerBasedFlexibleChecksumComputed(SdkHttpRequest sdkHttpRequest, ExecutionAttributes executionAttributes, ChecksumSpecs checksumSpecs, boolean hasRequestBody, boolean isContentStreaming) {
        return hasRequestBody && !HttpChecksumUtils.isHttpChecksumPresent(sdkHttpRequest, checksumSpecs) && HttpChecksumUtils.isStreamingUnsignedPayload(sdkHttpRequest, executionAttributes, checksumSpecs, isContentStreaming);
    }

    public static boolean isTrailerBasedChecksumForClientType(ExecutionAttributes executionAttributes, SdkHttpRequest httpRequest, ClientType clientType, ChecksumSpecs checksumSpecs, boolean hasRequestBody, boolean isContentSteaming) {
        ClientType actualClientType = executionAttributes.getAttribute(SdkExecutionAttribute.CLIENT_TYPE);
        return actualClientType.equals((Object)clientType) && checksumSpecs != null && HttpChecksumUtils.isTrailerBasedFlexibleChecksumComputed(httpRequest, executionAttributes, checksumSpecs, hasRequestBody, isContentSteaming);
    }

    public static Pair<Algorithm, String> getAlgorithmChecksumValuePair(SdkHttpResponse sdkHttpResponse, ChecksumSpecs resolvedChecksumSpecs) {
        return resolvedChecksumSpecs.responseValidationAlgorithms().stream().map(algorithm -> {
            Optional<String> firstMatchingHeader = sdkHttpResponse.firstMatchingHeader(HttpChecksumUtils.httpChecksumHeader(algorithm.name()));
            return firstMatchingHeader.map(s -> Pair.of(algorithm, s)).orElse(null);
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public static boolean isHttpChecksumValidationEnabled(ChecksumSpecs resolvedChecksumSpecs) {
        return resolvedChecksumSpecs != null && resolvedChecksumSpecs.isValidationEnabled() && resolvedChecksumSpecs.responseValidationAlgorithms() != null;
    }

    public static byte[] longToByte(Long input) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(input);
        return buffer.array();
    }
}

