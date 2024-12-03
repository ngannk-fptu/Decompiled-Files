/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer;

import java.time.Duration;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.internal.signer.DefaultV4RequestSigner;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4CanonicalRequest;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4Properties;
import software.amazon.awssdk.http.auth.aws.internal.signer.V4RequestSigningResult;
import software.amazon.awssdk.http.auth.aws.internal.signer.util.SignerUtils;
import software.amazon.awssdk.identity.spi.AwsSessionCredentialsIdentity;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public interface V4RequestSigner {
    public static V4RequestSigner create(V4Properties properties, String contentHash) {
        return new DefaultV4RequestSigner(properties, contentHash);
    }

    public static V4RequestSigner header(V4Properties properties) {
        return requestBuilder -> {
            if (properties.getCredentials() instanceof AwsSessionCredentialsIdentity) {
                requestBuilder.putHeader("X-Amz-Security-Token", ((AwsSessionCredentialsIdentity)properties.getCredentials()).sessionToken());
            }
            SignerUtils.addHostHeader(requestBuilder);
            SignerUtils.addDateHeader(requestBuilder, SignerUtils.formatDateTime(properties.getCredentialScope().getInstant()));
            V4RequestSigningResult result = V4RequestSigner.create(properties, SignerUtils.getContentHash(requestBuilder)).sign(requestBuilder);
            String authHeader = "AWS4-HMAC-SHA256 Credential=" + properties.getCredentialScope().scope(properties.getCredentials()) + ", SignedHeaders=" + result.getCanonicalRequest().getSignedHeadersString() + ", Signature=" + result.getSignature();
            requestBuilder.putHeader("Authorization", authHeader);
            return result;
        };
    }

    public static V4RequestSigner query(V4Properties properties) {
        return requestBuilder -> {
            if (properties.getCredentials() instanceof AwsSessionCredentialsIdentity) {
                requestBuilder.putRawQueryParameter("X-Amz-Security-Token", ((AwsSessionCredentialsIdentity)properties.getCredentials()).sessionToken());
            }
            SignerUtils.addHostHeader(requestBuilder);
            List<Pair<String, List<String>>> canonicalHeaders = V4CanonicalRequest.getCanonicalHeaders((SdkHttpRequest)requestBuilder.build());
            requestBuilder.putRawQueryParameter("X-Amz-Algorithm", "AWS4-HMAC-SHA256");
            requestBuilder.putRawQueryParameter("X-Amz-Date", properties.getCredentialScope().getDatetime());
            requestBuilder.putRawQueryParameter("X-Amz-SignedHeaders", V4CanonicalRequest.getSignedHeadersString(canonicalHeaders));
            requestBuilder.putRawQueryParameter("X-Amz-Credential", properties.getCredentialScope().scope(properties.getCredentials()));
            V4RequestSigningResult result = V4RequestSigner.create(properties, SignerUtils.getContentHash(requestBuilder)).sign(requestBuilder);
            requestBuilder.putRawQueryParameter("X-Amz-Signature", result.getSignature());
            return result;
        };
    }

    public static V4RequestSigner presigned(V4Properties properties, Duration expirationDuration) {
        return requestBuilder -> {
            if (properties.getCredentials() instanceof AwsSessionCredentialsIdentity) {
                requestBuilder.putRawQueryParameter("X-Amz-Security-Token", ((AwsSessionCredentialsIdentity)properties.getCredentials()).sessionToken());
            }
            SignerUtils.addHostHeader(requestBuilder);
            String contentHash = SignerUtils.getContentHash(requestBuilder);
            requestBuilder.removeHeader("x-amz-content-sha256");
            List<Pair<String, List<String>>> canonicalHeaders = V4CanonicalRequest.getCanonicalHeaders((SdkHttpRequest)requestBuilder.build());
            requestBuilder.putRawQueryParameter("X-Amz-Algorithm", "AWS4-HMAC-SHA256");
            requestBuilder.putRawQueryParameter("X-Amz-Date", properties.getCredentialScope().getDatetime());
            requestBuilder.putRawQueryParameter("X-Amz-SignedHeaders", V4CanonicalRequest.getSignedHeadersString(canonicalHeaders));
            requestBuilder.putRawQueryParameter("X-Amz-Credential", properties.getCredentialScope().scope(properties.getCredentials()));
            requestBuilder.putRawQueryParameter("X-Amz-Expires", Long.toString(expirationDuration.getSeconds()));
            V4RequestSigningResult result = V4RequestSigner.create(properties, contentHash).sign(requestBuilder);
            requestBuilder.putRawQueryParameter("X-Amz-Signature", result.getSignature());
            return result;
        };
    }

    public static V4RequestSigner anonymous(V4Properties properties) {
        return requestBuilder -> new V4RequestSigningResult("", new byte[0], null, null, requestBuilder);
    }

    public V4RequestSigningResult sign(SdkHttpRequest.Builder var1);
}

