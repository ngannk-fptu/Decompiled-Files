/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner.callables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BatchCallable<T>
implements Function<Void, Collection<T>> {
    private static Logger LOGGER = LoggerFactory.getLogger(BatchCallable.class);
    private final Callable<T> innerCallable;
    private final int batchSize;
    private int numberOfLoop;
    private List<T> resultList;

    public BatchCallable(int batchSize, long totalElement, Callable<T> innerCallable) {
        this.batchSize = batchSize;
        this.innerCallable = innerCallable;
        this.numberOfLoop = (int)Math.ceil((float)totalElement / (float)batchSize);
        this.resultList = new ArrayList<T>(this.numberOfLoop);
    }

    @Override
    public Collection<T> apply(Void args) {
        while (this.numberOfLoop > 0) {
            Object result = null;
            try {
                result = this.innerCallable.call();
            }
            catch (Exception e) {
                LOGGER.error("An exception occurred during batch processing. The batch was skipped.", (Throwable)e);
            }
            this.resultList.add(result);
            --this.numberOfLoop;
        }
        return this.resultList;
    }
}

