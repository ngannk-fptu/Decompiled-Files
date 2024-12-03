/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.base.Function
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.sal.confluence.lifecycle;

import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.sal.confluence.lifecycle.ExecutionStrategyTemplate;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.osgi.framework.ServiceReference;

public class ForkAndJoinExecutionStrategy<S>
extends ExecutionStrategyTemplate<S> {
    private final ExecutorService executorService;

    public ForkAndJoinExecutionStrategy(Function<ServiceReference, Option<ModuleCompleteKey>> moduleReferenceParser, ExecutorService executorService) {
        super(moduleReferenceParser);
        this.executorService = executorService;
    }

    @Override
    public void trigger() {
        ImmutableList.Builder serviceFutures = ImmutableList.builder();
        for (Callable serviceExecution : this.serviceExecutions) {
            serviceFutures.add(this.executorService.submit(serviceExecution));
        }
        for (Future serviceFuture : serviceFutures.build()) {
            try {
                serviceFuture.get();
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            catch (ExecutionException e) {
                Throwable throwable = e.getCause();
                if (throwable != null) {
                    Throwables.throwIfUnchecked((Throwable)throwable);
                    throw new RuntimeException(throwable);
                }
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected String getDescription() {
        return "fork and join";
    }
}

