/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import javax.naming.ldap.LdapName;
import org.springframework.ldap.core.DistinguishedName;

public interface BaseLdapPathSource {
    public DistinguishedName getBaseLdapPath();

    public LdapName getBaseLdapName();

    public String getBaseLdapPathAsString();
}

