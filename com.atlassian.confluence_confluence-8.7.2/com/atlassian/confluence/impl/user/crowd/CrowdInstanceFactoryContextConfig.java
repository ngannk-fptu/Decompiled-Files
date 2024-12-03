/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.tombstone.TombstoneDao
 *  com.atlassian.crowd.directory.ApacheDS
 *  com.atlassian.crowd.directory.ApacheDS15
 *  com.atlassian.crowd.directory.AppleOpenDirectory
 *  com.atlassian.crowd.directory.CachingDirectory
 *  com.atlassian.crowd.directory.FedoraDS
 *  com.atlassian.crowd.directory.GenericLDAP
 *  com.atlassian.crowd.directory.InternalDirectory
 *  com.atlassian.crowd.directory.InternalDirectoryForDelegation
 *  com.atlassian.crowd.directory.InternalDirectoryUtils
 *  com.atlassian.crowd.directory.LdapContextSourceProvider
 *  com.atlassian.crowd.directory.MicrosoftActiveDirectory
 *  com.atlassian.crowd.directory.NovelleDirectory
 *  com.atlassian.crowd.directory.OpenDS
 *  com.atlassian.crowd.directory.OpenLDAP
 *  com.atlassian.crowd.directory.OpenLDAPRfc2307
 *  com.atlassian.crowd.directory.PasswordConstraintsLoader
 *  com.atlassian.crowd.directory.RemoteCrowdDirectory
 *  com.atlassian.crowd.directory.Rfc2307
 *  com.atlassian.crowd.directory.SunONE
 *  com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper
 *  com.atlassian.crowd.directory.ldap.LDAPPropertiesMapperImpl
 *  com.atlassian.crowd.directory.ldap.util.LDAPPropertiesHelper
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.embedded.spi.GroupDao
 *  com.atlassian.crowd.embedded.spi.MembershipDao
 *  com.atlassian.crowd.embedded.spi.UserDao
 *  com.atlassian.crowd.password.factory.PasswordEncoderFactory
 *  com.atlassian.crowd.search.ldap.ActiveDirectoryQueryTranslaterImpl
 *  com.atlassian.crowd.search.ldap.LDAPQueryTranslater
 *  com.atlassian.crowd.service.factory.CrowdClientFactory
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.atlassian.crowd.util.PasswordHelper
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Resource
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Scope
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.confluence.impl.user.crowd.CrowdInstanceFactory;
import com.atlassian.crowd.dao.tombstone.TombstoneDao;
import com.atlassian.crowd.directory.ApacheDS;
import com.atlassian.crowd.directory.ApacheDS15;
import com.atlassian.crowd.directory.AppleOpenDirectory;
import com.atlassian.crowd.directory.CachingDirectory;
import com.atlassian.crowd.directory.FedoraDS;
import com.atlassian.crowd.directory.GenericLDAP;
import com.atlassian.crowd.directory.InternalDirectory;
import com.atlassian.crowd.directory.InternalDirectoryForDelegation;
import com.atlassian.crowd.directory.InternalDirectoryUtils;
import com.atlassian.crowd.directory.LdapContextSourceProvider;
import com.atlassian.crowd.directory.MicrosoftActiveDirectory;
import com.atlassian.crowd.directory.NovelleDirectory;
import com.atlassian.crowd.directory.OpenDS;
import com.atlassian.crowd.directory.OpenLDAP;
import com.atlassian.crowd.directory.OpenLDAPRfc2307;
import com.atlassian.crowd.directory.PasswordConstraintsLoader;
import com.atlassian.crowd.directory.RemoteCrowdDirectory;
import com.atlassian.crowd.directory.Rfc2307;
import com.atlassian.crowd.directory.SunONE;
import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapperImpl;
import com.atlassian.crowd.directory.ldap.util.LDAPPropertiesHelper;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.embedded.spi.GroupDao;
import com.atlassian.crowd.embedded.spi.MembershipDao;
import com.atlassian.crowd.embedded.spi.UserDao;
import com.atlassian.crowd.password.factory.PasswordEncoderFactory;
import com.atlassian.crowd.search.ldap.ActiveDirectoryQueryTranslaterImpl;
import com.atlassian.crowd.search.ldap.LDAPQueryTranslater;
import com.atlassian.crowd.service.factory.CrowdClientFactory;
import com.atlassian.crowd.util.InstanceFactory;
import com.atlassian.crowd.util.PasswordHelper;
import com.atlassian.event.api.EventPublisher;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Resource;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
class CrowdInstanceFactoryContextConfig {
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private PasswordEncoderFactory crowdPasswordEncoderFactory;
    @Resource
    private DirectoryDao embeddedCrowdDirectoryDao;
    @Resource
    private UserDao embeddedCrowdUserDao;
    @Resource
    private GroupDao embeddedCrowdGroupDao;
    @Resource
    private MembershipDao embeddedCrowdMembershipDao;
    @Resource
    private TombstoneDao embeddedCrowdTombstoneDao;
    @Resource
    private LDAPQueryTranslater crowdLdapQueryTranslator;
    @Resource
    private LdapContextSourceProvider ldapContextSourceProvider;
    @Resource
    private InternalDirectoryUtils crowdInternalDirectoryUtils;
    @Resource
    private ActiveDirectoryQueryTranslaterImpl activeDirectoryQueryTranslater;
    @Resource
    private PasswordHelper crowdPasswordHelper;
    @Resource
    private PasswordConstraintsLoader passwordConstraintsLoader;
    @Resource
    private CrowdClientFactory restCrowdClientFactory;
    @Resource
    private LDAPPropertiesHelper crowdLdapPropertiesHelper;
    private final Map<Class<?>, Supplier<?>> beanMap = ImmutableMap.builder().put(ApacheDS15.class, this::apacheDS15).put(ApacheDS.class, this::apacheDS).put(AppleOpenDirectory.class, this::appleOpenDirectory).put(FedoraDS.class, this::fedoraDS).put(GenericLDAP.class, this::genericLDAP).put(InternalDirectory.class, this::internalDirectory).put(InternalDirectoryForDelegation.class, this::internalDirectoryForDelegation).put(MicrosoftActiveDirectory.class, this::microsoftActiveDirectory).put(NovelleDirectory.class, this::novelleDirectory).put(OpenDS.class, this::openDS).put(OpenLDAP.class, this::openLDAP).put(OpenLDAPRfc2307.class, this::openLDAPRfc2307).put(Rfc2307.class, this::rfc2307).put(SunONE.class, this::sunONE).put(RemoteCrowdDirectory.class, this::remoteCrowdDirectory).put(CachingDirectory.class, this::crowdCachingDirectory).put(LDAPPropertiesMapperImpl.class, this::crowdDirectoryLdapPropertiesMapper).build();

    CrowdInstanceFactoryContextConfig() {
    }

    @Bean
    CrowdInstanceFactory instanceFactory() {
        return this::resolveInstance;
    }

    private <T> T resolveInstance(Class<T> clazz) {
        return (T)Optional.ofNullable(this.beanMap.get(clazz)).map(Supplier::get).map(clazz::cast).orElseThrow(() -> new NoSuchBeanDefinitionException(clazz));
    }

    @Bean
    @Scope(scopeName="prototype")
    ApacheDS15 apacheDS15() {
        return new ApacheDS15(this.crowdLdapQueryTranslator, this.eventPublisher, (InstanceFactory)this.instanceFactory(), this.crowdPasswordEncoderFactory, this.ldapContextSourceProvider);
    }

    @Bean
    @Scope(scopeName="prototype")
    ApacheDS apacheDS() {
        return new ApacheDS(this.crowdLdapQueryTranslator, this.eventPublisher, (InstanceFactory)this.instanceFactory(), this.crowdPasswordEncoderFactory, this.ldapContextSourceProvider);
    }

    @Bean
    @Scope(scopeName="prototype")
    AppleOpenDirectory appleOpenDirectory() {
        return new AppleOpenDirectory(this.crowdLdapQueryTranslator, this.eventPublisher, (InstanceFactory)this.instanceFactory(), this.crowdPasswordEncoderFactory, this.ldapContextSourceProvider);
    }

    @Bean
    @Scope(scopeName="prototype")
    FedoraDS fedoraDS() {
        return new FedoraDS(this.crowdLdapQueryTranslator, this.eventPublisher, (InstanceFactory)this.instanceFactory(), this.crowdPasswordEncoderFactory, this.ldapContextSourceProvider);
    }

    @Bean
    @Scope(scopeName="prototype")
    GenericLDAP genericLDAP() {
        return new GenericLDAP(this.crowdLdapQueryTranslator, this.eventPublisher, (InstanceFactory)this.instanceFactory(), this.crowdPasswordEncoderFactory, this.ldapContextSourceProvider);
    }

    @Bean
    @Scope(scopeName="prototype")
    InternalDirectory internalDirectory() {
        return new InternalDirectory(this.crowdInternalDirectoryUtils, this.crowdPasswordEncoderFactory, this.embeddedCrowdDirectoryDao, this.embeddedCrowdUserDao, this.embeddedCrowdGroupDao, this.embeddedCrowdMembershipDao, this.embeddedCrowdTombstoneDao, this.passwordConstraintsLoader);
    }

    @Bean
    @Scope(scopeName="prototype")
    InternalDirectoryForDelegation internalDirectoryForDelegation() {
        return new InternalDirectoryForDelegation(this.crowdInternalDirectoryUtils, this.crowdPasswordEncoderFactory, this.embeddedCrowdDirectoryDao, this.embeddedCrowdUserDao, this.embeddedCrowdGroupDao, this.embeddedCrowdMembershipDao, this.embeddedCrowdTombstoneDao, this.passwordConstraintsLoader);
    }

    @Bean
    @Scope(scopeName="prototype")
    MicrosoftActiveDirectory microsoftActiveDirectory() {
        return new MicrosoftActiveDirectory(this.activeDirectoryQueryTranslater, this.eventPublisher, (InstanceFactory)this.instanceFactory(), this.crowdPasswordHelper, this.ldapContextSourceProvider);
    }

    @Bean
    @Scope(scopeName="prototype")
    NovelleDirectory novelleDirectory() {
        return new NovelleDirectory(this.crowdLdapQueryTranslator, this.eventPublisher, (InstanceFactory)this.instanceFactory(), this.crowdPasswordHelper, this.ldapContextSourceProvider);
    }

    @Bean
    @Scope(scopeName="prototype")
    OpenDS openDS() {
        return new OpenDS(this.crowdLdapQueryTranslator, this.eventPublisher, (InstanceFactory)this.instanceFactory(), this.crowdPasswordEncoderFactory, this.ldapContextSourceProvider);
    }

    @Bean
    @Scope(scopeName="prototype")
    OpenLDAP openLDAP() {
        return new OpenLDAP(this.crowdLdapQueryTranslator, this.eventPublisher, (InstanceFactory)this.instanceFactory(), this.crowdPasswordEncoderFactory, this.ldapContextSourceProvider);
    }

    @Bean
    @Scope(scopeName="prototype")
    OpenLDAPRfc2307 openLDAPRfc2307() {
        return new OpenLDAPRfc2307(this.crowdLdapQueryTranslator, this.eventPublisher, (InstanceFactory)this.instanceFactory(), this.crowdPasswordEncoderFactory, this.ldapContextSourceProvider);
    }

    @Bean
    @Scope(scopeName="prototype")
    Rfc2307 rfc2307() {
        return new Rfc2307(this.crowdLdapQueryTranslator, this.eventPublisher, (InstanceFactory)this.instanceFactory(), this.crowdPasswordEncoderFactory, this.ldapContextSourceProvider);
    }

    @Bean
    @Scope(scopeName="prototype")
    SunONE sunONE() {
        return new SunONE(this.crowdLdapQueryTranslator, this.eventPublisher, (InstanceFactory)this.instanceFactory(), this.crowdPasswordHelper, this.ldapContextSourceProvider);
    }

    @Bean
    @Scope(scopeName="prototype")
    RemoteCrowdDirectory remoteCrowdDirectory() {
        return new RemoteCrowdDirectory(this.restCrowdClientFactory);
    }

    @Bean
    @Scope(scopeName="prototype")
    CachingDirectory crowdCachingDirectory() {
        return new CachingDirectory(this.crowdInternalDirectoryUtils, this.crowdPasswordEncoderFactory, this.embeddedCrowdDirectoryDao, this.embeddedCrowdUserDao, this.embeddedCrowdGroupDao, this.embeddedCrowdMembershipDao, this.embeddedCrowdTombstoneDao, this.passwordConstraintsLoader);
    }

    @Bean
    @Scope(scopeName="prototype")
    LDAPPropertiesMapper crowdDirectoryLdapPropertiesMapper() {
        return new LDAPPropertiesMapperImpl(this.crowdLdapPropertiesHelper);
    }
}

