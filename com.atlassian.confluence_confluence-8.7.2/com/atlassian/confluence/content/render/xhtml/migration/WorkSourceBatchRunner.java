/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.core.util.ProgressWrapper
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionCallbackWithoutResult
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.BatchException;
import com.atlassian.confluence.content.render.xhtml.migration.BatchTask;
import com.atlassian.confluence.content.render.xhtml.migration.BatchableWorkSource;
import com.atlassian.confluence.impl.util.concurrent.ConfluenceExecutors;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.core.util.ProgressWrapper;
import com.atlassian.util.concurrent.ThreadFactories;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class WorkSourceBatchRunner<T> {
    private final PlatformTransactionManager transactionManager;
    private final int numThreads;
    private final String threadName;
    private ProgressMeter progress;

    public WorkSourceBatchRunner(String threadName, int numThreads, PlatformTransactionManager transactionManager) {
        if (StringUtils.isBlank((CharSequence)threadName)) {
            throw new IllegalArgumentException("A threadName is required.");
        }
        this.threadName = threadName;
        if (numThreads <= 0) {
            throw new IllegalArgumentException("1 or more threads are required.");
        }
        this.numThreads = numThreads;
        this.transactionManager = transactionManager;
    }

    protected List<Exception> run(final BatchableWorkSource<T> workSource, final BatchTask<T> task, ExecutorService executor) throws ExecutionException, InterruptedException {
        ArrayList<Future<List>> futures = new ArrayList<Future<List>>(workSource.numberOfBatches());
        ProgressWrapper wrapper = this.progress != null ? new ProgressWrapper(this.progress, workSource.numberOfBatches()) : null;
        for (int i = 0; i < workSource.numberOfBatches(); ++i) {
            futures.add(executor.submit(() -> {
                final ArrayList exceptions = new ArrayList(1);
                TransactionTemplate template = new TransactionTemplate(this.transactionManager);
                template.setPropagationBehavior(0);
                template.setName("tx-template");
                template.execute((TransactionCallback)new TransactionCallbackWithoutResult(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                        List batch = workSource.getBatch();
                        try {
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
                        }
                        finally {
                            if (wrapper != null) {
                                wrapper.incrementCounter();
                            }
                        }
                    }
                });
                return exceptions;
            }));
        }
        ArrayList<Exception> allExceptions = new ArrayList<Exception>(1);
        for (Future future : futures) {
            allExceptions.addAll((Collection)future.get());
        }
        if (wrapper != null) {
            wrapper.setStatus("Finished");
        }
        return allExceptions;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Exception> run(BatchableWorkSource<T> workSource, BatchTask<T> task) throws ExecutionException, InterruptedException {
        List<Exception> exceptions;
        ExecutorService executor = ConfluenceExecutors.newFixedThreadPool(this.numThreads, ThreadFactories.namedThreadFactory((String)this.threadName, (ThreadFactories.Type)ThreadFactories.Type.DAEMON));
        try {
            exceptions = this.run(workSource, task, executor);
        }
        finally {
            executor.shutdown();
        }
        return exceptions;
    }

    public void setProgressWrapper(ProgressMeter progress) {
        this.progress = progress;
    }
}

