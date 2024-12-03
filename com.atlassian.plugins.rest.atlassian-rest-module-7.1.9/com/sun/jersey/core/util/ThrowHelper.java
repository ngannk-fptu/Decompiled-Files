/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.util;

public class ThrowHelper {
    public static <T extends Exception> T withInitCause(Exception cause, T effect) {
        effect.initCause(cause);
        return effect;
    }
}

