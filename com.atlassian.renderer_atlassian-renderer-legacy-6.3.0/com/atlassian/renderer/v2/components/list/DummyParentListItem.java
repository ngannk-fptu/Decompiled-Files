/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components.list;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.list.ListItem;

public class DummyParentListItem
extends ListItem {
    public DummyParentListItem() {
        super(null);
    }

    @Override
    public void toHtml(StringBuffer buffer, int depth, SubRenderer subRenderer, RenderContext context) {
        this.appendChildren(buffer, depth, subRenderer, context);
    }
}

