/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.password.factory.PasswordEncoderFactory
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.atlassian.event.api.EventPublisher
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.ldap.core.ContextSource
 *  org.springframework.ldap.core.LdapTemplate
 *  org.springframework.ldap.core.support.LdapContextSource
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.ApacheDS;
import com.atlassian.crowd.directory.LdapContextSourceProvider;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.password.factory.PasswordEncoderFactory;
import com.atlassian.crowd.search.ldap.LDAPQueryTranslater;
import com.atlassian.crowd.util.InstanceFactory;
import com.atlassian.event.api.EventPublisher;
import javax.naming.directory.Attributes;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

public class ApacheDS15
extends ApacheDS {
    private static final Logger logger = LoggerFactory.getLogger(ApacheDS15.class);

    public ApacheDS15(LDAPQueryTranslater ldapQueryTranslater, EventPublisher eventPublisher, InstanceFactory instanceFactory, PasswordEncoderFactory passwordEncoderFactory, LdapContextSourceProvider ldapContextSourceProvider) {
        super(ldapQueryTranslater, eventPublisher, instanceFactory, passwordEncoderFactory, ldapContextSourceProvider);
    }

    public static String getStaticDirectoryType() {
        return "Apache Directory Server 1.5.x";
    }

    @Override
    public String getDescriptiveName() {
        return ApacheDS15.getStaticDirectoryType();
    }

    @Override
    protected String getInitialGroupMemberDN() {
        return this.ldapPropertiesMapper.getUsername();
    }

    @Override
    protected void getNewUserDirectorySpecificAttributes(User user, Attributes attributes) {
        if (StringUtils.isBlank((CharSequence)user.getLastName())) {
            this.addDefaultSnToUserAttributes(attributes, " ");
        }
        if (StringUtils.isBlank((CharSequence)user.getEmailAddress())) {
            this.addDefaultValueToUserAttributesForAttribute(this.ldapPropertiesMapper.getUserEmailAttribute(), attributes, " ");
        }
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

