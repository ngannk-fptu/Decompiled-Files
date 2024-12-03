/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.sal.confluence.lifecycle;

import com.google.common.base.Function;
import org.osgi.framework.ServiceReference;

public interface ServiceExecutionStrategy<S> {
    public boolean add(ServiceReference var1, Function<S, ?> var2);

    public void trigger();
}

