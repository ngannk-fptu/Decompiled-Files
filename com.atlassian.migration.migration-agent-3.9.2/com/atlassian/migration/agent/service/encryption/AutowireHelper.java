/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.atlassian.migration.agent.service.encryption;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public final class AutowireHelper
implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static void autowire(Object classToAutowire, Object ... beansToAutowireInClass) {
        for (Object bean : beansToAutowireInClass) {
            if (bean != null) continue;
            applicationContext.getAutowireCapableBeanFactory().autowireBean(classToAutowire);
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        AutowireHelper.applicationContext = applicationContext;
    }
}

