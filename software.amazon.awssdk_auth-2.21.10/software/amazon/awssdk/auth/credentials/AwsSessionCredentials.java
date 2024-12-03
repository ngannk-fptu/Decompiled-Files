/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.identity.spi.AwsSessionCredentialsIdentity
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.auth.credentials;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.identity.spi.AwsSessionCredentialsIdentity;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@Immutable
@SdkPublicApi
public final class AwsSessionCredentials
implements AwsCredentials,
AwsSessionCredentialsIdentity {
    private final String accessKeyId;
    private final String secretAccessKey;
    private final String sessionToken;
    private final Instant expirationTime;

    private AwsSessionCredentials(Builder builder) {
        this.accessKeyId = (String)Validate.paramNotNull((Object)builder.accessKeyId, (String)"accessKey");
        this.secretAccessKey = (String)Validate.paramNotNull((Object)builder.secretAccessKey, (String)"secretKey");
        this.sessionToken = (String)Validate.paramNotNull((Object)builder.sessionToken, (String)"sessionToken");
        this.expirationTime = builder.expirationTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static AwsSessionCredentials create(String accessKey, String secretKey, String sessionToken) {
        return AwsSessionCredentials.builder().accessKeyId(accessKey).secretAccessKey(secretKey).sessionToken(sessionToken).build();
    }

    public String accessKeyId() {
        return this.accessKeyId;
    }

    public String secretAccessKey() {
        return this.secretAccessKey;
    }

    public Optional<Instant> expirationTime() {
        return Optional.ofNullable(this.expirationTime);
    }

    public String sessionToken() {
        return this.sessionToken;
    }

    public String toString() {
        return ToString.builder((String)"AwsSessionCredentials").add("accessKeyId", (Object)this.accessKeyId()).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AwsSessionCredentials that = (AwsSessionCredentials)o;
        return Objects.equals(this.accessKeyId, that.accessKeyId) && Objects.equals(this.secretAccessKey, that.secretAccessKey) && Objects.equals(this.sessionToken, that.sessionToken) && Objects.equals(this.expirationTime, that.expirationTime().orElse(null));
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + Objects.hashCode(this.accessKeyId());
        hashCode = 31 * hashCode + Objects.hashCode(this.secretAccessKey());
        hashCode = 31 * hashCode + Objects.hashCode(this.sessionToken());
        hashCode = 31 * hashCode + Objects.hashCode(this.expirationTime);
        return hashCode;
    }

    public static final class Builder {
        private String accessKeyId;
        private String secretAccessKey;
        private String sessionToken;
        private Instant expirationTime;

        public Builder accessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
            return this;
        }

        public Builder secretAccessKey(String secretAccessKey) {
            this.secretAccessKey = secretAccessKey;
            return this;
        }

        public Builder sessionToken(String sessionToken) {
            this.sessionToken = sessionToken;
            return this;
        }

        public Builder expirationTime(Instant expirationTime) {
            this.expirationTime = expirationTime;
            return this;
        }

        public AwsSessionCredentials build() {
            return new AwsSessionCredentials(this);
        }
    }
}

