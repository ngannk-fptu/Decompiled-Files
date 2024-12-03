/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.terracotta.modules.ehcache.writebehind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.modules.ehcache.async.ItemProcessor;
import org.terracotta.modules.ehcache.writebehind.operations.BatchAsyncOperation;
import org.terracotta.modules.ehcache.writebehind.operations.DeleteAllAsyncOperation;
import org.terracotta.modules.ehcache.writebehind.operations.DeleteAsyncOperation;
import org.terracotta.modules.ehcache.writebehind.operations.SingleAsyncOperation;
import org.terracotta.modules.ehcache.writebehind.operations.WriteAllAsyncOperation;
import org.terracotta.modules.ehcache.writebehind.operations.WriteAsyncOperation;

public class CacheWriterProcessor
implements ItemProcessor<SingleAsyncOperation> {
    private final CacheWriter cacheWriter;
    private static final Logger LOGGER = LoggerFactory.getLogger((String)CacheWriterProcessor.class.getName());

    public CacheWriterProcessor(CacheWriter cacheWriter) {
        this.cacheWriter = cacheWriter;
    }

    @Override
    public void process(SingleAsyncOperation item) {
        try {
            item.performSingleOperation(this.cacheWriter);
        }
        catch (Exception e) {
            throw new CacheException("Unexpected exception while processing write behind operation", e);
        }
    }

    CacheWriter getCacheWriter() {
        return this.cacheWriter;
    }

    @Override
    public void process(Collection<SingleAsyncOperation> items) {
        ArrayList<SingleAsyncOperation> itemsPerType = new ArrayList<SingleAsyncOperation>();
        Class opClass = WriteAsyncOperation.class;
        for (SingleAsyncOperation item : items) {
            if (item.getClass() == opClass) {
                itemsPerType.add(item);
                continue;
            }
            this.executeBatch(itemsPerType);
            opClass = item.getClass();
            itemsPerType.clear();
            itemsPerType.add(item);
        }
        this.executeBatch(itemsPerType);
    }

    private void executeBatch(List<SingleAsyncOperation> itemsPerType) {
        if (!itemsPerType.isEmpty()) {
            Class<?> opClass = itemsPerType.get(0).getClass();
            try {
                BatchAsyncOperation batch = this.createBatchOprForType(opClass, itemsPerType);
                batch.performBatchOperation(this.cacheWriter);
            }
            catch (Exception e) {
                LOGGER.warn("error while processing batch write behind operation " + e);
                throw new CacheException("Unexpected exception while processing write behind operation " + e, e);
            }
        }
    }

    private BatchAsyncOperation createBatchOprForType(Class operationClass, Collection<SingleAsyncOperation> operations) {
        if (operationClass == WriteAsyncOperation.class) {
            ArrayList<Element> elements = new ArrayList<Element>();
            for (SingleAsyncOperation operation : operations) {
                elements.add(operation.getElement());
            }
            return new WriteAllAsyncOperation(elements);
        }
        if (operationClass == DeleteAsyncOperation.class) {
            ArrayList<CacheEntry> entries = new ArrayList<CacheEntry>();
            for (SingleAsyncOperation operation : operations) {
                entries.add(new CacheEntry(operation.getKey(), operation.getElement()));
            }
            return new DeleteAllAsyncOperation(entries);
        }
        throw new RuntimeException("no batch operation created for " + operationClass.getName());
    }

    @Override
    public void throwAway(SingleAsyncOperation item, RuntimeException runtimeException) {
        try {
            item.throwAwayElement(this.cacheWriter, runtimeException);
        }
        catch (Exception e) {
            throw new CacheException("Unexpected exception while throwing away write behind operation", e);
        }
    }
}

