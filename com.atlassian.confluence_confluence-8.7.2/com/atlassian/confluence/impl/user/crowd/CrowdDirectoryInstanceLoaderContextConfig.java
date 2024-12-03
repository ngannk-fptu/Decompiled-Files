/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.crowd.core.event.MultiEventPublisher
 *  com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager
 *  com.atlassian.crowd.directory.TransactionalDirectoryCacheFactory
 *  com.atlassian.crowd.directory.ldap.cache.CacheRefresherFactory
 *  com.atlassian.crowd.directory.ldap.cache.DirectoryCacheFactory
 *  com.atlassian.crowd.directory.loader.CustomDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.DbCachingRemoteDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.DelegatedAuthenticationDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.DelegatedAuthenticationDirectoryInstanceLoaderImpl
 *  com.atlassian.crowd.directory.loader.DelegatingDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.DelegatingDirectoryInstanceLoaderImpl
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.InternalDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.InternalDirectoryInstanceLoaderImpl
 *  com.atlassian.crowd.directory.loader.LDAPDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.LDAPDirectoryInstanceLoaderImpl
 *  com.atlassian.crowd.directory.loader.RemoteCrowdDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.RemoteCrowdDirectoryInstanceLoaderImpl
 *  com.atlassian.crowd.directory.synchronisation.cache.CacheRefresherFactoryImpl
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.embedded.spi.GroupDao
 *  com.atlassian.crowd.embedded.spi.UserDao
 *  com.atlassian.crowd.manager.audit.AuditService
 *  com.atlassian.crowd.manager.audit.mapper.AuditLogGroupMapper
 *  com.atlassian.crowd.manager.audit.mapper.AuditLogUserMapper
 *  com.atlassian.crowd.manager.directory.SynchronisationStatusManager
 *  com.atlassian.crowd.manager.recovery.RecoveryModeDirectoryLoader
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.transaction.interceptor.TransactionInterceptor
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.impl.user.crowd.CacheableDirectoryInstanceLoader;
import com.atlassian.crowd.core.event.MultiEventPublisher;
import com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager;
import com.atlassian.crowd.directory.TransactionalDirectoryCacheFactory;
import com.atlassian.crowd.directory.ldap.cache.CacheRefresherFactory;
import com.atlassian.crowd.directory.ldap.cache.DirectoryCacheFactory;
import com.atlassian.crowd.directory.loader.CustomDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.DbCachingRemoteDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.DelegatedAuthenticationDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.DelegatedAuthenticationDirectoryInstanceLoaderImpl;
import com.atlassian.crowd.directory.loader.DelegatingDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.DelegatingDirectoryInstanceLoaderImpl;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.InternalDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.InternalDirectoryInstanceLoaderImpl;
import com.atlassian.crowd.directory.loader.LDAPDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.LDAPDirectoryInstanceLoaderImpl;
import com.atlassian.crowd.directory.loader.RemoteCrowdDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.RemoteCrowdDirectoryInstanceLoaderImpl;
import com.atlassian.crowd.directory.synchronisation.cache.CacheRefresherFactoryImpl;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.embedded.spi.GroupDao;
import com.atlassian.crowd.embedded.spi.UserDao;
import com.atlassian.crowd.manager.audit.AuditService;
import com.atlassian.crowd.manager.audit.mapper.AuditLogGroupMapper;
import com.atlassian.crowd.manager.audit.mapper.AuditLogUserMapper;
import com.atlassian.crowd.manager.directory.SynchronisationStatusManager;
import com.atlassian.crowd.manager.recovery.RecoveryModeDirectoryLoader;
import com.atlassian.crowd.util.InstanceFactory;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.AvailableToPlugins;
import java.util.Arrays;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Configuration
class CrowdDirectoryInstanceLoaderContextConfig {
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private CacheFactory cacheManager;
    @Resource
    private InstanceFactory instanceFactory;
    @Resource
    private DirectoryDao embeddedCrowdDirectoryDao;
    @Resource
    private UserDao embeddedCrowdUserDao;
    @Resource
    private GroupDao embeddedCrowdGroupDao;
    @Resource
    private SynchronisationStatusManager synchronisationStatusManager;
    @Resource
    private MultiEventPublisher crowdMultiEventPublisher5;
    @Resource
    private TransactionInterceptor crowdTransactionInterceptor;
    @Resource
    private AuditService crowdAuditService;
    @Resource
    private AuditLogUserMapper crowdAuditLogUserMapper;
    @Resource
    private AuditLogGroupMapper crowdAuditLogGroupMapper;
    @Resource
    private CrowdDarkFeatureManager crowdDarkFeatureManager;

    CrowdDirectoryInstanceLoaderContextConfig() {
    }

    @Bean
    @AvailableToPlugins
    DirectoryInstanceLoader directoryInstanceLoader() {
        return new CacheableDirectoryInstanceLoader((DelegatingDirectoryInstanceLoader)new DelegatingDirectoryInstanceLoaderImpl(Arrays.asList(new RecoveryModeDirectoryLoader(), this.crowdInternalDirectoryLoader(), this.delegatedAuthenticationDirectoryInstanceLoader(), this.crowdDatabaseCachingDirectoryLoader(), this.customDirectoryLoader())), (EventListenerRegistrar)this.eventPublisher, this.cacheManager);
    }

    @Bean(autowireCandidate=false)
    DirectoryInstanceLoader customDirectoryLoader() {
        return new CustomDirectoryInstanceLoader(this.instanceFactory);
    }

    @Bean(autowireCandidate=false)
    DirectoryInstanceLoader crowdDatabaseCachingDirectoryLoader() {
        return new DbCachingRemoteDirectoryInstanceLoader((DirectoryInstanceLoader)new DelegatingDirectoryInstanceLoaderImpl(Arrays.asList(this.crowdLdapDirectoryLoader(), this.remoteCrowdDirectoryInstanceLoader())), this.crowdInternalDirectoryLoader(), this.crowdDirectoryCacheFactory(), (CacheRefresherFactory)new CacheRefresherFactoryImpl(), this.crowdAuditService, this.crowdAuditLogUserMapper, this.crowdAuditLogGroupMapper, this.eventPublisher, this.embeddedCrowdDirectoryDao);
    }

    @Bean
    DirectoryCacheFactory crowdDirectoryCacheFactory() {
        return new TransactionalDirectoryCacheFactory(this.embeddedCrowdDirectoryDao, this.synchronisationStatusManager, this.crowdMultiEventPublisher5, this.embeddedCrowdUserDao, this.embeddedCrowdGroupDao, this.crowdTransactionInterceptor, this.crowdDarkFeatureManager);
    }

    @Bean(autowireCandidate=false)
    RemoteCrowdDirectoryInstanceLoader remoteCrowdDirectoryInstanceLoader() {
        return new RemoteCrowdDirectoryInstanceLoaderImpl(this.instanceFactory);
    }

    @Bean(autowireCandidate=false)
    InternalDirectoryInstanceLoader crowdInternalDirectoryLoader() {
        return new InternalDirectoryInstanceLoaderImpl(this.instanceFactory);
    }

    @Bean(autowireCandidate=false)
    DelegatedAuthenticationDirectoryInstanceLoader delegatedAuthenticationDirectoryInstanceLoader() {
        return new DelegatedAuthenticationDirectoryInstanceLoaderImpl(this.crowdLdapDirectoryLoader(), this.crowdInternalDirectoryLoader(), this.eventPublisher, this.embeddedCrowdDirectoryDao);
    }

    @Bean(autowireCandidate=false)
    LDAPDirectoryInstanceLoader crowdLdapDirectoryLoader() {
        return new LDAPDirectoryInstanceLoaderImpl(this.instanceFactory);
    }
}

