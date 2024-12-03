/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework.adapter;

import java.io.Serializable;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.aop.framework.adapter.AdvisorAdapter;
import org.springframework.aop.framework.adapter.ThrowsAdviceInterceptor;

class ThrowsAdviceAdapter
implements AdvisorAdapter,
Serializable {
    ThrowsAdviceAdapter() {
    }

    @Override
    public boolean supportsAdvice(Advice advice) {
        return advice instanceof ThrowsAdvice;
    }

    @Override
    public MethodInterceptor getInterceptor(Advisor advisor) {
        return new ThrowsAdviceInterceptor(advisor.getAdvice());
    }
}

