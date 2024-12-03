/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Grouping;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UpdateItem;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Updater;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractGrouping
implements Grouping {
    protected final Updater updater;
    protected final List<UpdateItem> updateItems;

    public AbstractGrouping(Updater updater) {
        this.updater = updater;
        this.updateItems = new ArrayList<UpdateItem>();
    }

    public Updater getUpdater() {
        return this.updater;
    }

    @Override
    public void addUpdateItem(UpdateItem updateItem) {
        if (!this.canAdd(updateItem)) {
            throw new IllegalArgumentException("Cannot add: " + updateItem + " to this grouping.");
        }
        this.updateItems.add(updateItem);
    }

    @Override
    public List<UpdateItem> getUpdateItems() {
        return Collections.unmodifiableList(this.updateItems);
    }

    @Override
    public int size() {
        return this.updateItems.size();
    }
}

