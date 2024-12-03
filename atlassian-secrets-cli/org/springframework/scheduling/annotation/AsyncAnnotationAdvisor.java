/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.annotation;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.AnnotationAsyncExecutionInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class AsyncAnnotationAdvisor
extends AbstractPointcutAdvisor
implements BeanFactoryAware {
    private AsyncUncaughtExceptionHandler exceptionHandler;
    private Advice advice;
    private Pointcut pointcut;

    public AsyncAnnotationAdvisor() {
        this(null, null);
    }

    public AsyncAnnotationAdvisor(@Nullable Executor executor, @Nullable AsyncUncaughtExceptionHandler exceptionHandler) {
        LinkedHashSet<Class<? extends Annotation>> asyncAnnotationTypes = new LinkedHashSet<Class<? extends Annotation>>(2);
        asyncAnnotationTypes.add(Async.class);
        try {
            asyncAnnotationTypes.add(ClassUtils.forName("javax.ejb.Asynchronous", AsyncAnnotationAdvisor.class.getClassLoader()));
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        this.exceptionHandler = exceptionHandler != null ? exceptionHandler : new SimpleAsyncUncaughtExceptionHandler();
        this.advice = this.buildAdvice(executor, this.exceptionHandler);
        this.pointcut = this.buildPointcut(asyncAnnotationTypes);
    }

    @Deprecated
    public void setTaskExecutor(Executor executor) {
        this.advice = this.buildAdvice(executor, this.exceptionHandler);
    }

    public void setAsyncAnnotationType(Class<? extends Annotation> asyncAnnotationType) {
        Assert.notNull(asyncAnnotationType, "'asyncAnnotationType' must not be null");
        HashSet<Class<? extends Annotation>> asyncAnnotationTypes = new HashSet<Class<? extends Annotation>>();
        asyncAnnotationTypes.add(asyncAnnotationType);
        this.pointcut = this.buildPointcut(asyncAnnotationTypes);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware)((Object)this.advice)).setBeanFactory(beanFactory);
        }
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    protected Advice buildAdvice(@Nullable Executor executor, AsyncUncaughtExceptionHandler exceptionHandler) {
        return new AnnotationAsyncExecutionInterceptor(executor, exceptionHandler);
    }

    protected Pointcut buildPointcut(Set<Class<? extends Annotation>> asyncAnnotationTypes) {
        Pointcut result = null;
        for (Class<? extends Annotation> asyncAnnotationType : asyncAnnotationTypes) {
            AnnotationMatchingPointcut cpc = new AnnotationMatchingPointcut(asyncAnnotationType, true);
            AnnotationMatchingPointcut mpc = new AnnotationMatchingPointcut(null, asyncAnnotationType, true);
            if (result == null) {
                result = new ComposablePointcut(cpc);
            } else {
                ((ComposablePointcut)result).union(cpc);
            }
            result = ((ComposablePointcut)result).union(mpc);
        }
        return result != null ? result : Pointcut.TRUE;
    }
}

