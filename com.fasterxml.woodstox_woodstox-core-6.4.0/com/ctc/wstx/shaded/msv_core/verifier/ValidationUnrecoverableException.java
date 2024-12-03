/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier;

import org.xml.sax.SAXParseException;

public class ValidationUnrecoverableException
extends SAXParseException {
    public ValidationUnrecoverableException(SAXParseException vv) {
        super(vv.getMessage(), vv.getPublicId(), vv.getSystemId(), vv.getLineNumber(), vv.getColumnNumber(), vv);
    }
}

