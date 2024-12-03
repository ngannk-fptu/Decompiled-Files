/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.ldap;

import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.LdapPoolType;
import com.atlassian.crowd.directory.ldap.LdapSecureMode;
import com.atlassian.crowd.directory.ldap.LdapTypeConfig;
import com.atlassian.crowd.directory.ldap.util.LDAPPropertiesHelper;
import com.atlassian.crowd.directory.ssl.LdapHostnameVerificationSSLSocketFactory;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.naming.InvalidNameException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LDAPPropertiesMapperImpl
implements LDAPPropertiesMapper {
    private static final Logger logger = LoggerFactory.getLogger(LDAPPropertiesMapperImpl.class);
    private Map<String, String> attributes;
    private final LDAPPropertiesHelper ldapPropertiesHelper;
    public static final String CONNECTION_INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    public static final String CONNECTION_SECURITY_AUTHENTICATION = "simple";
    public static final String CONNECTION_SSL_SECURITY_PROTOCOL = "ssl";
    public static final String CONNECTION_FACTORY = "java.naming.ldap.factory.socket";
    public static final String CONNECTION_FACTORY_SSL_IMPL = LdapHostnameVerificationSSLSocketFactory.class.getName();
    public static final String CONNECTION_BINARY_ATTRIBUTES = "java.naming.ldap.attributes.binary";
    private static final String JNDI_PROPERTIES_RESOURCE_NAME = "jndi.properties";
    private static final Map<String, Object> ENVIRONMENT_DEFAULTS = LDAPPropertiesMapperImpl.readDefaultsPropertiesFromClasspath();

    public LDAPPropertiesMapperImpl(LDAPPropertiesHelper ldapPropertiesHelper) {
        this.ldapPropertiesHelper = ldapPropertiesHelper;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Map<String, Object> readDefaultsPropertiesFromClasspath() {
        InputStream is = LDAPPropertiesMapperImpl.class.getResourceAsStream(JNDI_PROPERTIES_RESOURCE_NAME);
        try {
            if (is != null) {
                ImmutableMap safeMap;
                Properties defaultProperties = new Properties();
                defaultProperties.load(is);
                ImmutableMap immutableMap = safeMap = ImmutableMap.copyOf((Map)defaultProperties);
                return immutableMap;
            }
        }
        catch (IOException e) {
            logger.error("I/O error reading JNDI LDAP properties jndi.properties", (Throwable)e);
        }
        finally {
            IOUtils.closeQuietly((InputStream)is);
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> getImplementations() {
        return this.ldapPropertiesHelper.getImplementations();
    }

    @Override
    public Map<String, Properties> getConfigurationDetails() {
        return this.ldapPropertiesHelper.getConfigurationDetails();
    }

    @Override
    public Map<String, Object> getEnvironment() {
        String readTimeout;
        String connectionTimeout;
        HashMap<String, Object> environment = new HashMap<String, Object>();
        environment.put("java.naming.factory.initial", CONNECTION_INITIAL_CONTEXT_FACTORY);
        environment.put("java.naming.security.authentication", CONNECTION_SECURITY_AUTHENTICATION);
        if (this.isReferral()) {
            environment.put("java.naming.referral", "follow");
        }
        if (this.isLdaps()) {
            environment.put("java.naming.security.protocol", CONNECTION_SSL_SECURITY_PROTOCOL);
            environment.put(CONNECTION_FACTORY, CONNECTION_FACTORY_SSL_IMPL);
        }
        if (this.isUsingConnectionPooling()) {
            environment.put("com.sun.jndi.ldap.connect.pool", "true");
        }
        if (StringUtils.isNotBlank((CharSequence)(connectionTimeout = this.getConnectionTimeout()))) {
            environment.put("com.sun.jndi.ldap.connect.timeout", connectionTimeout);
        }
        if (StringUtils.isNotBlank((CharSequence)(readTimeout = this.getReadTimeout()))) {
            environment.put("com.sun.jndi.ldap.read.timeout", readTimeout);
        }
        environment.putAll(ENVIRONMENT_DEFAULTS);
        return environment;
    }

    protected boolean isLdaps() {
        return this.getSecureMode() == LdapSecureMode.LDAPS;
    }

    protected boolean isUsingConnectionPooling() {
        return this.getBooleanKey("ldap.pooling");
    }

    protected boolean getBooleanKey(String key) {
        return this.getBooleanKey(key, false);
    }

    protected boolean getBooleanKey(String key, boolean defaultValue) {
        String value = this.attributes.get(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    protected String getPoolInitSize() {
        return this.getAttribute("ldap.pool.initsize");
    }

    protected String getPoolPrefSize() {
        return this.getAttribute("ldap.pool.prefsize");
    }

    protected String getPoolMaxSize() {
        return this.getAttribute("ldap.pool.maxsize");
    }

    protected String getPoolTimeout() {
        return this.getAttribute("ldap.pool.timeout");
    }

    protected String getConnectionTimeout() {
        return this.getAttribute("ldap.connection.timeout");
    }

    protected String getReadTimeout() {
        return this.getAttribute("ldap.read.timeout");
    }

    @Override
    public int getSearchTimeLimit() {
        String timeLimit = this.getAttribute("ldap.search.timelimit");
        if (StringUtils.isNotBlank((CharSequence)timeLimit)) {
            return Integer.valueOf(timeLimit);
        }
        return 0;
    }

    @Override
    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getAttribute(String key) {
        String value = this.attributes.get(key);
        return value == null ? "" : value;
    }

    @Override
    public LdapSecureMode getSecureMode() {
        return LdapSecureMode.fromString(this.getAttribute("ldap.secure"));
    }

    public String getBaseDN() throws InvalidNameException {
        return this.getAttribute("ldap.basedn");
    }

    public String getGroupBaseDN() throws InvalidNameException {
        String additionalDN = this.getAttribute("ldap.group.dn");
        String groupSearchDN = additionalDN != null && !additionalDN.equals("") ? additionalDN + "," + this.getAttribute("ldap.basedn") : this.getAttribute("ldap.basedn");
        return groupSearchDN;
    }

    @Override
    public String getGroupFilter() {
        return this.getAttribute("ldap.group.filter");
    }

    @Override
    public String getConnectionURL() {
        return this.getAttribute("ldap.url");
    }

    @Override
    public String getUsername() {
        return this.getAttribute("ldap.userdn");
    }

    @Override
    public String getPassword() {
        return this.getAttribute("ldap.password");
    }

    @Override
    public String getGroupNameAttribute() {
        return this.getAttribute("ldap.group.name");
    }

    @Override
    public String getObjectClassAttribute() {
        return "objectClass";
    }

    @Override
    public String getRoleFilter() {
        return this.getAttribute("ldap.role.filter");
    }

    public String getRoleBaseDN() throws InvalidNameException {
        String additionalDN = this.getAttribute("ldap.role.dn");
        String roleSearchDN = additionalDN != null && !additionalDN.equals("") ? additionalDN + "," + this.getAttribute("ldap.basedn") : this.getAttribute("ldap.basedn");
        return roleSearchDN;
    }

    @Override
    public String getRoleNameAttribute() {
        return this.getAttribute("ldap.role.name");
    }

    @Override
    public String getUserFilter() {
        return this.getAttribute("ldap.user.filter");
    }

    public String getPrincipalBaseDN() {
        String additionalDN = this.getAttribute("ldap.user.dn");
        String principalSearchDN = additionalDN != null && !additionalDN.equals("") ? additionalDN + "," + this.getAttribute("ldap.basedn") : this.getAttribute("ldap.basedn");
        return principalSearchDN;
    }

    @Override
    public String getUserNameAttribute() {
        return this.getAttribute("ldap.user.username");
    }

    @Override
    public String getUserNameRdnAttribute() {
        return this.getAttribute("ldap.user.username.rdn");
    }

    @Override
    public String getUserEmailAttribute() {
        return this.getAttribute("ldap.user.email");
    }

    @Override
    public String getUserGroupMembershipsAttribute() {
        return this.getAttribute("ldap.user.group");
    }

    @Override
    public String getGroupObjectClass() {
        return this.getAttribute("ldap.group.objectclass");
    }

    @Override
    public String getGroupDescriptionAttribute() {
        return this.getAttribute("ldap.group.description");
    }

    @Override
    public String getGroupMemberAttribute() {
        return this.getAttribute("ldap.group.usernames");
    }

    @Override
    public String getRoleObjectClass() {
        return this.getAttribute("ldap.role.objectclass");
    }

    @Override
    public String getRoleDescriptionAttribute() {
        return this.getAttribute("ldap.role.description");
    }

    @Override
    public String getRoleMemberAttribute() {
        return this.getAttribute("ldap.role.usernames");
    }

    @Override
    public String getUserObjectClass() {
        return this.getAttribute("ldap.user.objectclass");
    }

    @Override
    public String getUserFirstNameAttribute() {
        return this.getAttribute("ldap.user.firstname");
    }

    @Override
    public String getUserLastNameAttribute() {
        return this.getAttribute("ldap.user.lastname");
    }

    @Override
    public String getUserDisplayNameAttribute() {
        return this.getAttribute("ldap.user.displayname");
    }

    @Override
    public String getUserPasswordAttribute() {
        return this.getAttribute("ldap.user.password");
    }

    @Override
    public String getUserEncryptionMethod() {
        return this.getAttribute("ldap.user.encryption");
    }

    @Override
    public boolean isPagedResultsControl() {
        boolean isPagedResultsControl = false;
        String isPagedResultsControlStr = this.getAttribute("ldap.pagedresults");
        if (isPagedResultsControlStr != null) {
            isPagedResultsControl = Boolean.valueOf(isPagedResultsControlStr);
        }
        return isPagedResultsControl;
    }

    @Override
    public int getPagedResultsSize() {
        int pagedResultsControlSize = 999;
        String isPagedResultsControlSizeStr = this.getAttribute("ldap.pagedresults.size");
        if (isPagedResultsControlSizeStr != null) {
            pagedResultsControlSize = Integer.valueOf(isPagedResultsControlSizeStr);
        }
        return pagedResultsControlSize;
    }

    @Override
    public boolean isNestedGroupsDisabled() {
        return this.getBooleanKey("ldap.nestedgroups.disabled", true);
    }

    @Override
    public boolean isFilteringExpiredUsers() {
        return this.getBooleanKey("ldap.filter.expiredUsers", false);
    }

    @Override
    public boolean isUsingUserMembershipAttribute() {
        return this.getBooleanKey("ldap.usermembership.use");
    }

    @Override
    public boolean isUsingUserMembershipAttributeForGroupMembership() {
        return this.getBooleanKey("ldap.usermembership.use.for.groups");
    }

    @Override
    public boolean isReferral() {
        return this.getBooleanKey("ldap.referral");
    }

    @Override
    public boolean isRelaxedDnStandardisation() {
        return this.getBooleanKey("ldap.relaxed.dn.standardisation");
    }

    @Override
    public boolean isRolesDisabled() {
        return this.getBooleanKey("ldap.roles.disabled");
    }

    @Override
    public boolean isLocalGroupsEnabled() {
        return this.getBooleanKey("ldap.local.groups");
    }

    @Override
    public boolean isLocalUserStatusEnabled() {
        return this.getBooleanKey("localUserStatusEnabled", false);
    }

    @Override
    public String getExternalIdAttribute() {
        return this.getAttribute("ldap.external.id");
    }

    @Override
    public String getGroupExternalIdAttribute() {
        return this.getAttribute("ldap.group.external.id");
    }

    @Override
    public boolean isPrimaryGroupSupported() {
        return this.getBooleanKey("ldap.activedirectory.use_primary_groups");
    }

    @Override
    public int getCacheSynchroniseInterval() {
        int cacheSynchroniseInterval = 3600;
        String cacheSynchroniseIntervalStr = this.getAttribute("directory.cache.synchronise.interval");
        if (cacheSynchroniseIntervalStr != null) {
            cacheSynchroniseInterval = Integer.valueOf(cacheSynchroniseIntervalStr);
        }
        return cacheSynchroniseInterval;
    }

    @Override
    public List<LdapTypeConfig> getLdapTypeConfigurations() {
        return this.ldapPropertiesHelper.getLdapTypeConfigs();
    }

    @Override
    public String getLdapPoolConfig() {
        return this.getAttribute("ldap.pool.config");
    }

    @Override
    public LdapPoolType getLdapPoolType() {
        return LdapPoolType.fromString(this.getAttribute("ldap.pool.type"));
    }
}

