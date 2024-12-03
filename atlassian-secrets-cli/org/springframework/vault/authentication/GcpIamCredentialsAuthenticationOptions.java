/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auth.oauth2.GoogleCredentials
 */
package org.springframework.vault.authentication;

import com.google.auth.oauth2.GoogleCredentials;
import java.time.Clock;
import java.time.Duration;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.DefaultGoogleCredentialsAccessors;
import org.springframework.vault.authentication.GcpIamAuthenticationSupport;
import org.springframework.vault.authentication.GoogleCredentialsAccountIdAccessor;
import org.springframework.vault.authentication.GoogleCredentialsSupplier;

public class GcpIamCredentialsAuthenticationOptions
extends GcpIamAuthenticationSupport {
    public static final String DEFAULT_GCP_AUTHENTICATION_PATH = "gcp";
    private final GoogleCredentialsSupplier credentialSupplier;
    private final GoogleCredentialsAccountIdAccessor serviceAccountIdAccessor;

    private GcpIamCredentialsAuthenticationOptions(String path, GoogleCredentialsSupplier credentialSupplier, String role, Duration jwtValidity, Clock clock, GoogleCredentialsAccountIdAccessor serviceAccountIdAccessor) {
        super(path, role, jwtValidity, clock);
        this.credentialSupplier = credentialSupplier;
        this.serviceAccountIdAccessor = serviceAccountIdAccessor;
    }

    public static GcpIamCredentialsAuthenticationOptionsBuilder builder() {
        return new GcpIamCredentialsAuthenticationOptionsBuilder();
    }

    public GoogleCredentialsSupplier getCredentialSupplier() {
        return this.credentialSupplier;
    }

    public GoogleCredentialsAccountIdAccessor getServiceAccountIdAccessor() {
        return this.serviceAccountIdAccessor;
    }

    public static class GcpIamCredentialsAuthenticationOptionsBuilder {
        private String path = "gcp";
        @Nullable
        private String role;
        @Nullable
        private GoogleCredentialsSupplier credentialsSupplier;
        private Duration jwtValidity = Duration.ofMinutes(15L);
        private Clock clock = Clock.systemDefaultZone();
        private GoogleCredentialsAccountIdAccessor serviceAccountIdAccessor = DefaultGoogleCredentialsAccessors.INSTANCE;

        GcpIamCredentialsAuthenticationOptionsBuilder() {
        }

        public GcpIamCredentialsAuthenticationOptionsBuilder path(String path) {
            Assert.hasText(path, "Path must not be empty");
            this.path = path;
            return this;
        }

        public GcpIamCredentialsAuthenticationOptionsBuilder credentials(GoogleCredentials credentials) {
            Assert.notNull((Object)credentials, "ServiceAccountCredentials must not be null");
            return this.credentialsSupplier(() -> credentials);
        }

        public GcpIamCredentialsAuthenticationOptionsBuilder credentialsSupplier(GoogleCredentialsSupplier credentialsSupplier) {
            Assert.notNull((Object)credentialsSupplier, "GcpServiceAccountCredentialsSupplier must not be null");
            this.credentialsSupplier = credentialsSupplier;
            return this;
        }

        public GcpIamCredentialsAuthenticationOptionsBuilder serviceAccountId(String serviceAccountId) {
            Assert.notNull((Object)serviceAccountId, "Service account id may not be null");
            return this.serviceAccountIdAccessor(credentials -> serviceAccountId);
        }

        GcpIamCredentialsAuthenticationOptionsBuilder serviceAccountIdAccessor(GoogleCredentialsAccountIdAccessor serviceAccountIdAccessor) {
            Assert.notNull((Object)serviceAccountIdAccessor, "GcpServiceAccountIdAccessor must not be null");
            this.serviceAccountIdAccessor = serviceAccountIdAccessor;
            return this;
        }

        public GcpIamCredentialsAuthenticationOptionsBuilder role(String role) {
            Assert.hasText(role, "Role must not be null or empty");
            this.role = role;
            return this;
        }

        public GcpIamCredentialsAuthenticationOptionsBuilder jwtValidity(Duration jwtValidity) {
            Assert.notNull((Object)jwtValidity, "JWT validity duration must not be null");
            this.jwtValidity = jwtValidity;
            return this;
        }

        public GcpIamCredentialsAuthenticationOptionsBuilder clock(Clock clock) {
            Assert.notNull((Object)clock, "Clock must not be null");
            this.clock = clock;
            return this;
        }

        public GcpIamCredentialsAuthenticationOptions build() {
            Assert.notNull((Object)this.credentialsSupplier, "GoogleCredentialsSupplier must not be null");
            Assert.notNull((Object)this.role, "Role must not be null");
            return new GcpIamCredentialsAuthenticationOptions(this.path, this.credentialsSupplier, this.role, this.jwtValidity, this.clock, this.serviceAccountIdAccessor);
        }
    }
}

