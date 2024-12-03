/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.writebehind.operations;

import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.operations.SingleOperationType;
import org.terracotta.modules.ehcache.writebehind.operations.SingleAsyncOperation;

public class DeleteAsyncOperation
implements SingleAsyncOperation {
    private static final long serialVersionUID = -5780204454577869853L;
    private final Object keySnapshot;
    private final Element elementSnapshot;
    private final long creationTime;

    public DeleteAsyncOperation(Object keySnapshot, Element elementSnapshot) {
        this(keySnapshot, elementSnapshot, System.currentTimeMillis());
    }

    public DeleteAsyncOperation(Object keySnapshot, Element elementSnapshot, long creationTime) {
        this.keySnapshot = keySnapshot;
        this.elementSnapshot = elementSnapshot;
        this.creationTime = creationTime;
    }

    @Override
    public void performSingleOperation(CacheWriter cacheWriter) {
        cacheWriter.delete(new CacheEntry(this.getKey(), this.getElement()));
    }

    @Override
    public Object getKey() {
        return this.keySnapshot;
    }

    @Override
    public Element getElement() {
        return this.elementSnapshot;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public void throwAwayElement(CacheWriter cacheWriter, RuntimeException e) {
        cacheWriter.throwAway(this.elementSnapshot, SingleOperationType.DELETE, e);
    }
}

