/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Maps
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.confluence.plugins.createcontent.TemplateRendererHelper;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={TemplateRendererHelper.class})
public class BlueprintsTemplateRendererHelper
implements TemplateRendererHelper {
    private TemplateRenderer templateRenderer;
    private static final String TEMPLATE_PROVIDER_PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-create-content-plugin:all-resources";
    private static final String MACRO_TEMPLATE = "Confluence.Templates.Shared.macroXhtml.soy";

    @Autowired
    public BlueprintsTemplateRendererHelper(@ComponentImport TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    @Override
    public String renderFromSoy(String pluginKey, String soyTemplate, Map<String, Object> soyContext) {
        StringBuilder output = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)output, pluginKey, soyTemplate, soyContext);
        return output.toString();
    }

    @Override
    public String renderMacroXhtml(String macroName, Map<String, String> parameters) {
        HashMap soyContext = Maps.newHashMap();
        soyContext.put("macroName", macroName);
        soyContext.put("parameters", parameters);
        return this.renderFromSoy(TEMPLATE_PROVIDER_PLUGIN_KEY, MACRO_TEMPLATE, soyContext);
    }
}

