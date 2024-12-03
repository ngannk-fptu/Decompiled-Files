/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.sal.confluence.lifecycle;

import com.atlassian.sal.confluence.lifecycle.ServiceExecutionStrategy;
import com.google.common.base.Function;
import org.osgi.framework.ServiceReference;

public class ServiceExecutionStrategyComposite<S>
implements ServiceExecutionStrategy<S> {
    private final ServiceExecutionStrategy<S>[] executionStrategies;

    public ServiceExecutionStrategyComposite(ServiceExecutionStrategy<S>[] executionStrategies) {
        this.executionStrategies = executionStrategies;
    }

    @Override
    public boolean add(ServiceReference serviceReference, Function<S, ?> serviceCallback) {
        for (ServiceExecutionStrategy<S> executionStrategy : this.executionStrategies) {
            if (!executionStrategy.add(serviceReference, serviceCallback)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void trigger() {
        for (ServiceExecutionStrategy<S> executionStrategy : this.executionStrategies) {
            executionStrategy.trigger();
        }
    }
}

