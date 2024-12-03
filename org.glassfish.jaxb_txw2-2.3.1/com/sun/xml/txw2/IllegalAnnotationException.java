/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import com.sun.xml.txw2.TxwException;

public class IllegalAnnotationException
extends TxwException {
    private static final long serialVersionUID = 1L;

    public IllegalAnnotationException(String message) {
        super(message);
    }

    public IllegalAnnotationException(Throwable cause) {
        super(cause);
    }

    public IllegalAnnotationException(String message, Throwable cause) {
        super(message, cause);
    }
}

