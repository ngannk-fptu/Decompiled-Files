/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.password.factory.PasswordEncoderFactory
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.ldap.core.ContextSource
 *  org.springframework.ldap.core.LdapTemplate
 *  org.springframework.ldap.core.support.LdapContextSource
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.GenericLDAP;
import com.atlassian.crowd.directory.LdapContextSourceProvider;
import com.atlassian.crowd.password.factory.PasswordEncoderFactory;
import com.atlassian.crowd.search.ldap.LDAPQueryTranslater;
import com.atlassian.crowd.util.InstanceFactory;
import com.atlassian.event.api.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

public class OpenDS
extends GenericLDAP {
    private static final Logger logger = LoggerFactory.getLogger(OpenDS.class);

    public OpenDS(LDAPQueryTranslater ldapQueryTranslater, EventPublisher eventPublisher, InstanceFactory instanceFactory, PasswordEncoderFactory passwordEncoderFactory, LdapContextSourceProvider ldapContextSourceProvider) {
        super(ldapQueryTranslater, eventPublisher, instanceFactory, passwordEncoderFactory, ldapContextSourceProvider);
    }

    public static String getStaticDirectoryType() {
        return "OpenDS";
    }

    @Override
    public String getDescriptiveName() {
        return OpenDS.getStaticDirectoryType();
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
            logger.error("afterPropertiesSet failed", (Throwable)e);
        }
        return new LdapTemplate((ContextSource)contextSource);
    }
}

