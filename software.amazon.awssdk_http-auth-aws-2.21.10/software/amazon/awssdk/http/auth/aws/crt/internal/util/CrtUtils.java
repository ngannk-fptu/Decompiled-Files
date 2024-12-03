/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.crt.auth.credentials.Credentials
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.AwsSessionCredentialsIdentity
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.http.auth.aws.crt.internal.util;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.crt.auth.credentials.Credentials;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.AwsSessionCredentialsIdentity;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public final class CrtUtils {
    private static final String BODY_HASH_NAME = "x-amz-content-sha256";
    private static final String REGION_SET_NAME = "X-amz-region-set";
    private static final Set<String> FORBIDDEN_HEADERS = Stream.of("x-amz-content-sha256", "X-Amz-Date", "Authorization", "X-amz-region-set").collect(Collectors.toCollection(() -> new TreeSet(String.CASE_INSENSITIVE_ORDER)));
    private static final Set<String> FORBIDDEN_PARAMS = Stream.of("X-Amz-Signature", "X-Amz-Date", "X-Amz-Credential", "X-Amz-Algorithm", "X-Amz-SignedHeaders", "X-amz-region-set", "X-Amz-Expires").collect(Collectors.toCollection(() -> new TreeSet(String.CASE_INSENSITIVE_ORDER)));

    private CrtUtils() {
    }

    public static SdkHttpRequest sanitizeRequest(SdkHttpRequest request) {
        SdkHttpRequest.Builder builder = (SdkHttpRequest.Builder)request.toBuilder();
        String path = builder.encodedPath();
        if (path == null || path.isEmpty()) {
            builder.encodedPath("/");
        }
        builder.clearHeaders();
        request.forEachHeader((name, value) -> {
            if (!FORBIDDEN_HEADERS.contains(name)) {
                builder.putHeader(name, value);
            }
        });
        String hostHeader = SdkHttpUtils.isUsingStandardPort((String)request.protocol(), (Integer)request.port()) ? request.host() : request.host() + ":" + request.port();
        builder.putHeader("Host", hostHeader);
        builder.clearQueryParameters();
        request.forEachRawQueryParameter((key, value) -> {
            if (!FORBIDDEN_PARAMS.contains(key)) {
                builder.putRawQueryParameter(key, value);
            }
        });
        return (SdkHttpRequest)builder.build();
    }

    public static Credentials toCredentials(AwsCredentialsIdentity credentialsIdentity) {
        byte[] sessionToken = null;
        if (credentialsIdentity == null || credentialsIdentity.accessKeyId() == null || credentialsIdentity.secretAccessKey() == null) {
            return null;
        }
        if (credentialsIdentity instanceof AwsSessionCredentialsIdentity) {
            sessionToken = ((AwsSessionCredentialsIdentity)credentialsIdentity).sessionToken().getBytes(StandardCharsets.UTF_8);
        }
        return new Credentials(credentialsIdentity.accessKeyId().getBytes(StandardCharsets.UTF_8), credentialsIdentity.secretAccessKey().getBytes(StandardCharsets.UTF_8), sessionToken);
    }
}

