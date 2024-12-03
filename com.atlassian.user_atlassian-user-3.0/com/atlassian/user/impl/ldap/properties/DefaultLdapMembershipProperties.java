/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap.properties;

import com.atlassian.user.impl.ldap.properties.LdapMembershipProperties;

public class DefaultLdapMembershipProperties
implements LdapMembershipProperties {
    private boolean isMembershipAttributeOnGroup = true;
    private String membershipAttribute;
    private boolean isMembershipAttributeUnqualified = false;

    public boolean isMembershipAttributeOnGroup() {
        return this.isMembershipAttributeOnGroup;
    }

    public String getMembershipAttribute() {
        return this.membershipAttribute;
    }

    public boolean isMembershipAttributeUnqualified() {
        return this.isMembershipAttributeUnqualified;
    }

    public void setMembershipAttributeOnGroup(boolean membershipAttributeOnGroup) {
        this.isMembershipAttributeOnGroup = membershipAttributeOnGroup;
    }

    public void setMembershipAttribute(String membershipAttribute) {
        this.membershipAttribute = membershipAttribute;
    }

    public void setMembershipAttributeUnqualified(boolean membershipAttributeUnqualified) {
        this.isMembershipAttributeUnqualified = membershipAttributeUnqualified;
    }
}

