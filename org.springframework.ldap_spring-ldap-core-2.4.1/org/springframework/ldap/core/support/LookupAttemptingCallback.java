/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import org.springframework.ldap.core.AuthenticatedLdapEntryContextCallback;
import org.springframework.ldap.core.AuthenticatedLdapEntryContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapEntryIdentification;
import org.springframework.ldap.support.LdapUtils;

public class LookupAttemptingCallback
implements AuthenticatedLdapEntryContextCallback,
AuthenticatedLdapEntryContextMapper<DirContextOperations> {
    @Override
    public void executeWithContext(DirContext ctx, LdapEntryIdentification ldapEntryIdentification) {
        this.mapWithContext(ctx, ldapEntryIdentification);
    }

    @Override
    public DirContextOperations mapWithContext(DirContext ctx, LdapEntryIdentification ldapEntryIdentification) {
        try {
            return (DirContextOperations)ctx.lookup(ldapEntryIdentification.getRelativeName());
        }
        catch (NamingException e) {
            throw LdapUtils.convertLdapException(e);
        }
    }
}

