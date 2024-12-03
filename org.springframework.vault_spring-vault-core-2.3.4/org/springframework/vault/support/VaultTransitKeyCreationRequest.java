/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  org.springframework.util.Assert
 */
package org.springframework.vault.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

public class VaultTransitKeyCreationRequest {
    private final boolean derived;
    @JsonProperty(value="type")
    private final String type;
    @JsonProperty(value="convergent_encryption")
    private final boolean convergentEncryption;
    private final boolean exportable;

    private VaultTransitKeyCreationRequest(boolean derived, String type, boolean convergentEncryption, boolean exportable) {
        this.derived = derived;
        this.type = type;
        this.convergentEncryption = convergentEncryption;
        this.exportable = exportable;
    }

    public static VaultTransitKeyCreationRequest ofKeyType(String type) {
        return VaultTransitKeyCreationRequest.builder().type(type).build();
    }

    public static VaultTransitKeyCreationRequestBuilder builder() {
        return new VaultTransitKeyCreationRequestBuilder();
    }

    public boolean getDerived() {
        return this.derived;
    }

    public boolean getConvergentEncryption() {
        return this.convergentEncryption;
    }

    public String getType() {
        return this.type;
    }

    public boolean getExportable() {
        return this.exportable;
    }

    public static class VaultTransitKeyCreationRequestBuilder {
        private boolean derived;
        private String type = "aes256-gcm96";
        private boolean convergentEncryption;
        private boolean exportable;

        VaultTransitKeyCreationRequestBuilder() {
        }

        public VaultTransitKeyCreationRequestBuilder type(String type) {
            Assert.hasText((String)type, (String)"Type must not be null or empty");
            this.type = type;
            return this;
        }

        public VaultTransitKeyCreationRequestBuilder derived(boolean derived) {
            this.derived = derived;
            return this;
        }

        public VaultTransitKeyCreationRequestBuilder convergentEncryption(boolean convergentEncryption) {
            this.convergentEncryption = convergentEncryption;
            return this;
        }

        public VaultTransitKeyCreationRequestBuilder exportable(boolean exportable) {
            this.exportable = exportable;
            return this;
        }

        public VaultTransitKeyCreationRequest build() {
            Assert.hasText((String)this.type, (String)"Type must not be empty");
            return new VaultTransitKeyCreationRequest(this.derived, this.type, this.convergentEncryption, this.exportable);
        }
    }
}

