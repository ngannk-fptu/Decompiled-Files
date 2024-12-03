/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate.ajax;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Theme;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;

@RequiresAnyConfluenceAccess
public class ChangesAction
extends ConfluenceActionSupport {
    private String theme;

    public void validate() {
        try {
            Theme.valueOf(this.theme);
        }
        catch (IllegalArgumentException e) {
            this.addActionError("The requested theme is not supported.");
        }
        super.validate();
    }

    public String execute() throws Exception {
        return this.theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}

