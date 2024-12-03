/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import java.io.InvalidClassException;

public class UnknownStreamClassException
extends InvalidClassException {
    public UnknownStreamClassException(ClassNotFoundException classNotFoundException) {
        super(classNotFoundException.getMessage());
    }
}

