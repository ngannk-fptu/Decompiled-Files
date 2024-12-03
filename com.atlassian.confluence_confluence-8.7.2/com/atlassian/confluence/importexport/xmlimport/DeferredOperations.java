/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.xmlimport.DeferredOperationsLogger;
import com.atlassian.confluence.importexport.xmlimport.Operation;
import com.atlassian.confluence.importexport.xmlimport.OperationSet;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveId;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class DeferredOperations {
    private static Logger log = LoggerFactory.getLogger(DeferredOperations.class);
    private boolean doingDeferred = false;
    private final Map<TransientHibernateHandle, Set<OperationSet>> operationSetsWaitingForThis = Maps.newHashMapWithExpectedSize((int)0);
    private final DeferredOperationsLogger deferredOperationsLogger = new DeferredOperationsLogger();
    private Set<TransientHibernateHandle> deferredAdditions = Sets.newHashSetWithExpectedSize((int)0);

    public void reportOutstandingOperations() throws Exception {
        for (Set<OperationSet> setOfSets : this.operationSetsWaitingForThis.values()) {
            for (OperationSet os : setOfSets) {
                log.warn("Uncompleted deferred operations waiting for:" + os.getWaitingFor());
                for (Operation o : os.getOperations()) {
                    log.warn(o.getDescription());
                }
            }
        }
    }

    public void addDeferredOperation(PrimitiveId idProperty, Set<TransientHibernateHandle> waitingFor, Operation operation) {
        this.deferredOperationsLogger.logNewDeferredOperation(idProperty, waitingFor, operation);
        OperationSet operations = this.findOperationSetFor(waitingFor);
        if (operations == null) {
            operations = new OperationSet(waitingFor);
            for (TransientHibernateHandle key : waitingFor) {
                Set s = this.operationSetsWaitingForThis.computeIfAbsent(key, k -> Sets.newHashSetWithExpectedSize((int)0));
                s.add(operations);
            }
        }
        operations.addOperation(operation);
    }

    private OperationSet findOperationSetFor(Set<TransientHibernateHandle> waitingFor) {
        if (log.isDebugEnabled()) {
            log.debug("Finding deferred operations for: " + waitingFor);
        }
        for (TransientHibernateHandle key : waitingFor) {
            Set<OperationSet> s = this.operationSetsWaitingForThis.get(key);
            if (s == null) continue;
            for (OperationSet os : s) {
                if (!os.getWaitingFor().equals(waitingFor)) continue;
                return os;
            }
        }
        return null;
    }

    public void doDeferredOperationsWaitingFor(TransientHibernateHandle key) throws Exception {
        if (this.doingDeferred) {
            this.deferredAdditions.add(key);
        } else {
            this.doingDeferred = true;
            Set<OperationSet> keySets = this.operationSetsWaitingForThis.get(key);
            if (keySets != null) {
                for (OperationSet k : keySets) {
                    if (k.getWaitingFor().size() == 1) {
                        for (Operation o : k.getOperations()) {
                            if (log.isDebugEnabled()) {
                                log.debug("Performing deferred operation: {}", (Object)o.getDescription());
                            }
                            o.execute();
                        }
                    }
                    k.getWaitingFor().remove(key);
                }
                this.operationSetsWaitingForThis.remove(key);
            }
            this.doingDeferred = false;
            while (!this.deferredAdditions.isEmpty()) {
                ArrayList<TransientHibernateHandle> additions = new ArrayList<TransientHibernateHandle>(this.deferredAdditions);
                this.deferredAdditions.clear();
                for (TransientHibernateHandle objectKey : additions) {
                    this.doDeferredOperationsWaitingFor(objectKey);
                }
            }
        }
        this.deferredOperationsLogger.logRemainingOperations(this.operationSetsWaitingForThis);
    }
}

