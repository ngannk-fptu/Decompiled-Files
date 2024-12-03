/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.oauth;

import java.net.URI;
import java.security.PublicKey;
import java.util.Objects;
import net.jcip.annotations.Immutable;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Immutable
public final class Consumer {
    private final String key;
    private final String name;
    private final String description;
    private final SignatureMethod signatureMethod;
    private final PublicKey publicKey;
    private final URI callback;
    private final boolean threeLOAllowed;
    private final boolean twoLOAllowed;
    private final String executingTwoLOUser;
    private final boolean twoLOImpersonationAllowed;

    private Consumer(InstanceBuilder builder) {
        this.key = builder.key;
        this.name = builder.name;
        this.signatureMethod = builder.signatureMethod;
        this.publicKey = builder.publicKey;
        this.description = builder.description;
        this.callback = builder.callback;
        this.threeLOAllowed = builder.threeLOAllowed;
        this.twoLOAllowed = builder.twoLOAllowed;
        this.executingTwoLOUser = builder.executingTwoLOUser;
        this.twoLOImpersonationAllowed = builder.twoLOImpersonationAllowed;
    }

    public static InstanceBuilder key(String key) {
        return new InstanceBuilder(Objects.requireNonNull(key, "key"));
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public SignatureMethod getSignatureMethod() {
        return this.signatureMethod;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public String getDescription() {
        return this.description;
    }

    public URI getCallback() {
        return this.callback;
    }

    public boolean getThreeLOAllowed() {
        return this.threeLOAllowed;
    }

    public boolean getTwoLOAllowed() {
        return this.twoLOAllowed;
    }

    public String getExecutingTwoLOUser() {
        return this.executingTwoLOUser;
    }

    public boolean getTwoLOImpersonationAllowed() {
        return this.twoLOImpersonationAllowed;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("key", (Object)this.key).append("name", (Object)this.name).append("description", (Object)this.description).append("callback", (Object)this.callback).append("signatureMethod", (Object)this.signatureMethod).append("publicKey", (Object)this.publicKey).append("threeLOAllowed", this.threeLOAllowed).append("twoLOAllowed", this.twoLOAllowed).append("executingTwoLOUser", (Object)this.executingTwoLOUser).append("twoLOImpersonationAllowed", this.twoLOImpersonationAllowed).toString();
    }

    public static final class InstanceBuilder {
        private final String key;
        private String name;
        private SignatureMethod signatureMethod;
        private PublicKey publicKey;
        private String description;
        private URI callback;
        private boolean twoLOAllowed;
        private String executingTwoLOUser;
        private boolean twoLOImpersonationAllowed;
        private boolean threeLOAllowed;

        public InstanceBuilder(String key) {
            this.key = key;
            this.threeLOAllowed = true;
        }

        public InstanceBuilder name(String name) {
            this.name = Objects.requireNonNull(name);
            return this;
        }

        public InstanceBuilder signatureMethod(SignatureMethod signatureMethod) {
            this.signatureMethod = Objects.requireNonNull(signatureMethod);
            return this;
        }

        public InstanceBuilder publicKey(PublicKey publicKey) {
            this.signatureMethod = SignatureMethod.RSA_SHA1;
            this.publicKey = Objects.requireNonNull(publicKey);
            return this;
        }

        public InstanceBuilder description(String description) {
            this.description = description;
            return this;
        }

        public InstanceBuilder callback(URI callback) {
            this.callback = callback;
            return this;
        }

        public InstanceBuilder threeLOAllowed(boolean threeLOAllowed) {
            this.threeLOAllowed = threeLOAllowed;
            return this;
        }

        public InstanceBuilder twoLOAllowed(boolean twoLOAllowed) {
            this.twoLOAllowed = twoLOAllowed;
            return this;
        }

        public InstanceBuilder executingTwoLOUser(String executeAsUser) {
            this.executingTwoLOUser = executeAsUser;
            return this;
        }

        public InstanceBuilder twoLOImpersonationAllowed(boolean twoLOImpersonationAllowed) {
            this.twoLOImpersonationAllowed = twoLOImpersonationAllowed;
            return this;
        }

        public Consumer build() {
            Objects.requireNonNull(this.name, "name");
            Objects.requireNonNull(this.signatureMethod, "signatureMethod");
            if (this.signatureMethod == SignatureMethod.RSA_SHA1) {
                Objects.requireNonNull(this.publicKey, "publicKey must be set when the signature method is RSA-SHA1");
            }
            return new Consumer(this);
        }
    }

    public static enum SignatureMethod {
        HMAC_SHA1,
        RSA_SHA1;

    }
}

