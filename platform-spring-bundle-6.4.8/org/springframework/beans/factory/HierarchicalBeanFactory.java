/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.lang.Nullable;

public interface HierarchicalBeanFactory
extends BeanFactory {
    @Nullable
    public BeanFactory getParentBeanFactory();

    public boolean containsLocalBean(String var1);
}

