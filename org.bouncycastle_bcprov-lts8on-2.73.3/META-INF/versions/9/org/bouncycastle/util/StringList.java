/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

import org.bouncycastle.util.Iterable;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface StringList
extends Iterable<String> {
    public boolean add(String var1);

    public String get(int var1);

    public int size();

    public String[] toStringArray();

    public String[] toStringArray(int var1, int var2);
}

