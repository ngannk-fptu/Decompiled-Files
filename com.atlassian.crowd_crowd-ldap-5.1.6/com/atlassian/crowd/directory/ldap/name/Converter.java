/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.ldap.name;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

public interface Converter {
    public LdapName getName(String var1) throws InvalidNameException;

    public LdapName getName(String var1, String var2, LdapName var3) throws InvalidNameException;
}

