/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.crypto.keygen.BytesKeyGenerator
 */
package org.springframework.vault.security;

import java.util.Collections;
import java.util.Map;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponse;

public class VaultBytesKeyGenerator
implements BytesKeyGenerator {
    private final VaultOperations vaultOperations;
    private final int length;
    private String transitPath;

    public VaultBytesKeyGenerator(VaultOperations vaultOperations) {
        this(vaultOperations, "transit", 32);
    }

    public VaultBytesKeyGenerator(VaultOperations vaultOperations, String transitPath, int length) {
        Assert.notNull((Object)vaultOperations, "VaultOperations must not be null");
        Assert.hasText(transitPath, "Transit path must not be null or empty");
        Assert.isTrue(length > 0, "Byte count must be greater zero");
        this.vaultOperations = vaultOperations;
        this.transitPath = transitPath;
        this.length = length;
    }

    public int getKeyLength() {
        return this.length;
    }

    public byte[] generateKey() {
        VaultResponse response = this.vaultOperations.write(String.format("%s/random/%d", this.transitPath, this.getKeyLength()), Collections.singletonMap("format", "base64"));
        String randomBytes = (String)((Map)response.getRequiredData()).get("random_bytes");
        return Base64Utils.decodeFromString(randomBytes);
    }
}

