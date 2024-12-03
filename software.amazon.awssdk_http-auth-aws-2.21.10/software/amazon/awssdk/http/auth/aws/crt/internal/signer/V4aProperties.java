/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http.auth.aws.crt.internal.signer;

import java.time.Clock;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.CredentialScope;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
@Immutable
public final class V4aProperties {
    private final AwsCredentialsIdentity credentials;
    private final CredentialScope credentialScope;
    private final Clock signingClock;
    private final boolean doubleUrlEncode;
    private final boolean normalizePath;

    private V4aProperties(Builder builder) {
        this.credentials = (AwsCredentialsIdentity)Validate.paramNotNull((Object)builder.credentials, (String)"Credentials");
        this.credentialScope = (CredentialScope)Validate.paramNotNull((Object)builder.credentialScope, (String)"CredentialScope");
        this.signingClock = (Clock)Validate.paramNotNull((Object)builder.signingClock, (String)"SigningClock");
        this.doubleUrlEncode = (Boolean)Validate.getOrDefault((Object)builder.doubleUrlEncode, () -> true);
        this.normalizePath = (Boolean)Validate.getOrDefault((Object)builder.normalizePath, () -> true);
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
            this.credentials = (AwsCredentialsIdentity)Validate.paramNotNull((Object)credentials, (String)"Credentials");
            return this;
        }

        public Builder credentialScope(CredentialScope credentialScope) {
            this.credentialScope = (CredentialScope)Validate.paramNotNull((Object)credentialScope, (String)"CredentialScope");
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

        public V4aProperties build() {
            return new V4aProperties(this);
        }
    }
}

