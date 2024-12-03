/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.writebehind.operations;

import net.sf.ehcache.writer.CacheWriter;

public interface BatchAsyncOperation {
    public void performBatchOperation(CacheWriter var1);
}

