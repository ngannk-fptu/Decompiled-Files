/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import com.sun.xml.txw2.TxwException;

public class IllegalSignatureException
extends TxwException {
    private static final long serialVersionUID = 1L;

    public IllegalSignatureException(String message) {
        super(message);
    }

    public IllegalSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalSignatureException(Throwable cause) {
        super(cause);
    }
}

