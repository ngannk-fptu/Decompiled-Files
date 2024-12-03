/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.credentials.AwsSessionCredentials
 */
package software.amazon.awssdk.services.sts.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.services.sts.model.Credentials;

@SdkInternalApi
public final class StsAuthUtils {
    private StsAuthUtils() {
    }

    public static AwsSessionCredentials toAwsSessionCredentials(Credentials credentials) {
        return AwsSessionCredentials.builder().accessKeyId(credentials.accessKeyId()).secretAccessKey(credentials.secretAccessKey()).sessionToken(credentials.sessionToken()).expirationTime(credentials.expiration()).build();
    }
}

