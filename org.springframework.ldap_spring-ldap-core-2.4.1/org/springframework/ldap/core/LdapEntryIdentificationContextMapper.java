/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapEntryIdentification;
import org.springframework.ldap.support.LdapUtils;

public class LdapEntryIdentificationContextMapper
implements ContextMapper<LdapEntryIdentification> {
    @Override
    public LdapEntryIdentification mapFromContext(Object ctx) {
        DirContextOperations adapter = (DirContextOperations)ctx;
        return new LdapEntryIdentification(LdapUtils.newLdapName(adapter.getNameInNamespace()), LdapUtils.newLdapName(adapter.getDn()));
    }
}

