/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.content.render.xhtml.model.pagelayouts;

import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutCell;
import com.atlassian.confluence.content.render.xhtml.model.pagelayouts.PageLayoutSectionLayoutType;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Iterator;

public class PageLayoutSection {
    private final Collection<PageLayoutCell> cells;

    public PageLayoutSection(Collection<PageLayoutCell> cells) {
        this.cells = ImmutableList.copyOf(cells);
    }

    public PageLayoutSection(PageLayoutCell ... cells) {
        this.cells = ImmutableList.copyOf((Object[])cells);
    }

    public Collection<PageLayoutCell> getCells() {
        return this.cells;
    }

    public PageLayoutSectionLayoutType getSectionLayout() {
        Iterator<PageLayoutCell> cellsIterator = this.cells.iterator();
        if (this.cells.size() == 1) {
            return PageLayoutSectionLayoutType.SINGLE;
        }
        if (this.cells.size() == 2) {
            PageLayoutCell cellLeft = cellsIterator.next();
            PageLayoutCell cellRight = cellsIterator.next();
            if (cellLeft.isAside() && cellRight.isNormal()) {
                return PageLayoutSectionLayoutType.TWO_LEFT_SIDEBAR;
            }
            if (cellRight.isAside() && cellLeft.isNormal()) {
                return PageLayoutSectionLayoutType.TWO_RIGHT_SIDEBAR;
            }
            return PageLayoutSectionLayoutType.TWO_EQUAL;
        }
        if (this.cells.size() == 3) {
            PageLayoutCell cellFirst = cellsIterator.next();
            cellsIterator.next();
            PageLayoutCell cellThird = cellsIterator.next();
            if (cellFirst.isSideBars() && cellThird.isSideBars()) {
                return PageLayoutSectionLayoutType.THREE_WITH_SIDEBARS;
            }
            return PageLayoutSectionLayoutType.THREE_EQUAL;
        }
        return PageLayoutSectionLayoutType.THREE_EQUAL;
    }

    public boolean hasOneCell() {
        return this.cells.size() == 1;
    }
}

