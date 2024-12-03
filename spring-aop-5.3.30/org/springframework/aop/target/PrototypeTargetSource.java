/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 */
package org.springframework.aop.target;

import org.springframework.aop.target.AbstractPrototypeBasedTargetSource;
import org.springframework.beans.BeansException;

public class PrototypeTargetSource
extends AbstractPrototypeBasedTargetSource {
    @Override
    public Object getTarget() throws BeansException {
        return this.newPrototypeInstance();
    }

    @Override
    public void releaseTarget(Object target) {
        this.destroyPrototypeInstance(target);
    }

    @Override
    public String toString() {
        return "PrototypeTargetSource for target bean with name '" + this.getTargetBeanName() + "'";
    }
}

