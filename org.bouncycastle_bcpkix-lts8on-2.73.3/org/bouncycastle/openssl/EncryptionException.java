/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.openssl;

import org.bouncycastle.openssl.PEMException;

public class EncryptionException
extends PEMException {
    private Throwable cause;

    public EncryptionException(String msg) {
        super(msg);
    }

    public EncryptionException(String msg, Throwable ex) {
        super(msg);
        this.cause = ex;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

