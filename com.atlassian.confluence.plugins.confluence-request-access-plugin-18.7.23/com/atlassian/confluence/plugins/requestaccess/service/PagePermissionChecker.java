/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 */
package com.atlassian.confluence.plugins.requestaccess.service;

import com.atlassian.confluence.pages.AbstractPage;

public interface PagePermissionChecker {
    public boolean canAuthenticatedUserGrantAccessToPage(AbstractPage var1);

    public boolean isUserPermittedToViewPage(String var1, AbstractPage var2);
}

