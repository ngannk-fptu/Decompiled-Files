/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.util;

import java.io.IOException;
import org.xml.sax.SAXException;

public class SAXIOException
extends IOException {
    protected SAXException saxe;

    public SAXIOException(SAXException saxe) {
        super(saxe.getMessage());
        this.saxe = saxe;
    }

    public SAXException getSAXException() {
        return this.saxe;
    }

    @Override
    public Throwable getCause() {
        return this.saxe;
    }
}

