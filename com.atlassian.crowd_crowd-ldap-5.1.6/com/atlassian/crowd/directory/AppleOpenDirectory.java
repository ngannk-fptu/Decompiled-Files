/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.password.factory.PasswordEncoderFactory
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.LdapContextSourceProvider;
import com.atlassian.crowd.directory.Rfc2307;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.password.factory.PasswordEncoderFactory;
import com.atlassian.crowd.search.ldap.LDAPQueryTranslater;
import com.atlassian.crowd.util.InstanceFactory;
import com.atlassian.event.api.EventPublisher;
import java.util.Map;

public class AppleOpenDirectory
extends Rfc2307 {
    public AppleOpenDirectory(LDAPQueryTranslater ldapQueryTranslater, EventPublisher eventPublisher, InstanceFactory instanceFactory, PasswordEncoderFactory passwordEncoderFactory, LdapContextSourceProvider ldapContextSourceProvider) {
        super(ldapQueryTranslater, eventPublisher, instanceFactory, passwordEncoderFactory, ldapContextSourceProvider);
    }

    public static String getStaticDirectoryType() {
        return "Apple Open Directory (Read-Only)";
    }

    @Override
    public String getDescriptiveName() {
        return AppleOpenDirectory.getStaticDirectoryType();
    }

    @Override
    protected Map<String, Object> getBaseEnvironmentProperties() {
        Map<String, Object> environment = super.getBaseEnvironmentProperties();
        environment.put("java.naming.security.authentication", "CRAM-MD5");
        return environment;
    }

    @Override
    public void updateUserCredential(String name, PasswordCredential credential) throws UserNotFoundException, InvalidCredentialException {
        throw new UnsupportedOperationException("Password changes not supported in Open Directory (" + name + ")");
    }
}

