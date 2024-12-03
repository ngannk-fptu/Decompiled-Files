/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$Nullable;
import java.util.Arrays;

public final class $Objects {
    private $Objects() {
    }

    public static boolean equal(@$Nullable Object a, @$Nullable Object b) {
        return a == b || a != null && a.equals(b);
    }

    public static int hashCode(Object ... objects) {
        return Arrays.hashCode(objects);
    }
}

