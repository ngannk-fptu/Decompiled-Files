/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.SynchronisableDirectoryProperties$SyncGroupMembershipsAfterAuth
 *  com.atlassian.crowd.embedded.api.PermissionOption
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.crowd.embedded.admin.ldap;

import com.atlassian.crowd.directory.SynchronisableDirectoryProperties;
import com.atlassian.crowd.embedded.admin.ldap.SharedLdapDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.util.ConfigurationWithPassword;
import com.atlassian.crowd.embedded.api.PermissionOption;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public final class LdapDirectoryConfiguration
extends SharedLdapDirectoryConfiguration
implements ConfigurationWithPassword {
    private long directoryId;
    private boolean active = true;
    private String name = "LDAP server";
    private String type;
    private boolean ldapSecure;
    private PermissionOption ldapPermissionOption = PermissionOption.READ_ONLY;
    private boolean nestedGroupsEnabled;
    private boolean localUserStatusEnabled;
    private boolean rolesDisabled = true;
    private String ldapAutoAddGroups;
    private String ldapBasedn;
    private String ldapUserdn;
    private String ldapPassword;
    private String ldapUserDn;
    private String ldapGroupDn;
    private boolean ldapPagedresults;
    private String ldapPagedresultsSize = "1000";
    private boolean ldapReferral;
    private boolean ldapFilterExpiredUsers;
    private boolean ldapUsermembershipUseForGroups;
    private boolean ldapUsermembershipUse;
    private boolean ldapRelaxedDnStandardisation;
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
    private String ldapUserEncryption = "sha";
    private String ldapGroupObjectclass;
    private String ldapGroupFilter;
    private String ldapGroupName;
    private String ldapGroupDescription;
    private String ldapGroupUsernames;
    private boolean crowdSyncIncrementalEnabled = true;
    private String ldapCacheSynchroniseIntervalInMin = "60";
    private String ldapConnectionTimeoutInSec = "10";
    private String ldapReadTimeoutInSec = "120";
    private String ldapSearchTimelimitInSec = "60";
    private String ldapExternalId;
    private boolean newForm = true;
    private SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth groupSyncOnAuthMode = SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.DEFAULT;

    @Override
    public long getDirectoryId() {
        return this.directoryId;
    }

    public void setDirectoryId(long directoryId) {
        this.directoryId = directoryId;
    }

    public boolean isNewConfiguration() {
        return this.directoryId == 0L;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
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

    public PermissionOption getLdapPermissionOption() {
        return this.ldapPermissionOption;
    }

    public void setLdapPermissionOption(PermissionOption ldapPermissionOption) {
        this.ldapPermissionOption = ldapPermissionOption;
    }

    public boolean isLdapPropogateChanges() {
        return this.ldapPermissionOption == PermissionOption.READ_WRITE;
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

    public boolean isNestedGroupsEnabled() {
        return this.nestedGroupsEnabled;
    }

    public void setNestedGroupsEnabled(boolean nestedGroupsEnabled) {
        this.nestedGroupsEnabled = nestedGroupsEnabled;
    }

    public boolean isLocalUserStatusEnabled() {
        return this.localUserStatusEnabled;
    }

    public void setLocalUserStatusEnabled(boolean localUserStatusEnabled) {
        this.localUserStatusEnabled = localUserStatusEnabled;
    }

    public String getLdapAutoAddGroups() {
        return this.ldapAutoAddGroups;
    }

    public void setLdapAutoAddGroups(String ldapAutoAddGroups) {
        this.ldapAutoAddGroups = ldapAutoAddGroups;
    }

    public boolean isLdapNestedgroupsDisabled() {
        return !this.nestedGroupsEnabled;
    }

    public void setLdapNestedgroupsDisabled(boolean ldapNestedgroupsDisabled) {
        this.nestedGroupsEnabled = !ldapNestedgroupsDisabled;
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

    public boolean isLdapFilterExpiredUsers() {
        return this.ldapFilterExpiredUsers;
    }

    public void setLdapFilterExpiredUsers(boolean ldapFilterExpiredUsers) {
        this.ldapFilterExpiredUsers = ldapFilterExpiredUsers;
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

    public boolean isNewDirectory() {
        return this.directoryId <= 0L;
    }

    public String getLdapConnectionTimeoutInSec() {
        return this.ldapConnectionTimeoutInSec;
    }

    public void setLdapConnectionTimeoutInSec(String ldapConnectionTimeoutInSec) {
        this.ldapConnectionTimeoutInSec = ldapConnectionTimeoutInSec;
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

    public boolean isCrowdSyncIncrementalEnabled() {
        return this.crowdSyncIncrementalEnabled;
    }

    public void setCrowdSyncIncrementalEnabled(boolean crowdSyncIncrementalEnabled) {
        this.crowdSyncIncrementalEnabled = crowdSyncIncrementalEnabled;
    }

    public String getLdapCacheSynchroniseIntervalInMin() {
        return this.ldapCacheSynchroniseIntervalInMin;
    }

    public void setLdapCacheSynchroniseIntervalInMin(String ldapCacheSynchroniseIntervalInMin) {
        this.ldapCacheSynchroniseIntervalInMin = ldapCacheSynchroniseIntervalInMin;
    }

    public boolean getLocalGroups() {
        return PermissionOption.READ_ONLY_LOCAL_GROUPS.equals((Object)this.ldapPermissionOption);
    }

    public void setLocalGroups(boolean localGroups) {
        if (localGroups) {
            this.setLdapPermissionOption(PermissionOption.READ_ONLY_LOCAL_GROUPS);
        }
    }

    public boolean isNewForm() {
        return this.newForm;
    }

    public void setNewForm(boolean newForm) {
        this.newForm = newForm;
    }

    public String getLdapExternalId() {
        return this.ldapExternalId;
    }

    public void setLdapExternalId(String ldapExternalId) {
        this.ldapExternalId = ldapExternalId;
    }

    @Override
    public void setPassword(String password) {
        this.setLdapPassword(password);
    }

    @Override
    public String getPassword() {
        return this.getLdapPassword();
    }

    @Override
    public String getPasswordAttributeKey() {
        return "ldap.password";
    }

    public void setGroupSyncOnAuthMode(SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth groupSyncOnAuthMode) {
        this.groupSyncOnAuthMode = groupSyncOnAuthMode;
    }

    public SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth getGroupSyncOnAuthMode() {
        return this.groupSyncOnAuthMode;
    }
}

