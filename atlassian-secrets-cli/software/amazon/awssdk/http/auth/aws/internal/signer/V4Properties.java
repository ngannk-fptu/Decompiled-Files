/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer;

import java.time.Clock;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.CredentialScope;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
@Immutable
public final class V4Properties {
    private final AwsCredentialsIdentity credentials;
    private final CredentialScope credentialScope;
    private final Clock signingClock;
    private final boolean doubleUrlEncode;
    private final boolean normalizePath;

    private V4Properties(Builder builder) {
        this.credentials = Validate.paramNotNull(builder.credentials, "Credentials");
        this.credentialScope = Validate.paramNotNull(builder.credentialScope, "CredentialScope");
        this.signingClock = Validate.paramNotNull(builder.signingClock, "SigningClock");
        this.doubleUrlEncode = Validate.getOrDefault(builder.doubleUrlEncode, () -> true);
        this.normalizePath = Validate.getOrDefault(builder.normalizePath, () -> true);
    }

    public static Builder builder() {
        return new Builder();
    }

    public AwsCredentialsIdentity getCredentials() {
        return this.credentials;
    }

    public CredentialScope getCredentialScope() {
        return this.credentialScope;
    }

    public Clock getSigningClock() {
        return this.signingClock;
    }

    public boolean shouldDoubleUrlEncode() {
        return this.doubleUrlEncode;
    }

    public boolean shouldNormalizePath() {
        return this.normalizePath;
    }

    public static class Builder {
        private AwsCredentialsIdentity credentials;
        private CredentialScope credentialScope;
        private Clock signingClock;
        private Boolean doubleUrlEncode;
        private Boolean normalizePath;

        public Builder credentials(AwsCredentialsIdentity credentials) {
            this.credentials = Validate.paramNotNull(credentials, "Credentials");
            return this;
        }

        public Builder credentialScope(CredentialScope credentialScope) {
            this.credentialScope = Validate.paramNotNull(credentialScope, "CredentialScope");
            return this;
        }

        public Builder signingClock(Clock signingClock) {
            this.signingClock = signingClock;
            return this;
        }

        public Builder doubleUrlEncode(Boolean doubleUrlEncode) {
            this.doubleUrlEncode = doubleUrlEncode;
            return this;
        }

        public Builder normalizePath(Boolean normalizePath) {
            this.normalizePath = normalizePath;
            return this;
        }

        public V4Properties build() {
            return new V4Properties(this);
        }
    }
}

