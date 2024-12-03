/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap.properties;

public interface LdapSearchProperties {
    public String getBaseUserNamespace();

    public String getUserFilter();

    public boolean isUserSearchScopeAllDepths();

    public String getUsernameAttribute();

    public String getFirstnameAttribute();

    public String getSurnameAttribute();

    public String getEmailAttribute();

    public String getBaseGroupNamespace();

    public String getGroupFilter();

    public boolean isGroupSearchScopeAllDepths();

    public String getGroupnameAttribute();

    public int getTimeLimitMillis();
}

