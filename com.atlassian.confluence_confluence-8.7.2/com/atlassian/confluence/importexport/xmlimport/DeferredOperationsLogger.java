/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.Maps
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.xmlimport.Operation;
import com.atlassian.confluence.importexport.xmlimport.OperationSet;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveId;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class DeferredOperationsLogger {
    private static final Logger log = LoggerFactory.getLogger(DeferredOperationsLogger.class);
    private int itemCount = 0;
    private static final int BATCH_SIZE_FOR_LOGGING_COUNTERS = Integer.getInteger(DeferredOperationsLogger.class.getCanonicalName() + ".batchSize", 100);
    private static final Function<TransientHibernateHandle, Class<?>> GET_ENTITY_TYPE_FOR_HIBERNATE_HANDLE = item -> item != null ? item.getClazz() : null;

    void logRemainingOperations(Map<TransientHibernateHandle, Set<OperationSet>> operationSetsWaitingForThis) {
        try {
            if (log.isDebugEnabled() && this.itemCount++ % BATCH_SIZE_FOR_LOGGING_COUNTERS == 0) {
                int total = 0;
                for (Set<OperationSet> value : operationSetsWaitingForThis.values()) {
                    total += value.size();
                }
                log.debug("Count of remaining deferred operations: {} - Details:", (Object)total);
                Counter counter = new Counter();
                counter.incrementPerCategory(operationSetsWaitingForThis.keySet(), GET_ENTITY_TYPE_FOR_HIBERNATE_HANDLE);
                Map set = counter.getCounts();
                for (Map.Entry count : set.entrySet()) {
                    log.debug("Count of deferred objects: {} = {}", (Object)((Class)count.getKey()).getCanonicalName(), (Object)count.getValue());
                }
            }
        }
        catch (Exception e) {
            log.info("Exception while logging the counts of deferred operations: {}", (Object)e.getMessage());
        }
    }

    public void logNewDeferredOperation(PrimitiveId idProperty, Set<TransientHibernateHandle> waitingFor, Operation operation) {
        try {
            if (log.isDebugEnabled()) {
                for (TransientHibernateHandle handle : waitingFor) {
                    log.debug("Deferring operation on {} until {} is met: {}", new Object[]{idProperty != null ? idProperty.getValue() : null, handle.getId(), operation.getDescription()});
                }
            }
        }
        catch (Exception e) {
            log.info("Exception while logging a deferred operation: " + e.getMessage());
        }
    }

    private static class Counter<T> {
        final Map<T, Integer> counts = Maps.newHashMapWithExpectedSize((int)0);

        private Counter() {
        }

        public int get(T key) {
            Integer value = this.counts.get(key);
            if (value == null) {
                return 0;
            }
            return value;
        }

        public <U> void incrementPerCategory(Collection<? extends U> collection, Function<U, T> getCategoryForItem) {
            for (U item : collection) {
                Object category = getCategoryForItem.apply(item);
                this.increment(category);
            }
        }

        public Integer increment(T key) {
            int value = this.get(key) + 1;
            this.counts.put(key, value);
            return value;
        }

        public Map<T, Integer> getCounts() {
            return Maps.newHashMap(this.counts);
        }
    }
}

