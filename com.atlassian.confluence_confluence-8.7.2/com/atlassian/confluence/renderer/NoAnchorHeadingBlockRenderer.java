/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.SubRenderer
 */
package com.atlassian.confluence.renderer;

import com.atlassian.confluence.renderer.ConfluenceHeadingBlockRenderer;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.SubRenderer;

public class NoAnchorHeadingBlockRenderer
extends ConfluenceHeadingBlockRenderer {
    private static final String REPLACE = "<h{0} id=\"{1}\">{2}</h{0}>";

    protected String renderHeading(String headingLevel, String body, RenderContext context, SubRenderer subRenderer) {
        return super.renderHeading(headingLevel, body, context, subRenderer, REPLACE);
    }
}

