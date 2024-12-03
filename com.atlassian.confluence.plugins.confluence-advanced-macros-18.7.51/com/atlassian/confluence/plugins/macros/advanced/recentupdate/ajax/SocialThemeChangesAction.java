/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate.ajax;

import com.atlassian.confluence.plugins.macros.advanced.recentupdate.DefaultGrouper;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Grouping;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Theme;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UpdateItem;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.ajax.AbstractChangesAction;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import java.util.List;

@RequiresAnyConfluenceAccess
public class SocialThemeChangesAction
extends AbstractChangesAction {
    private List<? extends Grouping> groupings;

    @Override
    public String execute() throws Exception {
        String superResult = super.execute();
        if ("input".equals(superResult)) {
            return superResult;
        }
        DefaultGrouper grouper = new DefaultGrouper();
        for (UpdateItem updateItem : this.updateItems) {
            grouper.addUpdateItem(updateItem);
        }
        this.groupings = grouper.getUpdateItemGroupings();
        return "success";
    }

    @Override
    protected Theme getTheme() {
        return Theme.social;
    }

    public List<? extends Grouping> getGroupings() {
        return this.groupings;
    }
}

