/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.radeox.macro.parameter.MacroParameter
 */
package com.atlassian.renderer.macro.macros;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.renderer.macro.macros.AbstractPanelMacro;
import java.io.IOException;
import java.io.Writer;
import org.radeox.macro.parameter.MacroParameter;

public class PanelMacro
extends AbstractPanelMacro {
    private WikiStyleRenderer wikiStyleRenderer;

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    public String getName() {
        return "panel";
    }

    @Override
    protected String getPanelCSSClass() {
        return "panel";
    }

    @Override
    protected String getPanelContentCSSClass() {
        return "panelContent";
    }

    @Override
    protected String getPanelHeaderCSSClass() {
        return "panelHeader";
    }

    @Override
    protected void writeContent(Writer writer, MacroParameter macroParameter, String content, String backgroundColor) throws IOException {
        String renderedContent = this.wikiStyleRenderer.convertWikiToXHtml((RenderContext)macroParameter.getContext().getParameters().get("RENDER_CONTEXT"), content.trim());
        super.writeContent(writer, macroParameter, renderedContent, backgroundColor);
    }
}

