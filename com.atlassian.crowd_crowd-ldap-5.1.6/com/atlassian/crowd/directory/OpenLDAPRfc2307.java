/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.password.factory.PasswordEncoderFactory
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.LdapContextSourceProvider;
import com.atlassian.crowd.directory.Rfc2307;
import com.atlassian.crowd.password.factory.PasswordEncoderFactory;
import com.atlassian.crowd.search.ldap.LDAPQueryTranslater;
import com.atlassian.crowd.util.InstanceFactory;
import com.atlassian.event.api.EventPublisher;

public class OpenLDAPRfc2307
extends Rfc2307 {
    public OpenLDAPRfc2307(LDAPQueryTranslater ldapQueryTranslater, EventPublisher eventPublisher, InstanceFactory instanceFactory, PasswordEncoderFactory passwordEncoderFactory, LdapContextSourceProvider ldapContextSourceProvider) {
        super(ldapQueryTranslater, eventPublisher, instanceFactory, passwordEncoderFactory, ldapContextSourceProvider);
    }

    public static String getStaticDirectoryType() {
        return "OpenLDAP (Read-Only Posix Schema)";
    }

    @Override
    public String getDescriptiveName() {
        return OpenLDAPRfc2307.getStaticDirectoryType();
    }
}

