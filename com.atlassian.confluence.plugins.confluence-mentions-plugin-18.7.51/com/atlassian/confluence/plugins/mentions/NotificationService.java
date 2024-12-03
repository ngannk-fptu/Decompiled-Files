/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.mentions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Set;

public interface NotificationService {
    public void sendMentions(Set<ConfluenceUser> var1, ConfluenceUser var2, ContentEntityObject var3);
}

