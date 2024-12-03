/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.renderer.RenderContext;
import java.util.Set;

public interface MacroReplacementRenderer {
    public Set<String> getHandledClasses();

    public String render(MacroDefinition var1, RenderContext var2);
}

