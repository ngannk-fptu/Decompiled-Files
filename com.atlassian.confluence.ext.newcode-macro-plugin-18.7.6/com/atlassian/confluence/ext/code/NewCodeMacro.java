/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.SubRenderer
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.renderer.v2.macro.basic.AbstractPanelMacro
 *  org.apache.commons.lang3.StringUtils
 *  org.jsoup.Jsoup
 *  org.jsoup.nodes.Document
 *  org.jsoup.nodes.Element
 *  org.jsoup.select.Elements
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.ext.code;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.ext.code.render.ContentFormatter;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.basic.AbstractPanelMacro;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NewCodeMacro
extends AbstractPanelMacro
implements Macro {
    private static final Logger LOG = LoggerFactory.getLogger(NewCodeMacro.class);
    private final ContentFormatter contentFormatter;
    private static boolean pdlEnabled = Long.parseLong(GeneralUtil.getBuildNumber()) >= 4000L;

    public NewCodeMacro(ContentFormatter contentFormatter, @ComponentImport SubRenderer subRenderer) {
        this.setSubRenderer(subRenderer);
        this.contentFormatter = contentFormatter;
    }

    protected String getPanelCSSClass() {
        return pdlEnabled ? "code panel pdl" : "code panel";
    }

    protected String getPanelHeaderCSSClass() {
        return pdlEnabled ? "codeHeader panelHeader pdl" : "codeHeader panelHeader";
    }

    protected String getPanelContentCSSClass() {
        return pdlEnabled ? "codeContent panelContent pdl" : "codeContent panelContent";
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public boolean suppressMacroRenderingDuringWysiwyg() {
        return false;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)parameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException e) {
            throw new MacroException((Throwable)e);
        }
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        LOG.debug("Executing newcode macro for body: {}", (Object)body);
        body = GeneralUtil.htmlEncode((String)body);
        try {
            boolean collapse;
            String formatted = this.contentFormatter.formatContent(conversionContext, parameters, body);
            Map<String, String> panelParameters = this.contentFormatter.getPanelParametersWithThemeLayout(parameters);
            String content = super.execute(panelParameters, formatted, (RenderContext)conversionContext.getPageContext());
            boolean bl = collapse = StringUtils.isNotBlank((CharSequence)parameters.get("collapse")) && Boolean.parseBoolean(parameters.get("collapse"));
            if (collapse) {
                content = this.addExpandCollapseHtml(content);
            }
            LOG.debug("Newcode macro execution finished, resulting content: {}", (Object)content);
            return content;
        }
        catch (MacroException e) {
            throw new MacroExecutionException((Throwable)e);
        }
        catch (Exception e) {
            return this.getText("newcode.render.invalid.parameter", e) + "<pre>" + body + "</pre>";
        }
    }

    private String getText(String key, Object ... params) {
        return ConfluenceActionSupport.getTextStatic((String)key, (Object[])params);
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.PLAIN_TEXT;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    public TokenType getTokenType(Map map, String body, RenderContext renderContext) {
        return TokenType.BLOCK;
    }

    protected void setPdlEnabled(boolean pdlEnabled) {
        NewCodeMacro.pdlEnabled = pdlEnabled;
    }

    private String addExpandCollapseHtml(String content) {
        Document doc = Jsoup.parse((String)content);
        Elements headerElements = doc.select("div.codeHeader");
        if (headerElements != null && StringUtils.isNotEmpty((CharSequence)headerElements.toString())) {
            Element headerElement = (Element)headerElements.get(0);
            headerElement.addClass("hide-border-bottom");
            headerElement.child(0).addClass("code-title");
            headerElement.child(0).after(this.addCollapseSourceHtml());
            Element contentElement = (Element)doc.select("div.codeContent").get(0);
            contentElement.addClass("hide-toolbar");
        } else {
            Element contentElement = (Element)doc.select("div.codeContent").get(0);
            contentElement.before(this.addHeaderHtml());
            contentElement.addClass("hide-toolbar");
        }
        return doc.body().html();
    }

    private String addHeaderHtml() {
        return "<div class=\"" + this.getPanelHeaderCSSClass() + " hide-border-bottom\"><b class='code-title'></b>" + this.addCollapseSourceHtml() + "</div>";
    }

    private String addCollapseSourceHtml() {
        return "<span class='collapse-source expand-control' style='display:none;'><span class='expand-control-icon icon'>&nbsp;</span><span class='expand-control-text'>" + this.getText("newcode.config.expand.source", new Object[0]) + "</span></span><span class='collapse-spinner-wrapper'></span>";
    }
}

