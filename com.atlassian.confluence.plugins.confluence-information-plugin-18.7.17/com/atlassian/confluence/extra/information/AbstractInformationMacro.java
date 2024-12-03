/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.StreamableMacro
 *  com.atlassian.confluence.macro.StreamableMacroAdapter
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.renderer.v2.macro.WysiwygBodyType
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.atlassian.webresource.api.assembler.RequiredResources
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.information;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.StreamableMacro;
import com.atlassian.confluence.macro.StreamableMacroAdapter;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.WysiwygBodyType;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.webresource.api.assembler.RequiredResources;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractInformationMacro
extends BaseMacro
implements StreamableMacro {
    private final PageBuilderService pageBuilderService;
    private final TemplateRenderer templateRenderer;

    AbstractInformationMacro(PageBuilderService pageBuilderService, TemplateRenderer templateRenderer) {
        this.pageBuilderService = pageBuilderService;
        this.templateRenderer = templateRenderer;
    }

    public Streamable executeToStream(Map<String, String> parameters, Streamable body, ConversionContext conversionContext) {
        this.includeRequiresResources(conversionContext);
        HashMap data = Maps.newHashMap();
        data.put("title", parameters.get("title"));
        data.put("useIcon", AbstractInformationMacro.isUseIcon(parameters));
        data.put("class", this.getCssClass());
        data.put("auiMessageClass", this.getAuiMessageClass());
        data.put("auiIconClass", this.getAuiIconClass());
        return writer -> {
            this.templateRenderer.renderTo((Appendable)writer, "confluence.extra.information:soy-templates", "Confluence.InformationMacro.before.soy", data);
            body.writeTo(writer);
            this.templateRenderer.renderTo((Appendable)writer, "confluence.extra.information:soy-templates", "Confluence.InformationMacro.after.soy", data);
        };
    }

    private void includeRequiresResources(ConversionContext conversionContext) {
        RequiredResources requiredResources = this.pageBuilderService.assembler().resources();
        requiredResources.requireWebResource("confluence.extra.information:information-plugin-adg-styles");
        if ("mobile".equals(conversionContext.getOutputDeviceType())) {
            requiredResources.requireWebResource("confluence.extra.information:information-plugin-mobile-styles");
        }
    }

    public String execute(Map<String, String> map, String s, ConversionContext conversionContext) throws MacroExecutionException {
        return StreamableMacroAdapter.executeFromStream((StreamableMacro)this, map, (String)s, (ConversionContext)conversionContext);
    }

    private static boolean isUseIcon(Map<String, String> parameters) {
        String useIconParam = parameters.get("icon");
        return StringUtils.isBlank((CharSequence)useIconParam) || Boolean.parseBoolean(useIconParam);
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.RICH_TEXT;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    protected abstract String getCssClass();

    protected abstract String getAuiMessageClass();

    protected abstract String getAuiIconClass();

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)parameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException e) {
            throw new MacroException((Throwable)e);
        }
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.ALL;
    }

    public WysiwygBodyType getWysiwygBodyType() {
        return WysiwygBodyType.WIKI_MARKUP;
    }

    public boolean hasBody() {
        return true;
    }

    public boolean isInline() {
        return false;
    }

    public TokenType getTokenType(Map map, String s, RenderContext renderContext) {
        return TokenType.BLOCK;
    }
}

