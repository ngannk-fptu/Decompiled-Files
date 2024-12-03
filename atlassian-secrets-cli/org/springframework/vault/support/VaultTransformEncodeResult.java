/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import org.springframework.lang.Nullable;
import org.springframework.vault.VaultException;
import org.springframework.vault.support.AbstractResult;
import org.springframework.vault.support.TransformCiphertext;

public class VaultTransformEncodeResult
extends AbstractResult<TransformCiphertext> {
    @Nullable
    private final TransformCiphertext cipherText;

    public VaultTransformEncodeResult(TransformCiphertext cipherText) {
        this.cipherText = cipherText;
    }

    public VaultTransformEncodeResult(VaultException exception) {
        super(exception);
        this.cipherText = null;
    }

    @Override
    @Nullable
    protected TransformCiphertext get0() {
        return this.cipherText;
    }

    @Nullable
    public String getAsString() {
        TransformCiphertext ciphertext = (TransformCiphertext)this.get();
        return ciphertext == null ? null : ciphertext.getCiphertext();
    }
}

