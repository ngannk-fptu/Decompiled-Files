/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.core.OperationTrigger;

public enum PageUpdateTrigger implements OperationTrigger
{
    PERSONAL_TASKLIST,
    VIEW_PAGE,
    EDIT_PAGE,
    REVERT,
    DISCARD_CHANGES,
    SPACE_CREATE,
    PAGE_RENAME,
    LINK_REFACTORING,
    UNKNOWN;


    public String lowerCase() {
        return this.name().toLowerCase();
    }
}

