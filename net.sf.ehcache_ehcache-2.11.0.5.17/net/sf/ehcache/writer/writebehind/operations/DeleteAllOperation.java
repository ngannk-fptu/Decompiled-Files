/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind.operations;

import java.util.List;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.operations.BatchOperation;

public class DeleteAllOperation
implements BatchOperation {
    private final List<CacheEntry> entries;

    public DeleteAllOperation(List<CacheEntry> entries) {
        this.entries = entries;
    }

    @Override
    public void performBatchOperation(CacheWriter cacheWriter) {
        cacheWriter.deleteAll(this.entries);
    }
}

