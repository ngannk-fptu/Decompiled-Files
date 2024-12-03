/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.pages.AbstractPage;

public interface PageAware {
    public AbstractPage getPage();

    public void setPage(AbstractPage var1);

    public boolean isPageRequired();

    public boolean isLatestVersionRequired();

    public boolean isViewPermissionRequired();

    default public boolean isEditPermissionRequired() {
        return false;
    }
}

