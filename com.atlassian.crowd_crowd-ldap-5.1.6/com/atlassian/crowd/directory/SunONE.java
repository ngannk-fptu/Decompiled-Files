/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.atlassian.crowd.util.PasswordHelper
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.ldap.core.ContextSource
 *  org.springframework.ldap.core.LdapTemplate
 *  org.springframework.ldap.core.support.LdapContextSource
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

public class SunONE
extends RFC4519Directory {
    private static final Logger logger = LoggerFactory.getLogger(SunONE.class);
    private final LDAPCredentialEncoder credentialEncoder;

    public SunONE(LDAPQueryTranslater ldapQueryTranslater, EventPublisher eventPublisher, InstanceFactory instanceFactory, PasswordHelper passwordHelper, LdapContextSourceProvider ldapContextSourceProvider) {
        super(ldapQueryTranslater, eventPublisher, instanceFactory, ldapContextSourceProvider);
        this.credentialEncoder = new EnforceUnencryptedCredentialEncoder(passwordHelper);
    }

    public static String getStaticDirectoryType() {
        return "Sun Directory Server Enterprise Edition";
    }

    public String getDescriptiveName() {
        return SunONE.getStaticDirectoryType();
    }

    @Override
    protected LDAPCredentialEncoder getCredentialEncoder() {
        return this.credentialEncoder;
    }

    @Override
    protected void getNewUserDirectorySpecificAttributes(User user, Attributes attributes) {
        this.addDefaultSnToUserAttributes(attributes, "");
    }

    protected LdapTemplate createChangeListenerTemplate() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(this.ldapPropertiesMapper.getConnectionURL());
        contextSource.setUserDn(this.ldapPropertiesMapper.getUsername());
        contextSource.setPassword(this.ldapPropertiesMapper.getPassword());
        contextSource.setBaseEnvironmentProperties(this.getBaseEnvironmentProperties());
        contextSource.setPooled(true);
        contextSource.setDirObjectFactory(null);
        try {
            contextSource.afterPropertiesSet();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), (Throwable)e);
        }
        return new LdapTemplate((ContextSource)contextSource);
    }
}

