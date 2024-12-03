/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.exc;

import com.ctc.wstx.util.ExceptionUtil;
import javax.xml.stream.XMLStreamException;

public class WstxLazyException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    final XMLStreamException mOrig;

    public WstxLazyException(XMLStreamException origEx) {
        super(origEx.getMessage());
        this.mOrig = origEx;
        ExceptionUtil.setInitCause(this, origEx);
    }

    public static void throwLazily(XMLStreamException ex) throws WstxLazyException {
        throw new WstxLazyException(ex);
    }

    public String getMessage() {
        return "[" + this.getClass().getName() + "] " + this.mOrig.getMessage();
    }

    public String toString() {
        return "[" + this.getClass().getName() + "] " + this.mOrig.toString();
    }
}

