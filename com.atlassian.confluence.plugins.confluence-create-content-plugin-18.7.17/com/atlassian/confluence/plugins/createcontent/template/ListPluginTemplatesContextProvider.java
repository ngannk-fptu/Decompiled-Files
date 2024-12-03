/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.confluence.plugins.createcontent.template;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.plugins.createcontent.template.PluginPageTemplateHelper;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.Map;

public class ListPluginTemplatesContextProvider
implements ContextProvider {
    private final PluginPageTemplateHelper pluginPageTemplateHelper;
    private final ContextPathHolder contextPathHolder;
    private final I18nResolver i18nResolver;

    public ListPluginTemplatesContextProvider(PluginPageTemplateHelper pluginPageTemplateHelper, @ComponentImport ContextPathHolder contextPathHolder, @ComponentImport I18nResolver i18nResolver) {
        this.pluginPageTemplateHelper = pluginPageTemplateHelper;
        this.contextPathHolder = contextPathHolder;
        this.i18nResolver = i18nResolver;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        context.put("systemTemplates", this.pluginPageTemplateHelper.getSystemPageTemplates());
        context.put("contextPath", this.contextPathHolder.getContextPath());
        context.put("i18nResolver", this.i18nResolver);
        return context;
    }
}

