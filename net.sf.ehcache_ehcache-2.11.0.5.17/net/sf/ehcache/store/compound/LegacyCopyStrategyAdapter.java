/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.compound;

import net.sf.ehcache.Element;
import net.sf.ehcache.store.compound.CopyStrategy;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;

public class LegacyCopyStrategyAdapter
implements ReadWriteCopyStrategy<Element> {
    private static final long serialVersionUID = -6986893869400882078L;
    private final CopyStrategy legacyCopyStrategy;

    public LegacyCopyStrategyAdapter(CopyStrategy legacyCopyStrategy) {
        this.legacyCopyStrategy = legacyCopyStrategy;
    }

    @Override
    public Element copyForWrite(Element value, ClassLoader loader) {
        return this.legacyCopyStrategy.copy(value);
    }

    @Override
    public Element copyForRead(Element storedValue, ClassLoader loader) {
        return this.legacyCopyStrategy.copy(storedValue);
    }
}

