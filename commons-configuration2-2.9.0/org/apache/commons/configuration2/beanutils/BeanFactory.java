/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.beanutils;

import org.apache.commons.configuration2.beanutils.BeanCreationContext;

public interface BeanFactory {
    public Object createBean(BeanCreationContext var1) throws Exception;

    public Class<?> getDefaultBeanClass();
}

