/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.tenancy.api.event.TenantArrivedEvent
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.efi;

import com.atlassian.confluence.efi.OnboardingManager;
import com.atlassian.confluence.efi.OnboardingUtils;
import com.atlassian.confluence.efi.store.GlobalStorageService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.tenancy.api.event.TenantArrivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OnboardingManagerImpl
implements OnboardingManager {
    private final EventPublisher eventPublisher;
    private final GlobalStorageService globalStorageService;
    private String pluginInstalledDateInMillis;

    @Autowired
    public OnboardingManagerImpl(GlobalStorageService globalStorageService, @ComponentImport EventPublisher eventPublisher) {
        this.globalStorageService = globalStorageService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @EventListener
    public void onTenantArrived(TenantArrivedEvent e) {
        if (this.getPluginInstalledDateInMillis() == Long.MIN_VALUE) {
            this.pluginInstalledDateInMillis = String.valueOf(DateTime.now().getMillis());
            this.globalStorageService.set("plugin-installed-date-in-millis", this.pluginInstalledDateInMillis);
        }
    }

    @Override
    public boolean isFirstSpaceCreated() {
        return StringUtils.equalsIgnoreCase((CharSequence)"true", (CharSequence)this.globalStorageService.get(OnboardingUtils.METADATA_IS_FIRST_SPACE_CREATED));
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    @Override
    public long getPluginInstalledDateInMillis() {
        this.pluginInstalledDateInMillis = StringUtils.isEmpty((CharSequence)this.pluginInstalledDateInMillis) ? this.globalStorageService.get("plugin-installed-date-in-millis") : this.pluginInstalledDateInMillis;
        return StringUtils.isEmpty((CharSequence)this.pluginInstalledDateInMillis) ? Long.MIN_VALUE : Long.valueOf(this.pluginInstalledDateInMillis);
    }
}

