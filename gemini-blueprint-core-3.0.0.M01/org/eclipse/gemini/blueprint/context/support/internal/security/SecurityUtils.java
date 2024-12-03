/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.context.ApplicationContext
 */
package org.eclipse.gemini.blueprint.context.support.internal.security;

import java.security.AccessControlContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;

public abstract class SecurityUtils {
    public static AccessControlContext getAccFrom(BeanFactory beanFactory) {
        AccessControlContext acc = null;
        if (beanFactory != null && beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory)beanFactory).getAccessControlContext();
        }
        return acc;
    }

    public static AccessControlContext getAccFrom(ApplicationContext ac) {
        return ac != null ? SecurityUtils.getAccFrom((BeanFactory)ac.getAutowireCapableBeanFactory()) : null;
    }
}

