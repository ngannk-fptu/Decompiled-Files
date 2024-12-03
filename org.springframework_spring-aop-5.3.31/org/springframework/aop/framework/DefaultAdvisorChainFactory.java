/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
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
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AdvisorChainFactory;
import org.springframework.aop.framework.InterceptorAndDynamicMethodMatcher;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.lang.Nullable;

public class DefaultAdvisorChainFactory
implements AdvisorChainFactory,
Serializable {
    @Override
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Advised config, Method method, @Nullable Class<?> targetClass) {
        AdvisorAdapterRegistry registry = GlobalAdvisorAdapterRegistry.getInstance();
        Advisor[] advisors = config.getAdvisors();
        ArrayList<Object> interceptorList = new ArrayList<Object>(advisors.length);
        Class<?> actualClass = targetClass != null ? targetClass : method.getDeclaringClass();
        Boolean hasIntroductions = null;
        for (Advisor advisor : advisors) {
            if (advisor instanceof PointcutAdvisor) {
                boolean match;
                PointcutAdvisor pointcutAdvisor = (PointcutAdvisor)advisor;
                if (!config.isPreFiltered() && !pointcutAdvisor.getPointcut().getClassFilter().matches(actualClass)) continue;
                MethodMatcher mm = pointcutAdvisor.getPointcut().getMethodMatcher();
                if (mm instanceof IntroductionAwareMethodMatcher) {
                    if (hasIntroductions == null) {
                        hasIntroductions = DefaultAdvisorChainFactory.hasMatchingIntroductions(advisors, actualClass);
                    }
                    match = ((IntroductionAwareMethodMatcher)mm).matches(method, actualClass, hasIntroductions);
                } else {
                    match = mm.matches(method, actualClass);
                }
                if (!match) continue;
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

    private static boolean hasMatchingIntroductions(Advisor[] advisors, Class<?> actualClass) {
        for (Advisor advisor : advisors) {
            IntroductionAdvisor ia;
            if (!(advisor instanceof IntroductionAdvisor) || !(ia = (IntroductionAdvisor)advisor).getClassFilter().matches(actualClass)) continue;
            return true;
        }
        return false;
    }
}

