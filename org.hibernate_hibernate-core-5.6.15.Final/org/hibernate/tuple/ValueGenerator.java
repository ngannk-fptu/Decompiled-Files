/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import org.hibernate.Session;

public interface ValueGenerator<T> {
    public T generateValue(Session var1, Object var2);
}

