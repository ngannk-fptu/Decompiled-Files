/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  javax.annotation.PreDestroy
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.jetbrains.annotations.NotNull
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.osgi.context.BundleContextAware
 */
package com.atlassian.plugins.authentication.impl.config;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.IdpSearchParameters;
import com.atlassian.plugins.authentication.impl.config.IdpConfigServiceImpl;
import com.atlassian.plugins.authentication.impl.config.IdpConfigValidatorProvider;
import com.atlassian.plugins.authentication.impl.config.ServiceTrackerFactory;
import com.atlassian.plugins.authentication.impl.config.SsoConfigDao;
import com.atlassian.plugins.authentication.impl.config.audit.IdpConfigMappers;
import com.atlassian.plugins.authentication.impl.util.ApplicationStateValidator;
import com.atlassian.plugins.authentication.impl.util.ProductLicenseDataProvider;
import com.atlassian.plugins.authentication.impl.web.oidc.OidcDiscoverySupport;
import java.util.List;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.osgi.context.BundleContextAware;

@Named
@ExportAsService(value={IdpConfigService.class})
public class AuditingIdpConfigService
implements IdpConfigService,
BundleContextAware {
    private static final Logger log = LoggerFactory.getLogger(AuditingIdpConfigService.class);
    public static final String PLUGIN_KEY = "com.atlassian.plugins.authentication.atlassian-authentication-plugin";
    public static final String RESOURCE_TYPE = "IDP Configuration";
    private final IdpConfigService delegate;
    private final IdpConfigMappers idpConfigMappers;
    private final ServiceTrackerFactory<AuditService> auditServiceTrackerFactory;
    private ServiceTracker<AuditService, Object> serviceTracker;

    @Inject
    public AuditingIdpConfigService(SsoConfigDao ssoConfigDao, IdpConfigValidatorProvider idpConfigValidatorProvider, OidcDiscoverySupport oidcDiscoverySupport, ProductLicenseDataProvider productLicenseDataProvider, ApplicationStateValidator applicationStateValidator, IdpConfigMappers idpConfigMappers, ServiceTrackerFactory<AuditService> serviceTrackerFactory) {
        this(new IdpConfigServiceImpl(ssoConfigDao, idpConfigValidatorProvider, oidcDiscoverySupport, productLicenseDataProvider, applicationStateValidator), idpConfigMappers, serviceTrackerFactory);
    }

    @VisibleForTesting
    AuditingIdpConfigService(IdpConfigService delegate, IdpConfigMappers idpConfigMappers, ServiceTrackerFactory<AuditService> auditServiceTrackerFactory) {
        this.delegate = delegate;
        this.idpConfigMappers = idpConfigMappers;
        this.auditServiceTrackerFactory = auditServiceTrackerFactory;
    }

    @PreDestroy
    public void preDestroy() {
        this.serviceTracker.close();
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.serviceTracker = this.auditServiceTrackerFactory.create(bundleContext, AuditService.class, null);
        this.serviceTracker.open();
    }

    @Override
    public List<IdpConfig> getIdpConfigs() {
        return this.delegate.getIdpConfigs();
    }

    @Override
    public List<IdpConfig> getIdpConfigs(IdpSearchParameters searchParameters) {
        return this.delegate.getIdpConfigs(searchParameters);
    }

    @Override
    public IdpConfig getIdpConfig(Long id) {
        return this.delegate.getIdpConfig(id);
    }

    @Override
    public IdpConfig updateIdpConfig(@NotNull IdpConfig newConfig) {
        IdpConfig oldConfig = this.getIdpConfig(newConfig.getId());
        newConfig = this.delegate.updateIdpConfig(newConfig);
        try {
            AuditService auditService = (AuditService)this.serviceTracker.getService();
            if (auditService != null) {
                auditService.audit(AuditEvent.fromI18nKeys((String)"com.atlassian.plugins.authentication.audit.category", (String)"com.atlassian.plugins.authentication.audit.action.update", (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).changedValues(this.idpConfigMappers.mapChanges(oldConfig, newConfig)).affectedObject(AuditResource.builder((String)newConfig.getName(), (String)RESOURCE_TYPE).build()).build());
            } else {
                log.debug("Could not audit log new idp configuration update, as audit log is not available");
            }
        }
        catch (Exception e) {
            log.error("Could not audit log a new event of idp configuration update", (Throwable)e);
        }
        return newConfig;
    }

    @Override
    public IdpConfig addIdpConfig(@NotNull IdpConfig newConfig) {
        IdpConfig idpConfig = this.delegate.addIdpConfig(newConfig);
        try {
            AuditService auditService = (AuditService)this.serviceTracker.getService();
            if (auditService != null) {
                auditService.audit(AuditEvent.fromI18nKeys((String)"com.atlassian.plugins.authentication.audit.category", (String)"com.atlassian.plugins.authentication.audit.action.addition", (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).changedValues(this.idpConfigMappers.mapChanges(null, idpConfig)).affectedObject(AuditResource.builder((String)newConfig.getName(), (String)RESOURCE_TYPE).build()).build());
            } else {
                log.debug("Could not audit log new idp configuration creation, as audit log is not available");
            }
        }
        catch (Exception e) {
            log.error("Could not audit log a new event of new idp configuration creation", (Throwable)e);
        }
        return idpConfig;
    }

    @Override
    public IdpConfig removeIdpConfig(Long idpConfigId) {
        IdpConfig oldIdpConfig = this.delegate.removeIdpConfig(idpConfigId);
        try {
            AuditService auditService = (AuditService)this.serviceTracker.getService();
            if (auditService != null) {
                auditService.audit(AuditEvent.fromI18nKeys((String)"com.atlassian.plugins.authentication.audit.category", (String)"com.atlassian.plugins.authentication.audit.action.removal", (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).changedValues(this.idpConfigMappers.mapChanges(oldIdpConfig, null)).affectedObject(AuditResource.builder((String)oldIdpConfig.getName(), (String)RESOURCE_TYPE).build()).build());
            } else {
                log.debug("Could not audit log idp configuration removal, as audit log is not available");
            }
        }
        catch (Exception e) {
            log.error("Could not audit log a new event of new idp configuration removal", (Throwable)e);
        }
        return oldIdpConfig;
    }

    @Override
    public IdpConfig refreshIdpConfig(IdpConfig configToRefresh) {
        return this.delegate.refreshIdpConfig(configToRefresh);
    }

    static interface I18nKeys {
        public static final String CATEGORY_KEY = "com.atlassian.plugins.authentication.audit.category";
        public static final String IDP_UPDATED_ACTION_KEY = "com.atlassian.plugins.authentication.audit.action.update";
        public static final String IDP_ADDED_ACTION_KEY = "com.atlassian.plugins.authentication.audit.action.addition";
        public static final String IDP_REMOVED_ACTION_KEY = "com.atlassian.plugins.authentication.audit.action.removal";
    }
}

