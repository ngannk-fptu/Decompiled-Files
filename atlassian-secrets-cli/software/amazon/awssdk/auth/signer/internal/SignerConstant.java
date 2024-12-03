/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class SignerConstant {
    public static final String AWS4_TERMINATOR = "aws4_request";
    public static final String AWS4_SIGNING_ALGORITHM = "AWS4-HMAC-SHA256";
    public static final long PRESIGN_URL_MAX_EXPIRATION_SECONDS = 604800L;
    public static final String X_AMZ_CONTENT_SHA256 = "x-amz-content-sha256";
    public static final String AUTHORIZATION = "Authorization";
    static final String X_AMZ_SECURITY_TOKEN = "X-Amz-Security-Token";
    static final String X_AMZ_CREDENTIAL = "X-Amz-Credential";
    static final String X_AMZ_DATE = "X-Amz-Date";
    static final String X_AMZ_EXPIRES = "X-Amz-Expires";
    static final String X_AMZ_SIGNED_HEADER = "X-Amz-SignedHeaders";
    static final String X_AMZ_SIGNATURE = "X-Amz-Signature";
    static final String X_AMZ_ALGORITHM = "X-Amz-Algorithm";
    static final String HOST = "Host";
    static final String LINE_SEPARATOR = "\n";

    private SignerConstant() {
    }
}

