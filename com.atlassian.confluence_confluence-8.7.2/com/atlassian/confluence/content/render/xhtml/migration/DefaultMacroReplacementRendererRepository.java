/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.MacroReplacementRenderer;
import com.atlassian.confluence.content.render.xhtml.migration.MacroReplacementRendererRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultMacroReplacementRendererRepository
implements MacroReplacementRendererRepository {
    private final Map<String, MacroReplacementRenderer> replacementRenderers;

    public DefaultMacroReplacementRendererRepository(Set<MacroReplacementRenderer> macroReplacementRenderers) {
        if (macroReplacementRenderers == null || macroReplacementRenderers.isEmpty()) {
            this.replacementRenderers = Collections.emptyMap();
        } else {
            this.replacementRenderers = new HashMap<String, MacroReplacementRenderer>();
            for (MacroReplacementRenderer renderer : macroReplacementRenderers) {
                for (String clazz : renderer.getHandledClasses()) {
                    this.replacementRenderers.put(clazz, renderer);
                }
            }
        }
    }

    @Override
    public MacroReplacementRenderer getMacroReplacementRenderer(String macroClass) {
        return this.replacementRenderers.get(macroClass);
    }
}

