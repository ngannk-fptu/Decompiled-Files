/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.exception;

import org.xml.sax.SAXException;

public class RuntimeSAXException
extends RuntimeException {
    public RuntimeSAXException(SAXException t) {
        super(t);
    }
}

