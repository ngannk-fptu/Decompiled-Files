/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.vault.support;

import org.springframework.lang.Nullable;
import org.springframework.vault.VaultException;
import org.springframework.vault.support.AbstractResult;
import org.springframework.vault.support.Ciphertext;

public class VaultEncryptionResult
extends AbstractResult<Ciphertext> {
    @Nullable
    private final Ciphertext cipherText;

    public VaultEncryptionResult(Ciphertext cipherText) {
        this.cipherText = cipherText;
    }

    public VaultEncryptionResult(VaultException exception) {
        super(exception);
        this.cipherText = null;
    }

    @Override
    @Nullable
    protected Ciphertext get0() {
        return this.cipherText;
    }
}

