/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider
 *  software.amazon.awssdk.crt.auth.credentials.Credentials
 *  software.amazon.awssdk.crt.auth.credentials.CredentialsProvider
 *  software.amazon.awssdk.crt.auth.credentials.DelegateCredentialsProvider$DelegateCredentialsProviderBuilder
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.AwsSessionCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 */
package software.amazon.awssdk.services.s3.internal.crt;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.crt.auth.credentials.Credentials;
import software.amazon.awssdk.crt.auth.credentials.CredentialsProvider;
import software.amazon.awssdk.crt.auth.credentials.DelegateCredentialsProvider;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.AwsSessionCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkInternalApi
public final class CrtCredentialsProviderAdapter
implements SdkAutoCloseable {
    private final IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider;
    private final CredentialsProvider crtCredentials;

    public CrtCredentialsProviderAdapter(IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
        this.crtCredentials = new DelegateCredentialsProvider.DelegateCredentialsProviderBuilder().withHandler(() -> {
            if (credentialsProvider instanceof AnonymousCredentialsProvider) {
                return Credentials.createAnonymousCredentials();
            }
            AwsCredentialsIdentity sdkCredentials = (AwsCredentialsIdentity)CompletableFutureUtils.joinLikeSync((CompletableFuture)credentialsProvider.resolveIdentity());
            byte[] accessKey = sdkCredentials.accessKeyId().getBytes(StandardCharsets.UTF_8);
            byte[] secreteKey = sdkCredentials.secretAccessKey().getBytes(StandardCharsets.UTF_8);
            byte[] sessionTokens = null;
            if (sdkCredentials instanceof AwsSessionCredentialsIdentity) {
                sessionTokens = ((AwsSessionCredentialsIdentity)sdkCredentials).sessionToken().getBytes(StandardCharsets.UTF_8);
            }
            return new Credentials(accessKey, secreteKey, sessionTokens);
        }).build();
    }

    public CredentialsProvider crtCredentials() {
        return this.crtCredentials;
    }

    public void close() {
        if (this.credentialsProvider instanceof SdkAutoCloseable) {
            ((SdkAutoCloseable)this.credentialsProvider).close();
        }
        this.crtCredentials.close();
    }
}

