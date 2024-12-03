/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.dvcs;

import org.bouncycastle.dvcs.DVCSException;

public class DVCSConstructionException
extends DVCSException {
    private static final long serialVersionUID = 660035299653583980L;

    public DVCSConstructionException(String message) {
        super(message);
    }

    public DVCSConstructionException(String message, Throwable cause) {
        super(message, cause);
    }
}

