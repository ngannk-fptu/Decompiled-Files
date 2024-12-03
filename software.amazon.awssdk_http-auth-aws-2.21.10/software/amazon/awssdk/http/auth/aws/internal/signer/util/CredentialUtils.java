/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.AwsSessionCredentialsIdentity
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.util;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.AwsSessionCredentialsIdentity;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public final class CredentialUtils {
    private CredentialUtils() {
    }

    public static boolean isAnonymous(AwsCredentialsIdentity credentials) {
        return credentials.secretAccessKey() == null && credentials.accessKeyId() == null;
    }

    public static AwsCredentialsIdentity sanitizeCredentials(AwsCredentialsIdentity credentials) {
        String accessKeyId = StringUtils.trim((String)credentials.accessKeyId());
        String secretKey = StringUtils.trim((String)credentials.secretAccessKey());
        if (credentials instanceof AwsSessionCredentialsIdentity) {
            AwsSessionCredentialsIdentity sessionCredentials = (AwsSessionCredentialsIdentity)credentials;
            return AwsSessionCredentialsIdentity.create((String)accessKeyId, (String)secretKey, (String)StringUtils.trim((String)sessionCredentials.sessionToken()));
        }
        if (accessKeyId == null && secretKey == null) {
            return credentials;
        }
        return AwsCredentialsIdentity.create((String)accessKeyId, (String)secretKey);
    }
}

