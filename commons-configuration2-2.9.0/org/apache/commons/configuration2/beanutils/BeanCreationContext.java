/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.beanutils;

import org.apache.commons.configuration2.beanutils.BeanDeclaration;

public interface BeanCreationContext {
    public Class<?> getBeanClass();

    public BeanDeclaration getBeanDeclaration();

    public Object getParameter();

    public void initBean(Object var1, BeanDeclaration var2);

    public Object createBean(BeanDeclaration var1);
}

