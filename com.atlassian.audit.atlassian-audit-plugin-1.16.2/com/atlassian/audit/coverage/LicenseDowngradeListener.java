/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditCoverageConfig
 *  com.atlassian.audit.entity.EffectiveCoverageLevel
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.sal.api.license.LicenseChangedEvent
 *  com.google.common.collect.Maps
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.audit.coverage;

import com.atlassian.audit.coverage.InternalAuditCoverageConfigService;
import com.atlassian.audit.coverage.ProductLicenseChecker;
import com.atlassian.audit.denylist.ExcludedActionsService;
import com.atlassian.audit.entity.AuditCoverageConfig;
import com.atlassian.audit.entity.EffectiveCoverageLevel;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.sal.api.license.LicenseChangedEvent;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class LicenseDowngradeListener
implements InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(LicenseDowngradeListener.class);
    private final ProductLicenseChecker licenseChecker;
    private final InternalAuditCoverageConfigService coverageConfigService;
    private final ExcludedActionsService excludedActionsService;
    private final EventListenerRegistrar eventListenerRegistrar;

    public LicenseDowngradeListener(ProductLicenseChecker licenseChecker, InternalAuditCoverageConfigService coverageConfigService, ExcludedActionsService excludedActionsService, EventListenerRegistrar eventListenerRegistrar) {
        this.licenseChecker = licenseChecker;
        this.coverageConfigService = coverageConfigService;
        this.excludedActionsService = excludedActionsService;
        this.eventListenerRegistrar = eventListenerRegistrar;
    }

    public void afterPropertiesSet() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @EventListener
    public void onLicenseChange(LicenseChangedEvent licenseChangedEvent) {
        if (this.licenseChecker.isNotDcLicense()) {
            log.info("License downgrade to Server has been detected");
            AuditCoverageConfig currentConfig = this.coverageConfigService.getConfig();
            AuditCoverageConfig maybeDowngradedConfig = new AuditCoverageConfig(Maps.transformValues((Map)currentConfig.getLevelByArea(), level -> Objects.requireNonNull(level).mostRestrictive(EffectiveCoverageLevel.BASE)));
            this.coverageConfigService.updateConfig(maybeDowngradedConfig);
            log.info("Audit coverage has been updated after license downgrade to Server");
            this.excludedActionsService.replaceExcludedActions(Collections.emptyList());
            log.info("Audit deny listed actions have been deleted after license downgrade to Server");
        }
    }

    public void destroy() {
        this.eventListenerRegistrar.unregister((Object)this);
    }
}

