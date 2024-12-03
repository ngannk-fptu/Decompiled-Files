/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.components.block.HeadingBlockRenderer
 */
package com.atlassian.confluence.renderer;

import com.atlassian.confluence.links.linktypes.AbstractPageLink;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.components.block.HeadingBlockRenderer;

public class ConfluenceHeadingBlockRenderer
extends HeadingBlockRenderer {
    protected String getAnchor(RenderContext context, String body) {
        return AbstractPageLink.generateUniqueAnchor((PageContext)context, body);
    }
}

