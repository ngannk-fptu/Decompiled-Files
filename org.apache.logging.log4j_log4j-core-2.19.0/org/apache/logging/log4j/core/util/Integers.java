/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.util.Strings
 */
package org.apache.logging.log4j.core.util;

import org.apache.logging.log4j.util.Strings;

public final class Integers {
    private static final int BITS_PER_INT = 32;

    private Integers() {
    }

    public static int parseInt(String s, int defaultValue) {
        return Strings.isEmpty((CharSequence)s) ? defaultValue : Integer.parseInt(s.trim());
    }

    public static int parseInt(String s) {
        return Integers.parseInt(s, 0);
    }

    public static int ceilingNextPowerOfTwo(int x) {
        return 1 << 32 - Integer.numberOfLeadingZeros(x - 1);
    }
}

