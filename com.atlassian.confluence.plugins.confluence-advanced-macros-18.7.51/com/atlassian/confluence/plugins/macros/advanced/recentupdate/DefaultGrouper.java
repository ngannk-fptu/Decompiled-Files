/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.plugins.macros.advanced.recentupdate.GenericGrouping;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Grouper;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Grouping;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UpdateItem;
import java.util.LinkedList;
import java.util.List;

public class DefaultGrouper
implements Grouper {
    private final LinkedList<Grouping> groupings = new LinkedList();

    @Override
    public void addUpdateItem(UpdateItem updateItem) {
        if (updateItem == null) {
            return;
        }
        if (this.groupings.isEmpty() || !this.groupings.getLast().canAdd(updateItem)) {
            this.groupings.add(new GenericGrouping(updateItem.getUpdater()));
        }
        this.groupings.getLast().addUpdateItem(updateItem);
    }

    @Override
    public List<Grouping> getUpdateItemGroupings() {
        return this.groupings;
    }
}

