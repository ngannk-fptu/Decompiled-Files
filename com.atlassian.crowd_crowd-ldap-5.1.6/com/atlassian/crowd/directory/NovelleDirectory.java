/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.atlassian.crowd.util.PasswordHelper
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.LdapContextSourceProvider;
import com.atlassian.crowd.directory.RFC4519Directory;
import com.atlassian.crowd.directory.ldap.credential.EnforceUnencryptedCredentialEncoder;
import com.atlassian.crowd.directory.ldap.credential.LDAPCredentialEncoder;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.search.ldap.LDAPQueryTranslater;
import com.atlassian.crowd.util.InstanceFactory;
import com.atlassian.crowd.util.PasswordHelper;
import com.atlassian.event.api.EventPublisher;
import javax.naming.directory.Attributes;

public class NovelleDirectory
extends RFC4519Directory {
    private final LDAPCredentialEncoder credentialEncoder;

    public NovelleDirectory(LDAPQueryTranslater ldapQueryTranslater, EventPublisher eventPublisher, InstanceFactory instanceFactory, PasswordHelper passwordHelper, LdapContextSourceProvider ldapContextSourceProvider) {
        super(ldapQueryTranslater, eventPublisher, instanceFactory, ldapContextSourceProvider);
        this.credentialEncoder = new EnforceUnencryptedCredentialEncoder(passwordHelper);
    }

    public static String getStaticDirectoryType() {
        return "Novell eDirectory Server";
    }

    public String getDescriptiveName() {
        return NovelleDirectory.getStaticDirectoryType();
    }

    @Override
    protected LDAPCredentialEncoder getCredentialEncoder() {
        return this.credentialEncoder;
    }

    @Override
    protected void getNewUserDirectorySpecificAttributes(User user, Attributes attributes) {
        this.addDefaultSnToUserAttributes(attributes, user.getName());
    }
}

