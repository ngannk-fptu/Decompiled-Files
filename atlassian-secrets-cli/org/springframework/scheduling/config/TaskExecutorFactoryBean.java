/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.config;

import java.util.concurrent.RejectedExecutionHandler;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

public class TaskExecutorFactoryBean
implements FactoryBean<TaskExecutor>,
BeanNameAware,
InitializingBean,
DisposableBean {
    @Nullable
    private String poolSize;
    @Nullable
    private Integer queueCapacity;
    @Nullable
    private RejectedExecutionHandler rejectedExecutionHandler;
    @Nullable
    private Integer keepAliveSeconds;
    @Nullable
    private String beanName;
    @Nullable
    private ThreadPoolTaskExecutor target;

    public void setPoolSize(String poolSize) {
        this.poolSize = poolSize;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        this.determinePoolSizeRange(executor);
        if (this.queueCapacity != null) {
            executor.setQueueCapacity(this.queueCapacity);
        }
        if (this.keepAliveSeconds != null) {
            executor.setKeepAliveSeconds(this.keepAliveSeconds);
        }
        if (this.rejectedExecutionHandler != null) {
            executor.setRejectedExecutionHandler(this.rejectedExecutionHandler);
        }
        if (this.beanName != null) {
            executor.setThreadNamePrefix(this.beanName + "-");
        }
        executor.afterPropertiesSet();
        this.target = executor;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void determinePoolSizeRange(ThreadPoolTaskExecutor executor) {
        if (!StringUtils.hasText(this.poolSize)) return;
        try {
            int maxPoolSize;
            int corePoolSize;
            int separatorIndex = this.poolSize.indexOf(45);
            if (separatorIndex != -1) {
                corePoolSize = Integer.valueOf(this.poolSize.substring(0, separatorIndex));
                if (corePoolSize > (maxPoolSize = Integer.valueOf(this.poolSize.substring(separatorIndex + 1, this.poolSize.length())).intValue())) {
                    throw new IllegalArgumentException("Lower bound of pool-size range must not exceed the upper bound");
                }
                if (this.queueCapacity == null) {
                    if (corePoolSize != 0) throw new IllegalArgumentException("A non-zero lower bound for the size range requires a queue-capacity value");
                    executor.setAllowCoreThreadTimeOut(true);
                    corePoolSize = maxPoolSize;
                }
            } else {
                Integer value = Integer.valueOf(this.poolSize);
                corePoolSize = value;
                maxPoolSize = value;
            }
            executor.setCorePoolSize(corePoolSize);
            executor.setMaxPoolSize(maxPoolSize);
            return;
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid pool-size value [" + this.poolSize + "]: only single maximum integer (e.g. \"5\") and minimum-maximum range (e.g. \"3-5\") are supported", ex);
        }
    }

    @Override
    @Nullable
    public TaskExecutor getObject() {
        return this.target;
    }

    @Override
    public Class<? extends TaskExecutor> getObjectType() {
        return this.target != null ? this.target.getClass() : ThreadPoolTaskExecutor.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        if (this.target != null) {
            this.target.destroy();
        }
    }
}

