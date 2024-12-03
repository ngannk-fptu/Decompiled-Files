/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UpdateItem;
import java.util.List;

public interface Grouping {
    public List<UpdateItem> getUpdateItems();

    public int size();

    public boolean canAdd(UpdateItem var1);

    public void addUpdateItem(UpdateItem var1);
}

