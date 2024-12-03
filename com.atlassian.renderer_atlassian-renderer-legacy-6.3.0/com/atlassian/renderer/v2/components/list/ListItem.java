/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 */
package com.atlassian.renderer.v2.components.list;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.list.ListBlockRenderer;
import com.atlassian.renderer.v2.components.list.ListType;
import com.atlassian.renderer.v2.components.list.WikiList;
import com.opensymphony.util.TextUtils;
import java.util.ArrayList;
import java.util.List;

class ListItem {
    private final String contents;
    private final List children = new ArrayList();
    private WikiList lastChild;

    public ListItem(String contents) {
        this.contents = contents;
    }

    public void toHtml(StringBuffer buffer, int depth, SubRenderer subRenderer, RenderContext context) {
        for (int i = 0; i < depth; ++i) {
            buffer.append("\t");
        }
        buffer.append("<li>");
        if (TextUtils.stringSet((String)this.contents)) {
            buffer.append(subRenderer.render(this.contents, context, RenderMode.LIST_ITEM));
        }
        if (this.lastChild != null) {
            buffer.append("\n");
            this.appendChildren(buffer, depth, subRenderer, context);
            RenderUtils.tabTo(buffer, depth);
        }
        buffer.append("</li>\n");
    }

    protected void appendChildren(StringBuffer buffer, int depth, SubRenderer subRenderer, RenderContext context) {
        for (WikiList wikiList : this.children) {
            wikiList.toHtml(buffer, depth, subRenderer, context);
        }
    }

    public void addListItem(String bullets, ListItem item) {
        if (this.lastChild == null || this.isSingleNonMatchingBullet(bullets)) {
            this.addList(bullets.substring(0, 1));
        }
        this.lastChild.addListItem(bullets, item);
    }

    private boolean isSingleNonMatchingBullet(String bullets) {
        return bullets.length() == 1 && !this.lastChild.type.bullet.equals(bullets);
    }

    private void addList(String bullet) {
        WikiList list;
        this.lastChild = list = new WikiList((ListType)ListBlockRenderer.LIST_TYPES.get(bullet));
        this.children.add(list);
    }
}

