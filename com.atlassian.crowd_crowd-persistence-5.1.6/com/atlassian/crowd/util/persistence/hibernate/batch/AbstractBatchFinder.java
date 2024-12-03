/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.util.persistence.hibernate.batch;

import com.atlassian.crowd.util.persistence.hibernate.batch.BatchFinder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBatchFinder
implements BatchFinder {
    private static final Logger logger = LoggerFactory.getLogger(AbstractBatchFinder.class);
    private int batchSize = 20;

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <E extends Serializable> Collection<E> find(long directoryID, Collection<String> names, Class<E> persistentClass) {
        if (logger.isDebugEnabled()) {
            logger.debug("Attempting to find " + names.size() + " objects of class " + persistentClass.getName());
        }
        ArrayList<E> results = new ArrayList<E>();
        ArrayList<String> nameBatch = new ArrayList<String>(this.batchSize);
        this.beforeFind();
        try {
            for (String name : names) {
                nameBatch.add(name);
                if (nameBatch.size() != this.batchSize) continue;
                results.addAll(this.processBatchFind(directoryID, nameBatch, persistentClass));
                nameBatch.clear();
            }
            if (!nameBatch.isEmpty()) {
                results.addAll(this.processBatchFind(directoryID, nameBatch, persistentClass));
                nameBatch.clear();
            }
        }
        finally {
            this.afterFind();
        }
        return results;
    }

    protected void beforeFind() {
    }

    protected void afterFind() {
    }

    protected abstract <E> Collection<E> processBatchFind(long var1, Collection<String> var3, Class<E> var4);
}

