/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanFactory;

public interface BeanFactoryAware
extends Aware {
    public void setBeanFactory(BeanFactory var1) throws BeansException;
}

