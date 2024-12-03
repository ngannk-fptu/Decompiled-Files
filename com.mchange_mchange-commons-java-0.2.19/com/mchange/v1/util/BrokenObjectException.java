/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

public class BrokenObjectException
extends Exception {
    Object broken;

    public BrokenObjectException(Object object, String string) {
        super(string);
        this.broken = object;
    }

    public BrokenObjectException(Object object) {
        this.broken = object;
    }

    public Object getBrokenObject() {
        return this.broken;
    }
}

