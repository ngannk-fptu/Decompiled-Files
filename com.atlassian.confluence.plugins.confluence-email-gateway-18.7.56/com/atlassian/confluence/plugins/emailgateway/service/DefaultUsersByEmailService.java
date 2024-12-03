/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.search.query.EmailTermQuery
 *  com.atlassian.user.search.query.Query
 *  javax.mail.internet.InternetAddress
 */
package com.atlassian.confluence.plugins.emailgateway.service;

import com.atlassian.confluence.plugins.emailgateway.api.UsersByEmailService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.search.query.EmailTermQuery;
import com.atlassian.user.search.query.Query;
import java.util.List;
import javax.mail.internet.InternetAddress;

public class DefaultUsersByEmailService
implements UsersByEmailService {
    private UserAccessor userAccessor;

    public DefaultUsersByEmailService(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    @Override
    public List<User> getUsersByEmail(String email) throws EntityException {
        return this.userAccessor.findUsersAsList((Query)new EmailTermQuery(email));
    }

    @Override
    public User getUniqueUserByEmail(String email) throws EntityException {
        List<User> users = this.getUsersByEmail(email);
        if (users.isEmpty()) {
            return null;
        }
        if (users.size() > 1) {
            throw new EntityException("Sender email address matched more than one Confluence user : " + email);
        }
        return users.get(0);
    }

    @Override
    public User getUniqueUserByEmail(InternetAddress email) throws EntityException {
        return this.getUniqueUserByEmail(email.getAddress());
    }
}

