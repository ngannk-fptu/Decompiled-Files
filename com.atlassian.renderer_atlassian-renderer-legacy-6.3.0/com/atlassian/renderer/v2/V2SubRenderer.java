/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.Renderer;
import com.atlassian.renderer.v2.SubRenderer;

public class V2SubRenderer
implements SubRenderer {
    private Renderer renderer;

    public V2SubRenderer() {
    }

    public V2SubRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public String render(String wiki, RenderContext renderContext) {
        return this.renderer.render(wiki, renderContext);
    }

    @Override
    public String renderAsText(String originalContent, RenderContext context) {
        return originalContent;
    }

    @Override
    public String getRendererType() {
        return this.renderer.getRendererType();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String render(String wiki, RenderContext renderContext, RenderMode newRenderMode) {
        try {
            if (newRenderMode != null) {
                renderContext.pushRenderMode(newRenderMode);
            }
            String string = this.renderer.render(wiki, renderContext);
            return string;
        }
        finally {
            if (newRenderMode != null) {
                renderContext.popRenderMode();
            }
        }
    }
}

