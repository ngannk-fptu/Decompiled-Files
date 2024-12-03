/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.V2SubRenderer
 *  com.atlassian.renderer.v2.macro.basic.ColorMacro
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.MacroReplacementRenderer;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.V2SubRenderer;
import com.atlassian.renderer.v2.macro.basic.ColorMacro;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class ColorMacroReplacementRenderer
implements MacroReplacementRenderer {
    private final V2SubRenderer migrationSubRenderer;

    public ColorMacroReplacementRenderer(V2SubRenderer migrationSubRenderer) {
        this.migrationSubRenderer = migrationSubRenderer;
    }

    @Override
    public Set<String> getHandledClasses() {
        return Collections.singleton(ColorMacro.class.getName());
    }

    @Override
    public String render(MacroDefinition macro, RenderContext renderContext) {
        String color = macro.getDefaultParameterValue();
        String body = this.migrationSubRenderer.render(macro.getBodyText(), renderContext, RenderMode.INLINE.or(RenderMode.allow((long)4L)));
        return StringUtils.isEmpty((CharSequence)body) ? "" : (StringUtils.isEmpty((CharSequence)color) ? body : "<span style=\"color: " + color + "\">" + body + "</span>");
    }
}

