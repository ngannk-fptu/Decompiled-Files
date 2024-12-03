/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  com.google.common.annotations.VisibleForTesting
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.confluence.image.effects;

import com.atlassian.confluence.image.effects.ImageEffectsConfig;
import com.atlassian.confluence.image.effects.ImageFilterRejectedExecutionHandler;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.util.concurrent.ThreadFactories;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.beans.factory.FactoryBean;

@Named(value="imageFilterExecutor")
public class ImageFilterExecutor
implements FactoryBean<ExecutorService> {
    @ComponentImport
    @Inject
    @VisibleForTesting
    ThreadLocalDelegateExecutorFactory factory;
    @Inject
    @VisibleForTesting
    ImageEffectsConfig config;

    public Class<?> getObjectType() {
        return ExecutorService.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public ExecutorService getObject() throws Exception {
        ThreadPoolExecutor raw = new ThreadPoolExecutor(this.config.getCorePoolSize(), this.config.getMaximumPoolSize(), this.config.getKeepAliveTime(), this.config.getTimeUnit(), new LinkedBlockingQueue<Runnable>(this.config.getQueueSize()), ThreadFactories.namedThreadFactory((String)"ImageFilterEffect", (ThreadFactories.Type)ThreadFactories.Type.DAEMON), new ImageFilterRejectedExecutionHandler());
        return this.factory.createExecutorService((ExecutorService)raw);
    }
}

