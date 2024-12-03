/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.identity.spi.internal;

import java.util.Objects;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.identity.spi.AwsSessionCredentialsIdentity;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultAwsSessionCredentialsIdentity
implements AwsSessionCredentialsIdentity {
    private final String accessKeyId;
    private final String secretAccessKey;
    private final String sessionToken;

    private DefaultAwsSessionCredentialsIdentity(Builder builder) {
        this.accessKeyId = builder.accessKeyId;
        this.secretAccessKey = builder.secretAccessKey;
        this.sessionToken = builder.sessionToken;
        Validate.paramNotNull(this.accessKeyId, "accessKeyId");
        Validate.paramNotNull(this.secretAccessKey, "secretAccessKey");
        Validate.paramNotNull(this.sessionToken, "sessionToken");
    }

    public static AwsSessionCredentialsIdentity.Builder builder() {
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

    @Override
    public String sessionToken() {
        return this.sessionToken;
    }

    public String toString() {
        return ToString.builder("AwsSessionCredentialsIdentity").add("accessKeyId", this.accessKeyId).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AwsSessionCredentialsIdentity that = (AwsSessionCredentialsIdentity)o;
        return Objects.equals(this.accessKeyId, that.accessKeyId()) && Objects.equals(this.secretAccessKey, that.secretAccessKey()) && Objects.equals(this.sessionToken, that.sessionToken());
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + Objects.hashCode(this.accessKeyId);
        hashCode = 31 * hashCode + Objects.hashCode(this.secretAccessKey);
        hashCode = 31 * hashCode + Objects.hashCode(this.sessionToken);
        return hashCode;
    }

    private static final class Builder
    implements AwsSessionCredentialsIdentity.Builder {
        private String accessKeyId;
        private String secretAccessKey;
        private String sessionToken;

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
        public Builder sessionToken(String sessionToken) {
            this.sessionToken = sessionToken;
            return this;
        }

        @Override
        public AwsSessionCredentialsIdentity build() {
            return new DefaultAwsSessionCredentialsIdentity(this);
        }
    }
}

