/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind.operations;

import java.util.List;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.operations.BatchOperation;

public class WriteAllOperation
implements BatchOperation {
    private final List<Element> elements;

    public WriteAllOperation(List<Element> elements) {
        this.elements = elements;
    }

    @Override
    public void performBatchOperation(CacheWriter cacheWriter) {
        cacheWriter.writeAll(this.elements);
    }
}

