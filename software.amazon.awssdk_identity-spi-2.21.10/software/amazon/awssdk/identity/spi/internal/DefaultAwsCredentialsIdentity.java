/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.identity.spi.internal;

import java.util.Objects;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultAwsCredentialsIdentity
implements AwsCredentialsIdentity {
    private final String accessKeyId;
    private final String secretAccessKey;

    private DefaultAwsCredentialsIdentity(Builder builder) {
        this.accessKeyId = builder.accessKeyId;
        this.secretAccessKey = builder.secretAccessKey;
        Validate.paramNotNull((Object)this.accessKeyId, (String)"accessKeyId");
        Validate.paramNotNull((Object)this.secretAccessKey, (String)"secretAccessKey");
    }

    public static AwsCredentialsIdentity.Builder builder() {
        return new Builder();
    }

    @Override
    public String accessKeyId() {
        return this.accessKeyId;
    }

    @Override
    public String secretAccessKey() {
        return this.secretAccessKey;
    }

    public String toString() {
        return ToString.builder((String)"AwsCredentialsIdentity").add("accessKeyId", (Object)this.accessKeyId).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AwsCredentialsIdentity that = (AwsCredentialsIdentity)o;
        return Objects.equals(this.accessKeyId, that.accessKeyId()) && Objects.equals(this.secretAccessKey, that.secretAccessKey());
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + Objects.hashCode(this.accessKeyId);
        hashCode = 31 * hashCode + Objects.hashCode(this.secretAccessKey);
        return hashCode;
    }

    private static final class Builder
    implements AwsCredentialsIdentity.Builder {
        private String accessKeyId;
        private String secretAccessKey;

        private Builder() {
        }

        @Override
        public Builder accessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
            return this;
        }

        @Override
        public Builder secretAccessKey(String secretAccessKey) {
            this.secretAccessKey = secretAccessKey;
            return this;
        }

        @Override
        public AwsCredentialsIdentity build() {
            return new DefaultAwsCredentialsIdentity(this);
        }
    }
}

