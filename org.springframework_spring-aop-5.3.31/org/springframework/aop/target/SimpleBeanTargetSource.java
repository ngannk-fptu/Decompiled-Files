/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.target;

import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;

public class SimpleBeanTargetSource
extends AbstractBeanFactoryBasedTargetSource {
    @Override
    public Object getTarget() throws Exception {
        return this.getBeanFactory().getBean(this.getTargetBeanName());
    }
}

