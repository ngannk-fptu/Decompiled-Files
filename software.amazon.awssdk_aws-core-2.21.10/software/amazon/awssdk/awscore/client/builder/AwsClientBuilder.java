/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
 *  software.amazon.awssdk.core.client.builder.SdkClientBuilder
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.regions.Region
 */
package software.amazon.awssdk.awscore.client.builder;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode;
import software.amazon.awssdk.core.client.builder.SdkClientBuilder;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.regions.Region;

@SdkPublicApi
public interface AwsClientBuilder<BuilderT extends AwsClientBuilder<BuilderT, ClientT>, ClientT>
extends SdkClientBuilder<BuilderT, ClientT> {
    default public BuilderT credentialsProvider(AwsCredentialsProvider credentialsProvider) {
        return this.credentialsProvider((IdentityProvider<AwsCredentialsIdentity>)credentialsProvider);
    }

    default public BuilderT credentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider) {
        throw new UnsupportedOperationException();
    }

    public BuilderT region(Region var1);

    default public BuilderT defaultsMode(DefaultsMode defaultsMode) {
        throw new UnsupportedOperationException();
    }

    public BuilderT dualstackEnabled(Boolean var1);

    public BuilderT fipsEnabled(Boolean var1);
}

