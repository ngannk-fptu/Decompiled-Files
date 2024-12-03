/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.beans.factory.NamedBean;
import org.springframework.lang.Nullable;

public abstract class ExposeBeanNameAdvisors {
    private static final String BEAN_NAME_ATTRIBUTE = ExposeBeanNameAdvisors.class.getName() + ".BEAN_NAME";

    public static String getBeanName() throws IllegalStateException {
        return ExposeBeanNameAdvisors.getBeanName(ExposeInvocationInterceptor.currentInvocation());
    }

    public static String getBeanName(MethodInvocation mi) throws IllegalStateException {
        if (!(mi instanceof ProxyMethodInvocation)) {
            throw new IllegalArgumentException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
        }
        ProxyMethodInvocation pmi = (ProxyMethodInvocation)mi;
        String beanName = (String)pmi.getUserAttribute(BEAN_NAME_ATTRIBUTE);
        if (beanName == null) {
            throw new IllegalStateException("Cannot get bean name; not set on MethodInvocation: " + mi);
        }
        return beanName;
    }

    public static Advisor createAdvisorWithoutIntroduction(String beanName) {
        return new DefaultPointcutAdvisor(new ExposeBeanNameInterceptor(beanName));
    }

    public static Advisor createAdvisorIntroducingNamedBean(String beanName) {
        return new DefaultIntroductionAdvisor(new ExposeBeanNameIntroduction(beanName));
    }

    private static class ExposeBeanNameIntroduction
    extends DelegatingIntroductionInterceptor
    implements NamedBean {
        private final String beanName;

        public ExposeBeanNameIntroduction(String beanName) {
            this.beanName = beanName;
        }

        @Override
        @Nullable
        public Object invoke(MethodInvocation mi) throws Throwable {
            if (!(mi instanceof ProxyMethodInvocation)) {
                throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
            }
            ProxyMethodInvocation pmi = (ProxyMethodInvocation)mi;
            pmi.setUserAttribute(BEAN_NAME_ATTRIBUTE, this.beanName);
            return super.invoke(mi);
        }

        @Override
        public String getBeanName() {
            return this.beanName;
        }
    }

    private static class ExposeBeanNameInterceptor
    implements MethodInterceptor {
        private final String beanName;

        public ExposeBeanNameInterceptor(String beanName) {
            this.beanName = beanName;
        }

        @Override
        @Nullable
        public Object invoke(MethodInvocation mi) throws Throwable {
            if (!(mi instanceof ProxyMethodInvocation)) {
                throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
            }
            ProxyMethodInvocation pmi = (ProxyMethodInvocation)mi;
            pmi.setUserAttribute(BEAN_NAME_ATTRIBUTE, this.beanName);
            return mi.proceed();
        }
    }
}

