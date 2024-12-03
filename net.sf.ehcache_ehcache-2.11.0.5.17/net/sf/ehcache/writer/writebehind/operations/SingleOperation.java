/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind.operations;

import java.util.List;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.operations.BatchOperation;
import net.sf.ehcache.writer.writebehind.operations.KeyBasedOperation;
import net.sf.ehcache.writer.writebehind.operations.SingleOperationType;

public interface SingleOperation
extends KeyBasedOperation {
    public void performSingleOperation(CacheWriter var1);

    public BatchOperation createBatchOperation(List<? extends SingleOperation> var1);

    public SingleOperationType getType();

    public void throwAway(CacheWriter var1, RuntimeException var2);
}

