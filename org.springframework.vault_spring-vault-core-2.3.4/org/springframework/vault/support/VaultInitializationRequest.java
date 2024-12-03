/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package org.springframework.vault.support;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VaultInitializationRequest {
    @JsonProperty(value="secret_shares")
    private final int secretShares;
    @JsonProperty(value="secret_threshold")
    private final int secretThreshold;

    private VaultInitializationRequest(int secretShares, int secretThreshold) {
        this.secretShares = secretShares;
        this.secretThreshold = secretThreshold;
    }

    public static VaultInitializationRequest create(int secretShares, int secretThreshold) {
        return new VaultInitializationRequest(secretShares, secretThreshold);
    }

    public int getSecretShares() {
        return this.secretShares;
    }

    public int getSecretThreshold() {
        return this.secretThreshold;
    }
}

