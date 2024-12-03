/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.vault.authentication;

import java.time.Clock;
import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.vault.authentication.ResourceCredentialSupplier;

public class PcfAuthenticationOptions {
    public static final String DEFAULT_PCF_AUTHENTICATION_PATH = "pcf";
    private final String path;
    private final String role;
    private final Clock clock;
    private final Supplier<String> instanceCertSupplier;
    private final Supplier<String> instanceKeySupplier;

    private PcfAuthenticationOptions(String path, String role, Clock clock, Supplier<String> instanceCertSupplier, Supplier<String> instanceKeySupplier) {
        this.path = path;
        this.role = role;
        this.clock = clock;
        this.instanceCertSupplier = instanceCertSupplier;
        this.instanceKeySupplier = instanceKeySupplier;
    }

    public static PcfAuthenticationOptionsBuilder builder() {
        return new PcfAuthenticationOptionsBuilder();
    }

    public String getPath() {
        return this.path;
    }

    public String getRole() {
        return this.role;
    }

    public Clock getClock() {
        return this.clock;
    }

    public Supplier<String> getInstanceCertSupplier() {
        return this.instanceCertSupplier;
    }

    public Supplier<String> getInstanceKeySupplier() {
        return this.instanceKeySupplier;
    }

    public static class PcfAuthenticationOptionsBuilder {
        private String path = "pcf";
        private Clock clock = Clock.systemUTC();
        @Nullable
        private String role;
        @Nullable
        private Supplier<String> instanceCertSupplier;
        @Nullable
        private Supplier<String> instanceKeySupplier;

        PcfAuthenticationOptionsBuilder() {
        }

        public PcfAuthenticationOptionsBuilder path(String path) {
            Assert.hasText((String)path, (String)"Path must not be empty");
            this.path = path;
            return this;
        }

        public PcfAuthenticationOptionsBuilder role(String role) {
            Assert.hasText((String)role, (String)"Role must not be empty");
            this.role = role;
            return this;
        }

        public PcfAuthenticationOptionsBuilder clock(Clock clock) {
            Assert.notNull((Object)clock, (String)"Clock must not be null");
            this.clock = clock;
            return this;
        }

        public PcfAuthenticationOptionsBuilder instanceCertificate(Supplier<String> instanceCertSupplier) {
            Assert.notNull(instanceCertSupplier, (String)"Instance certificate supplier must not be null");
            this.instanceCertSupplier = instanceCertSupplier;
            return this;
        }

        public PcfAuthenticationOptionsBuilder instanceKey(Supplier<String> instanceKeySupplier) {
            Assert.notNull(instanceKeySupplier, (String)"Instance certificate supplier must not be null");
            this.instanceKeySupplier = instanceKeySupplier;
            return this;
        }

        public PcfAuthenticationOptions build() {
            ResourceCredentialSupplier instanceKeySupplier;
            Assert.notNull((Object)this.role, (String)"Role must not be null");
            ResourceCredentialSupplier instanceCertSupplier = this.instanceCertSupplier;
            if (instanceCertSupplier == null) {
                instanceCertSupplier = new ResourceCredentialSupplier(PcfAuthenticationOptionsBuilder.resolveEnvVariable("CF_INSTANCE_CERT"));
            }
            if ((instanceKeySupplier = this.instanceKeySupplier) == null) {
                instanceKeySupplier = new ResourceCredentialSupplier(PcfAuthenticationOptionsBuilder.resolveEnvVariable("CF_INSTANCE_KEY"));
            }
            return new PcfAuthenticationOptions(this.path, this.role, this.clock, instanceCertSupplier, instanceKeySupplier);
        }

        private static String resolveEnvVariable(String name) {
            String value = System.getenv(name);
            if (StringUtils.isEmpty((Object)value)) {
                throw new IllegalStateException(String.format("Environment variable %s not set", name));
            }
            return value;
        }
    }
}

