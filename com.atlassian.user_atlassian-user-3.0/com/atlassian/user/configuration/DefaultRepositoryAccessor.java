/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.configuration;

import com.atlassian.user.GroupManager;
import com.atlassian.user.UserManager;
import com.atlassian.user.configuration.RepositoryAccessor;
import com.atlassian.user.properties.PropertySetFactory;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.query.EntityQueryParser;
import com.atlassian.user.security.authentication.Authenticator;
import com.atlassian.user.security.password.PasswordEncryptor;

public class DefaultRepositoryAccessor
implements RepositoryAccessor {
    private UserManager userManager;
    private GroupManager groupManager;
    private RepositoryIdentifier repository;
    private PropertySetFactory propertySetFactory;
    private Authenticator authenticator;
    private PasswordEncryptor encryptor;
    private EntityQueryParser entityQueryParser;

    public UserManager getUserManager() {
        return this.userManager;
    }

    public GroupManager getGroupManager() {
        return this.groupManager;
    }

    public RepositoryIdentifier getIdentifier() {
        return this.repository;
    }

    public PropertySetFactory getPropertySetFactory() {
        return this.propertySetFactory;
    }

    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    public PasswordEncryptor getPasswordEncryptor() {
        return this.encryptor;
    }

    public void setPasswordEncryptor(PasswordEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setGroupManager(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    public void setRepository(RepositoryIdentifier repository) {
        this.repository = repository;
    }

    public void setPropertySetFactory(PropertySetFactory propertySetFactory) {
        this.propertySetFactory = propertySetFactory;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public EntityQueryParser getEntityQueryParser() {
        return this.entityQueryParser;
    }

    public void setEntityQueryParser(EntityQueryParser entityQueryParser) {
        this.entityQueryParser = entityQueryParser;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof RepositoryAccessor)) {
            return false;
        }
        RepositoryAccessor that = (RepositoryAccessor)o;
        return !(this.repository == null ? that.getIdentifier() != null : !this.repository.equals(that.getIdentifier()));
    }

    public int hashCode() {
        return this.repository != null ? this.repository.hashCode() : 0;
    }
}

