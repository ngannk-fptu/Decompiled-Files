/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components.list;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.Renderable;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.list.ListItem;

class ListRenderable
extends ListItem
implements Renderable {
    public ListRenderable() {
        super(null);
    }

    @Override
    public void toHtml(StringBuffer buffer, int depth, SubRenderer subRenderer, RenderContext context) {
        this.appendChildren(buffer, depth, subRenderer, context);
    }

    @Override
    public void render(SubRenderer subRenderer, RenderContext context, StringBuffer buffer) {
        this.toHtml(buffer, 0, subRenderer, context);
    }
}

