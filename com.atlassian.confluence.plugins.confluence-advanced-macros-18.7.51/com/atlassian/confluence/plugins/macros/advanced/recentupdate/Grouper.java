/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Grouping;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UpdateItem;
import java.util.List;

public interface Grouper {
    public void addUpdateItem(UpdateItem var1);

    public List<Grouping> getUpdateItemGroupings();
}

