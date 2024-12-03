/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.configuration;

import com.atlassian.user.GroupManager;
import com.atlassian.user.UserManager;
import com.atlassian.user.properties.PropertySetFactory;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.query.EntityQueryParser;
import com.atlassian.user.security.authentication.Authenticator;

public interface RepositoryAccessor {
    public RepositoryIdentifier getIdentifier();

    public UserManager getUserManager();

    public GroupManager getGroupManager();

    public PropertySetFactory getPropertySetFactory();

    public Authenticator getAuthenticator();

    public EntityQueryParser getEntityQueryParser();
}

