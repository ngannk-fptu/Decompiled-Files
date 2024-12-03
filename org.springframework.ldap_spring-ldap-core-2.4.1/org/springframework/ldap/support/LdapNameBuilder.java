/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.support;

import javax.naming.Name;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import org.springframework.ldap.InvalidNameException;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.util.Assert;

public final class LdapNameBuilder {
    private final LdapName ldapName;

    private LdapNameBuilder(LdapName ldapName) {
        this.ldapName = ldapName;
    }

    public static LdapNameBuilder newInstance() {
        return new LdapNameBuilder(LdapUtils.emptyLdapName());
    }

    public static LdapNameBuilder newInstance(Name name) {
        return new LdapNameBuilder(LdapUtils.newLdapName(name));
    }

    public static LdapNameBuilder newInstance(String name) {
        return new LdapNameBuilder(LdapUtils.newLdapName(name));
    }

    public LdapNameBuilder add(String key, Object value) {
        Assert.hasText((String)key, (String)"key must not be blank");
        Assert.notNull((Object)value, (String)"value must not be null");
        try {
            this.ldapName.add(new Rdn(key, value));
            return this;
        }
        catch (javax.naming.InvalidNameException e) {
            throw new InvalidNameException(e);
        }
    }

    public LdapNameBuilder add(Name name) {
        Assert.notNull((Object)name, (String)"name must not be null");
        try {
            this.ldapName.addAll(this.ldapName.size(), name);
            return this;
        }
        catch (javax.naming.InvalidNameException e) {
            throw new InvalidNameException(e);
        }
    }

    public LdapNameBuilder add(String name) {
        Assert.notNull((Object)name, (String)"name must not be null");
        return this.add(LdapUtils.newLdapName(name));
    }

    public LdapName build() {
        return LdapUtils.newLdapName(this.ldapName);
    }
}

