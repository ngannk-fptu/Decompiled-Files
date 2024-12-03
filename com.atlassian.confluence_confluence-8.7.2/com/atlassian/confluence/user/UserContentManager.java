/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface UserContentManager {
    @Deprecated
    public boolean hasAuthoredContent(User var1);

    public boolean hasAuthoredContent(ConfluenceUser var1);
}

