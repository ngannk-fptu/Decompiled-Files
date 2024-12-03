/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.util.AssertException;

public class DebugUtils {
    private DebugUtils() {
    }

    public static void myAssert(boolean bl) {
        if (!bl) {
            throw new AssertException();
        }
    }

    public static void myAssert(boolean bl, String string) {
        if (!bl) {
            throw new AssertException(string);
        }
    }
}

