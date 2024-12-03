/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.event.Event
 *  com.atlassian.spring.container.ContainerManager
 *  oshi.SystemInfo
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.confluence.impl.health.HealthCheckTemplate;
import com.atlassian.confluence.impl.util.sandbox.misc.PluginSandboxCheck;
import com.atlassian.confluence.internal.health.HealthCheckResult;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.johnson.event.Event;
import com.atlassian.spring.container.ContainerManager;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import oshi.SystemInfo;

public class OperatingSystemFreeMemoryHealthCheck
extends HealthCheckTemplate {
    private static final int MIN_FREE_MEMORY_MEGABYTES = Integer.getInteger("confluence.min.free.memory.megabytes", 2048);
    private static final String FAILURE_MESSAGE_KEY = "johnson.message.insufficient.os.free.memory";
    private static final URL KB_URL = UrlBuilder.createURL("https://confluence.atlassian.com/display/CONFKB/Startup+check%3A+available+memory");
    private static final Supplier<I18NBean> I18N_BEAN_SUPPLIER = () -> (I18NBean)ContainerManager.getComponent((String)"i18NBean", I18NBean.class);
    private final LicenseService licenseService;
    private final Supplier<I18NBean> i18NBeanSupplier;

    public OperatingSystemFreeMemoryHealthCheck(LicenseService licenseService) {
        super(Collections.emptyList());
        this.licenseService = Objects.requireNonNull(licenseService);
        this.i18NBeanSupplier = I18N_BEAN_SUPPLIER;
    }

    @Override
    protected Set<LifecyclePhase> getApplicablePhases() {
        return Collections.singleton(LifecyclePhase.PLUGIN_FRAMEWORK_STARTED);
    }

    @Override
    protected List<HealthCheckResult> doPerform() {
        if (this.licenseService.isLicensedForDataCenterOrExempt() && this.atLeastOneSandboxEnabled()) {
            SystemInfo si = new SystemInfo();
            long freeMemoryInMegabytes = si.getHardware().getMemory().getAvailable() / 0x100000L;
            String errorMessage = this.i18NBeanSupplier.get().getText(FAILURE_MESSAGE_KEY, new Object[]{freeMemoryInMegabytes, MIN_FREE_MEMORY_MEGABYTES});
            if (freeMemoryInMegabytes < (long)MIN_FREE_MEMORY_MEGABYTES) {
                return HealthCheckResult.fail(this, this.createFailureEvent(errorMessage), KB_URL, "os-free-memory-less-than-required-minimum", errorMessage);
            }
        }
        return Collections.emptyList();
    }

    private boolean atLeastOneSandboxEnabled() {
        return !PluginSandboxCheck.documentConversionSandboxExplicitlyDisabled() || !PluginSandboxCheck.pdfExportSandboxExplicitlyDisabled();
    }

    private Event createFailureEvent(String errorMessage) {
        Event event = new Event(JohnsonEventType.FREE_MEMORY.eventType(), errorMessage, JohnsonEventLevel.WARNING.level());
        event.addAttribute((Object)"dismissible", (Object)true);
        return event;
    }
}

