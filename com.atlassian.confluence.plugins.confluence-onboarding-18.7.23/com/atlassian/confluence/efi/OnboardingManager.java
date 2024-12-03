/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.tenancy.api.event.TenantArrivedEvent
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.efi;

import com.atlassian.tenancy.api.event.TenantArrivedEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public interface OnboardingManager
extends InitializingBean,
DisposableBean {
    public static final String PLUGIN_INSTALLED_DATE_IN_MILLIS = "plugin-installed-date-in-millis";
    public static final String PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-onboarding";

    public long getPluginInstalledDateInMillis();

    public void onTenantArrived(TenantArrivedEvent var1);

    public boolean isFirstSpaceCreated();
}

