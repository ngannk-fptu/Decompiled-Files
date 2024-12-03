/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components.list;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.list.ListItem;
import com.atlassian.renderer.v2.components.list.ListType;
import java.util.ArrayList;
import java.util.List;

class WikiList {
    public final ListType type;
    private final List items = new ArrayList();
    private ListItem lastItem;

    public WikiList(ListType type) {
        this.type = type;
    }

    public void addListItem(String bullets, ListItem item) {
        if (bullets.length() == 1) {
            this.addItem(item);
        } else {
            if (this.lastItem == null) {
                this.addItem(new ListItem(""));
            }
            this.lastItem.addListItem(bullets.substring(1), item);
        }
    }

    private void addItem(ListItem item) {
        this.lastItem = item;
        this.items.add(item);
    }

    public void toHtml(StringBuffer buffer, int depth, SubRenderer subRenderer, RenderContext context) {
        RenderUtils.tabTo(buffer, depth);
        buffer.append(this.type.openingTag).append("\n");
        for (ListItem listItem : this.items) {
            listItem.toHtml(buffer, depth + 1, subRenderer, context);
        }
        RenderUtils.tabTo(buffer, depth);
        buffer.append(this.type.closingTag).append("\n");
    }
}

