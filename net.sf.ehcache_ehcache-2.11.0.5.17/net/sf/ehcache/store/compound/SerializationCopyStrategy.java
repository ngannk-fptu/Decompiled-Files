/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.compound;

import net.sf.ehcache.Element;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;
import net.sf.ehcache.store.compound.ReadWriteSerializationCopyStrategy;

public class SerializationCopyStrategy
implements ReadWriteCopyStrategy<Element> {
    private static final long serialVersionUID = -7932063007392582487L;
    private final ReadWriteSerializationCopyStrategy copyStrategy = new ReadWriteSerializationCopyStrategy();

    @Override
    public Element copyForWrite(Element value, ClassLoader loader) {
        return this.copyStrategy.copyForRead(this.copyStrategy.copyForWrite(value, loader), loader);
    }

    @Override
    public Element copyForRead(Element storedValue, ClassLoader loader) {
        return this.copyStrategy.copyForRead(this.copyStrategy.copyForWrite(storedValue, loader), loader);
    }
}

