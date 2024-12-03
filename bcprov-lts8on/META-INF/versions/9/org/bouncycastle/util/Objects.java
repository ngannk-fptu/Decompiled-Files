/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class Objects {
    public static boolean areEqual(Object a, Object b) {
        return a == b || null != a && null != b && a.equals(b);
    }

    public static int hashCode(Object obj) {
        return null == obj ? 0 : obj.hashCode();
    }
}

