/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components.block;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.block.BlockRenderer;
import com.atlassian.renderer.v2.components.block.LineWalker;

public class BlankLineBlockRenderer
implements BlockRenderer {
    @Override
    public String renderNextBlock(String thisLine, LineWalker nextLines, RenderContext context, SubRenderer subRenderer) {
        if (!RenderUtils.isBlank(thisLine)) {
            return null;
        }
        return context.isRenderingForWysiwyg() ? "<p user=\"true\" style=\"display:none\"/>" : "";
    }
}

