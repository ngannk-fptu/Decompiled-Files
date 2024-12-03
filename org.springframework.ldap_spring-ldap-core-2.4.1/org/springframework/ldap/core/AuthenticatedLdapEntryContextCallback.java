/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import javax.naming.directory.DirContext;
import org.springframework.ldap.core.LdapEntryIdentification;

public interface AuthenticatedLdapEntryContextCallback {
    public void executeWithContext(DirContext var1, LdapEntryIdentification var2);
}

