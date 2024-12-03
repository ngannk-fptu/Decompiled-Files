/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 */
package software.amazon.awssdk.identity.spi;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.internal.DefaultAwsSessionCredentialsIdentity;

@SdkPublicApi
@ThreadSafe
public interface AwsSessionCredentialsIdentity
extends AwsCredentialsIdentity {
    public String sessionToken();

    public static Builder builder() {
        return DefaultAwsSessionCredentialsIdentity.builder();
    }

    public static AwsSessionCredentialsIdentity create(String accessKeyId, String secretAccessKey, String sessionToken) {
        return AwsSessionCredentialsIdentity.builder().accessKeyId(accessKeyId).secretAccessKey(secretAccessKey).sessionToken(sessionToken).build();
    }

    public static interface Builder
    extends AwsCredentialsIdentity.Builder {
        @Override
        public Builder accessKeyId(String var1);

        @Override
        public Builder secretAccessKey(String var1);

        public Builder sessionToken(String var1);

        @Override
        public AwsSessionCredentialsIdentity build();
    }
}

