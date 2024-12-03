/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import org.hibernate.cache.spi.Region;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface DirectAccessRegion
extends Region {
    public Object getFromCache(Object var1, SharedSessionContractImplementor var2);

    public void putIntoCache(Object var1, Object var2, SharedSessionContractImplementor var3);
}

