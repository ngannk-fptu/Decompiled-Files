/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.MacroReplacementRenderer;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.renderer.RenderContext;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class NolinkMacroReplacementRenderer
implements MacroReplacementRenderer {
    @Override
    public Set<String> getHandledClasses() {
        return Collections.singleton("com.atlassian.confluence.plugins.macros.basic.NoLinkMacro");
    }

    @Override
    public String render(MacroDefinition macro, RenderContext renderContext) {
        String nolink = macro.getDefaultParameterValue();
        if (StringUtils.isBlank((CharSequence)nolink) && macro.getParameters() != null) {
            nolink = macro.getParameters().get("URL");
        }
        if (StringUtils.isNotBlank((CharSequence)nolink)) {
            return "<span class=\"nolink\">" + nolink + "</span>";
        }
        return "";
    }
}

