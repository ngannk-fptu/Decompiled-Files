/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.presigner;

import java.net.URI;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkPublicApi
public interface SdkPresigner
extends SdkAutoCloseable {
    @Override
    public void close();

    @SdkPublicApi
    public static interface Builder {
        public Builder region(Region var1);

        default public Builder credentialsProvider(AwsCredentialsProvider credentialsProvider) {
            return this.credentialsProvider((IdentityProvider<? extends AwsCredentialsIdentity>)credentialsProvider);
        }

        default public Builder credentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider) {
            throw new UnsupportedOperationException();
        }

        public Builder dualstackEnabled(Boolean var1);

        public Builder fipsEnabled(Boolean var1);

        public Builder endpointOverride(URI var1);

        public SdkPresigner build();
    }
}

