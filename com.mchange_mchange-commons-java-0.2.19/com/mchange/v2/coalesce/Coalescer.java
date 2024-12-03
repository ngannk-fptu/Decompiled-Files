/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.coalesce;

import java.util.Iterator;

public interface Coalescer {
    public Object coalesce(Object var1);

    public int countCoalesced();

    public Iterator iterator();
}

