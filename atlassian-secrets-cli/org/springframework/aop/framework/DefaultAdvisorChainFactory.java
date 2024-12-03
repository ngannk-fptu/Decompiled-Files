/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AdvisorChainFactory;
import org.springframework.aop.framework.InterceptorAndDynamicMethodMatcher;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.support.MethodMatchers;
import org.springframework.lang.Nullable;

public class DefaultAdvisorChainFactory
implements AdvisorChainFactory,
Serializable {
    @Override
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Advised config, Method method, @Nullable Class<?> targetClass) {
        ArrayList<Object> interceptorList = new ArrayList<Object>(config.getAdvisors().length);
        Class<?> actualClass = targetClass != null ? targetClass : method.getDeclaringClass();
        boolean hasIntroductions = DefaultAdvisorChainFactory.hasMatchingIntroductions(config, actualClass);
        AdvisorAdapterRegistry registry = GlobalAdvisorAdapterRegistry.getInstance();
        for (Advisor advisor : config.getAdvisors()) {
            if (advisor instanceof PointcutAdvisor) {
                MethodMatcher mm;
                PointcutAdvisor pointcutAdvisor = (PointcutAdvisor)advisor;
                if (!config.isPreFiltered() && !pointcutAdvisor.getPointcut().getClassFilter().matches(actualClass) || !MethodMatchers.matches(mm = pointcutAdvisor.getPointcut().getMethodMatcher(), method, actualClass, hasIntroductions)) continue;
                MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
                if (mm.isRuntime()) {
                    for (MethodInterceptor interceptor : interceptors) {
                        interceptorList.add(new InterceptorAndDynamicMethodMatcher(interceptor, mm));
                    }
                    continue;
                }
                interceptorList.addAll(Arrays.asList(interceptors));
                continue;
            }
            if (advisor instanceof IntroductionAdvisor) {
                IntroductionAdvisor ia = (IntroductionAdvisor)advisor;
                if (!config.isPreFiltered() && !ia.getClassFilter().matches(actualClass)) continue;
                MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
                interceptorList.addAll(Arrays.asList(interceptors));
                continue;
            }
            MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
            interceptorList.addAll(Arrays.asList(interceptors));
        }
        return interceptorList;
    }

    private static boolean hasMatchingIntroductions(Advised config, Class<?> actualClass) {
        for (Advisor advisor : config.getAdvisors()) {
            IntroductionAdvisor ia;
            if (!(advisor instanceof IntroductionAdvisor) || !(ia = (IntroductionAdvisor)advisor).getClassFilter().matches(actualClass)) continue;
            return true;
        }
        return false;
    }
}

