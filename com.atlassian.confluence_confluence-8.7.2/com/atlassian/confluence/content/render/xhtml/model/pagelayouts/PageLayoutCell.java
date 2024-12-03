/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.pagelayouts;

import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutCellType;

public class PageLayoutCell {
    private final PageLayoutCellType type;
    private final Streamable body;

    public PageLayoutCell(PageLayoutCellType type, Streamable body) {
        this.type = type;
        this.body = body;
    }

    public boolean isNormal() {
        return this.type == PageLayoutCellType.NORMAL;
    }

    public boolean isAside() {
        return this.type == PageLayoutCellType.ASIDE;
    }

    public boolean isSideBars() {
        return this.type == PageLayoutCellType.SIDEBARS;
    }

    public Streamable getBody() {
        return this.body;
    }

    public PageLayoutCellType getType() {
        return this.type;
    }
}

