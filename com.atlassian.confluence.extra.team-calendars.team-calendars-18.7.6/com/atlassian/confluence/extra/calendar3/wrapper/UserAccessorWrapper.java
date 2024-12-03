/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.Group
 */
package com.atlassian.confluence.extra.calendar3.wrapper;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.Group;
import java.util.List;

public interface UserAccessorWrapper {
    public List<String> getUserGroups(ConfluenceUser var1);

    public Group getGroup(String var1);

    public ConfluenceUser getUser(String var1);
}

