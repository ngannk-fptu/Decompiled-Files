/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.Macro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.renderer.v2.macro.WysiwygBodyType
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 */
package com.atlassian.confluence.plugins.expand;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugins.expand.ExpandMacro;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.WysiwygBodyType;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.util.Map;

public class ExpandV2Macro
implements Macro {
    private final ExpandMacro expandMacro;

    public ExpandV2Macro(I18nResolver i18nResolver, PageBuilderService pageBuilderService, TemplateRenderer templateRenderer) {
        this.expandMacro = new ExpandMacro(i18nResolver, pageBuilderService, templateRenderer);
    }

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.BLOCK;
    }

    public boolean isInline() {
        return false;
    }

    public boolean hasBody() {
        return true;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.ALL;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.expandMacro.execute(parameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException ex) {
            throw new MacroException(ex.getMessage(), ex.getCause());
        }
    }

    public boolean suppressSurroundingTagDuringWysiwygRendering() {
        return false;
    }

    public boolean suppressMacroRenderingDuringWysiwyg() {
        return false;
    }

    public WysiwygBodyType getWysiwygBodyType() {
        return WysiwygBodyType.WIKI_MARKUP;
    }
}

