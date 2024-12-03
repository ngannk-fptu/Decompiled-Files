/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.internal.signer.SigningMethod;

@SdkInternalApi
public final class HttpChecksumConstant {
    public static final String HTTP_CHECKSUM_HEADER_PREFIX = "x-amz-checksum";
    public static final String X_AMZ_TRAILER = "x-amz-trailer";
    public static final String CONTENT_SHA_256_FOR_UNSIGNED_TRAILER = "STREAMING-UNSIGNED-PAYLOAD-TRAILER";
    public static final String AWS_CHUNKED_HEADER = "aws-chunked";
    public static final ExecutionAttribute<String> HTTP_CHECKSUM_VALUE = new ExecutionAttribute("HttpChecksumValue");
    public static final ExecutionAttribute<SigningMethod> SIGNING_METHOD = new ExecutionAttribute("SigningMethod");
    public static final String HEADER_FOR_TRAILER_REFERENCE = "x-amz-trailer";
    public static final int DEFAULT_ASYNC_CHUNK_SIZE = 16384;

    private HttpChecksumConstant() {
    }
}

