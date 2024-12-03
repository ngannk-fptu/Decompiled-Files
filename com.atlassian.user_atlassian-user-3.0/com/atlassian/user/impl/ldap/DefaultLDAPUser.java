/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap;

import com.atlassian.user.impl.DefaultUser;
import com.atlassian.user.impl.ldap.LDAPEntity;

public class DefaultLDAPUser
extends DefaultUser
implements LDAPEntity {
    private String distinguishedName;

    public DefaultLDAPUser(String name, String distinguishedName) {
        super(name);
        this.distinguishedName = distinguishedName;
    }

    public String getDistinguishedName() {
        return this.distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }
}

