/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  javax.annotation.Resource
 *  org.apache.struts2.views.velocity.VelocityManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Lazy
 */
package com.atlassian.confluence.impl.sitemesh;

import com.atlassian.confluence.setup.sitemesh.ConfluenceSpaceDecoratorMapper;
import com.atlassian.confluence.setup.sitemesh.PluginDecoratorMapper;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.plugin.PluginAccessor;
import javax.annotation.Resource;
import org.apache.struts2.views.velocity.VelocityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Lazy
@Configuration
public class SitemeshBeans {
    @Resource
    private ThemeManager themeManager;
    @Resource
    private VelocityManager velocityManager;
    @Resource
    private PluginAccessor pluginAccessor;

    @Bean
    ConfluenceSpaceDecoratorMapper spaceDecoratorMapper() {
        return new ConfluenceSpaceDecoratorMapper(this.themeManager, this.velocityManager);
    }

    @Bean
    PluginDecoratorMapper pluginDecoratorMapper() {
        return new PluginDecoratorMapper(this.pluginAccessor, this.velocityManager);
    }
}

