/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor
 *  org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.function.SingletonSupplier
 */
package org.springframework.scheduling.annotation;

import java.lang.annotation.Annotation;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.AsyncAnnotationAdvisor;
import org.springframework.util.Assert;
import org.springframework.util.function.SingletonSupplier;

public class AsyncAnnotationBeanPostProcessor
extends AbstractBeanFactoryAwareAdvisingPostProcessor {
    public static final String DEFAULT_TASK_EXECUTOR_BEAN_NAME = "taskExecutor";
    protected final Log logger = LogFactory.getLog(((Object)((Object)this)).getClass());
    @Nullable
    private Supplier<Executor> executor;
    @Nullable
    private Supplier<AsyncUncaughtExceptionHandler> exceptionHandler;
    @Nullable
    private Class<? extends Annotation> asyncAnnotationType;

    public AsyncAnnotationBeanPostProcessor() {
        this.setBeforeExistingAdvisors(true);
    }

    public void configure(@Nullable Supplier<Executor> executor, @Nullable Supplier<AsyncUncaughtExceptionHandler> exceptionHandler) {
        this.executor = executor;
        this.exceptionHandler = exceptionHandler;
    }

    public void setExecutor(Executor executor) {
        this.executor = SingletonSupplier.of((Object)executor);
    }

    public void setExceptionHandler(AsyncUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = SingletonSupplier.of((Object)exceptionHandler);
    }

    public void setAsyncAnnotationType(Class<? extends Annotation> asyncAnnotationType) {
        Assert.notNull(asyncAnnotationType, (String)"'asyncAnnotationType' must not be null");
        this.asyncAnnotationType = asyncAnnotationType;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        AsyncAnnotationAdvisor advisor = new AsyncAnnotationAdvisor(this.executor, this.exceptionHandler);
        if (this.asyncAnnotationType != null) {
            advisor.setAsyncAnnotationType(this.asyncAnnotationType);
        }
        advisor.setBeanFactory(beanFactory);
        this.advisor = advisor;
    }
}

