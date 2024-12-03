/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;

public interface ApplicationContext
extends EnvironmentCapable,
ListableBeanFactory,
HierarchicalBeanFactory,
MessageSource,
ApplicationEventPublisher,
ResourcePatternResolver {
    @Nullable
    public String getId();

    public String getApplicationName();

    public String getDisplayName();

    public long getStartupDate();

    @Nullable
    public ApplicationContext getParent();

    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;
}

