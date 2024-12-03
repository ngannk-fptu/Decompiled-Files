/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.api.client.googleapis.auth.oauth2.GoogleCredential
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.vault.authentication;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import java.time.Clock;
import java.time.Duration;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.DefaultGcpCredentialAccessors;
import org.springframework.vault.authentication.GcpCredentialSupplier;
import org.springframework.vault.authentication.GcpIamAuthenticationSupport;
import org.springframework.vault.authentication.GcpProjectIdAccessor;
import org.springframework.vault.authentication.GcpServiceAccountIdAccessor;

@Deprecated
public class GcpIamAuthenticationOptions
extends GcpIamAuthenticationSupport {
    public static final String DEFAULT_GCP_AUTHENTICATION_PATH = "gcp";
    private final GcpCredentialSupplier credentialSupplier;
    private final GcpServiceAccountIdAccessor serviceAccountIdAccessor;
    private final GcpProjectIdAccessor projectIdAccessor;

    private GcpIamAuthenticationOptions(String path, GcpCredentialSupplier credentialSupplier, String role, Duration jwtValidity, Clock clock, GcpServiceAccountIdAccessor serviceAccountIdSupplier, GcpProjectIdAccessor projectIdAccessor) {
        super(path, role, jwtValidity, clock);
        this.credentialSupplier = credentialSupplier;
        this.serviceAccountIdAccessor = serviceAccountIdSupplier;
        this.projectIdAccessor = projectIdAccessor;
    }

    public static GcpIamAuthenticationOptionsBuilder builder() {
        return new GcpIamAuthenticationOptionsBuilder();
    }

    public GcpCredentialSupplier getCredentialSupplier() {
        return this.credentialSupplier;
    }

    public GcpServiceAccountIdAccessor getServiceAccountIdAccessor() {
        return this.serviceAccountIdAccessor;
    }

    public GcpProjectIdAccessor getProjectIdAccessor() {
        return this.projectIdAccessor;
    }

    public static class GcpIamAuthenticationOptionsBuilder {
        private String path = "gcp";
        @Nullable
        private String role;
        @Nullable
        private GcpCredentialSupplier credentialSupplier;
        private Duration jwtValidity = Duration.ofMinutes(15L);
        private Clock clock = Clock.systemDefaultZone();
        private GcpServiceAccountIdAccessor serviceAccountIdAccessor = DefaultGcpCredentialAccessors.INSTANCE;
        private GcpProjectIdAccessor projectIdAccessor = DefaultGcpCredentialAccessors.INSTANCE;

        GcpIamAuthenticationOptionsBuilder() {
        }

        public GcpIamAuthenticationOptionsBuilder path(String path) {
            Assert.hasText((String)path, (String)"Path must not be empty");
            this.path = path;
            return this;
        }

        public GcpIamAuthenticationOptionsBuilder credential(GoogleCredential credential) {
            Assert.notNull((Object)credential, (String)"Credential must not be null");
            return this.credentialSupplier(() -> credential);
        }

        public GcpIamAuthenticationOptionsBuilder credentialSupplier(GcpCredentialSupplier credentialSupplier) {
            Assert.notNull((Object)credentialSupplier, (String)"GcpCredentialSupplier must not be null");
            this.credentialSupplier = credentialSupplier;
            return this;
        }

        public GcpIamAuthenticationOptionsBuilder serviceAccountId(String serviceAccountId) {
            Assert.notNull((Object)serviceAccountId, (String)"Service account id may not be null");
            return this.serviceAccountIdAccessor(credential -> serviceAccountId);
        }

        GcpIamAuthenticationOptionsBuilder serviceAccountIdAccessor(GcpServiceAccountIdAccessor serviceAccountIdAccessor) {
            Assert.notNull((Object)serviceAccountIdAccessor, (String)"GcpServiceAccountIdAccessor must not be null");
            this.serviceAccountIdAccessor = serviceAccountIdAccessor;
            return this;
        }

        public GcpIamAuthenticationOptionsBuilder projectId(String projectId) {
            Assert.notNull((Object)projectId, (String)"GCP project id must not be null");
            return this.projectIdAccessor(credential -> projectId);
        }

        GcpIamAuthenticationOptionsBuilder projectIdAccessor(GcpProjectIdAccessor projectIdAccessor) {
            Assert.notNull((Object)projectIdAccessor, (String)"GcpProjectIdAccessor must not be null");
            this.projectIdAccessor = projectIdAccessor;
            return this;
        }

        public GcpIamAuthenticationOptionsBuilder role(String role) {
            Assert.hasText((String)role, (String)"Role must not be null or empty");
            this.role = role;
            return this;
        }

        public GcpIamAuthenticationOptionsBuilder jwtValidity(Duration jwtValidity) {
            Assert.notNull((Object)jwtValidity, (String)"JWT validity duration must not be null");
            this.jwtValidity = jwtValidity;
            return this;
        }

        public GcpIamAuthenticationOptionsBuilder clock(Clock clock) {
            Assert.notNull((Object)clock, (String)"Clock must not be null");
            this.clock = clock;
            return this;
        }

        public GcpIamAuthenticationOptions build() {
            Assert.notNull((Object)this.credentialSupplier, (String)"GcpCredentialSupplier must not be null");
            Assert.notNull((Object)this.role, (String)"Role must not be null");
            return new GcpIamAuthenticationOptions(this.path, this.credentialSupplier, this.role, this.jwtValidity, this.clock, this.serviceAccountIdAccessor, this.projectIdAccessor);
        }
    }
}

