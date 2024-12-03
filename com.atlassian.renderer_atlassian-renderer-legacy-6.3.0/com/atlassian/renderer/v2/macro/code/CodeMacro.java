/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 */
package com.atlassian.renderer.v2.macro.code;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.basic.AbstractPanelMacro;
import com.atlassian.renderer.v2.macro.code.SimpleSourceCodeFormatterRepository;
import com.atlassian.renderer.v2.macro.code.SourceCodeFormatter;
import com.atlassian.renderer.v2.macro.code.SourceCodeFormatterRepository;
import com.opensymphony.util.TextUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CodeMacro
extends AbstractPanelMacro {
    private static final String DEFAULT_LANG = "java";
    private SourceCodeFormatterRepository codeFormatterRepository;

    public CodeMacro(SubRenderer subRenderer, List formatters) {
        this.setCodeFormatterRepository(new SimpleSourceCodeFormatterRepository(formatters));
        this.setSubRenderer(subRenderer);
    }

    public void setCodeFormatterRepository(SourceCodeFormatterRepository codeFormatterRepository) {
        this.codeFormatterRepository = codeFormatterRepository;
    }

    public CodeMacro() {
    }

    @Override
    public boolean suppressMacroRenderingDuringWysiwyg() {
        return false;
    }

    @Override
    protected String getPanelCSSClass() {
        return "code panel";
    }

    @Override
    protected String getPanelHeaderCSSClass() {
        return "codeHeader panelHeader";
    }

    @Override
    protected String getPanelContentCSSClass() {
        return "codeContent panelContent";
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.allow(128L);
    }

    @Override
    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        String language = this.getLanguage(parameters).toLowerCase();
        SourceCodeFormatter formatter = this.getFormatter(language);
        String preamble = "";
        String classAttr = "";
        if (formatter == null) {
            preamble = RenderUtils.blockError("Unable to find source-code formatter for language: " + language + ".", "Available languages are: " + TextUtils.join((String)", ", (Collection)this.codeFormatterRepository.getAvailableLanguages()));
            formatter = this.getFormatter(DEFAULT_LANG);
        } else {
            classAttr = " class=\"code-" + HtmlEscaper.escapeAll(language, false) + "\"";
        }
        String code = body;
        if (code.startsWith("\n")) {
            code = code.substring(1);
        }
        if (code.endsWith("\n")) {
            code = code.substring(0, code.length());
        }
        return super.execute(parameters, preamble + "<pre" + classAttr + ">" + formatter.format(code, language) + "</pre>", renderContext);
    }

    @Override
    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.BLOCK;
    }

    private SourceCodeFormatter getFormatter(String language) {
        return this.codeFormatterRepository.getSourceCodeFormatter(language);
    }

    private String getLanguage(Map parameters) {
        String lang = (String)parameters.get("lang");
        if (!TextUtils.stringSet((String)lang)) {
            lang = (String)parameters.get("0");
        }
        if (!TextUtils.stringSet((String)lang)) {
            lang = DEFAULT_LANG;
        }
        return lang;
    }
}

