/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.compound;

import net.sf.ehcache.Element;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;

public class NullReadWriteCopyStrategy
implements ReadWriteCopyStrategy<Element> {
    private static final long serialVersionUID = -3210079128116741621L;

    @Override
    public Element copyForWrite(Element value, ClassLoader loader) {
        return value;
    }

    @Override
    public Element copyForRead(Element storedValue, ClassLoader loader) {
        return storedValue;
    }
}

