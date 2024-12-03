/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.ApplicationContext
 */
package com.atlassian.upm.license.compatibility;

import com.atlassian.upm.SysCommon;
import com.atlassian.upm.license.compatibility.CompatiblePluginLicenseManager;
import com.atlassian.upm.license.compatibility.LegacyCompatiblePluginLicenseManager;
import com.atlassian.upm.license.compatibility.OnDemandCompatiblePluginLicenseManager;
import com.atlassian.upm.license.compatibility.PluginLicenseManagerAccessor;
import com.atlassian.upm.license.compatibility.UpmCompatiblePluginLicenseManagerFactory;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

public class PluginLicenseManagerAccessorImpl
implements PluginLicenseManagerAccessor,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(PluginLicenseManagerAccessorImpl.class);
    private final ApplicationContext applicationContext;
    private final LegacyCompatiblePluginLicenseManager legacyPluginLicenseManager;
    private CompatiblePluginLicenseManager upmPluginLicenseManager;
    private OnDemandCompatiblePluginLicenseManager onDemandPluginLicenseManager;
    private boolean reattempted = false;

    public PluginLicenseManagerAccessorImpl(ApplicationContext applicationContext, LegacyCompatiblePluginLicenseManager legacyPluginLicenseManager) {
        this.applicationContext = (ApplicationContext)Preconditions.checkNotNull((Object)applicationContext, (Object)"applicationContext");
        this.legacyPluginLicenseManager = (LegacyCompatiblePluginLicenseManager)Preconditions.checkNotNull((Object)legacyPluginLicenseManager, (Object)"legacyPluginLicenseManager");
    }

    @Override
    public CompatiblePluginLicenseManager getPluginLicenseManager() {
        CompatiblePluginLicenseManager currentPluginLicenseManager = this.legacyPluginLicenseManager;
        if (this.upmPluginLicenseManager == null && !this.reattempted) {
            this.acquirePluginLicenseManager();
            this.reattempted = true;
        }
        if (this.upmPluginLicenseManager != null) {
            currentPluginLicenseManager = this.upmPluginLicenseManager;
        }
        if (this.isOnDemand()) {
            if (this.onDemandPluginLicenseManager == null) {
                this.onDemandPluginLicenseManager = new OnDemandCompatiblePluginLicenseManager(this.legacyPluginLicenseManager, currentPluginLicenseManager);
            }
            return this.onDemandPluginLicenseManager;
        }
        return currentPluginLicenseManager;
    }

    @Override
    public LegacyCompatiblePluginLicenseManager getLegacyPluginLicenseManager() {
        return this.legacyPluginLicenseManager;
    }

    @Override
    public boolean isOnDemand() {
        return SysCommon.isOnDemand();
    }

    @Override
    public boolean isUpmPluginLicenseManagerResolved() {
        return !(this.getPluginLicenseManager() instanceof LegacyCompatiblePluginLicenseManager);
    }

    private Class<?> getUpmPluginLicenseManagerFactoryClass() {
        try {
            this.getClass().getClassLoader().loadClass("com.atlassian.upm.api.license.PluginLicenseManager");
            return this.getClass().getClassLoader().loadClass("com.atlassian.upm.license.compatibility.UpmCompatiblePluginLicenseManagerFactory");
        }
        catch (Exception e) {
            log.info("The installed version of UPM is not licensing aware. Defaulting to the plugin's legacy licensing support.");
            return null;
        }
    }

    public void afterPropertiesSet() {
        this.acquirePluginLicenseManager();
    }

    private void acquirePluginLicenseManager() {
        try {
            Class<?> upmPluginLicenseManagerFactoryClass = this.getUpmPluginLicenseManagerFactoryClass();
            if (upmPluginLicenseManagerFactoryClass != null) {
                this.upmPluginLicenseManager = ((UpmCompatiblePluginLicenseManagerFactory)this.applicationContext.getAutowireCapableBeanFactory().createBean(this.getUpmPluginLicenseManagerFactoryClass(), 3, false)).get();
            }
        }
        catch (Exception e) {
            log.debug("Could not create UPM Plugin License Manager.", (Throwable)e);
        }
    }
}

