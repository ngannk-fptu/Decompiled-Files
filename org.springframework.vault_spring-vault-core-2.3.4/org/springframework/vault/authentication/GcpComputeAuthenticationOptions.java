/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.vault.authentication;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class GcpComputeAuthenticationOptions {
    public static final String DEFAULT_GCP_AUTHENTICATION_PATH = "gcp";
    private final String path;
    private final String serviceAccount;
    private final String role;

    private GcpComputeAuthenticationOptions(String path, String serviceAccount, String role) {
        this.path = path;
        this.serviceAccount = serviceAccount;
        this.role = role;
    }

    public static GcpComputeAuthenticationOptionsBuilder builder() {
        return new GcpComputeAuthenticationOptionsBuilder();
    }

    public String getPath() {
        return this.path;
    }

    public String getServiceAccount() {
        return this.serviceAccount;
    }

    public String getRole() {
        return this.role;
    }

    public static class GcpComputeAuthenticationOptionsBuilder {
        private String path = "gcp";
        @Nullable
        private String role;
        private String serviceAccount = "default";

        GcpComputeAuthenticationOptionsBuilder() {
        }

        public GcpComputeAuthenticationOptionsBuilder path(String path) {
            Assert.hasText((String)path, (String)"Path must not be empty");
            this.path = path;
            return this;
        }

        public GcpComputeAuthenticationOptionsBuilder serviceAccount(String serviceAccount) {
            Assert.hasText((String)serviceAccount, (String)"Service account must not be null");
            this.serviceAccount = serviceAccount;
            return this;
        }

        public GcpComputeAuthenticationOptionsBuilder role(String role) {
            Assert.hasText((String)role, (String)"Role must not be null or empty");
            this.role = role;
            return this;
        }

        public GcpComputeAuthenticationOptions build() {
            Assert.notNull((Object)this.role, (String)"Role must not be null");
            return new GcpComputeAuthenticationOptions(this.path, this.serviceAccount, this.role);
        }
    }
}

