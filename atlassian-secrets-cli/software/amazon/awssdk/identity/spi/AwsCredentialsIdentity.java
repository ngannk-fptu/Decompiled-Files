/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.identity.spi;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.identity.spi.internal.DefaultAwsCredentialsIdentity;

@SdkPublicApi
@ThreadSafe
public interface AwsCredentialsIdentity
extends Identity {
    public String accessKeyId();

    public String secretAccessKey();

    public static Builder builder() {
        return DefaultAwsCredentialsIdentity.builder();
    }

    public static AwsCredentialsIdentity create(String accessKeyId, String secretAccessKey) {
        return AwsCredentialsIdentity.builder().accessKeyId(accessKeyId).secretAccessKey(secretAccessKey).build();
    }

    public static interface Builder {
        public Builder accessKeyId(String var1);

        public Builder secretAccessKey(String var1);

        public AwsCredentialsIdentity build();
    }
}

