/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;

public class CMSStreamException
extends IOException {
    private final Throwable underlying;

    CMSStreamException(String msg) {
        super(msg);
        this.underlying = null;
    }

    CMSStreamException(String msg, Throwable underlying) {
        super(msg);
        this.underlying = underlying;
    }

    @Override
    public Throwable getCause() {
        return this.underlying;
    }
}

