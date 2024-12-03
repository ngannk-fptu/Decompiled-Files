/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.SubRenderer
 *  com.atlassian.renderer.v2.components.block.HeadingBlockRenderer
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.SubRenderer;

public class HeadingBlockRenderer
extends com.atlassian.renderer.v2.components.block.HeadingBlockRenderer {
    private static final String REPLACE = "<h{0}>{2}</h{0}>";

    protected String renderHeading(String headingLevel, String body, RenderContext context, SubRenderer subRenderer) {
        return this.renderHeading(headingLevel, body, context, subRenderer, REPLACE);
    }
}

