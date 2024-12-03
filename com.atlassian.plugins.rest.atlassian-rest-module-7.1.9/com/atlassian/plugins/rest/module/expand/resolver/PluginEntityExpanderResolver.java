/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 */
package com.atlassian.plugins.rest.module.expand.resolver;

import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugins.rest.common.expand.EntityExpander;
import com.atlassian.plugins.rest.common.expand.Expander;
import com.atlassian.plugins.rest.common.expand.resolver.AbstractAnnotationEntityExpanderResolver;
import java.util.Objects;

public class PluginEntityExpanderResolver
extends AbstractAnnotationEntityExpanderResolver {
    private final ContainerManagedPlugin plugin;

    public PluginEntityExpanderResolver(ContainerManagedPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    protected final EntityExpander<?> getEntityExpander(Expander expander) {
        return (EntityExpander)this.plugin.getContainerAccessor().createBean(expander.value());
    }
}

