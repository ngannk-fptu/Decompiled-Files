/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.components.MacroRendererComponent;
import com.atlassian.renderer.v2.components.MacroTag;
import com.atlassian.renderer.v2.components.WikiContentHandler;

public class WikiContentRendererHandler
implements WikiContentHandler {
    private MacroRendererComponent macroRendererComponent;
    private RenderContext context;

    public WikiContentRendererHandler(MacroRendererComponent macroRendererComponent, RenderContext context) {
        this.macroRendererComponent = macroRendererComponent;
        this.context = context;
    }

    @Override
    public void handleMacro(StringBuffer buffer, MacroTag macroTag, String body) {
        this.macroRendererComponent.makeMacro(buffer, macroTag, body, this.context);
    }

    @Override
    public void handleText(StringBuffer buffer, String s) {
        buffer.append(s);
    }
}

