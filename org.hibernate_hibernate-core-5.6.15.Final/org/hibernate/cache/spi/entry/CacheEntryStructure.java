/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.entry;

import org.hibernate.engine.spi.SessionFactoryImplementor;

public interface CacheEntryStructure {
    public Object structure(Object var1);

    public Object destructure(Object var1, SessionFactoryImplementor var2);
}

