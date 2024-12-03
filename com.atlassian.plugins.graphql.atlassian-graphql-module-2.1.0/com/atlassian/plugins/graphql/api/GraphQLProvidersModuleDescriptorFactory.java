/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory
 *  com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory
 *  com.atlassian.plugin.spring.scanner.annotation.export.ModuleType
 *  com.google.common.base.Preconditions
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.plugins.graphql.api;

import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ModuleType;
import com.atlassian.plugins.graphql.api.GraphQLProvidersModuleDescriptor;
import com.google.common.base.Preconditions;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ModuleType(value={ListableModuleDescriptorFactory.class})
public class GraphQLProvidersModuleDescriptorFactory
extends SingleModuleDescriptorFactory<GraphQLProvidersModuleDescriptor> {
    private static final String TYPE = "graphql";

    @Inject
    public GraphQLProvidersModuleDescriptorFactory(HostContainer hostContainer) {
        super((HostContainer)Preconditions.checkNotNull((Object)hostContainer), TYPE, GraphQLProvidersModuleDescriptor.class);
    }
}

