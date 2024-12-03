/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.writebehind.operations;

import java.util.Collection;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;
import org.terracotta.modules.ehcache.writebehind.operations.BatchAsyncOperation;

public class WriteAllAsyncOperation
implements BatchAsyncOperation {
    private final Collection<Element> elements;

    public WriteAllAsyncOperation(Collection<Element> elements) {
        this.elements = elements;
    }

    @Override
    public void performBatchOperation(CacheWriter cacheWriter) {
        cacheWriter.writeAll(this.elements);
    }
}

