/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.compound;

import net.sf.ehcache.Element;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;
import net.sf.ehcache.store.compound.ReadWriteSerializationCopyStrategy;

public class ImmutableValueElementCopyStrategy
implements ReadWriteCopyStrategy<Element> {
    private static final long serialVersionUID = 6938731518478806173L;
    private final ReadWriteSerializationCopyStrategy copyStrategy = new ReadWriteSerializationCopyStrategy();

    @Override
    public Element copyForWrite(Element value, ClassLoader loader) {
        if (value == null) {
            return null;
        }
        return this.copyStrategy.duplicateElementWithNewValue(value, value.getObjectValue());
    }

    @Override
    public Element copyForRead(Element storedValue, ClassLoader loader) {
        if (storedValue == null) {
            return null;
        }
        return this.copyStrategy.duplicateElementWithNewValue(storedValue, storedValue.getObjectValue());
    }
}

