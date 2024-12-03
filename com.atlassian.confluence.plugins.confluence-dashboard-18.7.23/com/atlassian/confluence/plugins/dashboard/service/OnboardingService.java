/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.dashboard.service;

import com.atlassian.confluence.user.ConfluenceUser;

public interface OnboardingService {
    public boolean shouldShowDialog(ConfluenceUser var1);

    public boolean shouldShowTips(ConfluenceUser var1);

    public boolean isNewUser(ConfluenceUser var1);
}

