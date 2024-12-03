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

public class ClientCertificateAuthenticationOptions {
    public static final String DEFAULT_CERT_PATH = "cert";
    private final String path;
    @Nullable
    private final String role;

    private ClientCertificateAuthenticationOptions(String path, @Nullable String role) {
        this.path = path;
        this.role = role;
    }

    public static ClientCertificateAuthenticationOptionsBuilder builder() {
        return new ClientCertificateAuthenticationOptionsBuilder();
    }

    public String getPath() {
        return this.path;
    }

    @Nullable
    public String getRole() {
        return this.role;
    }

    public static class ClientCertificateAuthenticationOptionsBuilder {
        private String path = "cert";
        @Nullable
        private String role;

        ClientCertificateAuthenticationOptionsBuilder() {
        }

        public ClientCertificateAuthenticationOptionsBuilder path(String path) {
            Assert.hasText((String)path, (String)"Path must not be empty");
            this.path = path;
            return this;
        }

        public ClientCertificateAuthenticationOptionsBuilder role(String name) {
            Assert.hasText((String)name, (String)"Role must not be empty");
            this.role = name;
            return this;
        }

        public ClientCertificateAuthenticationOptions build() {
            return new ClientCertificateAuthenticationOptions(this.path, this.role);
        }
    }
}

