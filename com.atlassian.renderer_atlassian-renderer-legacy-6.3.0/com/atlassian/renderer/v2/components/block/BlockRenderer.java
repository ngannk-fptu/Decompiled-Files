/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components.block;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.block.LineWalker;

public interface BlockRenderer {
    public String renderNextBlock(String var1, LineWalker var2, RenderContext var3, SubRenderer var4);
}

