/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro.basic;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.basic.AbstractPanelMacro;
import java.util.Map;

public class NoformatMacro
extends AbstractPanelMacro {
    public static final String PREFORMATTED_CONTENT_WRAPPER_CLASS = "preformattedContent";

    public NoformatMacro() {
    }

    public NoformatMacro(SubRenderer subRenderer) {
        this.setSubRenderer(subRenderer);
    }

    @Override
    protected String getPanelCSSClass() {
        return "preformatted panel";
    }

    @Override
    protected String getPanelHeaderCSSClass() {
        return "preformattedHeader panelHeader";
    }

    @Override
    protected String getPanelContentCSSClass() {
        return "preformattedContent panelContent";
    }

    @Override
    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        if ("true".equals(parameters.get("nopanel"))) {
            return this.getBodyContent(parameters, body, renderContext);
        }
        return super.execute(parameters, body, renderContext);
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.allow(128L);
    }

    @Override
    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        if ("true".equals(parameters.get("nopanel"))) {
            return TokenType.INLINE;
        }
        return TokenType.BLOCK;
    }

    @Override
    protected String getBodyContent(Map parameters, String body, RenderContext renderContext) throws MacroException {
        if (body.startsWith("\n")) {
            body = body.substring(1);
        }
        if (body.startsWith("\r\n")) {
            body = body.substring(2);
        }
        return super.getBodyContent(parameters, "<pre>" + body + "</pre>", renderContext);
    }

    @Override
    public boolean suppressMacroRenderingDuringWysiwyg() {
        return false;
    }
}

