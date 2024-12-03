/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.SynchronisableDirectoryProperties$SyncGroupMembershipsAfterAuth
 *  com.atlassian.crowd.directory.ldap.connectionpool.SpringLdapPoolConfigService
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.crowd.embedded.admin.directory;

import com.atlassian.crowd.directory.SynchronisableDirectoryProperties;
import com.atlassian.crowd.directory.ldap.connectionpool.SpringLdapPoolConfigService;
import com.atlassian.crowd.embedded.admin.directory.LdapConnectionPoolDirectoryAttributes;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.commons.lang3.math.NumberUtils;

public class LdapDirectoryAttributes {
    private String ldapUrl;
    private String ldapBasedn;
    private String ldapUserdn;
    private boolean ldapSecure;
    private String ldapPassword;
    private boolean ldapPropogateChanges;
    private String ldapUserDn;
    private String ldapGroupDn;
    private boolean ldapNestedgroupsDisabled;
    private boolean localUserStatusEnabled;
    private boolean rolesDisabled = true;
    private boolean ldapPagedresults;
    private String ldapPagedresultsSize;
    private boolean ldapReferral;
    private boolean ldapFilterExpiredUsers;
    private boolean ldapUsermembershipUseForGroups;
    private boolean ldapUsermembershipUse;
    private boolean ldapRelaxedDnStandardisation;
    private String ldapUserEncryption;
    private String ldapUserObjectclass;
    private String ldapUserFilter;
    private String ldapUserUsername;
    private String ldapUserUsernameRdn;
    private String ldapUserFirstname;
    private String ldapUserLastname;
    private String ldapUserDisplayname;
    private String ldapUserEmail;
    private String ldapUserGroup;
    private String ldapUserPassword;
    private String ldapGroupObjectclass;
    private String ldapGroupFilter;
    private String ldapGroupName;
    private String ldapGroupDescription;
    private String ldapGroupUsernames;
    private boolean localGroups;
    private boolean incrementalSyncEnabled;
    private String ldapCacheSynchroniseIntervalInMin;
    private String ldapPoolInitSize;
    private String ldapPoolPrefSize;
    private String ldapPoolMaxSize;
    private String ldapPoolTimeoutInSec;
    private String ldapConnectionTimeoutInSec;
    private String ldapReadTimeoutInSec;
    private String ldapSearchTimelimitInSec;
    private String ldapAutoAddGroups;
    private String ldapExternalId;
    private SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth groupSyncOnAuthMode = SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.DEFAULT;
    private LdapConnectionPoolDirectoryAttributes ldapCpAttributes = new LdapConnectionPoolDirectoryAttributes();

    public String getLdapUrl() {
        return this.ldapUrl;
    }

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public boolean isLdapSecure() {
        return this.ldapSecure;
    }

    public void setLdapSecure(boolean ldapSecure) {
        this.ldapSecure = ldapSecure;
    }

    public String getLdapBasedn() {
        return this.ldapBasedn;
    }

    public void setLdapBasedn(String ldapBasedn) {
        this.ldapBasedn = ldapBasedn;
    }

    public String getLdapUserdn() {
        return this.ldapUserdn;
    }

    public void setLdapUserdn(String ldapUserdn) {
        this.ldapUserdn = ldapUserdn;
    }

    public String getLdapPassword() {
        return this.ldapPassword;
    }

    public void setLdapPassword(String ldapPassword) {
        this.ldapPassword = ldapPassword;
    }

    public boolean isLdapPropogateChanges() {
        return this.ldapPropogateChanges;
    }

    public void setLdapPropogateChanges(boolean ldapPropogateChanges) {
        this.ldapPropogateChanges = ldapPropogateChanges;
    }

    public String getLdapUserDn() {
        return this.ldapUserDn;
    }

    public void setLdapUserDn(String ldapUserDn) {
        this.ldapUserDn = ldapUserDn;
    }

    public String getLdapGroupDn() {
        return this.ldapGroupDn;
    }

    public void setLdapGroupDn(String ldapGroupDn) {
        this.ldapGroupDn = ldapGroupDn;
    }

    public boolean isLdapNestedgroupsDisabled() {
        return this.ldapNestedgroupsDisabled;
    }

    public void setLdapNestedgroupsDisabled(boolean ldapNestedgroupsDisabled) {
        this.ldapNestedgroupsDisabled = ldapNestedgroupsDisabled;
    }

    public boolean isLocalUserStatusEnabled() {
        return this.localUserStatusEnabled;
    }

    public void setLocalUserStatusEnabled(boolean localUserStatusEnabled) {
        this.localUserStatusEnabled = localUserStatusEnabled;
    }

    public boolean isLdapFilterExpiredUsers() {
        return this.ldapFilterExpiredUsers;
    }

    public void setLdapFilterExpiredUsers(boolean ldapFilterExpiredUsers) {
        this.ldapFilterExpiredUsers = ldapFilterExpiredUsers;
    }

    public boolean isRolesDisabled() {
        return this.rolesDisabled;
    }

    public void setRolesDisabled(boolean rolesDisabled) {
        this.rolesDisabled = rolesDisabled;
    }

    public boolean isLdapPagedresults() {
        return this.ldapPagedresults;
    }

    public void setLdapPagedresults(boolean ldapPagedresults) {
        this.ldapPagedresults = ldapPagedresults;
    }

    public String getLdapPagedresultsSize() {
        return this.ldapPagedresultsSize;
    }

    public void setLdapPagedresultsSize(String ldapPagedresultsSize) {
        this.ldapPagedresultsSize = ldapPagedresultsSize;
    }

    public boolean isLdapReferral() {
        return this.ldapReferral;
    }

    public void setLdapReferral(boolean ldapReferral) {
        this.ldapReferral = ldapReferral;
    }

    public boolean isLdapUsermembershipUseForGroups() {
        return this.ldapUsermembershipUseForGroups;
    }

    public void setLdapUsermembershipUseForGroups(boolean ldapUsermembershipUseForGroups) {
        this.ldapUsermembershipUseForGroups = ldapUsermembershipUseForGroups;
    }

    public boolean isLdapUsermembershipUse() {
        return this.ldapUsermembershipUse;
    }

    public void setLdapUsermembershipUse(boolean ldapUsermembershipUse) {
        this.ldapUsermembershipUse = ldapUsermembershipUse;
    }

    public boolean isLdapRelaxedDnStandardisation() {
        return this.ldapRelaxedDnStandardisation;
    }

    public void setLdapRelaxedDnStandardisation(boolean ldapRelaxedDnStandardisation) {
        this.ldapRelaxedDnStandardisation = ldapRelaxedDnStandardisation;
    }

    public String getLdapUserEncryption() {
        return this.ldapUserEncryption;
    }

    public void setLdapUserEncryption(String ldapUserEncryption) {
        this.ldapUserEncryption = ldapUserEncryption;
    }

    public String getLdapUserObjectclass() {
        return this.ldapUserObjectclass;
    }

    public void setLdapUserObjectclass(String ldapUserObjectclass) {
        this.ldapUserObjectclass = ldapUserObjectclass;
    }

    public String getLdapUserFilter() {
        return this.ldapUserFilter;
    }

    public void setLdapUserFilter(String ldapUserFilter) {
        this.ldapUserFilter = ldapUserFilter;
    }

    public String getLdapUserUsername() {
        return this.ldapUserUsername;
    }

    public void setLdapUserUsername(String ldapUserUsername) {
        this.ldapUserUsername = ldapUserUsername;
    }

    public String getLdapUserUsernameRdn() {
        return this.ldapUserUsernameRdn;
    }

    public void setLdapUserUsernameRdn(String ldapUserUsernameRdn) {
        this.ldapUserUsernameRdn = ldapUserUsernameRdn;
    }

    public String getLdapUserFirstname() {
        return this.ldapUserFirstname;
    }

    public void setLdapUserFirstname(String ldapUserFirstname) {
        this.ldapUserFirstname = ldapUserFirstname;
    }

    public String getLdapUserLastname() {
        return this.ldapUserLastname;
    }

    public void setLdapUserLastname(String ldapUserLastname) {
        this.ldapUserLastname = ldapUserLastname;
    }

    public String getLdapUserDisplayname() {
        return this.ldapUserDisplayname;
    }

    public void setLdapUserDisplayname(String ldapUserDisplayname) {
        this.ldapUserDisplayname = ldapUserDisplayname;
    }

    public String getLdapUserEmail() {
        return this.ldapUserEmail;
    }

    public void setLdapUserEmail(String ldapUserEmail) {
        this.ldapUserEmail = ldapUserEmail;
    }

    public String getLdapUserGroup() {
        return this.ldapUserGroup;
    }

    public void setLdapUserGroup(String ldapUserGroup) {
        this.ldapUserGroup = ldapUserGroup;
    }

    public String getLdapUserPassword() {
        return this.ldapUserPassword;
    }

    public void setLdapUserPassword(String ldapUserPassword) {
        this.ldapUserPassword = ldapUserPassword;
    }

    public String getLdapGroupObjectclass() {
        return this.ldapGroupObjectclass;
    }

    public void setLdapGroupObjectclass(String ldapGroupObjectclass) {
        this.ldapGroupObjectclass = ldapGroupObjectclass;
    }

    public String getLdapGroupFilter() {
        return this.ldapGroupFilter;
    }

    public void setLdapGroupFilter(String ldapGroupFilter) {
        this.ldapGroupFilter = ldapGroupFilter;
    }

    public String getLdapGroupName() {
        return this.ldapGroupName;
    }

    public void setLdapGroupName(String ldapGroupName) {
        this.ldapGroupName = ldapGroupName;
    }

    public String getLdapGroupDescription() {
        return this.ldapGroupDescription;
    }

    public void setLdapGroupDescription(String ldapGroupDescription) {
        this.ldapGroupDescription = ldapGroupDescription;
    }

    public String getLdapGroupUsernames() {
        return this.ldapGroupUsernames;
    }

    public void setLdapGroupUsernames(String ldapGroupUsernames) {
        this.ldapGroupUsernames = ldapGroupUsernames;
    }

    public boolean isIncrementalSyncEnabled() {
        return this.incrementalSyncEnabled;
    }

    public void setIncrementalSyncEnabled(boolean incrementalSyncEnabled) {
        this.incrementalSyncEnabled = incrementalSyncEnabled;
    }

    public String getLdapCacheSynchroniseIntervalInMin() {
        return this.ldapCacheSynchroniseIntervalInMin;
    }

    public void setLdapCacheSynchroniseIntervalInMin(String ldapCacheSynchroniseIntervalInMin) {
        this.ldapCacheSynchroniseIntervalInMin = ldapCacheSynchroniseIntervalInMin;
    }

    public boolean isLocalGroups() {
        return this.localGroups;
    }

    public void setLocalGroups(boolean localGroups) {
        this.localGroups = localGroups;
    }

    public String getLdapPoolInitSize() {
        return this.ldapPoolInitSize;
    }

    public void setLdapPoolInitSize(String ldapPoolInitSize) {
        this.ldapPoolInitSize = ldapPoolInitSize;
    }

    public String getLdapPoolMaxSize() {
        return this.ldapPoolMaxSize;
    }

    public void setLdapPoolMaxSize(String ldapPoolMaxSize) {
        this.ldapPoolMaxSize = ldapPoolMaxSize;
    }

    public String getLdapPoolPrefSize() {
        return this.ldapPoolPrefSize;
    }

    public void setLdapPoolPrefSize(String ldapPoolPrefSize) {
        this.ldapPoolPrefSize = ldapPoolPrefSize;
    }

    public String getLdapConnectionTimeoutInSec() {
        return this.ldapConnectionTimeoutInSec;
    }

    public void setLdapConnectionTimeoutInSec(String ldapConnectionTimeoutInSec) {
        this.ldapConnectionTimeoutInSec = ldapConnectionTimeoutInSec;
    }

    public String getLdapPoolTimeoutInSec() {
        return this.ldapPoolTimeoutInSec;
    }

    public void setLdapPoolTimeoutInSec(String ldapPoolTimeoutInSec) {
        this.ldapPoolTimeoutInSec = ldapPoolTimeoutInSec;
    }

    public String getLdapReadTimeoutInSec() {
        return this.ldapReadTimeoutInSec;
    }

    public void setLdapReadTimeoutInSec(String ldapReadTimeoutInSec) {
        this.ldapReadTimeoutInSec = ldapReadTimeoutInSec;
    }

    public String getLdapSearchTimelimitInSec() {
        return this.ldapSearchTimelimitInSec;
    }

    public void setLdapSearchTimelimitInSec(String ldapSearchTimelimitInSec) {
        this.ldapSearchTimelimitInSec = ldapSearchTimelimitInSec;
    }

    public void setLdapAutoAddGroups(String groups) {
        this.ldapAutoAddGroups = groups;
    }

    public String getLdapAutoAddGroups() {
        return this.ldapAutoAddGroups;
    }

    public String getLdapExternalId() {
        return this.ldapExternalId;
    }

    public void setLdapExternalId(String ldapExternalId) {
        this.ldapExternalId = ldapExternalId;
    }

    public void setGroupSyncOnAuthMode(SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth membershipSyncOnAuth) {
        this.groupSyncOnAuthMode = membershipSyncOnAuth;
    }

    public SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth getGroupSyncOnAuthMode() {
        return this.groupSyncOnAuthMode;
    }

    public LdapConnectionPoolDirectoryAttributes getLdapCpAttributes() {
        return this.ldapCpAttributes;
    }

    public void setLdapCpAttributes(LdapConnectionPoolDirectoryAttributes ldapCpAttributes) {
        this.ldapCpAttributes = ldapCpAttributes;
    }

    public Function<SpringLdapPoolConfigService, Map<String, String>> toAttributesMap() {
        return ldapPoolConfigService -> {
            HashMap map = Maps.newHashMapWithExpectedSize((int)50);
            map.put("ldap.url", this.ldapUrl);
            map.put("ldap.secure", String.valueOf(this.ldapSecure));
            map.put("ldap.basedn", this.ldapBasedn);
            map.put("ldap.userdn", this.ldapUserdn);
            map.put("ldap.password", this.ldapPassword);
            map.put("ldap.propogate.changes", String.valueOf(this.ldapPropogateChanges));
            map.put("ldap.user.dn", this.ldapUserDn);
            map.put("ldap.group.dn", this.ldapGroupDn);
            map.put("ldap.nestedgroups.disabled", String.valueOf(this.ldapNestedgroupsDisabled));
            map.put("ldap.filter.expiredUsers", String.valueOf(this.ldapFilterExpiredUsers));
            map.put("localUserStatusEnabled", String.valueOf(this.localUserStatusEnabled));
            map.put("ldap.roles.disabled", String.valueOf(this.rolesDisabled));
            map.put("ldap.pagedresults", String.valueOf(this.ldapPagedresults));
            map.put("ldap.pagedresults.size", this.ldapPagedresultsSize);
            map.put("ldap.referral", String.valueOf(this.ldapReferral));
            map.put("ldap.usermembership.use.for.groups", String.valueOf(this.ldapUsermembershipUseForGroups));
            map.put("ldap.usermembership.use", String.valueOf(this.ldapUsermembershipUse));
            map.put("ldap.relaxed.dn.standardisation", String.valueOf(this.ldapRelaxedDnStandardisation));
            map.put("ldap.user.encryption", this.ldapUserEncryption);
            map.put("ldap.user.objectclass", this.ldapUserObjectclass);
            map.put("ldap.user.filter", this.ldapUserFilter);
            map.put("ldap.user.username", this.ldapUserUsername);
            map.put("ldap.user.username.rdn", this.ldapUserUsernameRdn);
            map.put("ldap.user.firstname", this.ldapUserFirstname);
            map.put("ldap.user.lastname", this.ldapUserLastname);
            map.put("ldap.user.displayname", this.ldapUserDisplayname);
            map.put("ldap.user.email", this.ldapUserEmail);
            map.put("ldap.user.group", this.ldapUserGroup);
            map.put("ldap.user.password", this.ldapUserPassword);
            map.put("ldap.group.objectclass", this.ldapGroupObjectclass);
            map.put("ldap.group.filter", this.ldapGroupFilter);
            map.put("ldap.group.name", this.ldapGroupName);
            map.put("ldap.group.description", this.ldapGroupDescription);
            map.put("ldap.group.usernames", this.ldapGroupUsernames);
            map.put("ldap.local.groups", String.valueOf(this.localGroups));
            map.put("ldap.pool.initsize", this.ldapPoolInitSize);
            map.put("ldap.pool.prefsize", this.ldapPoolPrefSize);
            map.put("ldap.pool.maxsize", this.ldapPoolMaxSize);
            map.put("crowd.sync.incremental.enabled", String.valueOf(this.incrementalSyncEnabled));
            map.put("directory.cache.synchronise.interval", Long.toString(NumberUtils.toLong((String)this.ldapCacheSynchroniseIntervalInMin) * 60L));
            map.put("ldap.pool.timeout", Long.toString(TimeUnit.MILLISECONDS.convert(NumberUtils.toLong((String)this.ldapPoolTimeoutInSec), TimeUnit.SECONDS)));
            map.put("ldap.connection.timeout", Long.toString(TimeUnit.MILLISECONDS.convert(NumberUtils.toLong((String)this.ldapConnectionTimeoutInSec), TimeUnit.SECONDS)));
            map.put("ldap.read.timeout", Long.toString(TimeUnit.MILLISECONDS.convert(NumberUtils.toLong((String)this.ldapReadTimeoutInSec), TimeUnit.SECONDS)));
            map.put("ldap.search.timelimit", Long.toString(TimeUnit.MILLISECONDS.convert(NumberUtils.toLong((String)this.ldapSearchTimelimitInSec), TimeUnit.SECONDS)));
            map.put("autoAddGroups", this.ldapAutoAddGroups);
            map.put("ldap.external.id", this.ldapExternalId);
            map.put("crowd.sync.group.membership.after.successful.user.auth.enabled", this.groupSyncOnAuthMode.getValue());
            map.putAll(this.ldapCpAttributes.toAttributesMap().apply((SpringLdapPoolConfigService)ldapPoolConfigService));
            return map;
        };
    }

    public static Function<SpringLdapPoolConfigService, LdapDirectoryAttributes> fromAttributesMap(Map<String, String> map) {
        return ldapPoolConfigService -> {
            LdapDirectoryAttributes attributes = new LdapDirectoryAttributes();
            attributes.setLdapUrl((String)map.get("ldap.url"));
            attributes.setLdapSecure(Boolean.parseBoolean((String)map.get("ldap.secure")));
            attributes.setLdapBasedn((String)map.get("ldap.basedn"));
            attributes.setLdapUserdn((String)map.get("ldap.userdn"));
            attributes.setLdapPassword((String)map.get("ldap.password"));
            attributes.setLdapPropogateChanges(Boolean.parseBoolean((String)map.get("ldap.propogate.changes")));
            attributes.setLdapUserDn((String)map.get("ldap.user.dn"));
            attributes.setLdapGroupDn((String)map.get("ldap.group.dn"));
            attributes.setLdapNestedgroupsDisabled(Boolean.parseBoolean((String)map.get("ldap.nestedgroups.disabled")));
            attributes.setLocalUserStatusEnabled(Boolean.parseBoolean((String)map.get("localUserStatusEnabled")));
            attributes.setLdapFilterExpiredUsers(Boolean.parseBoolean((String)map.get("ldap.filter.expiredUsers")));
            attributes.setRolesDisabled(Boolean.parseBoolean((String)map.get("ldap.roles.disabled")));
            attributes.setLdapPagedresults(Boolean.parseBoolean((String)map.get("ldap.pagedresults")));
            attributes.setLdapPagedresultsSize((String)map.get("ldap.pagedresults.size"));
            attributes.setLdapReferral(Boolean.parseBoolean((String)map.get("ldap.referral")));
            attributes.setLdapUsermembershipUseForGroups(Boolean.parseBoolean((String)map.get("ldap.usermembership.use.for.groups")));
            attributes.setLdapUsermembershipUse(Boolean.parseBoolean((String)map.get("ldap.usermembership.use")));
            attributes.setLdapRelaxedDnStandardisation(Boolean.parseBoolean((String)map.get("ldap.relaxed.dn.standardisation")));
            attributes.setLdapUserEncryption((String)map.get("ldap.user.encryption"));
            attributes.setLdapUserObjectclass((String)map.get("ldap.user.objectclass"));
            attributes.setLdapUserFilter((String)map.get("ldap.user.filter"));
            attributes.setLdapUserUsername((String)map.get("ldap.user.username"));
            attributes.setLdapUserUsernameRdn((String)map.get("ldap.user.username.rdn"));
            attributes.setLdapUserFirstname((String)map.get("ldap.user.firstname"));
            attributes.setLdapUserLastname((String)map.get("ldap.user.lastname"));
            attributes.setLdapUserDisplayname((String)map.get("ldap.user.displayname"));
            attributes.setLdapUserEmail((String)map.get("ldap.user.email"));
            attributes.setLdapUserGroup((String)map.get("ldap.user.group"));
            attributes.setLdapUserPassword((String)map.get("ldap.user.password"));
            attributes.setLdapGroupObjectclass((String)map.get("ldap.group.objectclass"));
            attributes.setLdapGroupFilter((String)map.get("ldap.group.filter"));
            attributes.setLdapGroupName((String)map.get("ldap.group.name"));
            attributes.setLdapGroupDescription((String)map.get("ldap.group.description"));
            attributes.setLdapGroupUsernames((String)map.get("ldap.group.usernames"));
            attributes.setLocalGroups(Boolean.parseBoolean((String)map.get("ldap.local.groups")));
            attributes.setLdapPoolInitSize((String)map.get("ldap.pool.initsize"));
            attributes.setLdapPoolPrefSize((String)map.get("ldap.pool.prefsize"));
            attributes.setLdapPoolMaxSize((String)map.get("ldap.pool.maxsize"));
            attributes.setIncrementalSyncEnabled(Boolean.parseBoolean((String)map.get("crowd.sync.incremental.enabled")));
            attributes.setLdapCacheSynchroniseIntervalInMin(Long.toString(NumberUtils.toLong((String)((String)map.get("directory.cache.synchronise.interval"))) / 60L));
            attributes.setLdapPoolTimeoutInSec(Long.toString(TimeUnit.SECONDS.convert(NumberUtils.toLong((String)((String)map.get("ldap.pool.timeout"))), TimeUnit.MILLISECONDS)));
            attributes.setLdapConnectionTimeoutInSec(Long.toString(TimeUnit.SECONDS.convert(NumberUtils.toLong((String)((String)map.get("ldap.connection.timeout"))), TimeUnit.MILLISECONDS)));
            attributes.setLdapReadTimeoutInSec(Long.toString(TimeUnit.SECONDS.convert(NumberUtils.toLong((String)((String)map.get("ldap.read.timeout"))), TimeUnit.MILLISECONDS)));
            attributes.setLdapSearchTimelimitInSec(Long.toString(TimeUnit.SECONDS.convert(NumberUtils.toLong((String)((String)map.get("ldap.search.timelimit"))), TimeUnit.MILLISECONDS)));
            attributes.setLdapAutoAddGroups((String)map.get("autoAddGroups"));
            attributes.setLdapExternalId((String)map.get("ldap.external.id"));
            attributes.setGroupSyncOnAuthMode(SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.forValue((String)((String)map.get("crowd.sync.group.membership.after.successful.user.auth.enabled"))));
            attributes.setLdapCpAttributes(LdapConnectionPoolDirectoryAttributes.fromAttributesMap(map).apply((SpringLdapPoolConfigService)ldapPoolConfigService));
            return attributes;
        };
    }
}

