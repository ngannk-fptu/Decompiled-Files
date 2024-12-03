/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.VaultException;
import org.springframework.vault.support.AbstractResult;
import org.springframework.vault.support.Plaintext;

public class VaultDecryptionResult
extends AbstractResult<Plaintext> {
    @Nullable
    private final Plaintext plaintext;

    public VaultDecryptionResult(Plaintext plaintext) {
        Assert.notNull((Object)plaintext, "Plaintext must not be null");
        this.plaintext = plaintext;
    }

    public VaultDecryptionResult(VaultException exception) {
        super(exception);
        this.plaintext = null;
    }

    @Override
    @Nullable
    protected Plaintext get0() {
        return this.plaintext;
    }

    @Nullable
    public String getAsString() {
        Plaintext plaintext = (Plaintext)this.get();
        return plaintext == null ? null : plaintext.asString();
    }
}

