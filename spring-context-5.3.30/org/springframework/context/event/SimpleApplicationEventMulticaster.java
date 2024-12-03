/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.core.ResolvableType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ErrorHandler
 */
package org.springframework.context.event;

import java.util.concurrent.Executor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.AbstractApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.ErrorHandler;

public class SimpleApplicationEventMulticaster
extends AbstractApplicationEventMulticaster {
    @Nullable
    private Executor taskExecutor;
    @Nullable
    private ErrorHandler errorHandler;
    @Nullable
    private volatile Log lazyLogger;

    public SimpleApplicationEventMulticaster() {
    }

    public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
        this.setBeanFactory(beanFactory);
    }

    public void setTaskExecutor(@Nullable Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Nullable
    protected Executor getTaskExecutor() {
        return this.taskExecutor;
    }

    public void setErrorHandler(@Nullable ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Nullable
    protected ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    @Override
    public void multicastEvent(ApplicationEvent event) {
        this.multicastEvent(event, this.resolveDefaultEventType(event));
    }

    @Override
    public void multicastEvent(ApplicationEvent event, @Nullable ResolvableType eventType) {
        ResolvableType type = eventType != null ? eventType : this.resolveDefaultEventType(event);
        Executor executor = this.getTaskExecutor();
        for (ApplicationListener<?> listener : this.getApplicationListeners(event, type)) {
            if (executor != null) {
                executor.execute(() -> this.invokeListener(listener, event));
                continue;
            }
            this.invokeListener(listener, event);
        }
    }

    private ResolvableType resolveDefaultEventType(ApplicationEvent event) {
        return ResolvableType.forInstance((Object)event);
    }

    protected void invokeListener(ApplicationListener<?> listener, ApplicationEvent event) {
        ErrorHandler errorHandler = this.getErrorHandler();
        if (errorHandler != null) {
            try {
                this.doInvokeListener(listener, event);
            }
            catch (Throwable err) {
                errorHandler.handleError(err);
            }
        } else {
            this.doInvokeListener(listener, event);
        }
    }

    private void doInvokeListener(ApplicationListener listener, ApplicationEvent event) {
        try {
            listener.onApplicationEvent(event);
        }
        catch (ClassCastException ex) {
            String msg = ex.getMessage();
            if (msg == null || this.matchesClassCastMessage(msg, event.getClass()) || event instanceof PayloadApplicationEvent && this.matchesClassCastMessage(msg, ((PayloadApplicationEvent)event).getPayload().getClass())) {
                Log loggerToUse = this.lazyLogger;
                if (loggerToUse == null) {
                    this.lazyLogger = loggerToUse = LogFactory.getLog(this.getClass());
                }
                if (loggerToUse.isTraceEnabled()) {
                    loggerToUse.trace((Object)("Non-matching event type for listener: " + listener), (Throwable)ex);
                }
            }
            throw ex;
        }
    }

    private boolean matchesClassCastMessage(String classCastMessage, Class<?> eventClass) {
        if (classCastMessage.startsWith(eventClass.getName())) {
            return true;
        }
        if (classCastMessage.startsWith(eventClass.toString())) {
            return true;
        }
        int moduleSeparatorIndex = classCastMessage.indexOf(47);
        return moduleSeparatorIndex != -1 && classCastMessage.startsWith(eventClass.getName(), moduleSeparatorIndex + 1);
    }
}

