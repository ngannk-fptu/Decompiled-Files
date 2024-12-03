/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework.adapter;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.adapter.AdvisorAdapter;
import org.springframework.aop.framework.adapter.UnknownAdviceTypeException;

public interface AdvisorAdapterRegistry {
    public Advisor wrap(Object var1) throws UnknownAdviceTypeException;

    public MethodInterceptor[] getInterceptors(Advisor var1) throws UnknownAdviceTypeException;

    public void registerAdvisorAdapter(AdvisorAdapter var1);
}

