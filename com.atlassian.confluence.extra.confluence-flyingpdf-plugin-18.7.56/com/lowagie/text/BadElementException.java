/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.DocumentException;

public class BadElementException
extends DocumentException {
    private static final long serialVersionUID = -799006030723822254L;

    public BadElementException(Exception ex) {
        super(ex);
    }

    BadElementException() {
    }

    public BadElementException(String message) {
        super(message);
    }
}

