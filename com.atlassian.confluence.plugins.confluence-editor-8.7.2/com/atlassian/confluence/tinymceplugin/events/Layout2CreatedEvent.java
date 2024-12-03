/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 */
package com.atlassian.confluence.tinymceplugin.events;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.tinymceplugin.events.LayoutCreatedEvent;

public class Layout2CreatedEvent
extends LayoutCreatedEvent {
    private final int cells;
    private final int rows;

    public Layout2CreatedEvent(String layoutType, AbstractPage page, int cells, int rows) {
        super(layoutType == null ? "PL2" : layoutType, page);
        this.cells = cells;
        this.rows = rows;
    }

    public int getCells() {
        return this.cells;
    }

    public int getRows() {
        return this.rows;
    }

    @Override
    public String toString() {
        return "Layout2CreatedEvent{layoutType='" + this.getLayoutType() + "', page=" + this.getPage() + ", cells=" + this.cells + ", rows=" + this.rows + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Layout2CreatedEvent that = (Layout2CreatedEvent)o;
        if (this.cells != that.cells) {
            return false;
        }
        return this.rows == that.rows;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.cells;
        result = 31 * result + this.rows;
        return result;
    }
}

