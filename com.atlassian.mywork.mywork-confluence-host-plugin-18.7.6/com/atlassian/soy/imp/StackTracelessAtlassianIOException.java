/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.soy.imp;

import java.io.IOException;

public class StackTracelessAtlassianIOException
extends IOException {
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}

