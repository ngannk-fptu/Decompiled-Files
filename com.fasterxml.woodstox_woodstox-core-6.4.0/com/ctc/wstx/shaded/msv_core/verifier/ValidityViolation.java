/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier;

import com.ctc.wstx.shaded.msv_core.verifier.ErrorInfo;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public class ValidityViolation
extends SAXParseException {
    private ErrorInfo errorInfo;

    public ErrorInfo getErrorInfo() {
        return this.errorInfo;
    }

    public ValidityViolation(Locator loc, String msg, ErrorInfo ei) {
        super(msg, loc);
        this.errorInfo = ei;
    }
}

