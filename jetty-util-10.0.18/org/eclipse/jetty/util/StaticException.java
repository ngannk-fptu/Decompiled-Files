/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

public class StaticException
extends Exception {
    public StaticException(String message) {
        this(message, false);
    }

    public StaticException(String message, boolean writableStackTrace) {
        super(message, null, false, writableStackTrace);
    }
}

