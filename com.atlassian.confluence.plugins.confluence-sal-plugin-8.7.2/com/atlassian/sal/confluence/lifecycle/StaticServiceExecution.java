/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.sal.confluence.lifecycle;

import com.atlassian.sal.confluence.lifecycle.ServiceExecutionTemplate;
import com.google.common.base.Function;
import org.osgi.framework.ServiceReference;

public class StaticServiceExecution<S, R>
extends ServiceExecutionTemplate<S, R> {
    protected final Function<S, R> serviceCallback;

    public StaticServiceExecution(ServiceReference serviceReference, Function<S, R> serviceCallback) {
        super(serviceReference);
        this.serviceCallback = serviceCallback;
    }

    @Override
    protected R execute(S service) {
        return (R)this.serviceCallback.apply(service);
    }
}

