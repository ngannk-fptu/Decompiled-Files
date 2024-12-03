/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.target;

import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.aop.target.AbstractPrototypeBasedTargetSource;
import org.springframework.aop.target.PoolingConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.lang.Nullable;

public abstract class AbstractPoolingTargetSource
extends AbstractPrototypeBasedTargetSource
implements PoolingConfig,
DisposableBean {
    private int maxSize = -1;

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public int getMaxSize() {
        return this.maxSize;
    }

    @Override
    public final void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        super.setBeanFactory(beanFactory);
        try {
            this.createPool();
        }
        catch (Throwable ex) {
            throw new BeanInitializationException("Could not create instance pool for TargetSource", ex);
        }
    }

    protected abstract void createPool() throws Exception;

    @Override
    @Nullable
    public abstract Object getTarget() throws Exception;

    @Override
    public abstract void releaseTarget(Object var1) throws Exception;

    public DefaultIntroductionAdvisor getPoolingConfigMixin() {
        DelegatingIntroductionInterceptor dii = new DelegatingIntroductionInterceptor(this);
        return new DefaultIntroductionAdvisor(dii, PoolingConfig.class);
    }
}

