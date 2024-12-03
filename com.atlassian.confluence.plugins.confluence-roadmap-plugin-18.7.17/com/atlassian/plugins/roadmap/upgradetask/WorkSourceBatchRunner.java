/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.migration.BatchException
 *  com.atlassian.confluence.content.render.xhtml.migration.BatchTask
 *  com.atlassian.confluence.content.render.xhtml.migration.BatchableWorkSource
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 */
package com.atlassian.plugins.roadmap.upgradetask;

import com.atlassian.confluence.content.render.xhtml.migration.BatchException;
import com.atlassian.confluence.content.render.xhtml.migration.BatchTask;
import com.atlassian.confluence.content.render.xhtml.migration.BatchableWorkSource;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.util.concurrent.ThreadFactories;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WorkSourceBatchRunner<T> {
    private static final String THREAD_NAME = "roadmap-macro-migration";
    private static final int NUM_THREADS = 4;
    private final TransactionTemplate transactionTemplate;

    public WorkSourceBatchRunner(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    protected List<Exception> run(BatchableWorkSource<T> workSource, BatchTask<T> task, ExecutorService executor) throws ExecutionException, InterruptedException {
        ArrayList<Future<List>> futures = new ArrayList<Future<List>>(workSource.numberOfBatches());
        for (int i = 0; i < workSource.numberOfBatches(); ++i) {
            futures.add(executor.submit(() -> {
                ArrayList exceptions = new ArrayList(1);
                this.transactionTemplate.execute(() -> {
                    List batch = workSource.getBatch();
                    int size = batch.size();
                    for (int i1 = 0; i1 < size; ++i1) {
                        try {
                            task.apply(batch.get(i1), i1, batch.size());
                            continue;
                        }
                        catch (BatchException ex) {
                            exceptions.addAll(ex.getBatchExceptions());
                            continue;
                        }
                        catch (Exception ex) {
                            exceptions.add(ex);
                        }
                    }
                    return null;
                });
                return exceptions;
            }));
        }
        ArrayList<Exception> allExceptions = new ArrayList<Exception>(1);
        for (Future future : futures) {
            allExceptions.addAll((Collection)future.get());
        }
        return allExceptions;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Exception> run(BatchableWorkSource<T> workSource, BatchTask<T> task) throws ExecutionException, InterruptedException {
        List<Exception> exceptions;
        ExecutorService executor = Executors.newFixedThreadPool(4, ThreadFactories.namedThreadFactory((String)THREAD_NAME, (ThreadFactories.Type)ThreadFactories.Type.DAEMON));
        try {
            exceptions = this.run(workSource, task, executor);
        }
        finally {
            executor.shutdown();
        }
        return exceptions;
    }
}

