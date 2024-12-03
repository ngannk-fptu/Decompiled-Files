/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface Memoable {
    public Memoable copy();

    public void reset(Memoable var1);
}

