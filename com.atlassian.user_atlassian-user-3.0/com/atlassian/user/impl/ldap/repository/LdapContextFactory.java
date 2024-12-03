/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap.repository;

import com.atlassian.user.impl.RepositoryException;
import java.util.Hashtable;
import javax.naming.directory.DirContext;

public interface LdapContextFactory {
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String SECURITY_PRINCIPAL = "securityPrincipal";
    public static final String SECURITY_CREDENTIAL = "securityCredential";
    public static final String SECURITY_AUTHENTICATION = "securityAuthentication";
    public static final String PROVIDER_URL = "providerURL";
    public static final String JNDI_INITIAL_CONTEXT_FACTORY = "initialContextFactory";
    public static final String BATCH_SIZE = "batchSize";
    public static final String SECURITY_PROTOCOL = "securityProtocol";
    public static final String TIME_TO_LIVE = "timeToLive";
    public static final String BASE_USER_NAMESPACE = "baseUserNamespace";
    public static final String BASE_GROUP_NAMESPACE = "baseGroupNamespace";
    public static final String USERNAME_ATTRIBUTE = "usernameAttribute";
    public static final String GROUPNAME_ATTRIBUTE = "groupnameAttribute";
    public static final String FIRSTNAME_ATTRIBUTE = "firstnameAttribute";
    public static final String PASSWORD_ATTRIBUTE = "passwordAttribute";
    public static final String SURNAME_ATTRIBUTE = "surnameAttribute";
    public static final String EMAIL_ATTRIBUTE = "emailAttribute";
    public static final String MEMBERSHIP_ATTRIBUTE = "membershipAttribute";
    public static final String USER_SEARCH_ALL_DEPTHS = "userSearchAllDepths";
    public static final String GROUP_SEARCH_ALL_DEPTHS = "groupSearchAllDepths";
    public static final String GROUP_SEARCH_FILTER = "groupSearchFilter";
    public static final String USER_SEARCH_FILTER = "userSearchFilter";
    public static final String USE_UNQUALIFIED_USER_NAME_FOR_MEMBERSHIP_COMPARISON = "useUnqualifiedUsernameForMembershipComparison";

    public Hashtable getJNDIEnv();

    public Hashtable getAuthenticationJndiEnvironment(String var1, String var2);

    public DirContext getLDAPContext() throws RepositoryException;
}

