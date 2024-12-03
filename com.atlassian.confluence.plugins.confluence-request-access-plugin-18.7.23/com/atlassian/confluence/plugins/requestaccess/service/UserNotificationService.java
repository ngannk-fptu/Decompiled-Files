/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.requestaccess.service;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.LinkedHashSet;

public interface UserNotificationService {
    public LinkedHashSet<ConfluenceUser> findRequestAccessRecipient(AbstractPage var1);
}

