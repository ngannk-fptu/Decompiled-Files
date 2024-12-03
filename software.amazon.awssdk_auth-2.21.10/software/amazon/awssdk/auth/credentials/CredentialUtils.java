/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.AwsSessionCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 */
package software.amazon.awssdk.auth.credentials;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.AwsSessionCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@SdkProtectedApi
public final class CredentialUtils {
    private CredentialUtils() {
    }

    public static boolean isAnonymous(AwsCredentials credentials) {
        return CredentialUtils.isAnonymous((AwsCredentialsIdentity)credentials);
    }

    public static boolean isAnonymous(AwsCredentialsIdentity credentials) {
        return credentials.secretAccessKey() == null && credentials.accessKeyId() == null;
    }

    public static AwsCredentials toCredentials(AwsCredentialsIdentity awsCredentialsIdentity) {
        if (awsCredentialsIdentity == null) {
            return null;
        }
        if (awsCredentialsIdentity instanceof AwsCredentials) {
            return (AwsCredentials)awsCredentialsIdentity;
        }
        if (awsCredentialsIdentity instanceof AwsSessionCredentialsIdentity) {
            AwsSessionCredentialsIdentity awsSessionCredentialsIdentity = (AwsSessionCredentialsIdentity)awsCredentialsIdentity;
            return AwsSessionCredentials.create(awsSessionCredentialsIdentity.accessKeyId(), awsSessionCredentialsIdentity.secretAccessKey(), awsSessionCredentialsIdentity.sessionToken());
        }
        if (CredentialUtils.isAnonymous(awsCredentialsIdentity)) {
            return AwsBasicCredentials.ANONYMOUS_CREDENTIALS;
        }
        return AwsBasicCredentials.create(awsCredentialsIdentity.accessKeyId(), awsCredentialsIdentity.secretAccessKey());
    }

    public static AwsCredentialsProvider toCredentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> identityProvider) {
        if (identityProvider == null) {
            return null;
        }
        if (identityProvider instanceof AwsCredentialsProvider) {
            return (AwsCredentialsProvider)identityProvider;
        }
        return () -> {
            AwsCredentialsIdentity awsCredentialsIdentity = (AwsCredentialsIdentity)CompletableFutureUtils.joinLikeSync((CompletableFuture)identityProvider.resolveIdentity());
            return CredentialUtils.toCredentials(awsCredentialsIdentity);
        };
    }
}

