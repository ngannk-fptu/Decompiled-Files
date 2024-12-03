/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import java.io.IOException;

public class FakeIOException
extends IOException {
    private static final long serialVersionUID = 1L;

    public FakeIOException(String message) {
        super(message);
    }
}

