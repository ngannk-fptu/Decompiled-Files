/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.base.Function
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.sal.confluence.lifecycle;

import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.sal.confluence.lifecycle.ServiceExecutionStrategy;
import com.atlassian.sal.confluence.lifecycle.StaticServiceExecution;
import com.google.common.base.Function;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.osgi.framework.ServiceReference;

public abstract class ExecutionStrategyTemplate<S>
implements ServiceExecutionStrategy<S> {
    protected final Function<ServiceReference, Option<ModuleCompleteKey>> moduleReferenceParser;
    protected final Queue<Callable> serviceExecutions = new ConcurrentLinkedQueue<Callable>();

    protected ExecutionStrategyTemplate(Function<ServiceReference, Option<ModuleCompleteKey>> moduleReferenceParser) {
        this.moduleReferenceParser = moduleReferenceParser;
    }

    @Override
    public boolean add(ServiceReference serviceReference, Function<S, ?> serviceCallback) {
        return this.serviceExecutions.add(this.createServiceExecution(serviceReference, serviceCallback));
    }

    protected Callable createServiceExecution(ServiceReference serviceReference, Function<S, ?> serviceCallback) {
        return new StaticServiceExecution(serviceReference, serviceCallback){

            @Override
            protected String serviceReferenceToString() {
                StringBuilder sb = new StringBuilder();
                sb.append(ExecutionStrategyTemplate.this.getDescription());
                Option maybeModuleReference = Objects.requireNonNull((Option)ExecutionStrategyTemplate.this.moduleReferenceParser.apply((Object)this.serviceReference));
                if (maybeModuleReference.isDefined()) {
                    sb.append(" ");
                    sb.append(maybeModuleReference.get());
                }
                sb.append(" ");
                sb.append(super.serviceReferenceToString());
                return sb.toString();
            }
        };
    }

    protected String getDescription() {
        return this.getClass().getSimpleName();
    }
}

