/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

import java.util.Iterator;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public interface Iterable<T>
extends java.lang.Iterable<T> {
    @Override
    public Iterator<T> iterator();
}

