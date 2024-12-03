/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate.ajax;

import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Theme;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.ajax.AbstractChangesAction;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;

@RequiresAnyConfluenceAccess
public class ConciseThemeChangesAction
extends AbstractChangesAction {
    @Override
    protected Theme getTheme() {
        return Theme.concise;
    }
}

