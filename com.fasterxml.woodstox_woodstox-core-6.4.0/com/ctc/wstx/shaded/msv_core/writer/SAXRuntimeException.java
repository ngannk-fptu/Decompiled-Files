/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.writer;

import org.xml.sax.SAXException;

public class SAXRuntimeException
extends RuntimeException {
    public final SAXException e;

    public SAXRuntimeException(SAXException e) {
        this.e = e;
    }
}

