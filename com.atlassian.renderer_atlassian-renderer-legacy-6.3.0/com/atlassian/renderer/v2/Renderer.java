/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2;

import com.atlassian.renderer.RenderContext;

public interface Renderer {
    public String render(String var1, RenderContext var2);

    public String renderAsText(String var1, RenderContext var2);

    public String getRendererType();
}

