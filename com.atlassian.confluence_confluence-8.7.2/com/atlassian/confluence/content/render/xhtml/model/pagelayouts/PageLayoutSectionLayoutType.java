/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.pagelayouts;

import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutCellType;

public enum PageLayoutSectionLayoutType {
    SINGLE(PageLayoutCellType.NORMAL),
    TWO_LEFT_SIDEBAR(PageLayoutCellType.ASIDE, PageLayoutCellType.NORMAL),
    TWO_RIGHT_SIDEBAR(PageLayoutCellType.NORMAL, PageLayoutCellType.ASIDE),
    TWO_EQUAL(PageLayoutCellType.NORMAL, PageLayoutCellType.NORMAL),
    THREE_EQUAL(PageLayoutCellType.NORMAL, PageLayoutCellType.NORMAL, PageLayoutCellType.NORMAL),
    THREE_WITH_SIDEBARS(PageLayoutCellType.SIDEBARS, PageLayoutCellType.NORMAL, PageLayoutCellType.SIDEBARS);

    private final PageLayoutCellType[] expectedCellTypes;

    private PageLayoutSectionLayoutType(PageLayoutCellType ... cellTypes) {
        this.expectedCellTypes = cellTypes;
    }

    public PageLayoutCellType[] getExpectedCellTypes() {
        return this.expectedCellTypes;
    }
}

