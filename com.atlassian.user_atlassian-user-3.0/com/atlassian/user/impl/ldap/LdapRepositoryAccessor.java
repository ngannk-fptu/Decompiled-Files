/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap;

import com.atlassian.user.configuration.DefaultRepositoryAccessor;
import com.atlassian.user.impl.ldap.LDAPGroupFactory;
import com.atlassian.user.impl.ldap.LDAPUserFactory;
import com.atlassian.user.impl.ldap.adaptor.LDAPGroupAdaptor;
import com.atlassian.user.impl.ldap.properties.LdapConnectionProperties;
import com.atlassian.user.impl.ldap.properties.LdapMembershipProperties;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.user.impl.ldap.repository.LdapContextFactory;
import com.atlassian.user.impl.ldap.search.LDAPUserAdaptor;

public class LdapRepositoryAccessor
extends DefaultRepositoryAccessor {
    private LdapContextFactory contextFactory;
    private LDAPGroupAdaptor groupAdaptor;
    private LDAPUserAdaptor userAdaptor;
    private LDAPGroupFactory groupFactory;
    private LDAPUserFactory userFactory;
    private LdapSearchProperties searchProperties;
    private LdapMembershipProperties membershipProperties;
    private LdapConnectionProperties connectionProperties;

    public LdapContextFactory getContextFactory() {
        return this.contextFactory;
    }

    public void setContextFactory(LdapContextFactory contextFactory) {
        this.contextFactory = contextFactory;
    }

    public LDAPGroupAdaptor getGroupAdaptor() {
        return this.groupAdaptor;
    }

    public void setGroupAdaptor(LDAPGroupAdaptor groupAdaptor) {
        this.groupAdaptor = groupAdaptor;
    }

    public LDAPUserAdaptor getUserAdaptor() {
        return this.userAdaptor;
    }

    public void setUserAdaptor(LDAPUserAdaptor userAdaptor) {
        this.userAdaptor = userAdaptor;
    }

    public LDAPGroupFactory getGroupFactory() {
        return this.groupFactory;
    }

    public void setGroupFactory(LDAPGroupFactory groupFactory) {
        this.groupFactory = groupFactory;
    }

    public LDAPUserFactory getUserFactory() {
        return this.userFactory;
    }

    public void setUserFactory(LDAPUserFactory userFactory) {
        this.userFactory = userFactory;
    }

    public LdapSearchProperties getSearchProperties() {
        return this.searchProperties;
    }

    public void setSearchProperties(LdapSearchProperties searchProperties) {
        this.searchProperties = searchProperties;
    }

    public LdapMembershipProperties getMembershipProperties() {
        return this.membershipProperties;
    }

    public void setMembershipProperties(LdapMembershipProperties membershipProperties) {
        this.membershipProperties = membershipProperties;
    }

    public LdapConnectionProperties getConnectionProperties() {
        return this.connectionProperties;
    }

    public void setConnectionProperties(LdapConnectionProperties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }
}

