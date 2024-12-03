/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.spi;

import org.hibernate.engine.jdbc.spi.JdbcWrapper;

public interface InvalidatableWrapper<T>
extends JdbcWrapper<T> {
    public void invalidate();
}

