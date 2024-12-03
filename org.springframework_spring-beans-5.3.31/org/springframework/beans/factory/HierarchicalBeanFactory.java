/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
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

