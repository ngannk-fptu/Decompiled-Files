/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.VaultException;
import org.springframework.vault.support.AbstractResult;
import org.springframework.vault.support.TransformPlaintext;

public class VaultTransformDecodeResult
extends AbstractResult<TransformPlaintext> {
    @Nullable
    private final TransformPlaintext plaintext;

    public VaultTransformDecodeResult(TransformPlaintext plaintext) {
        Assert.notNull((Object)plaintext, "Plaintext must not be null");
        this.plaintext = plaintext;
    }

    public VaultTransformDecodeResult(VaultException exception) {
        super(exception);
        this.plaintext = null;
    }

    @Override
    @Nullable
    protected TransformPlaintext get0() {
        return this.plaintext;
    }

    @Nullable
    public String getAsString() {
        TransformPlaintext plaintext = (TransformPlaintext)this.get();
        return plaintext == null ? null : plaintext.asString();
    }
}

