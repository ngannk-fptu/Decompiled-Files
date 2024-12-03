/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.core;

import javax.naming.ldap.LdapName;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.util.Assert;

public class LdapEntryIdentification {
    private final LdapName relativeDn;
    private final LdapName absoluteDn;

    public LdapEntryIdentification(DistinguishedName absoluteDn, DistinguishedName relativeDn) {
        Assert.notNull((Object)absoluteDn, (String)"Absolute DN must not be null");
        Assert.notNull((Object)relativeDn, (String)"Relative DN must not be null");
        this.absoluteDn = LdapUtils.newLdapName(absoluteDn);
        this.relativeDn = LdapUtils.newLdapName(relativeDn);
    }

    public LdapEntryIdentification(LdapName absoluteDn, LdapName relativeDn) {
        Assert.notNull((Object)absoluteDn, (String)"Absolute DN must not be null");
        Assert.notNull((Object)relativeDn, (String)"Relative DN must not be null");
        this.absoluteDn = LdapUtils.newLdapName(absoluteDn);
        this.relativeDn = LdapUtils.newLdapName(relativeDn);
    }

    public LdapName getAbsoluteName() {
        return LdapUtils.newLdapName(this.absoluteDn);
    }

    public LdapName getRelativeName() {
        return LdapUtils.newLdapName(this.relativeDn);
    }

    public DistinguishedName getRelativeDn() {
        return new DistinguishedName(this.relativeDn);
    }

    public DistinguishedName getAbsoluteDn() {
        return new DistinguishedName(this.absoluteDn);
    }

    public boolean equals(Object obj) {
        if (obj != null && obj.getClass().equals(this.getClass())) {
            LdapEntryIdentification that = (LdapEntryIdentification)obj;
            return this.absoluteDn.equals(that.absoluteDn) && this.relativeDn.equals(that.relativeDn);
        }
        return false;
    }

    public int hashCode() {
        return this.absoluteDn.hashCode() ^ this.relativeDn.hashCode();
    }
}

