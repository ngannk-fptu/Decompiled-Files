/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sax;

import org.xml.sax.SAXException;

public final class WrappedSaxException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    protected final SAXException mCause;

    public WrappedSaxException(SAXException cause) {
        this.mCause = cause;
    }

    public SAXException getSaxException() {
        return this.mCause;
    }

    public String toString() {
        return this.mCause.toString();
    }
}

