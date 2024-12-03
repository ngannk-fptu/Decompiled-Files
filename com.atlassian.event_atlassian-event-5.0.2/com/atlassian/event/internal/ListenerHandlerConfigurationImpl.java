/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 */
package com.atlassian.event.internal;

import com.atlassian.event.config.ListenerHandlersConfiguration;
import com.atlassian.event.internal.AnnotatedMethodsListenerHandler;
import com.atlassian.event.spi.ListenerHandler;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nonnull;

public class ListenerHandlerConfigurationImpl
implements ListenerHandlersConfiguration {
    @Override
    @Nonnull
    public List<ListenerHandler> getListenerHandlers() {
        return Lists.newArrayList((Object[])new ListenerHandler[]{new AnnotatedMethodsListenerHandler()});
    }
}

