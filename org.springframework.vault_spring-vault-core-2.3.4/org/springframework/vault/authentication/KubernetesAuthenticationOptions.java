/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.vault.authentication;

import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.KubernetesServiceAccountTokenFile;

public class KubernetesAuthenticationOptions {
    public static final String DEFAULT_KUBERNETES_AUTHENTICATION_PATH = "kubernetes";
    private final String path;
    private final String role;
    private final Supplier<String> jwtSupplier;

    private KubernetesAuthenticationOptions(String path, String role, Supplier<String> jwtSupplier) {
        this.path = path;
        this.role = role;
        this.jwtSupplier = jwtSupplier;
    }

    public static KubernetesAuthenticationOptionsBuilder builder() {
        return new KubernetesAuthenticationOptionsBuilder();
    }

    public String getPath() {
        return this.path;
    }

    public String getRole() {
        return this.role;
    }

    public Supplier<String> getJwtSupplier() {
        return this.jwtSupplier;
    }

    public static class KubernetesAuthenticationOptionsBuilder {
        private String path = "kubernetes";
        @Nullable
        private String role;
        @Nullable
        private Supplier<String> jwtSupplier;

        public KubernetesAuthenticationOptionsBuilder path(String path) {
            Assert.hasText((String)path, (String)"Path must not be empty");
            this.path = path;
            return this;
        }

        public KubernetesAuthenticationOptionsBuilder role(String role) {
            Assert.hasText((String)role, (String)"Role must not be empty");
            this.role = role;
            return this;
        }

        public KubernetesAuthenticationOptionsBuilder jwtSupplier(Supplier<String> jwtSupplier) {
            Assert.notNull(jwtSupplier, (String)"JwtSupplier must not be null");
            this.jwtSupplier = jwtSupplier;
            return this;
        }

        public KubernetesAuthenticationOptions build() {
            Assert.notNull((Object)this.role, (String)"Role must not be null");
            return new KubernetesAuthenticationOptions(this.path, this.role, this.jwtSupplier == null ? new KubernetesServiceAccountTokenFile() : this.jwtSupplier);
        }
    }
}

