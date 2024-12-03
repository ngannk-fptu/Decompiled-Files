/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.ldap;

import com.atlassian.crowd.directory.ldap.LdapPoolType;
import com.atlassian.crowd.directory.ldap.LdapSecureMode;
import com.atlassian.crowd.directory.ldap.LdapTypeConfig;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface LDAPPropertiesMapper {
    public static final String LDAP_URL_KEY = "ldap.url";
    public static final String LDAP_SECURE_KEY = "ldap.secure";
    public static final String LDAP_REFERRAL_KEY = "ldap.referral";
    public static final String LDAP_POOLING_KEY = "ldap.pooling";
    public static final String LDAP_BASEDN_KEY = "ldap.basedn";
    public static final String LDAP_USERDN_KEY = "ldap.userdn";
    public static final String LDAP_PASSWORD_KEY = "ldap.password";
    public static final String LDAP_PROPOGATE_CHANGES = "ldap.propogate.changes";
    public static final String GROUP_DN_ADDITION = "ldap.group.dn";
    public static final String GROUP_DESCRIPTION_KEY = "ldap.group.description";
    public static final String GROUP_NAME_KEY = "ldap.group.name";
    public static final String GROUP_OBJECTCLASS_KEY = "ldap.group.objectclass";
    public static final String GROUP_OBJECTFILTER_KEY = "ldap.group.filter";
    public static final String GROUP_USERNAMES_KEY = "ldap.group.usernames";
    public static final String ROLE_DN_ADDITION = "ldap.role.dn";
    public static final String ROLE_DESCRIPTION_KEY = "ldap.role.description";
    public static final String ROLE_NAME_KEY = "ldap.role.name";
    public static final String ROLE_OBJECTCLASS_KEY = "ldap.role.objectclass";
    public static final String ROLE_OBJECTFILTER_KEY = "ldap.role.filter";
    public static final String ROLE_USERNAMES_KEY = "ldap.role.usernames";
    public static final String USER_DN_ADDITION = "ldap.user.dn";
    public static final String USER_EMAIL_KEY = "ldap.user.email";
    public static final String USER_FIRSTNAME_KEY = "ldap.user.firstname";
    public static final String USER_GROUP_KEY = "ldap.user.group";
    public static final String USER_LASTNAME_KEY = "ldap.user.lastname";
    public static final String USER_DISPLAYNAME_KEY = "ldap.user.displayname";
    public static final String USER_OBJECTCLASS_KEY = "ldap.user.objectclass";
    public static final String USER_OBJECTFILTER_KEY = "ldap.user.filter";
    public static final String USER_USERNAME_KEY = "ldap.user.username";
    public static final String USER_USERNAME_RDN_KEY = "ldap.user.username.rdn";
    public static final String USER_PASSWORD_KEY = "ldap.user.password";
    public static final String LDAP_PAGEDRESULTS_KEY = "ldap.pagedresults";
    public static final String LDAP_NESTED_GROUPS_DISABLED = "ldap.nestedgroups.disabled";
    public static final String LDAP_FILTER_EXPIRED_USERS = "ldap.filter.expiredUsers";
    public static final String LDAP_USING_USER_MEMBERSHIP_ATTRIBUTE = "ldap.usermembership.use";
    public static final String LDAP_USING_USER_MEMBERSHIP_ATTRIBUTE_FOR_GROUP_MEMBERSHIP = "ldap.usermembership.use.for.groups";
    public static final String LDAP_USER_ENCRYPTION_METHOD = "ldap.user.encryption";
    public static final String LDAP_PAGEDRESULTS_SIZE = "ldap.pagedresults.size";
    public static final String LDAP_RELAXED_DN_STANDARDISATION = "ldap.relaxed.dn.standardisation";
    public static final String ROLES_DISABLED = "ldap.roles.disabled";
    public static final String LOCAL_GROUPS = "ldap.local.groups";
    public static final String PRIMARY_GROUP_SUPPORT = "ldap.activedirectory.use_primary_groups";
    public static final String LDAP_POOL_INITSIZE = "ldap.pool.initsize";
    public static final String LDAP_POOL_PREFSIZE = "ldap.pool.prefsize";
    public static final String LDAP_POOL_MAXSIZE = "ldap.pool.maxsize";
    public static final String LDAP_POOL_TIMEOUT = "ldap.pool.timeout";
    public static final String LDAP_SEARCH_TIMELIMIT = "ldap.search.timelimit";
    public static final String LDAP_EXTERNAL_ID = "ldap.external.id";
    public static final String LDAP_GROUP_EXTERNAL_ID = "ldap.group.external.id";
    public static final String LDAP_POOL_CONFIG = "ldap.pool.config";
    public static final String LDAP_POOL_TYPE = "ldap.pool.type";

    public Map<String, String> getImplementations();

    public Map<String, Properties> getConfigurationDetails();

    public Map<String, Object> getEnvironment();

    public Map<String, String> getAttributes();

    public void setAttributes(Map<String, String> var1);

    public String getAttribute(String var1);

    public String getGroupFilter();

    public String getConnectionURL();

    public String getUsername();

    public String getPassword();

    public String getGroupNameAttribute();

    public String getObjectClassAttribute();

    @Deprecated
    public String getRoleFilter();

    @Deprecated
    public String getRoleNameAttribute();

    public String getUserFilter();

    public String getUserNameAttribute();

    public String getUserNameRdnAttribute();

    public String getUserEmailAttribute();

    public String getUserGroupMembershipsAttribute();

    public String getGroupObjectClass();

    public String getGroupDescriptionAttribute();

    public String getGroupMemberAttribute();

    @Deprecated
    public String getRoleObjectClass();

    @Deprecated
    public String getRoleDescriptionAttribute();

    @Deprecated
    public String getRoleMemberAttribute();

    public String getUserObjectClass();

    public String getUserFirstNameAttribute();

    public String getUserLastNameAttribute();

    public String getUserDisplayNameAttribute();

    public String getUserPasswordAttribute();

    public String getUserEncryptionMethod();

    public boolean isPagedResultsControl();

    public int getPagedResultsSize();

    public int getSearchTimeLimit();

    public boolean isNestedGroupsDisabled();

    public boolean isFilteringExpiredUsers();

    public boolean isUsingUserMembershipAttribute();

    public boolean isUsingUserMembershipAttributeForGroupMembership();

    public boolean isReferral();

    public boolean isRelaxedDnStandardisation();

    public boolean isRolesDisabled();

    public boolean isLocalUserStatusEnabled();

    public boolean isLocalGroupsEnabled();

    public boolean isPrimaryGroupSupported();

    public int getCacheSynchroniseInterval();

    public List<LdapTypeConfig> getLdapTypeConfigurations();

    public String getExternalIdAttribute();

    public String getGroupExternalIdAttribute();

    public LdapSecureMode getSecureMode();

    public String getLdapPoolConfig();

    public LdapPoolType getLdapPoolType();
}

