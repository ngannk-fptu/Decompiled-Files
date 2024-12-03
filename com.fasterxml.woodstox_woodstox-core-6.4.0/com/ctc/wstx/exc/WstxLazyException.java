/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.exc;

import javax.xml.stream.XMLStreamException;

public class WstxLazyException
extends RuntimeException {
    final XMLStreamException mOrig;

    public WstxLazyException(XMLStreamException origEx) {
        super(origEx.getMessage(), origEx);
        this.mOrig = origEx;
    }

    public static void throwLazily(XMLStreamException ex) throws WstxLazyException {
        throw new WstxLazyException(ex);
    }

    @Override
    public String getMessage() {
        return "[" + this.getClass().getName() + "] " + this.mOrig.getMessage();
    }

    @Override
    public String toString() {
        return "[" + this.getClass().getName() + "] " + this.mOrig.toString();
    }
}

