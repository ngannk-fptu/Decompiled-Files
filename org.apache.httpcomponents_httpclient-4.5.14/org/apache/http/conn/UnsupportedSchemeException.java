/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.conn;

import java.io.IOException;

public class UnsupportedSchemeException
extends IOException {
    private static final long serialVersionUID = 3597127619218687636L;

    public UnsupportedSchemeException(String message) {
        super(message);
    }
}

