/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap.properties.factory;

import com.atlassian.user.impl.ldap.properties.DefaultLdapMembershipProperties;
import com.atlassian.user.impl.ldap.properties.LdapMembershipProperties;
import java.util.Properties;

public class LdapMembershipPropertiesFactory {
    public LdapMembershipProperties createInstance(Properties properties) {
        DefaultLdapMembershipProperties result = new DefaultLdapMembershipProperties();
        result.setMembershipAttribute(properties.getProperty("membershipAttribute"));
        String membershipAttributeOnGroup = properties.getProperty("staticGroups");
        result.setMembershipAttributeOnGroup(Boolean.valueOf(membershipAttributeOnGroup));
        String membershipAttributeUnqualified = properties.getProperty("useUnqualifiedUsernameForMembershipComparison");
        result.setMembershipAttributeUnqualified(Boolean.valueOf(membershipAttributeUnqualified));
        return result;
    }
}

