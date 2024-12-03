/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.aspectj;

import java.util.List;
import org.springframework.aop.Advisor;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.aspectj.AbstractAspectJAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.InstantiationModelAwarePointcutAdvisor;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;

public abstract class AspectJProxyUtils {
    public static boolean makeAdvisorChainAspectJCapableIfNecessary(List<Advisor> advisors) {
        if (!advisors.isEmpty()) {
            boolean foundAspectJAdvice = false;
            for (Advisor advisor : advisors) {
                if (!AspectJProxyUtils.isAspectJAdvice(advisor)) continue;
                foundAspectJAdvice = true;
            }
            if (foundAspectJAdvice && !advisors.contains(ExposeInvocationInterceptor.ADVISOR)) {
                advisors.add(0, ExposeInvocationInterceptor.ADVISOR);
                return true;
            }
        }
        return false;
    }

    private static boolean isAspectJAdvice(Advisor advisor) {
        return advisor instanceof InstantiationModelAwarePointcutAdvisor || advisor.getAdvice() instanceof AbstractAspectJAdvice || advisor instanceof PointcutAdvisor && ((PointcutAdvisor)advisor).getPointcut() instanceof AspectJExpressionPointcut;
    }
}

