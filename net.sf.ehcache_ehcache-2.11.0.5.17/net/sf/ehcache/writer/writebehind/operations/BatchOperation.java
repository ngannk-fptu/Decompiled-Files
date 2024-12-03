/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind.operations;

import net.sf.ehcache.writer.CacheWriter;

public interface BatchOperation {
    public void performBatchOperation(CacheWriter var1);
}

