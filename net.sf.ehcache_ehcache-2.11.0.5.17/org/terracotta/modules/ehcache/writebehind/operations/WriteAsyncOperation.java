/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.writebehind.operations;

import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.operations.SingleOperationType;
import org.terracotta.modules.ehcache.writebehind.operations.SingleAsyncOperation;

public class WriteAsyncOperation
implements SingleAsyncOperation {
    private static final long serialVersionUID = 1631728715404189659L;
    private final Element snapshot;
    private final long creationTime;

    public WriteAsyncOperation(Element snapshot) {
        this(snapshot, System.currentTimeMillis());
    }

    public WriteAsyncOperation(Element snapshot, long creationTime) {
        this.snapshot = snapshot;
        this.creationTime = creationTime;
    }

    @Override
    public Element getElement() {
        return this.snapshot;
    }

    @Override
    public void performSingleOperation(CacheWriter cacheWriter) {
        cacheWriter.write(this.snapshot);
    }

    @Override
    public Object getKey() {
        return this.snapshot.getObjectKey();
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public void throwAwayElement(CacheWriter cacheWriter, RuntimeException e) {
        cacheWriter.throwAway(this.snapshot, SingleOperationType.WRITE, e);
    }
}

