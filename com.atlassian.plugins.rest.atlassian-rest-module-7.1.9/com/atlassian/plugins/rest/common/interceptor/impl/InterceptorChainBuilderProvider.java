/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 */
package com.atlassian.plugins.rest.common.interceptor.impl;

import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugins.rest.common.expand.interceptor.ExpandInterceptor;
import com.atlassian.plugins.rest.common.interceptor.impl.InterceptorChainBuilder;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import java.lang.reflect.Type;
import javax.ws.rs.core.Context;

public class InterceptorChainBuilderProvider
extends SingletonTypeInjectableProvider<Context, InterceptorChainBuilder> {
    public InterceptorChainBuilderProvider(ContainerManagedPlugin plugin, ExpandInterceptor expandInterceptor) {
        super((Type)((Object)InterceptorChainBuilder.class), new InterceptorChainBuilder(plugin, expandInterceptor));
    }
}

