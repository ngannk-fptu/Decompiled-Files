/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.raw;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class Nat384 {
    public static void mul(int[] x, int[] y, int[] zz) {
        Nat192.mul(x, y, zz);
        Nat192.mul(x, 6, y, 6, zz, 12);
        int c18 = Nat192.addToEachOther(zz, 6, zz, 12);
        int c12 = c18 + Nat192.addTo(zz, 0, zz, 6, 0);
        c18 += Nat192.addTo(zz, 18, zz, 12, c12);
        int[] dx = Nat192.create();
        int[] dy = Nat192.create();
        boolean neg = Nat192.diff(x, 6, x, 0, dx, 0) != Nat192.diff(y, 6, y, 0, dy, 0);
        int[] tt = Nat192.createExt();
        Nat192.mul(dx, dy, tt);
        Nat.addWordAt(24, c18 += neg ? Nat.addTo(12, tt, 0, zz, 6) : Nat.subFrom(12, tt, 0, zz, 6), zz, 18);
    }

    public static void square(int[] x, int[] zz) {
        Nat192.square(x, zz);
        Nat192.square(x, 6, zz, 12);
        int c18 = Nat192.addToEachOther(zz, 6, zz, 12);
        int c12 = c18 + Nat192.addTo(zz, 0, zz, 6, 0);
        c18 += Nat192.addTo(zz, 18, zz, 12, c12);
        int[] dx = Nat192.create();
        Nat192.diff(x, 6, x, 0, dx, 0);
        int[] tt = Nat192.createExt();
        Nat192.square(dx, tt);
        Nat.addWordAt(24, c18 += Nat.subFrom(12, tt, 0, zz, 6), zz, 18);
    }
}

