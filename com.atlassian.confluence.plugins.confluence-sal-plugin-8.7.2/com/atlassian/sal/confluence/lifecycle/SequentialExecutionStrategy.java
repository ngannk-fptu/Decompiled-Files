/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.base.Function
 *  com.google.common.base.Throwables
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.sal.confluence.lifecycle;

import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.sal.confluence.lifecycle.ExecutionStrategyTemplate;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import org.osgi.framework.ServiceReference;

public class SequentialExecutionStrategy<S>
extends ExecutionStrategyTemplate<S> {
    private final ExecutorService executorService;

    public SequentialExecutionStrategy(Function<ServiceReference, Option<ModuleCompleteKey>> moduleReferenceParser, ExecutorService executorService) {
        super(moduleReferenceParser);
        this.executorService = executorService;
    }

    @Override
    public void trigger() {
        for (Callable callable : this.serviceExecutions) {
            try {
                this.executorService.submit(callable).get();
            }
            catch (ExecutionException e) {
                Throwable throwable = e.getCause();
                if (throwable != null) {
                    Throwables.throwIfUnchecked((Throwable)throwable);
                    throw new RuntimeException(throwable);
                }
                throw new RuntimeException(e);
            }
            catch (Exception e) {
                Throwables.throwIfUnchecked((Throwable)e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected String getDescription() {
        return "sequential";
    }
}

