/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.transaction;

import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionException;
import org.springframework.util.Assert;

public class TransactionSystemException
extends TransactionException {
    @Nullable
    private Throwable applicationException;

    public TransactionSystemException(String msg) {
        super(msg);
    }

    public TransactionSystemException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public void initApplicationException(Throwable ex) {
        Assert.notNull((Object)ex, (String)"Application exception must not be null");
        if (this.applicationException != null) {
            throw new IllegalStateException("Already holding an application exception: " + this.applicationException);
        }
        this.applicationException = ex;
    }

    @Nullable
    public final Throwable getApplicationException() {
        return this.applicationException;
    }

    @Nullable
    public Throwable getOriginalException() {
        return this.applicationException != null ? this.applicationException : this.getCause();
    }

    public boolean contains(@Nullable Class<?> exType) {
        return super.contains(exType) || exType != null && exType.isInstance(this.applicationException);
    }
}

