/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.Renderer
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.Renderer;
import com.atlassian.spring.container.ContainerManager;

public class LazyLoadingMigrationRenderer
implements Renderer {
    private volatile Renderer renderer;

    public String render(String originalContent, RenderContext renderContext) {
        return this.getRenderer().render(originalContent, renderContext);
    }

    public String renderAsText(String originalContent, RenderContext context) {
        return this.getRenderer().renderAsText(originalContent, context);
    }

    public String getRendererType() {
        return this.getRenderer().getRendererType();
    }

    private Renderer getRenderer() {
        if (this.renderer == null) {
            this.renderer = (Renderer)ContainerManager.getComponent((String)"migrationRenderer");
        }
        return this.renderer;
    }
}

