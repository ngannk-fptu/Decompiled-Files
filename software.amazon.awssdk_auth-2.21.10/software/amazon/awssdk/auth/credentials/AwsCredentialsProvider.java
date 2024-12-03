/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.identity.spi.ResolveIdentityRequest
 */
package software.amazon.awssdk.auth.credentials;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.ResolveIdentityRequest;

@FunctionalInterface
@SdkPublicApi
public interface AwsCredentialsProvider
extends IdentityProvider<AwsCredentialsIdentity> {
    public AwsCredentials resolveCredentials();

    default public Class<AwsCredentialsIdentity> identityType() {
        return AwsCredentialsIdentity.class;
    }

    default public CompletableFuture<AwsCredentialsIdentity> resolveIdentity(ResolveIdentityRequest request) {
        return CompletableFuture.completedFuture(this.resolveCredentials());
    }
}

