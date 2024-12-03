/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.webhook.NoopWebhookDAOImpl
 *  com.atlassian.crowd.dao.webhook.WebhookDAO
 *  com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager
 *  com.atlassian.crowd.embedded.api.ApplicationFactory
 *  com.atlassian.crowd.embedded.spi.DcLicenseChecker
 *  com.atlassian.crowd.embedded.spi.UserDao
 *  com.atlassian.crowd.event.EventStore
 *  com.atlassian.crowd.event.EventStoreGeneric
 *  com.atlassian.crowd.manager.application.ApplicationService
 *  com.atlassian.crowd.manager.application.AuthenticationOrderOptimizer
 *  com.atlassian.crowd.manager.application.filtering.AccessFilterFactory
 *  com.atlassian.crowd.manager.application.filtering.AccessFilterFactoryImpl
 *  com.atlassian.crowd.manager.application.search.DefaultSearchStrategyFactory
 *  com.atlassian.crowd.manager.application.search.SearchStrategyFactory
 *  com.atlassian.crowd.manager.avatar.AvatarProvider
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.manager.permission.PermissionManager
 *  com.atlassian.crowd.manager.recovery.RecoveryModeAwareApplicationService
 *  com.atlassian.crowd.manager.recovery.RecoveryModeService
 *  com.atlassian.crowd.manager.webhook.WebhookRegistry
 *  com.atlassian.crowd.manager.webhook.WebhookRegistryImpl
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.confluence.impl.user.crowd.NopCrowdAvatarProvider;
import com.atlassian.crowd.dao.webhook.NoopWebhookDAOImpl;
import com.atlassian.crowd.dao.webhook.WebhookDAO;
import com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager;
import com.atlassian.crowd.embedded.api.ApplicationFactory;
import com.atlassian.crowd.embedded.spi.DcLicenseChecker;
import com.atlassian.crowd.embedded.spi.UserDao;
import com.atlassian.crowd.event.EventStore;
import com.atlassian.crowd.event.EventStoreGeneric;
import com.atlassian.crowd.manager.application.ApplicationService;
import com.atlassian.crowd.manager.application.AuthenticationOrderOptimizer;
import com.atlassian.crowd.manager.application.filtering.AccessFilterFactory;
import com.atlassian.crowd.manager.application.filtering.AccessFilterFactoryImpl;
import com.atlassian.crowd.manager.application.search.DefaultSearchStrategyFactory;
import com.atlassian.crowd.manager.application.search.SearchStrategyFactory;
import com.atlassian.crowd.manager.avatar.AvatarProvider;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.permission.PermissionManager;
import com.atlassian.crowd.manager.recovery.RecoveryModeAwareApplicationService;
import com.atlassian.crowd.manager.recovery.RecoveryModeService;
import com.atlassian.crowd.manager.webhook.WebhookRegistry;
import com.atlassian.crowd.manager.webhook.WebhookRegistryImpl;
import com.atlassian.event.api.EventPublisher;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CrowdApplicationServiceContextConfig {
    @Resource
    private DirectoryManager crowdDirectoryManager;
    @Resource
    private PermissionManager crowdPermissionManager;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private ApplicationFactory embeddedCrowdApplicationFactory;
    @Resource
    private RecoveryModeService recoveryModeService;
    @Resource
    private UserDao embeddedCrowdUserDao;
    @Resource
    private DcLicenseChecker licenseService;
    @Resource
    private CrowdDarkFeatureManager crowdDarkFeatureManager;

    CrowdApplicationServiceContextConfig() {
    }

    @Bean
    ApplicationService crowdApplicationService() {
        return new RecoveryModeAwareApplicationService(this.crowdDirectoryManager, (SearchStrategyFactory)new DefaultSearchStrategyFactory(this.crowdDirectoryManager), this.crowdPermissionManager, this.eventPublisher, (EventStore)new EventStoreGeneric(0L), (WebhookRegistry)new WebhookRegistryImpl((WebhookDAO)new NoopWebhookDAOImpl()), (AvatarProvider)new NopCrowdAvatarProvider(), this.embeddedCrowdApplicationFactory, this.recoveryModeService, new AuthenticationOrderOptimizer(this.embeddedCrowdUserDao, this.recoveryModeService), (AccessFilterFactory)new AccessFilterFactoryImpl(this.crowdDirectoryManager, this.licenseService), this.crowdDarkFeatureManager);
    }
}

