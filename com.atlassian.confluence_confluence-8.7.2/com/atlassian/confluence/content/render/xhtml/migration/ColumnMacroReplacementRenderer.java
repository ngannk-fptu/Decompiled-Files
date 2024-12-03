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

public class ColumnMacroReplacementRenderer
implements MacroReplacementRenderer {
    private final Renderer migrationSubRenderer;

    public ColumnMacroReplacementRenderer(Renderer migrationSubRenderer) {
        this.migrationSubRenderer = migrationSubRenderer;
    }

    @Override
    public Set<String> getHandledClasses() {
        return Collections.singleton("com.atlassian.confluence.extra.layout.ColumnMacro");
    }

    @Override
    public String render(MacroDefinition macro, RenderContext renderContext) {
        String width = macro.getParameters() != null ? macro.getParameters().get("width") : null;
        return "<td class=\"layout-cell\"" + (String)(width != null ? " width=\"" + width + "\"" : "") + ">" + this.migrationSubRenderer.render(macro.getBodyText(), renderContext) + "</td>";
    }
}

