/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.openssl;

import java.io.IOException;

public class PEMException
extends IOException {
    Exception underlying;

    public PEMException(String message) {
        super(message);
    }

    public PEMException(String message, Exception underlying) {
        super(message);
        this.underlying = underlying;
    }

    public Exception getUnderlyingException() {
        return this.underlying;
    }

    @Override
    public Throwable getCause() {
        return this.underlying;
    }
}

