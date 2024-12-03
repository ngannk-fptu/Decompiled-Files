/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  com.atlassian.renderer.v2.macro.MacroManager
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.confluence.macro.V2CompatibilityModuleDescriptorPredicate;
import com.atlassian.confluence.macro.xhtml.DelegatingReadOnlyMacroManager;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.macro.xhtml.MacroManagerFactory;
import com.atlassian.confluence.macro.xhtml.MacroManagerFactoryImpl;
import com.atlassian.confluence.macro.xhtml.UserMacroLibraryMacroManager;
import com.atlassian.confluence.macro.xhtml.UserMacroPluginMacroManager;
import com.atlassian.confluence.macro.xhtml.V2CompatibilityMacroManager;
import com.atlassian.confluence.macro.xhtml.XhtmlMacroManager;
import com.atlassian.confluence.renderer.DefaultMacroManager;
import com.atlassian.confluence.renderer.UserMacroLibrary;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.spring.AvailableToPlugins;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MacroManagerConfig {
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private PluginEventManager pluginEventManager;
    @Resource
    private MacroManager v2CompatibilityMacroManager;
    @Resource
    private UserMacroLibrary userMacroLibrary;

    MacroManagerConfig() {
    }

    @Bean
    @AvailableToPlugins(interfaces={com.atlassian.renderer.v2.macro.MacroManager.class, com.atlassian.confluence.renderer.MacroManager.class})
    com.atlassian.renderer.v2.macro.MacroManager macroManager() {
        DefaultMacroManager bean = new DefaultMacroManager();
        bean.setUserMacroLibrary(this.userMacroLibrary);
        bean.setPluginEventManager(this.pluginEventManager);
        return bean;
    }

    @Bean
    @AvailableToPlugins
    MacroManagerFactory macroManagerFactory() {
        return new MacroManagerFactoryImpl(this.xhtmlMacroManager(), this.xhtmlOnlyMacroManager(), this.userMacroMacroManager());
    }

    @Bean
    @AvailableToPlugins
    MacroManager xhtmlMacroManager() {
        return new DelegatingReadOnlyMacroManager(List.of(this.xhtmlOnlyMacroManager(), this.v2CompatibilityMacroManager, this.userMacroMacroManager()));
    }

    @Bean
    MacroManager xhtmlOnlyMacroManager() {
        XhtmlMacroManager bean = new XhtmlMacroManager(this.eventPublisher);
        bean.setPluginEventManager(this.pluginEventManager);
        return bean;
    }

    @Bean
    MacroManager userMacroMacroManager() {
        UserMacroPluginMacroManager userMacroPluginMacroManager = new UserMacroPluginMacroManager(this.eventPublisher);
        userMacroPluginMacroManager.setPluginEventManager(this.pluginEventManager);
        return new DelegatingReadOnlyMacroManager(List.of(userMacroPluginMacroManager, new UserMacroLibraryMacroManager(this.userMacroLibrary)));
    }

    @Bean
    V2CompatibilityMacroManager v2CompatibilityMacroManager() {
        V2CompatibilityMacroManager bean = new V2CompatibilityMacroManager(new V2CompatibilityModuleDescriptorPredicate(), this.eventPublisher);
        bean.setPluginEventManager(this.pluginEventManager);
        return bean;
    }
}

