/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.service;

import com.atlassian.confluence.extra.calendar3.service.UserSearchRequest;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;

public interface UserSearchService {
    public Collection<ConfluenceUser> search(UserSearchRequest var1);
}

