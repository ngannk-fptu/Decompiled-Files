/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;

public interface RendererComponent {
    public boolean shouldRender(RenderMode var1);

    public String render(String var1, RenderContext var2);
}

