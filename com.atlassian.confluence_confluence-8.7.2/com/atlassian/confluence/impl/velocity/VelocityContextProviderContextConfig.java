/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.velocity;

import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.impl.velocity.ReadOnlyBeanContextItemProvider;
import com.atlassian.confluence.internal.user.UserAccessorInternal;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceService;
import com.atlassian.confluence.setup.settings.SpaceSettingsManager;
import com.atlassian.confluence.setup.velocity.ClusterContextItemProvider;
import com.atlassian.confluence.setup.velocity.ContextItemProviderChain;
import com.atlassian.confluence.setup.velocity.DynamicContextItemProvider;
import com.atlassian.confluence.setup.velocity.NamedBeanContextItemProvider;
import com.atlassian.confluence.setup.velocity.PluginContextItemProvider;
import com.atlassian.confluence.setup.velocity.PrototypeBeanContextItemProvider;
import com.atlassian.confluence.setup.velocity.WebResourceContextItemProvider;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VelocityContextProviderContextConfig {
    @Resource
    PluginAccessor pluginAccessor;
    @Resource
    ClusterManager clusterManager;
    @Resource
    PageBuilderService pageBuilderService;
    @Resource
    WebResourceAssemblerFactory webResourceAssemblerFactory;
    @Resource
    WebResourceUrlProvider webResourceUrlProvider;
    @Resource
    ConfluenceWebResourceService confluenceWebResourceService;
    @Resource
    LocaleManager localeManager;
    @Resource
    SpaceSettingsManager spaceSettingsManager;
    @Resource
    UserAccessorInternal userAccessor;
    @Resource
    PersonService apiPersonService;
    @Resource
    WebInterfaceManager webInterfaceManager;
    @Resource
    NamedBeanContextItemProvider velocityContextBeanProvider;
    @Resource
    PrototypeBeanContextItemProvider prototypeBeanContextItemProvider;

    @Bean
    DynamicContextItemProvider dynamicContextItemProvider() {
        return new DynamicContextItemProvider();
    }

    @Bean
    PluginContextItemProvider pluginContextItemProvider() {
        return new PluginContextItemProvider(this.pluginAccessor);
    }

    @Bean
    ClusterContextItemProvider clusterContextItemProvider() {
        return new ClusterContextItemProvider(this.clusterManager);
    }

    @Bean
    WebResourceContextItemProvider webResourceContextItemProvider() {
        return new WebResourceContextItemProvider(this.pageBuilderService, this.webResourceAssemblerFactory, this.webResourceUrlProvider, this.confluenceWebResourceService, this.localeManager);
    }

    @Bean
    ReadOnlyBeanContextItemProvider readOnlyBeanContextItemProvider() {
        return new ReadOnlyBeanContextItemProvider(this.spaceSettingsManager, this.userAccessor, this.apiPersonService, this.webInterfaceManager);
    }

    @Bean
    ContextItemProviderChain velocityContextItemProvider() {
        ContextItemProviderChain contextItemProviderChain = new ContextItemProviderChain();
        contextItemProviderChain.setProviders(List.of(this.pluginContextItemProvider(), this.velocityContextBeanProvider, this.webResourceContextItemProvider(), this.prototypeBeanContextItemProvider, this.readOnlyBeanContextItemProvider(), this.dynamicContextItemProvider(), this.clusterContextItemProvider()));
        return contextItemProviderChain;
    }
}

