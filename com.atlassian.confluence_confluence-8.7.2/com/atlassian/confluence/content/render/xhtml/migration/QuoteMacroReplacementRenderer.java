/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.V2SubRenderer
 *  com.atlassian.renderer.v2.macro.basic.QuoteMacro
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.MacroReplacementRenderer;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.V2SubRenderer;
import com.atlassian.renderer.v2.macro.basic.QuoteMacro;
import java.util.Collections;
import java.util.Set;

public class QuoteMacroReplacementRenderer
implements MacroReplacementRenderer {
    private final V2SubRenderer migrationSubRenderer;

    public QuoteMacroReplacementRenderer(V2SubRenderer migrationSubRenderer) {
        this.migrationSubRenderer = migrationSubRenderer;
    }

    @Override
    public Set<String> getHandledClasses() {
        return Collections.singleton(QuoteMacro.class.getName());
    }

    @Override
    public String render(MacroDefinition macro, RenderContext renderContext) {
        return "<blockquote>" + this.migrationSubRenderer.render(macro.getBodyText(), renderContext) + "</blockquote>";
    }
}

