/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.target;

import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;
import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

public class LazyInitTargetSource
extends AbstractBeanFactoryBasedTargetSource {
    @Nullable
    private Object target;

    @Override
    @Nullable
    public synchronized Object getTarget() throws BeansException {
        if (this.target == null) {
            this.target = this.getBeanFactory().getBean(this.getTargetBeanName());
            this.postProcessTargetObject(this.target);
        }
        return this.target;
    }

    protected void postProcessTargetObject(Object targetObject) {
    }
}

