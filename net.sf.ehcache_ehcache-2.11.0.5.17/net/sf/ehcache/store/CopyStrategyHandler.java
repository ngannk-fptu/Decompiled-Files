/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import net.sf.ehcache.Element;
import net.sf.ehcache.store.compound.ReadWriteCopyStrategy;

public class CopyStrategyHandler {
    private final boolean copyOnRead;
    private final boolean copyOnWrite;
    private final ReadWriteCopyStrategy<Element> copyStrategy;
    private final ClassLoader loader;

    public CopyStrategyHandler(boolean copyOnRead, boolean copyOnWrite, ReadWriteCopyStrategy<Element> copyStrategy, ClassLoader loader) {
        this.copyOnRead = copyOnRead;
        this.copyOnWrite = copyOnWrite;
        this.copyStrategy = copyStrategy;
        this.loader = loader;
        if (this.isCopyActive() && this.copyStrategy == null) {
            throw new IllegalArgumentException("Copy strategy cannot be null with copyOnRead or copyOnWrite true");
        }
    }

    public Element copyElementForReadIfNeeded(Element element) {
        if (element == null) {
            return null;
        }
        if (this.copyOnRead && this.copyOnWrite) {
            return this.copyStrategy.copyForRead(element, this.loader);
        }
        if (this.copyOnRead) {
            return this.copyStrategy.copyForRead(this.copyStrategy.copyForWrite(element, this.loader), this.loader);
        }
        return element;
    }

    Element copyElementForWriteIfNeeded(Element element) {
        if (element == null) {
            return null;
        }
        if (this.copyOnRead && this.copyOnWrite) {
            return this.copyStrategy.copyForWrite(element, this.loader);
        }
        if (this.copyOnWrite) {
            return this.copyStrategy.copyForRead(this.copyStrategy.copyForWrite(element, this.loader), this.loader);
        }
        return element;
    }

    Element copyElementForRemovalIfNeeded(Element element) {
        if (element == null) {
            return null;
        }
        if (this.copyOnRead && this.copyOnWrite) {
            return this.copyStrategy.copyForWrite(element, this.loader);
        }
        return element;
    }

    boolean isCopyActive() {
        return this.copyOnRead || this.copyOnWrite;
    }
}

