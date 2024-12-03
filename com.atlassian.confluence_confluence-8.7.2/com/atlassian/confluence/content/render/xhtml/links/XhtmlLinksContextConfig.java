/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.links;

import com.atlassian.confluence.content.render.xhtml.links.AbsoluteHrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.links.DefaultHrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.links.HtmlExportHrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.links.OutputTypeAwareHrefEvaluator;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class XhtmlLinksContextConfig {
    @Resource
    private PermissionManager permissionManager;
    @Resource
    private ContextPathHolder contextPathHolder;
    @Resource
    private SettingsManager settingsManager;

    XhtmlLinksContextConfig() {
    }

    @Bean
    DefaultHrefEvaluator defaultHrefEvaluator() {
        return new DefaultHrefEvaluator(this.contextPathHolder, this.permissionManager);
    }

    @Bean
    AbsoluteHrefEvaluator absoluteHrefEvaluator() {
        return new AbsoluteHrefEvaluator(this.defaultHrefEvaluator(), this.settingsManager, this.contextPathHolder);
    }

    @Bean
    HtmlExportHrefEvaluator htmlExportHrefEvaluator() {
        return new HtmlExportHrefEvaluator(this.defaultHrefEvaluator(), this.absoluteHrefEvaluator());
    }

    @Bean
    OutputTypeAwareHrefEvaluator hrefEvaluator() {
        return new OutputTypeAwareHrefEvaluator((Map<String, HrefEvaluator>)ImmutableMap.of((Object)"html_export", (Object)this.htmlExportHrefEvaluator(), (Object)"email", (Object)this.absoluteHrefEvaluator(), (Object)"feed", (Object)this.absoluteHrefEvaluator()), this.defaultHrefEvaluator());
    }
}

