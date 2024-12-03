/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultUser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsernameToUserTranslatingPaginationSupport
extends PaginationSupport<User> {
    private static final Logger log = LoggerFactory.getLogger(UsernameToUserTranslatingPaginationSupport.class);
    UserAccessor userAccessor;

    public UsernameToUserTranslatingPaginationSupport(int pageSize, int startIndexValue) {
        super(pageSize);
        this.setStartIndex(startIndexValue);
    }

    public List<User> getPage() {
        List usernamesHack = super.getPage();
        List usernames = usernamesHack;
        if (usernames == null) {
            return Collections.emptyList();
        }
        ArrayList<User> users = new ArrayList<User>(usernames.size());
        for (String username : usernames) {
            ConfluenceUser user;
            if (log.isDebugEnabled()) {
                log.debug("Retrieving user with username: " + username);
            }
            if ((user = this.userAccessor.getUserByName(username)) != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Found user: [class=" + user.getClass() + ",name=" + user.getName() + ",fullName=" + user.getFullName() + ",email=" + user.getEmail() + "]");
                }
                users.add(user);
                continue;
            }
            if (log.isDebugEnabled()) {
                log.debug("Could not find a user for username: " + username + ". This username will be displayed as is.");
            }
            users.add((User)new DefaultUser(username));
        }
        return users;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }
}

