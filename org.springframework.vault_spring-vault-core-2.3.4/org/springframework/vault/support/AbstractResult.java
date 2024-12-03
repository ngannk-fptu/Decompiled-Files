/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.vault.support;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.VaultException;

public abstract class AbstractResult<V> {
    @Nullable
    private final VaultException exception;

    protected AbstractResult() {
        this.exception = null;
    }

    protected AbstractResult(VaultException exception) {
        Assert.notNull((Object)((Object)exception), (String)"VaultException must not be null");
        this.exception = exception;
    }

    public boolean isSuccessful() {
        return this.exception == null;
    }

    @Nullable
    public Exception getCause() {
        return this.exception;
    }

    @Nullable
    public V get() {
        if (this.isSuccessful()) {
            return this.get0();
        }
        throw new VaultException(this.exception.getMessage(), (Throwable)((Object)this.exception));
    }

    @Nullable
    protected abstract V get0();
}

