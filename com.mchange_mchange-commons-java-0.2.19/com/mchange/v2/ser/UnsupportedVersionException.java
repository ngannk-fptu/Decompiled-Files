/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.ser;

import java.io.InvalidClassException;

public class UnsupportedVersionException
extends InvalidClassException {
    public UnsupportedVersionException(String string) {
        super(string);
    }

    public UnsupportedVersionException(Object object, int n) {
        this(object.getClass().getName() + " -- unsupported version: " + n);
    }
}

