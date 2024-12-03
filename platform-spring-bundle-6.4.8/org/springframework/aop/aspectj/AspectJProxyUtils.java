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
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public abstract class AspectJProxyUtils {
    public static boolean makeAdvisorChainAspectJCapableIfNecessary(List<Advisor> advisors) {
        if (!advisors.isEmpty()) {
            boolean foundAspectJAdvice = false;
            for (Advisor advisor : advisors) {
                if (!AspectJProxyUtils.isAspectJAdvice(advisor)) continue;
                foundAspectJAdvice = true;
                break;
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

    static boolean isVariableName(@Nullable String name) {
        if (!StringUtils.hasLength(name)) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            return false;
        }
        for (int i2 = 1; i2 < name.length(); ++i2) {
            if (Character.isJavaIdentifierPart(name.charAt(i2))) continue;
            return false;
        }
        return true;
    }
}

