/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.crypto.codec.Utf8
 *  org.springframework.security.crypto.encrypt.BytesEncryptor
 */
package org.springframework.vault.security;

import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.support.Ciphertext;
import org.springframework.vault.support.Plaintext;

public class VaultBytesEncryptor
implements BytesEncryptor {
    private final VaultTransitOperations transitOperations;
    private final String keyName;

    public VaultBytesEncryptor(VaultTransitOperations transitOperations, String keyName) {
        Assert.notNull((Object)transitOperations, "VaultTransitOperations must not be null");
        Assert.hasText(keyName, "Key name must not be null or empty");
        this.transitOperations = transitOperations;
        this.keyName = keyName;
    }

    public byte[] encrypt(byte[] plaintext) {
        Assert.notNull((Object)plaintext, "Plaintext must not be null");
        Assert.isTrue(!ObjectUtils.isEmpty((Object)plaintext), "Plaintext must not be empty");
        Ciphertext ciphertext = this.transitOperations.encrypt(this.keyName, Plaintext.of(plaintext));
        return Utf8.encode((CharSequence)ciphertext.getCiphertext());
    }

    public byte[] decrypt(byte[] ciphertext) {
        Assert.notNull((Object)ciphertext, "Ciphertext must not be null");
        Assert.isTrue(!ObjectUtils.isEmpty((Object)ciphertext), "Ciphertext must not be empty");
        Plaintext plaintext = this.transitOperations.decrypt(this.keyName, Ciphertext.of(Utf8.decode((byte[])ciphertext)));
        return plaintext.getPlaintext();
    }
}

