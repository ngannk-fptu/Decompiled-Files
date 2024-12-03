/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.spi.db;

import com.sun.xml.ws.spi.db.DatabindingException;

public interface PropertyAccessor<B, V> {
    public V get(B var1) throws DatabindingException;

    public void set(B var1, V var2) throws DatabindingException;
}

