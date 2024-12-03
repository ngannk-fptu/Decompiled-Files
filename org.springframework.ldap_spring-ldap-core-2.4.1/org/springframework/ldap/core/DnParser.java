/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapRdn;
import org.springframework.ldap.core.ParseException;

public interface DnParser {
    public DistinguishedName dn() throws ParseException;

    public LdapRdn rdn() throws ParseException;
}

