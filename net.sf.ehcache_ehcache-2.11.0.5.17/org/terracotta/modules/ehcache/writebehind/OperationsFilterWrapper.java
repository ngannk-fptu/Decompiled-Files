/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.writebehind;

import java.util.List;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.writer.writebehind.OperationConverter;
import net.sf.ehcache.writer.writebehind.OperationsFilter;
import net.sf.ehcache.writer.writebehind.operations.KeyBasedOperation;
import org.terracotta.modules.ehcache.async.ItemsFilter;
import org.terracotta.modules.ehcache.writebehind.KeyBasedOperationWrapper;
import org.terracotta.modules.ehcache.writebehind.operations.SingleAsyncOperation;

public class OperationsFilterWrapper
implements ItemsFilter<SingleAsyncOperation> {
    private final OperationsFilter<KeyBasedOperation> delegate;

    public OperationsFilterWrapper(OperationsFilter<KeyBasedOperation> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void filter(List<SingleAsyncOperation> items) {
        this.delegate.filter(items, new OperationConverter<KeyBasedOperation>(){

            @Override
            public KeyBasedOperation convert(Object source) {
                SingleAsyncOperation operation = (SingleAsyncOperation)source;
                try {
                    return new KeyBasedOperationWrapper(operation.getKey(), operation.getCreationTime());
                }
                catch (Exception e) {
                    throw new CacheException(e);
                }
            }
        });
    }

    OperationsFilter<KeyBasedOperation> getDelegate() {
        return this.delegate;
    }
}

