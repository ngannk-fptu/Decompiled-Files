/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.Renderer
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.MacroReplacementRenderer;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.Renderer;
import java.util.Collections;
import java.util.Set;

public class SectionMacroReplacementRenderer
implements MacroReplacementRenderer {
    private final Renderer migrationSubRenderer;

    public SectionMacroReplacementRenderer(Renderer migrationSubRenderer) {
        this.migrationSubRenderer = migrationSubRenderer;
    }

    @Override
    public Set<String> getHandledClasses() {
        return Collections.singleton("com.atlassian.confluence.extra.layout.SectionMacro");
    }

    @Override
    public String render(MacroDefinition macro, RenderContext renderContext) {
        boolean showBorder = macro.getParameters() != null && Boolean.valueOf(macro.getParameters().get("border")) != false;
        return "<table class=\"" + (showBorder ? " bordered-layout" : "layout") + "\"><tr>" + this.migrationSubRenderer.render(macro.getBodyText(), renderContext) + "</tr></table>";
    }
}

