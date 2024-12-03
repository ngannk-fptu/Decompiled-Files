/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.StreamableMacroAdapter
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.atlassian.webresource.api.assembler.RequiredResources
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.plugins.expand;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.StreamableMacroAdapter;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.webresource.api.assembler.RequiredResources;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class ExpandMacro
extends StreamableMacroAdapter {
    private static final String SOY_TEMPLATE_MODULE = "com.atlassian.confluence.plugins.expand-macro:expand-macro-soy-templates";
    private final I18nResolver i18nResolver;
    private final PageBuilderService pageBuilderService;
    private final TemplateRenderer templateRenderer;

    public ExpandMacro(I18nResolver i18nResolver, PageBuilderService pageBuilderService, TemplateRenderer templateRenderer) {
        this.i18nResolver = i18nResolver;
        this.pageBuilderService = pageBuilderService;
        this.templateRenderer = templateRenderer;
    }

    public Streamable executeToStream(Map<String, String> parameters, Streamable body, ConversionContext context) {
        boolean forMobile = ExpandMacro.isForMobile(context);
        boolean forDynamicDisplay = ExpandMacro.isForDynamicDisplay(context);
        RequiredResources requiredResources = this.pageBuilderService.assembler().resources();
        requiredResources.requireContext(forMobile ? "atl.confluence.macros.expand.mobile" : "atl.confluence.macros.expand.desktop");
        String defaultTitleKey = forMobile ? "expand-macro.mobile.default-title" : "expand-macro.default-title";
        ImmutableMap templateModel = ImmutableMap.of((Object)"title", (Object)this.getTitle(parameters, defaultTitleKey), (Object)"toggleId", (Object)this.getToggleId());
        if (!forDynamicDisplay) {
            requiredResources.requireWebResource("com.atlassian.confluence.plugins.expand-macro:expand-macro-css-only");
        }
        String beforeTemplateName = forDynamicDisplay ? "Confluence.ExpandMacro.dynamicBefore.soy" : "Confluence.ExpandMacro.staticBefore.soy";
        String afterTemplateName = "Confluence.ExpandMacro.after.soy";
        return arg_0 -> this.lambda$executeToStream$0(beforeTemplateName, (Map)templateModel, body, arg_0);
    }

    private static boolean isForMobile(ConversionContext context) {
        return "mobile".equals(context.getOutputDeviceType());
    }

    private static boolean isForDynamicDisplay(ConversionContext context) {
        return Sets.newHashSet((Object[])new String[]{"display", "preview"}).contains(context.getOutputType());
    }

    private String getTitle(Map<String, String> parameters, String defaultTitleKey) {
        if (parameters.containsKey("title")) {
            return parameters.get("title");
        }
        if (parameters.containsKey("0")) {
            return parameters.get("0");
        }
        return this.i18nResolver.getText(defaultTitleKey);
    }

    private String getToggleId() {
        return String.valueOf((int)(Math.random() * 2.147483647E9));
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.RICH_TEXT;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    private /* synthetic */ void lambda$executeToStream$0(String beforeTemplateName, Map templateModel, Streamable body, Writer writer) throws IOException {
        this.templateRenderer.renderTo((Appendable)writer, SOY_TEMPLATE_MODULE, beforeTemplateName, templateModel);
        body.writeTo(writer);
        this.templateRenderer.renderTo((Appendable)writer, SOY_TEMPLATE_MODULE, "Confluence.ExpandMacro.after.soy", templateModel);
    }
}

