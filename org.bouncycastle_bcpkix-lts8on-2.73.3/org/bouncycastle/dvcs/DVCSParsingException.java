/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.dvcs;

import org.bouncycastle.dvcs.DVCSException;

public class DVCSParsingException
extends DVCSException {
    private static final long serialVersionUID = -7895880961377691266L;

    public DVCSParsingException(String message) {
        super(message);
    }

    public DVCSParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}

