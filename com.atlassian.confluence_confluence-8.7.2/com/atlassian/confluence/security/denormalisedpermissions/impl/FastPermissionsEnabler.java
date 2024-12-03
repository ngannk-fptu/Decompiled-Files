/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.hibernate.dialect.Dialect
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.confluence.core.persistence.schema.api.SchemaInformationService;
import com.atlassian.confluence.event.events.admin.AsyncImportFinishedEvent;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionStateManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hibernate.dialect.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class FastPermissionsEnabler
implements DisposableBean,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(FastPermissionsEnabler.class);
    private static final String DISABLE_FAST_PERMISSIONS_ENABLING_PARAMETER_NAME = "confluence.disable-fast-permissions-enabling-on-site-restore";
    private final EventPublisher eventPublisher;
    private final DenormalisedPermissionStateManager denormalisedPermissionStateManager;
    private final SchemaInformationService schemaInformationService;
    private final AtomicBoolean requestToEnableServiceIsActive = new AtomicBoolean();
    private final AtomicBoolean applicationIsUpAndRunning = new AtomicBoolean();
    private final boolean disableFastPermissionsEnabling;

    public FastPermissionsEnabler(EventPublisher eventPublisher, DenormalisedPermissionStateManager denormalisedPermissionStateManager, SchemaInformationService schemaInformationService) {
        this.eventPublisher = eventPublisher;
        this.denormalisedPermissionStateManager = denormalisedPermissionStateManager;
        this.schemaInformationService = schemaInformationService;
        this.disableFastPermissionsEnabling = Boolean.getBoolean(DISABLE_FAST_PERMISSIONS_ENABLING_PARAMETER_NAME);
    }

    @VisibleForTesting
    public FastPermissionsEnabler(EventPublisher eventPublisher, DenormalisedPermissionStateManager denormalisedPermissionStateManager, SchemaInformationService schemaInformationService, boolean disableFastPermissionsEnabling) {
        this.eventPublisher = eventPublisher;
        this.denormalisedPermissionStateManager = denormalisedPermissionStateManager;
        this.schemaInformationService = schemaInformationService;
        this.disableFastPermissionsEnabling = disableFastPermissionsEnabling;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void turnFastPermissionsOn() {
        FastPermissionsEnabler fastPermissionsEnabler = this;
        synchronized (fastPermissionsEnabler) {
            if (this.applicationIsUpAndRunning.get()) {
                log.info("Fast permissions will be turned on immediately.");
                this.denormalisedPermissionStateManager.enableService();
            } else {
                log.info("Confluence is still not ready. Fast permissions will be turned on when the application is up and running.");
                this.requestToEnableServiceIsActive.set(true);
            }
        }
    }

    @EventListener
    public void onImportFinishEvent(AsyncImportFinishedEvent event) {
        if (event.isSiteImport()) {
            if (this.disableFastPermissionsEnabling) {
                log.warn("Fast permissions won't be enabled on site restore because '{}' parameter was set to true", (Object)DISABLE_FAST_PERMISSIONS_ENABLING_PARAMETER_NAME);
            } else {
                log.info("Site import has been finished, fast permissions will be turned on to make Confluence more performant.");
                this.turnFastPermissionsOn();
            }
        }
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventListener
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        FastPermissionsEnabler fastPermissionsEnabler = this;
        synchronized (fastPermissionsEnabler) {
            this.applicationIsUpAndRunning.set(true);
            if (this.requestToEnableServiceIsActive.get()) {
                log.info("Confluence is up an running and fast permissions are turning on now. Before, there was a request to turn fast permissions on, but the application was not ready at that moment.");
                if (this.isDatabaseSupported()) {
                    this.denormalisedPermissionStateManager.enableService();
                }
                this.requestToEnableServiceIsActive.set(false);
            }
        }
    }

    private boolean isDatabaseSupported() {
        Dialect dialect = this.schemaInformationService.getDialect();
        String dialectName = dialect.toString();
        if (HibernateConfig.isH2Dialect((String)dialectName)) {
            log.info("Fast Permissions will not be turned on by default because H2 DB can use storage that does not support locks and transactions properly. If required, Fast Permissions can be turned on manually.");
            return false;
        }
        return true;
    }
}

