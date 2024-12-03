/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.util;

import java.time.Duration;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class SignerConstant {
    public static final String AWS4_TERMINATOR = "aws4_request";
    public static final String AWS4_SIGNING_ALGORITHM = "AWS4-HMAC-SHA256";
    public static final String X_AMZ_CONTENT_SHA256 = "x-amz-content-sha256";
    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String X_AMZ_SECURITY_TOKEN = "X-Amz-Security-Token";
    public static final String X_AMZ_CREDENTIAL = "X-Amz-Credential";
    public static final String X_AMZ_DATE = "X-Amz-Date";
    public static final String X_AMZ_EXPIRES = "X-Amz-Expires";
    public static final String X_AMZ_SIGNED_HEADERS = "X-Amz-SignedHeaders";
    public static final String X_AMZ_SIGNATURE = "X-Amz-Signature";
    public static final String X_AMZ_ALGORITHM = "X-Amz-Algorithm";
    public static final String X_AMZ_DECODED_CONTENT_LENGTH = "x-amz-decoded-content-length";
    public static final String X_AMZ_TRAILER = "x-amz-trailer";
    public static final String AWS_CHUNKED = "aws-chunked";
    public static final String HOST = "Host";
    public static final String LINE_SEPARATOR = "\n";
    public static final String UNSIGNED_PAYLOAD = "UNSIGNED-PAYLOAD";
    public static final String STREAMING_EVENTS_PAYLOAD = "STREAMING-AWS4-HMAC-SHA256-EVENTS";
    public static final String STREAMING_UNSIGNED_PAYLOAD_TRAILER = "STREAMING-UNSIGNED-PAYLOAD-TRAILER";
    public static final String STREAMING_ECDSA_SIGNED_PAYLOAD = "STREAMING-AWS4-ECDSA-P256-SHA256-PAYLOAD";
    public static final String STREAMING_ECDSA_SIGNED_PAYLOAD_TRAILER = "STREAMING-AWS4-ECDSA-P256-SHA256-PAYLOAD-TRAILER";
    public static final String STREAMING_SIGNED_PAYLOAD = "STREAMING-AWS4-HMAC-SHA256-PAYLOAD";
    public static final String STREAMING_SIGNED_PAYLOAD_TRAILER = "STREAMING-AWS4-HMAC-SHA256-PAYLOAD-TRAILER";
    public static final Duration PRESIGN_URL_MAX_EXPIRATION_DURATION = Duration.ofDays(7L);

    private SignerConstant() {
    }
}

