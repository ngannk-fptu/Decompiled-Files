/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.crowd.dao.application.ApplicationDAO
 *  com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.embedded.spi.MembershipDao
 *  com.atlassian.crowd.model.group.InternalGroup
 *  com.atlassian.crowd.model.user.InternalUser
 *  com.atlassian.crowd.util.persistence.hibernate.batch.BatchFinder
 *  com.atlassian.crowd.util.persistence.hibernate.batch.BatchProcessor
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.osgi.container.OsgiContainerManager
 *  javax.annotation.Resource
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.core.persistence.schema.hibernate.HibernateSchemaInformationService;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCacheFactory;
import com.atlassian.confluence.impl.user.crowd.ApplicationCache;
import com.atlassian.confluence.impl.user.crowd.CachedCrowdApplicationDao;
import com.atlassian.confluence.impl.user.crowd.CachedCrowdGroupDao;
import com.atlassian.confluence.impl.user.crowd.CachedCrowdInternalMembershipDao;
import com.atlassian.confluence.impl.user.crowd.CachedCrowdMembershipDao;
import com.atlassian.confluence.impl.user.crowd.CachedCrowdUserDao;
import com.atlassian.confluence.impl.user.crowd.DefaultApplicationCache;
import com.atlassian.confluence.impl.user.crowd.DefaultCrowdDarkFeatureManager;
import com.atlassian.confluence.impl.user.crowd.DefaultGroupMembershipCache;
import com.atlassian.confluence.impl.user.crowd.DefaultMembershipCache;
import com.atlassian.confluence.impl.user.crowd.GroupMembershipCache;
import com.atlassian.confluence.impl.user.crowd.MembershipCache;
import com.atlassian.confluence.impl.user.crowd.hibernate.HibernateApplicationDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.HibernateDirectoryDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.HibernateGroupDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.HibernateInternalMembershipDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.HibernateMembershipDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.HibernateUserDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalGroupDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalMembershipDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalUserDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.batch.Hibernate5BatchFinder;
import com.atlassian.confluence.impl.user.crowd.hibernate.batch.Hibernate5BatchProcessor;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.crowd.dao.application.ApplicationDAO;
import com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.embedded.spi.MembershipDao;
import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.model.user.InternalUser;
import com.atlassian.crowd.util.persistence.hibernate.batch.BatchFinder;
import com.atlassian.crowd.util.persistence.hibernate.batch.BatchProcessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import javax.annotation.Resource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class EmbeddedCrowdDaoContextConfig {
    @Resource
    private TransactionAwareCacheFactory transactionalCacheFactory;
    @Resource
    private CacheFactory cacheManager;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private SessionFactory sessionFactory5;
    @Resource
    private ConfluenceUserDao confluenceUserDao;
    @Resource
    private BatchOperationManager batchOperationManager;
    @Resource
    private OsgiContainerManager osgiContainerManager;
    @Resource
    private HibernateSchemaInformationService schemaInformationService;
    @Resource
    private DarkFeaturesManager darkFeaturesManager;

    EmbeddedCrowdDaoContextConfig() {
    }

    @Bean
    InternalUserDao<InternalUser> embeddedCrowdUserDao() {
        return new CachedCrowdUserDao(this.embeddedCrowdUserDaoTarget(), this.transactionalCacheFactory, this.cacheManager, this.eventPublisher);
    }

    @Bean(autowireCandidate=false)
    HibernateUserDao embeddedCrowdUserDaoTarget() {
        return new HibernateUserDao(this.sessionFactory5, this::embeddedCrowdDirectoryDao, this.internalMembershipDao(), this.confluenceUserDao, this.batchProcessor5(), this.batchFinder(), this.batchOperationManager);
    }

    @Bean
    InternalGroupDao<InternalGroup> embeddedCrowdGroupDao() {
        return new CachedCrowdGroupDao(this.embeddedCrowdGroupDaoTarget(), this.transactionalCacheFactory);
    }

    @Bean(autowireCandidate=false)
    HibernateGroupDao embeddedCrowdGroupDaoTarget() {
        return new HibernateGroupDao(this.sessionFactory5, this::embeddedCrowdDirectoryDao, this.internalMembershipDao(), this.batchProcessor5(), this.batchFinder());
    }

    @Bean
    DirectoryDao embeddedCrowdDirectoryDao() {
        return new HibernateDirectoryDao(this.sessionFactory5, this.embeddedCrowdUserDao(), this.embeddedCrowdGroupDao(), this.internalMembershipDao());
    }

    @Bean
    InternalMembershipDao internalMembershipDao() {
        return new CachedCrowdInternalMembershipDao(this.internalMembershipDaoTarget(), this.embeddedCrowdMembershipCache(), this.embeddedCrowdGroupParentMembershipCache(), this.embeddedCrowdGroupChildMembershipCache());
    }

    @Bean(autowireCandidate=false)
    HibernateInternalMembershipDao internalMembershipDaoTarget() {
        return new HibernateInternalMembershipDao(this.sessionFactory5);
    }

    @Bean
    MembershipDao embeddedCrowdMembershipDao() {
        return new CachedCrowdMembershipDao(this.embeddedCrowdMembershipDaoTarget(), this.embeddedCrowdMembershipCache(), this.embeddedCrowdGroupParentMembershipCache(), this.embeddedCrowdGroupChildMembershipCache());
    }

    @Bean
    HibernateMembershipDao embeddedCrowdMembershipDaoTarget() {
        return new HibernateMembershipDao(this.sessionFactory5, this.embeddedCrowdUserDao(), this.embeddedCrowdGroupDao(), this.batchProcessor5(), this.schemaInformationService);
    }

    @Bean
    ApplicationDAO embeddedCrowdApplicationDao() {
        CachedCrowdApplicationDao bean = new CachedCrowdApplicationDao();
        bean.setDelegate(this.embeddedCrowdApplicationDaoTarget());
        bean.setCache(this.embeddedCrowdApplicationCache());
        return bean;
    }

    @Bean
    HibernateApplicationDao embeddedCrowdApplicationDaoTarget() {
        return new HibernateApplicationDao(this.sessionFactory5, this.embeddedCrowdDirectoryDao());
    }

    @Bean
    ApplicationCache embeddedCrowdApplicationCache() {
        return new DefaultApplicationCache(this.transactionalCacheFactory, this.eventPublisher);
    }

    @Bean
    MembershipCache embeddedCrowdMembershipCache() {
        return new DefaultMembershipCache(this.transactionalCacheFactory);
    }

    @Bean
    GroupMembershipCache embeddedCrowdGroupParentMembershipCache() {
        return DefaultGroupMembershipCache.createParentGroupMembershipCache(this.transactionalCacheFactory);
    }

    @Bean
    GroupMembershipCache embeddedCrowdGroupChildMembershipCache() {
        return DefaultGroupMembershipCache.createChildGroupMembershipCache(this.transactionalCacheFactory);
    }

    @Bean
    CrowdDarkFeatureManager crowdDarkFeatureManager() {
        return new DefaultCrowdDarkFeatureManager(this.darkFeaturesManager);
    }

    @Bean
    BatchFinder batchFinder() {
        Hibernate5BatchFinder bean = new Hibernate5BatchFinder(this.sessionFactory5);
        bean.setBatchSize(100);
        return bean;
    }

    @Bean
    BatchProcessor<Session> batchProcessor5() {
        Hibernate5BatchProcessor bean = new Hibernate5BatchProcessor(this.sessionFactory5);
        bean.setBatchSize(100);
        return bean;
    }
}

