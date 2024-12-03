/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.group.InternalGroup
 *  com.atlassian.crowd.model.user.InternalUser
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.model.user.InternalUser;

public interface InternalMembershipDao {
    public void removeAllGroupRelationships(InternalGroup var1);

    public void removeAllUserRelationships(InternalUser var1);

    public void removeAllRelationships(Directory var1);

    public void rename(String var1, InternalUser var2);
}

