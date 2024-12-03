/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseChangedEvent
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.event;

import com.addonengine.addons.analytics.event.AsyncAfCEnabledEvent;
import com.addonengine.addons.analytics.event.AsyncLicenseChangedToDcEvent;
import com.addonengine.addons.analytics.service.confluence.LicenseRemovalService;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseChangedEvent;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

@ConfluenceComponent
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 \u001b2\u00020\u00012\u00020\u0002:\u0001\u001bB#\b\u0007\u0012\b\b\u0001\u0010\u0003\u001a\u00020\u0004\u0012\b\b\u0001\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\b\u0010\u000e\u001a\u00020\u000fH\u0016J\b\u0010\u0010\u001a\u00020\u000fH\u0016J\u0010\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u0013H\u0007J\u0010\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u0015H\u0007J\u0010\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0017\u001a\u00020\u0018H\u0007J\u0010\u0010\u0019\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u001aH\u0007R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006\u001c"}, d2={"Lcom/addonengine/addons/analytics/event/LegacyLicenseCleaner;", "Lorg/springframework/beans/factory/InitializingBean;", "Lorg/springframework/beans/factory/DisposableBean;", "eventPublisher", "Lcom/atlassian/event/api/EventPublisher;", "licenseService", "Lcom/atlassian/confluence/license/LicenseService;", "licenseRemovalService", "Lcom/addonengine/addons/analytics/service/confluence/LicenseRemovalService;", "(Lcom/atlassian/event/api/EventPublisher;Lcom/atlassian/confluence/license/LicenseService;Lcom/addonengine/addons/analytics/service/confluence/LicenseRemovalService;)V", "getEventPublisher", "()Lcom/atlassian/event/api/EventPublisher;", "getLicenseService", "()Lcom/atlassian/confluence/license/LicenseService;", "afterPropertiesSet", "", "destroy", "onAfCEnabledEvent", "event", "Lcom/addonengine/addons/analytics/event/AsyncAfCEnabledEvent;", "onLicenseChangedEvent", "Lcom/atlassian/sal/api/license/LicenseChangedEvent;", "onLicenseChangedToDcEvent", "ignored", "Lcom/addonengine/addons/analytics/event/AsyncLicenseChangedToDcEvent;", "onPluginEnabledEvent", "Lcom/atlassian/plugin/event/events/PluginEnabledEvent;", "Companion", "analytics"})
public final class LegacyLicenseCleaner
implements InitializingBean,
DisposableBean {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final EventPublisher eventPublisher;
    @NotNull
    private final LicenseService licenseService;
    @NotNull
    private final LicenseRemovalService licenseRemovalService;
    private static final Logger log = LoggerFactory.getLogger((String)"atlassian.plugin");

    @Autowired
    public LegacyLicenseCleaner(@ComponentImport @NotNull EventPublisher eventPublisher, @ComponentImport @NotNull LicenseService licenseService, @NotNull LicenseRemovalService licenseRemovalService) {
        Intrinsics.checkNotNullParameter((Object)eventPublisher, (String)"eventPublisher");
        Intrinsics.checkNotNullParameter((Object)licenseService, (String)"licenseService");
        Intrinsics.checkNotNullParameter((Object)licenseRemovalService, (String)"licenseRemovalService");
        this.eventPublisher = eventPublisher;
        this.licenseService = licenseService;
        this.licenseRemovalService = licenseRemovalService;
    }

    @NotNull
    public final EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    @NotNull
    public final LicenseService getLicenseService() {
        return this.licenseService;
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public final void onLicenseChangedEvent(@NotNull LicenseChangedEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        BaseLicenseDetails newLicense = event.getNewLicense();
        if (newLicense != null && newLicense.isDataCenter()) {
            this.eventPublisher.publish((Object)new AsyncLicenseChangedToDcEvent());
        }
    }

    @EventListener
    public final void onLicenseChangedToDcEvent(@NotNull AsyncLicenseChangedToDcEvent ignored) {
        Intrinsics.checkNotNullParameter((Object)ignored, (String)"ignored");
        log.info("Confluence is running with a DC license. Removing legacy licenses of AfC (if exist)");
        this.licenseRemovalService.removeIfExist();
    }

    @EventListener
    public final void onPluginEnabledEvent(@NotNull PluginEnabledEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (Intrinsics.areEqual((Object)"com.addonengine.analytics", (Object)event.getPlugin().getKey())) {
            this.eventPublisher.publish((Object)new AsyncAfCEnabledEvent());
        }
    }

    @EventListener
    public final void onAfCEnabledEvent(@NotNull AsyncAfCEnabledEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (this.licenseService.isLicensedForDataCenterOrExempt()) {
            log.debug("AfC has been enabled. Removing legacy licenses of AfC (if exist) from Data Center");
            this.licenseRemovalService.removeIfExist();
        }
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0016\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2={"Lcom/addonengine/addons/analytics/event/LegacyLicenseCleaner$Companion;", "", "()V", "log", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "analytics"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

