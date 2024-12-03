/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind.operations;

import java.util.ArrayList;
import java.util.List;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.operations.BatchOperation;
import net.sf.ehcache.writer.writebehind.operations.DeleteAllOperation;
import net.sf.ehcache.writer.writebehind.operations.KeyBasedOperation;
import net.sf.ehcache.writer.writebehind.operations.SingleOperation;
import net.sf.ehcache.writer.writebehind.operations.SingleOperationType;

public class DeleteOperation
implements SingleOperation {
    private final CacheEntry entry;
    private final long creationTime;

    public DeleteOperation(CacheEntry entry) {
        this(entry, System.currentTimeMillis());
    }

    public DeleteOperation(CacheEntry entry, long creationTime) {
        this.entry = this.duplicateCacheEntryElement(entry);
        this.creationTime = creationTime;
    }

    private CacheEntry duplicateCacheEntryElement(CacheEntry entry) {
        if (null == entry.getElement()) {
            return entry;
        }
        Element element = entry.getElement();
        return new CacheEntry(entry.getKey(), new Element(element.getObjectKey(), element.getObjectValue(), element.getVersion(), element.getCreationTime(), element.getLastAccessTime(), element.getHitCount(), false, element.getTimeToLive(), element.getTimeToIdle(), element.getLastUpdateTime()));
    }

    @Override
    public void performSingleOperation(CacheWriter cacheWriter) {
        cacheWriter.delete(this.entry);
    }

    @Override
    public BatchOperation createBatchOperation(List<? extends SingleOperation> operations) {
        ArrayList<CacheEntry> entries = new ArrayList<CacheEntry>();
        for (KeyBasedOperation keyBasedOperation : operations) {
            entries.add(((DeleteOperation)keyBasedOperation).entry);
        }
        return new DeleteAllOperation(entries);
    }

    @Override
    public Object getKey() {
        return this.entry.getKey();
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    public CacheEntry getEntry() {
        return this.entry;
    }

    @Override
    public SingleOperationType getType() {
        return SingleOperationType.DELETE;
    }

    @Override
    public void throwAway(CacheWriter cacheWriter, RuntimeException e) {
        Element element = this.entry.getElement();
        if (element == null) {
            element = new Element(this.entry.getKey(), null);
        }
        cacheWriter.throwAway(element, SingleOperationType.DELETE, e);
    }

    public int hashCode() {
        return this.getKey().hashCode();
    }

    public boolean equals(Object other) {
        if (other instanceof DeleteOperation) {
            return this.getCreationTime() == ((DeleteOperation)other).getCreationTime() && this.getKey().equals(((DeleteOperation)other).getKey());
        }
        return false;
    }
}

