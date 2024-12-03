/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.configuration;

import com.atlassian.user.configuration.ConfigurationException;
import com.atlassian.user.configuration.DelegationAccessor;

public interface Configuration {
    public static final String DELEGATION = "delegation";
    public static final String PROCESSOR = "processor";
    public static final String CLASSES = "classes";
    public static final String HIBERNATE = "hibernate";
    public static final String LDAP = "ldap";
    public static final String OSUSER = "osuser";
    public static final String MEMORY = "memory";
    public static final String DEFAULT = "default";
    public static final String PARAM = "param";
    public static final String REPOSITORIES = "repositories";
    public static final String REPOSITORY = "repository";
    public static final String CLASS = "class";
    public static final String AUTHENTICATOR = "authenticator";
    public static final String USERMANAGER = "userManager";
    public static final String GROUPMANAGER = "groupManager";
    public static final String PASSWORD_ENCRYPTOR = "passwordEncryptor";
    public static final String PROPERTYSET_FACTORY = "propertySetFactory";
    public static final String CACHEFACTORY = "cacheFactory";
    public static final String USERFACTORY = "userFactory";
    public static final String USERADAPTOR = "userAdaptor";
    public static final String GROUPFACTORY = "groupFactory";
    public static final String GROUPADAPTOR = "groupAdaptor";
    public static final String ENTITY_QUERY_PARSER = "entityQueryParser";
    public static final String EXTERNAL_ENTITY_DAO = "externalEntityDAO";
    public static final String HIBERNATE_CONFIGURATION_PROVIDER = "configurationProvider";
    public static final String HIBERNATE_SESSION_FACTORY = "sessionFactory";
    public static final String ACCESSOR = "accessor";
    public static final String OSU_CREDENTIALS_PROVIDER = "credentialsProvider";
    public static final String OSU_AUTHENTICATOR = "authenticator";
    public static final String OSU_PROFILE_PROVIDER = "profileProvider";
    public static final String OSU_ACCESS_PROVIDER = "accessProvider";
    public static final String OSU_CREDENTIALS_PROVIDER_LIST = "credentialsProviderList";
    public static final String EXTERNAL_REPOSITORY = "externalRepository";
    public static final String MAXSIZE = "maxSize";
    public static final String INITSIZE = "initSize";
    public static final String PREFSIZE = "prefSize";
    public static final String DEBUG = "debugLevel";
    public static final String SECURITY_PROTOCOL = "securityProtocol";
    public static final String AUTHENTICATION = "authentication";
    public static final String TIMEOUT = "timeout";
    public static final String TRUSTSTORE = "trustStore";
    public static final String SERVER = "server";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String SECURITY_PRINCIPAL = "securityPrincipal";
    public static final String SECURITY_CREDENTIAL = "securityCredential";
    public static final String BASE_CONTEXT = "baseContext";
    public static final String TIME_TO_LIVE = "timeToLive";
    public static final String BATCH_SIZE = "batchSize";
    public static final String POOLING_ON = "poolingOn";
    public static final String INITIAL_CONTEXT_FACTORY_JNDI = "initialContextFactory";
    public static final String CONNECT_TIMEOUT = "connectTimeout";
    public static final String READ_TIMEOUT = "readTimeout";
    public static final String LDAP_SCHEMA_MAPPINGS_PROPERTIES = "schemaMappings";
    public static final String LDAP_CONNECTION_POOL_PROPERTIES = "connectionPool";
    public static final String MAPPINGS = "mappings";
    public static final String BASE_USER_NAMESPACE = "baseUserNamespace";
    public static final String BASE_GROUP_NAMESPACE = "baseGroupNamespace";
    public static final String USERNAME_ATTRIBUTE = "usernameAttribute";
    public static final String USER_SEARCH_FILTER = "userSearchFilter";
    public static final String GROUP_SEARCH_FILTER = "groupSearchFilter";
    public static final String FIRSTNAME_ATTRIBUTE = "firstnameAttribute";
    public static final String SURNAME_ATTRIBUTE = "surnameAttribute";
    public static final String EMAIL_ATTRIBUTE = "emailAttribute";
    public static final String GROUPNAME_ATTRIBUTE = "groupnameAttribute";
    public static final String MEMBERSHIP_ATTRIBUTE = "membershipAttribute";
    public static final String USER_SEARCH_ALL_DEPTHS = "userSearchAllDepths";
    public static final String GROUP_SEARCH_ALL_DEPTHS = "groupSearchAllDepths";
    public static final String USE_UNQUALIFIED_USER_NAME_FOR_MEMBERSHIP_COMPARISON = "useUnqualifiedUsernameForMembershipComparison";
    public static final String ORDER = "order";
    public static final String KEY = "key";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String CACHE = "cache";
    public static final String POOL_AUTHENTICATION = "poolAuthentication";
    public static final String PROVIDER_URL = "providerUrl";

    public DelegationAccessor getDelegationAccessor();

    public void init() throws ConfigurationException;

    public boolean isInitialized();
}

