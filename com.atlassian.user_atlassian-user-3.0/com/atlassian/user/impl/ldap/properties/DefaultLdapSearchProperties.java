/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap.properties;

import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;

public class DefaultLdapSearchProperties
implements LdapSearchProperties {
    private static final int DEFAULT_TIME_LIMIT_MILLIS = 0;
    private String baseUserNamespace;
    private String userFilter;
    private boolean userSearchScopeAllDepths;
    private String usernameAttribute;
    private String firstnameAttribute;
    private String surnameAttribute;
    private String emailAttribute;
    private String baseGroupNamespace;
    private String groupFilter;
    private boolean groupSearchScopeAllDepths;
    private String groupnameAttribute;
    private int timeLimitMillis = 0;

    public String getBaseUserNamespace() {
        return this.baseUserNamespace;
    }

    public String getUserFilter() {
        return this.userFilter;
    }

    public String getBaseGroupNamespace() {
        return this.baseGroupNamespace;
    }

    public String getGroupFilter() {
        return this.groupFilter;
    }

    public String getUsernameAttribute() {
        return this.usernameAttribute;
    }

    public String getGroupnameAttribute() {
        return this.groupnameAttribute;
    }

    public String getFirstnameAttribute() {
        return this.firstnameAttribute;
    }

    public String getSurnameAttribute() {
        return this.surnameAttribute;
    }

    public String getEmailAttribute() {
        return this.emailAttribute;
    }

    public boolean isUserSearchScopeAllDepths() {
        return this.userSearchScopeAllDepths;
    }

    public boolean isGroupSearchScopeAllDepths() {
        return this.groupSearchScopeAllDepths;
    }

    public int getTimeLimitMillis() {
        return this.timeLimitMillis;
    }

    public void setBaseUserNamespace(String baseUserNamespace) {
        this.baseUserNamespace = baseUserNamespace;
    }

    public void setBaseGroupNamespace(String baseGroupNamespace) {
        this.baseGroupNamespace = baseGroupNamespace;
    }

    public void setUsernameAttribute(String usernameAttribute) {
        this.usernameAttribute = usernameAttribute;
    }

    public void setGroupnameAttribute(String groupnameAttribute) {
        this.groupnameAttribute = groupnameAttribute;
    }

    public void setFirstnameAttribute(String firstnameAttribute) {
        this.firstnameAttribute = firstnameAttribute;
    }

    public void setSurnameAttribute(String surnameAttribute) {
        this.surnameAttribute = surnameAttribute;
    }

    public void setEmailAttribute(String emailAttribute) {
        this.emailAttribute = emailAttribute;
    }

    public void setUserSearchScopeAllDepths(boolean userSearchScopeAllDepths) {
        this.userSearchScopeAllDepths = userSearchScopeAllDepths;
    }

    public void setGroupSearchScopeAllDepths(boolean groupSearchScopeAllDepths) {
        this.groupSearchScopeAllDepths = groupSearchScopeAllDepths;
    }

    public void setTimeLimitMillis(int timeLimitMillis) {
        this.timeLimitMillis = timeLimitMillis;
    }

    public void setUserFilter(String userFilter) {
        this.userFilter = userFilter;
    }

    public void setGroupFilter(String groupFilter) {
        this.groupFilter = groupFilter;
    }
}

