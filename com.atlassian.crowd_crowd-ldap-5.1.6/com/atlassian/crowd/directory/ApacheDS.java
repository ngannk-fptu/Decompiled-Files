/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.password.factory.PasswordEncoderFactory
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.LdapContextSourceProvider;
import com.atlassian.crowd.directory.RFC4519Directory;
import com.atlassian.crowd.directory.ldap.credential.EncryptingCredentialEncoder;
import com.atlassian.crowd.directory.ldap.credential.LDAPCredentialEncoder;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.password.factory.PasswordEncoderFactory;
import com.atlassian.crowd.search.ldap.LDAPQueryTranslater;
import com.atlassian.crowd.util.InstanceFactory;
import com.atlassian.event.api.EventPublisher;
import javax.naming.directory.Attributes;

public class ApacheDS
extends RFC4519Directory {
    private final PasswordEncoderFactory passwordEncoderFactory;

    public ApacheDS(LDAPQueryTranslater ldapQueryTranslater, EventPublisher eventPublisher, InstanceFactory instanceFactory, PasswordEncoderFactory passwordEncoderFactory, LdapContextSourceProvider ldapContextSourceProvider) {
        super(ldapQueryTranslater, eventPublisher, instanceFactory, ldapContextSourceProvider);
        this.passwordEncoderFactory = passwordEncoderFactory;
    }

    public static String getStaticDirectoryType() {
        return "Apache Directory Server 1.0.x";
    }

    public String getDescriptiveName() {
        return ApacheDS.getStaticDirectoryType();
    }

    @Override
    protected LDAPCredentialEncoder getCredentialEncoder() {
        return new EncryptingCredentialEncoder(this.passwordEncoderFactory, this.ldapPropertiesMapper.getUserEncryptionMethod());
    }

    @Override
    protected void getNewUserDirectorySpecificAttributes(User user, Attributes attributes) {
        this.addDefaultSnToUserAttributes(attributes, "");
    }
}

