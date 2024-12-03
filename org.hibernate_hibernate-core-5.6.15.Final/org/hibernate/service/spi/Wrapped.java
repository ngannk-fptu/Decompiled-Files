/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.service.spi;

public interface Wrapped {
    public boolean isUnwrappableAs(Class var1);

    public <T> T unwrap(Class<T> var1);
}

