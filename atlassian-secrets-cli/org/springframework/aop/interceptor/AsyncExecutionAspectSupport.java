/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.interceptor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;

public abstract class AsyncExecutionAspectSupport
implements BeanFactoryAware {
    public static final String DEFAULT_TASK_EXECUTOR_BEAN_NAME = "taskExecutor";
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final Map<Method, AsyncTaskExecutor> executors = new ConcurrentHashMap<Method, AsyncTaskExecutor>(16);
    @Nullable
    private volatile Executor defaultExecutor;
    private AsyncUncaughtExceptionHandler exceptionHandler;
    @Nullable
    private BeanFactory beanFactory;

    public AsyncExecutionAspectSupport(@Nullable Executor defaultExecutor) {
        this(defaultExecutor, new SimpleAsyncUncaughtExceptionHandler());
    }

    public AsyncExecutionAspectSupport(@Nullable Executor defaultExecutor, AsyncUncaughtExceptionHandler exceptionHandler) {
        this.defaultExecutor = defaultExecutor;
        this.exceptionHandler = exceptionHandler;
    }

    public void setExecutor(Executor defaultExecutor) {
        this.defaultExecutor = defaultExecutor;
    }

    public void setExceptionHandler(AsyncUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    protected AsyncTaskExecutor determineAsyncExecutor(Method method) {
        AsyncTaskExecutor executor = this.executors.get(method);
        if (executor == null) {
            Executor targetExecutor;
            String qualifier = this.getExecutorQualifier(method);
            if (StringUtils.hasLength(qualifier)) {
                targetExecutor = this.findQualifiedExecutor(this.beanFactory, qualifier);
            } else {
                targetExecutor = this.defaultExecutor;
                if (targetExecutor == null) {
                    Map<Method, AsyncTaskExecutor> map = this.executors;
                    synchronized (map) {
                        if (this.defaultExecutor == null) {
                            this.defaultExecutor = this.getDefaultExecutor(this.beanFactory);
                        }
                        targetExecutor = this.defaultExecutor;
                    }
                }
            }
            if (targetExecutor == null) {
                return null;
            }
            executor = targetExecutor instanceof AsyncListenableTaskExecutor ? (AsyncListenableTaskExecutor)targetExecutor : new TaskExecutorAdapter(targetExecutor);
            this.executors.put(method, executor);
        }
        return executor;
    }

    @Nullable
    protected abstract String getExecutorQualifier(Method var1);

    @Nullable
    protected Executor findQualifiedExecutor(@Nullable BeanFactory beanFactory, String qualifier) {
        if (beanFactory == null) {
            throw new IllegalStateException("BeanFactory must be set on " + this.getClass().getSimpleName() + " to access qualified executor '" + qualifier + "'");
        }
        return BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, Executor.class, qualifier);
    }

    @Nullable
    protected Executor getDefaultExecutor(@Nullable BeanFactory beanFactory) {
        if (beanFactory != null) {
            try {
                return beanFactory.getBean(TaskExecutor.class);
            }
            catch (NoUniqueBeanDefinitionException ex) {
                this.logger.debug("Could not find unique TaskExecutor bean", ex);
                try {
                    return beanFactory.getBean(DEFAULT_TASK_EXECUTOR_BEAN_NAME, Executor.class);
                }
                catch (NoSuchBeanDefinitionException ex2) {
                    if (this.logger.isInfoEnabled()) {
                        this.logger.info("More than one TaskExecutor bean found within the context, and none is named 'taskExecutor'. Mark one of them as primary or name it 'taskExecutor' (possibly as an alias) in order to use it for async processing: " + ex.getBeanNamesFound());
                    }
                }
            }
            catch (NoSuchBeanDefinitionException ex) {
                this.logger.debug("Could not find default TaskExecutor bean", ex);
                try {
                    return beanFactory.getBean(DEFAULT_TASK_EXECUTOR_BEAN_NAME, Executor.class);
                }
                catch (NoSuchBeanDefinitionException ex2) {
                    this.logger.info("No task executor bean found for async processing: no bean of type TaskExecutor and no bean named 'taskExecutor' either");
                }
            }
        }
        return null;
    }

    @Nullable
    protected Object doSubmit(Callable<Object> task, AsyncTaskExecutor executor, Class<?> returnType) {
        if (CompletableFuture.class.isAssignableFrom(returnType)) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return task.call();
                }
                catch (Throwable ex) {
                    throw new CompletionException(ex);
                }
            }, executor);
        }
        if (ListenableFuture.class.isAssignableFrom(returnType)) {
            return ((AsyncListenableTaskExecutor)executor).submitListenable(task);
        }
        if (Future.class.isAssignableFrom(returnType)) {
            return executor.submit(task);
        }
        executor.submit(task);
        return null;
    }

    protected void handleError(Throwable ex, Method method, Object ... params) throws Exception {
        if (Future.class.isAssignableFrom(method.getReturnType())) {
            ReflectionUtils.rethrowException(ex);
        } else {
            try {
                this.exceptionHandler.handleUncaughtException(ex, method, params);
            }
            catch (Throwable ex2) {
                this.logger.error("Exception handler for async method '" + method.toGenericString() + "' threw unexpected exception itself", ex2);
            }
        }
    }
}

