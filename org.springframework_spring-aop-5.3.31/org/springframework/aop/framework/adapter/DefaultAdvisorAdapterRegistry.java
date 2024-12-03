/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.adapter.AdvisorAdapter;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.AfterReturningAdviceAdapter;
import org.springframework.aop.framework.adapter.MethodBeforeAdviceAdapter;
import org.springframework.aop.framework.adapter.ThrowsAdviceAdapter;
import org.springframework.aop.framework.adapter.UnknownAdviceTypeException;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class DefaultAdvisorAdapterRegistry
implements AdvisorAdapterRegistry,
Serializable {
    private final List<AdvisorAdapter> adapters = new ArrayList<AdvisorAdapter>(3);

    public DefaultAdvisorAdapterRegistry() {
        this.registerAdvisorAdapter(new MethodBeforeAdviceAdapter());
        this.registerAdvisorAdapter(new AfterReturningAdviceAdapter());
        this.registerAdvisorAdapter(new ThrowsAdviceAdapter());
    }

    @Override
    public Advisor wrap(Object adviceObject) throws UnknownAdviceTypeException {
        if (adviceObject instanceof Advisor) {
            return (Advisor)adviceObject;
        }
        if (!(adviceObject instanceof Advice)) {
            throw new UnknownAdviceTypeException(adviceObject);
        }
        Advice advice = (Advice)adviceObject;
        if (advice instanceof MethodInterceptor) {
            return new DefaultPointcutAdvisor(advice);
        }
        for (AdvisorAdapter adapter : this.adapters) {
            if (!adapter.supportsAdvice(advice)) continue;
            return new DefaultPointcutAdvisor(advice);
        }
        throw new UnknownAdviceTypeException(advice);
    }

    @Override
    public MethodInterceptor[] getInterceptors(Advisor advisor) throws UnknownAdviceTypeException {
        ArrayList<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>(3);
        Advice advice = advisor.getAdvice();
        if (advice instanceof MethodInterceptor) {
            interceptors.add((MethodInterceptor)advice);
        }
        for (AdvisorAdapter adapter : this.adapters) {
            if (!adapter.supportsAdvice(advice)) continue;
            interceptors.add(adapter.getInterceptor(advisor));
        }
        if (interceptors.isEmpty()) {
            throw new UnknownAdviceTypeException(advisor.getAdvice());
        }
        return interceptors.toArray(new MethodInterceptor[0]);
    }

    @Override
    public void registerAdvisorAdapter(AdvisorAdapter adapter) {
        this.adapters.add(adapter);
    }
}

