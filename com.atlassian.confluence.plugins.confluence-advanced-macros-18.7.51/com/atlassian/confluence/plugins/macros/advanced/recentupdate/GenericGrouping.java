/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.plugins.macros.advanced.recentupdate.AbstractGrouping;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UpdateItem;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Updater;

public class GenericGrouping
extends AbstractGrouping {
    public GenericGrouping(Updater updater) {
        super(updater);
    }

    private boolean forUpdater(Updater user) {
        if (this.updater == null) {
            return user == null;
        }
        return this.updater.equals(user);
    }

    @Override
    public boolean canAdd(UpdateItem updateItem) {
        return this.forUpdater(updateItem.getUpdater());
    }
}

