/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Streamables
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.StreamableMacro
 *  com.atlassian.confluence.macro.StreamableMacroAdapter
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  org.apache.commons.lang3.BooleanUtils
 */
package com.atlassian.confluence.extra.masterdetail;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.StreamableMacro;
import com.atlassian.confluence.macro.StreamableMacroAdapter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;

public class DetailsMacro
extends BaseMacro
implements StreamableMacro {
    public static final String MASTERDETAIL_RESOURCES = "confluence.extra.masterdetail:master-details-resources";
    private final PageBuilderService pageBuilderService;
    private final I18nResolver i18nResolver;

    public DetailsMacro(@ComponentImport PageBuilderService pageBuilderService, @ComponentImport I18nResolver i18nResolver) {
        this.pageBuilderService = pageBuilderService;
        this.i18nResolver = i18nResolver;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)parameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException macroError) {
            throw new MacroException((Throwable)macroError);
        }
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.ALL;
    }

    public boolean hasBody() {
        return true;
    }

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.BLOCK;
    }

    public Streamable executeToStream(Map<String, String> parameters, Streamable body, ConversionContext context) throws MacroExecutionException {
        String id = parameters.get("id");
        if (id != null && id.length() > 256) {
            return Streamables.from((String)RenderUtils.blockError((String)this.i18nResolver.getText("details.error.id.length"), (String)""));
        }
        if (!BooleanUtils.toBoolean((String)parameters.get("hidden"))) {
            this.pageBuilderService.assembler().resources().requireWebResource(MASTERDETAIL_RESOURCES);
            return writer -> {
                writer.write("<div class='plugin-tabmeta-details'>");
                body.writeTo(writer);
                writer.write("</div>");
            };
        }
        return Streamables.empty();
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        return StreamableMacroAdapter.executeFromStream((StreamableMacro)this, parameters, (String)body, (ConversionContext)context);
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.RICH_TEXT;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }
}

