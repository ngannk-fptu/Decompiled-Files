/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.aop.framework.autoproxy.target;

import org.springframework.aop.framework.autoproxy.target.AbstractBeanFactoryBasedTargetSourceCreator;
import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.aop.target.PrototypeTargetSource;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.lang.Nullable;

public class QuickTargetSourceCreator
extends AbstractBeanFactoryBasedTargetSourceCreator {
    public static final String PREFIX_COMMONS_POOL = ":";
    public static final String PREFIX_THREAD_LOCAL = "%";
    public static final String PREFIX_PROTOTYPE = "!";

    @Override
    @Nullable
    protected final AbstractBeanFactoryBasedTargetSource createBeanFactoryBasedTargetSource(Class<?> beanClass, String beanName) {
        if (beanName.startsWith(PREFIX_COMMONS_POOL)) {
            CommonsPool2TargetSource cpts = new CommonsPool2TargetSource();
            cpts.setMaxSize(25);
            return cpts;
        }
        if (beanName.startsWith(PREFIX_THREAD_LOCAL)) {
            return new ThreadLocalTargetSource();
        }
        if (beanName.startsWith(PREFIX_PROTOTYPE)) {
            return new PrototypeTargetSource();
        }
        return null;
    }
}

