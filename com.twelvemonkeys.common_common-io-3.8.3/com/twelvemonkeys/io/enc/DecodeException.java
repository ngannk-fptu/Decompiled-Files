/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io.enc;

import java.io.IOException;

public class DecodeException
extends IOException {
    public DecodeException(String string) {
        super(string);
    }

    public DecodeException(String string, Throwable throwable) {
        super(string);
        this.initCause(throwable);
    }

    public DecodeException(Throwable throwable) {
        this(throwable.getMessage(), throwable);
    }
}

