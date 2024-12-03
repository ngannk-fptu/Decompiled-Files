/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.ldap.connectionpool.SpringLdapPoolConfigService
 */
package com.atlassian.crowd.embedded.admin.directory;

import com.atlassian.crowd.directory.ldap.connectionpool.SpringLdapPoolConfigService;
import com.atlassian.crowd.embedded.admin.directory.LdapConnectionPoolDirectoryAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class LdapDelegatingDirectoryAttributes {
    private String ldapUrl;
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
    private boolean createUserOnAuth;
    private boolean updateUserOnAuth;
    private String delegatedToClass;
    private String ldapAutoAddGroups;
    private boolean ldapSecure;
    private String ldapGroupObjectclass;
    private String ldapGroupFilter;
    private String ldapGroupName;
    private String ldapGroupDescription;
    private String ldapGroupUsernames;
    private String ldapUserGroup;
    private boolean ldapUsermembershipUseForGroups;
    private boolean ldapUsermembershipUse;
    private boolean ldapNestedgroupsDisabled;
    private boolean synchroniseGroupMemberships;
    private String ldapGroupDn;
    private boolean ldapPagedresults;
    private String ldapPagedresultsSize;
    private boolean ldapReferral;
    private String ldapExternalId;
    private LdapConnectionPoolDirectoryAttributes ldapCpAttributes = new LdapConnectionPoolDirectoryAttributes();

    public String getLdapUrl() {
        return this.ldapUrl;
    }

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
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

    public String getDelegatedToClass() {
        return this.delegatedToClass;
    }

    public void setDelegatedToClass(String delegatedToClass) {
        this.delegatedToClass = delegatedToClass;
    }

    public void setLdapAutoAddGroups(String groups) {
        this.ldapAutoAddGroups = groups;
    }

    public String getLdapAutoAddGroups() {
        return this.ldapAutoAddGroups;
    }

    public boolean getLdapSecure() {
        return this.ldapSecure;
    }

    public void setLdapSecure(boolean value) {
        this.ldapSecure = value;
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

    public void setLdapPagedresults(boolean ldapPagedresults) {
        this.ldapPagedresults = ldapPagedresults;
    }

    public boolean isLdapPagedresults() {
        return this.ldapPagedresults;
    }

    public void setLdapPagedresultsSize(String ldapPagedresultsSize) {
        this.ldapPagedresultsSize = ldapPagedresultsSize;
    }

    public String getLdapPagedresultsSize() {
        return this.ldapPagedresultsSize;
    }

    public void setLdapReferral(boolean ldapReferral) {
        this.ldapReferral = ldapReferral;
    }

    public boolean isLdapReferral() {
        return this.ldapReferral;
    }

    public boolean isLdapNestedgroupsDisabled() {
        return this.ldapNestedgroupsDisabled;
    }

    public void setLdapNestedgroupsDisabled(boolean ldapNestedgroupsDisabled) {
        this.ldapNestedgroupsDisabled = ldapNestedgroupsDisabled;
    }

    public String getLdapExternalId() {
        return this.ldapExternalId;
    }

    public void setLdapExternalId(String ldapExternalId) {
        this.ldapExternalId = ldapExternalId;
    }

    public LdapConnectionPoolDirectoryAttributes getLdapCpAttributes() {
        return this.ldapCpAttributes;
    }

    public void setLdapCpAttributes(LdapConnectionPoolDirectoryAttributes ldapCpAttributes) {
        this.ldapCpAttributes = ldapCpAttributes;
    }

    public Function<SpringLdapPoolConfigService, Map<String, String>> toAttributesMap() {
        return ldapPoolConfigService -> {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ldap.url", this.ldapUrl);
            map.put("ldap.basedn", this.ldapBasedn);
            map.put("ldap.userdn", this.ldapUserdn);
            map.put("ldap.password", this.ldapPassword);
            map.put("ldap.user.dn", this.ldapUserDn);
            map.put("ldap.user.objectclass", this.ldapUserObjectclass);
            map.put("ldap.user.filter", this.ldapUserFilter);
            map.put("ldap.user.username", this.ldapUserUsername);
            map.put("ldap.user.username.rdn", this.ldapUserUsernameRdn);
            map.put("ldap.user.firstname", this.ldapUserFirstname);
            map.put("ldap.user.lastname", this.ldapUserLastname);
            map.put("ldap.user.displayname", this.ldapUserDisplayname);
            map.put("ldap.user.email", this.ldapUserEmail);
            map.put("crowd.delegated.directory.type", this.delegatedToClass);
            map.put("crowd.delegated.directory.auto.create.user", String.valueOf(this.createUserOnAuth));
            map.put("crowd.delegated.directory.auto.update.user", String.valueOf(this.updateUserOnAuth));
            map.put("autoAddGroups", this.ldapAutoAddGroups);
            map.put("ldap.secure", String.valueOf(this.ldapSecure));
            map.put("ldap.group.objectclass", this.ldapGroupObjectclass);
            map.put("ldap.group.filter", this.ldapGroupFilter);
            map.put("ldap.group.name", this.ldapGroupName);
            map.put("ldap.group.description", this.ldapGroupDescription);
            map.put("ldap.group.usernames", this.ldapGroupUsernames);
            map.put("ldap.user.group", this.ldapUserGroup);
            map.put("ldap.usermembership.use.for.groups", String.valueOf(this.ldapUsermembershipUseForGroups));
            map.put("ldap.usermembership.use", String.valueOf(this.ldapUsermembershipUse));
            map.put("crowd.delegated.directory.importGroups", String.valueOf(this.synchroniseGroupMemberships));
            map.put("ldap.group.dn", this.ldapGroupDn);
            map.put("ldap.pagedresults", String.valueOf(this.ldapPagedresults));
            map.put("ldap.pagedresults.size", this.ldapPagedresultsSize);
            map.put("ldap.referral", String.valueOf(this.ldapReferral));
            map.put("ldap.nestedgroups.disabled", String.valueOf(this.ldapNestedgroupsDisabled));
            map.put("ldap.external.id", this.ldapExternalId);
            map.putAll(this.ldapCpAttributes.toAttributesMap().apply((SpringLdapPoolConfigService)ldapPoolConfigService));
            return map;
        };
    }

    public static Function<SpringLdapPoolConfigService, LdapDelegatingDirectoryAttributes> fromAttributesMap(Map<String, String> map) {
        return ldapPoolConfigService -> {
            LdapDelegatingDirectoryAttributes attributes = new LdapDelegatingDirectoryAttributes();
            attributes.setLdapUrl((String)map.get("ldap.url"));
            attributes.setLdapBasedn((String)map.get("ldap.basedn"));
            attributes.setLdapUserdn((String)map.get("ldap.userdn"));
            attributes.setLdapPassword((String)map.get("ldap.password"));
            attributes.setLdapUserDn((String)map.get("ldap.user.dn"));
            attributes.setLdapUserObjectclass((String)map.get("ldap.user.objectclass"));
            attributes.setLdapUserFilter((String)map.get("ldap.user.filter"));
            attributes.setLdapUserUsername((String)map.get("ldap.user.username"));
            attributes.setLdapUserUsernameRdn((String)map.get("ldap.user.username.rdn"));
            attributes.setLdapUserFirstname((String)map.get("ldap.user.firstname"));
            attributes.setLdapUserLastname((String)map.get("ldap.user.lastname"));
            attributes.setLdapUserDisplayname((String)map.get("ldap.user.displayname"));
            attributes.setLdapUserEmail((String)map.get("ldap.user.email"));
            attributes.setCreateUserOnAuth(Boolean.parseBoolean((String)map.get("crowd.delegated.directory.auto.create.user")));
            attributes.setUpdateUserOnAuth(Boolean.parseBoolean((String)map.get("crowd.delegated.directory.auto.update.user")));
            attributes.setDelegatedToClass((String)map.get("crowd.delegated.directory.type"));
            attributes.setLdapAutoAddGroups((String)map.get("autoAddGroups"));
            attributes.setLdapSecure(Boolean.parseBoolean((String)map.get("ldap.secure")));
            attributes.setLdapGroupObjectclass((String)map.get("ldap.group.objectclass"));
            attributes.setLdapGroupFilter((String)map.get("ldap.group.filter"));
            attributes.setLdapGroupName((String)map.get("ldap.group.name"));
            attributes.setLdapGroupDescription((String)map.get("ldap.group.description"));
            attributes.setLdapGroupUsernames((String)map.get("ldap.group.usernames"));
            attributes.setLdapUserGroup((String)map.get("ldap.user.group"));
            attributes.setLdapUsermembershipUseForGroups(Boolean.parseBoolean((String)map.get("ldap.usermembership.use.for.groups")));
            attributes.setLdapUsermembershipUse(Boolean.parseBoolean((String)map.get("ldap.usermembership.use")));
            attributes.setSynchroniseGroupMemberships(Boolean.parseBoolean((String)map.get("crowd.delegated.directory.importGroups")));
            attributes.setLdapGroupDn((String)map.get("ldap.group.dn"));
            attributes.setLdapPagedresults(Boolean.parseBoolean((String)map.get("ldap.pagedresults")));
            attributes.setLdapPagedresultsSize((String)map.get("ldap.pagedresults.size"));
            attributes.setLdapReferral(Boolean.parseBoolean((String)map.get("ldap.referral")));
            attributes.setLdapNestedgroupsDisabled(Boolean.parseBoolean((String)map.get("ldap.nestedgroups.disabled")));
            attributes.setLdapExternalId((String)map.get("ldap.external.id"));
            attributes.setLdapCpAttributes(LdapConnectionPoolDirectoryAttributes.fromAttributesMap(map).apply((SpringLdapPoolConfigService)ldapPoolConfigService));
            return attributes;
        };
    }
}

