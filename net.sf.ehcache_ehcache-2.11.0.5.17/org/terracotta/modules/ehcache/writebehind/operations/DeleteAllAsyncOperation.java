/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.writebehind.operations;

import java.util.Collection;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.writer.CacheWriter;
import org.terracotta.modules.ehcache.writebehind.operations.BatchAsyncOperation;

public class DeleteAllAsyncOperation
implements BatchAsyncOperation {
    private final Collection<CacheEntry> entries;

    public DeleteAllAsyncOperation(Collection<CacheEntry> entries) {
        this.entries = entries;
    }

    @Override
    public void performBatchOperation(CacheWriter cacheWriter) {
        cacheWriter.deleteAll(this.entries);
    }
}

