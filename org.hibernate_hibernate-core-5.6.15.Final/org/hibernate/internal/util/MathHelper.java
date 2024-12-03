/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util;

public final class MathHelper {
    private MathHelper() {
    }

    public static int ceilingPowerOfTwo(int value) {
        return 1 << -Integer.numberOfLeadingZeros(value - 1);
    }
}

