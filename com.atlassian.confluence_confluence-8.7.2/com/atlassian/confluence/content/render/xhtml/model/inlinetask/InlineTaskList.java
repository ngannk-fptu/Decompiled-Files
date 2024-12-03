/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.inlinetask;

import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem;
import java.util.ArrayList;
import java.util.List;

public class InlineTaskList {
    private List<InlineTaskListItem> listItems = new ArrayList<InlineTaskListItem>();

    public void addItem(InlineTaskListItem listItem) {
        this.listItems.add(listItem);
    }

    public List<InlineTaskListItem> getItems() {
        return this.listItems;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InlineTaskList)) {
            return false;
        }
        InlineTaskList that = (InlineTaskList)o;
        return !(this.listItems != null ? !this.listItems.equals(that.listItems) : that.listItems != null);
    }

    public int hashCode() {
        return this.listItems != null ? this.listItems.hashCode() : 0;
    }

    public String toString() {
        return "InlineTaskList{listItems=" + this.listItems + "}";
    }
}

