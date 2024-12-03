/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import java.util.Iterator;

public interface RangeIterator
extends Iterator {
    public void skip(long var1);

    public long getSize();

    public long getPosition();
}

