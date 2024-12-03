/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.admin.delegatingldap;

import com.atlassian.crowd.embedded.admin.ldap.SharedLdapDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.util.ConfigurationWithPassword;

public final class DelegatingLdapDirectoryConfiguration
extends SharedLdapDirectoryConfiguration
implements ConfigurationWithPassword {
    private long directoryId;
    private boolean active = true;
    private String name = "Delegated LDAP Authentication";
    private String type;
    private boolean ldapSecure;
    private String ldapBasedn;
    private String ldapUserdn;
    private String ldapPassword;
    private String ldapUserDn;
    private String ldapUserObjectclass;
    private String ldapUserFilter;
    private String ldapUserUsername;
    private String ldapUserUsernameRdn;
    private String ldapUserFirstname;
    private String ldapUserLastname;
    private String ldapUserDisplayname;
    private String ldapUserEmail;
    private String ldapGroupDn;
    private String ldapGroupObjectclass;
    private String ldapGroupFilter;
    private String ldapGroupName;
    private String ldapGroupDescription;
    private String ldapGroupUsernames;
    private String ldapUserGroup;
    private boolean ldapUsermembershipUseForGroups;
    private boolean ldapUsermembershipUse;
    private boolean nestedGroupsEnabled;
    private boolean synchroniseGroupMemberships;
    private boolean createUserOnAuth;
    private boolean updateUserOnAuth;
    private String ldapAutoAddGroups = "";
    private boolean ldapPagedresults;
    private String ldapPagedresultsSize = "1000";
    private boolean ldapReferral;
    private String ldapExternalId;
    private boolean newForm = true;

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

    public String getLdapUserDn() {
        return this.ldapUserDn;
    }

    public void setLdapUserDn(String ldapUserDn) {
        this.ldapUserDn = ldapUserDn;
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

    public boolean isCreateUserOnAuth() {
        return this.createUserOnAuth;
    }

    public void setCreateUserOnAuth(boolean createUserOnAuth) {
        this.createUserOnAuth = createUserOnAuth;
    }

    public boolean isUpdateUserOnAuth() {
        return this.updateUserOnAuth;
    }

    public void setUpdateUserOnAuth(boolean updateUserOnAuth) {
        this.updateUserOnAuth = updateUserOnAuth;
    }

    public boolean isNewDirectory() {
        return this.directoryId <= 0L;
    }

    public boolean isNewForm() {
        return this.newForm;
    }

    public void setNewForm(boolean newForm) {
        this.newForm = newForm;
    }

    public void setLdapAutoAddGroups(String groups) {
        this.ldapAutoAddGroups = groups;
    }

    public String getLdapAutoAddGroups() {
        return this.ldapAutoAddGroups;
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

    public String getLdapUserGroup() {
        return this.ldapUserGroup;
    }

    public void setLdapUserGroup(String ldapUserGroup) {
        this.ldapUserGroup = ldapUserGroup;
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

    public boolean isSynchroniseGroupMemberships() {
        return this.synchroniseGroupMemberships;
    }

    public void setSynchroniseGroupMemberships(boolean synchroniseGroupMemberships) {
        this.synchroniseGroupMemberships = synchroniseGroupMemberships;
    }

    public String getLdapGroupDn() {
        return this.ldapGroupDn;
    }

    public void setLdapGroupDn(String ldapGroupDn) {
        this.ldapGroupDn = ldapGroupDn;
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

    public boolean isNestedGroupsEnabled() {
        return this.nestedGroupsEnabled;
    }

    public void setNestedGroupsEnabled(boolean nestedGroupsEnabled) {
        this.nestedGroupsEnabled = nestedGroupsEnabled;
    }

    public boolean isLdapNestedgroupsDisabled() {
        return !this.nestedGroupsEnabled;
    }

    public void setLdapNestedgroupsDisabled(boolean ldapNestedgroupsDisabled) {
        this.nestedGroupsEnabled = !ldapNestedgroupsDisabled;
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
}

