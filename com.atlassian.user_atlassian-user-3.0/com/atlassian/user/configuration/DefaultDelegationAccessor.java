/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.configuration;

import com.atlassian.user.GroupManager;
import com.atlassian.user.UserManager;
import com.atlassian.user.configuration.DelegationAccessor;
import com.atlassian.user.configuration.RepositoryAccessor;
import com.atlassian.user.impl.delegation.DelegatingGroupManager;
import com.atlassian.user.impl.delegation.DelegatingUserManager;
import com.atlassian.user.impl.delegation.properties.DelegatingPropertySetFactory;
import com.atlassian.user.impl.delegation.search.query.DelegatingEntityQueryParser;
import com.atlassian.user.impl.delegation.security.authentication.DelegatingAuthenticator;
import com.atlassian.user.properties.PropertySetFactory;
import com.atlassian.user.repository.DefaultRepositoryIdentifier;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.query.EntityQueryParser;
import com.atlassian.user.security.authentication.Authenticator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultDelegationAccessor
implements DelegationAccessor {
    private Map<RepositoryIdentifier, RepositoryAccessor> repositoryAccessors = new HashMap<RepositoryIdentifier, RepositoryAccessor>();
    private List<RepositoryIdentifier> delegationOrder = new ArrayList<RepositoryIdentifier>();
    private DelegatingAuthenticator delegatingAuthenticator;
    private DelegatingGroupManager delegatingGroupManager;
    private DelegatingPropertySetFactory delegatingPropertySetFactory;
    private DelegatingUserManager delegatingUserManager;
    private DelegatingEntityQueryParser delegatingEntityQueryParser;
    private List<Authenticator> authenticators = new ArrayList<Authenticator>();
    private List<UserManager> userManagers = new ArrayList<UserManager>();
    private List<GroupManager> groupManagers = new ArrayList<GroupManager>();
    private List<PropertySetFactory> propertySetFactories = new ArrayList<PropertySetFactory>();
    private List<EntityQueryParser> entityQueryParsers = new ArrayList<EntityQueryParser>();

    public DefaultDelegationAccessor() {
    }

    public DefaultDelegationAccessor(List<RepositoryAccessor> repositoryAccessors) {
        for (RepositoryAccessor accessor : repositoryAccessors) {
            this.addRepositoryAccessor(accessor);
        }
    }

    @Override
    public RepositoryIdentifier getIdentifier() {
        return new DefaultRepositoryIdentifier("delegatingRepository", "Delegating Repository");
    }

    @Override
    public UserManager getUserManager() {
        return this.delegatingUserManager;
    }

    @Override
    public GroupManager getGroupManager() {
        return this.delegatingGroupManager;
    }

    @Override
    public PropertySetFactory getPropertySetFactory() {
        return this.delegatingPropertySetFactory;
    }

    @Override
    public Authenticator getAuthenticator() {
        return this.delegatingAuthenticator;
    }

    @Override
    public EntityQueryParser getEntityQueryParser() {
        return this.delegatingEntityQueryParser;
    }

    @Override
    public RepositoryAccessor getRepositoryAccessor(String key) {
        for (RepositoryIdentifier identifier : this.repositoryAccessors.keySet()) {
            if (!identifier.getKey().equals(key)) continue;
            return this.repositoryAccessors.get(identifier);
        }
        return null;
    }

    @Override
    public List getRepositoryAccessors() {
        LinkedList<RepositoryAccessor> result = new LinkedList<RepositoryAccessor>();
        for (RepositoryIdentifier identifier : this.delegationOrder) {
            result.add(this.repositoryAccessors.get(identifier));
        }
        return result;
    }

    @Override
    public void addRepositoryAccessor(RepositoryAccessor accessor) {
        this.repositoryAccessors.put(accessor.getIdentifier(), accessor);
        this.delegationOrder.add(accessor.getIdentifier());
        if (accessor.getAuthenticator() != null) {
            this.authenticators.add(accessor.getAuthenticator());
        }
        if (accessor.getUserManager() != null) {
            this.userManagers.add(accessor.getUserManager());
        }
        if (accessor.getGroupManager() != null) {
            this.groupManagers.add(accessor.getGroupManager());
        }
        if (accessor.getPropertySetFactory() != null) {
            this.propertySetFactories.add(accessor.getPropertySetFactory());
        }
        if (accessor.getEntityQueryParser() != null) {
            this.entityQueryParsers.add(accessor.getEntityQueryParser());
        }
        this.delegatingUserManager = new DelegatingUserManager(this.userManagers);
        this.delegatingAuthenticator = new DelegatingAuthenticator(this.delegatingUserManager, this.authenticators);
        this.delegatingPropertySetFactory = new DelegatingPropertySetFactory(this.propertySetFactories);
        this.delegatingGroupManager = new DelegatingGroupManager(this.groupManagers);
        this.delegatingEntityQueryParser = new DelegatingEntityQueryParser(this.entityQueryParsers);
    }
}

