/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.KeyedBandanaContext
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 */
package com.atlassian.confluence.plugins.gatekeeper.service;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.plugins.gatekeeper.license.AddonLicenseManager;
import com.atlassian.confluence.setup.bandana.KeyedBandanaContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;

public class Configuration
implements KeyedBandanaContext {
    private static final String ALLOW_WHO_CAN_VIEW_BUTTON = "allow.who-can-view.button";
    private BandanaManager bandanaManager;
    private AddonLicenseManager addonLicenseManager;

    public Configuration(BandanaManager bandanaManager, AddonLicenseManager addonLicenseManager) {
        this.bandanaManager = bandanaManager;
        this.addonLicenseManager = addonLicenseManager;
    }

    public String getContextKey() {
        return "com.atlassian.confluence.plugins.gatekeeper.confluence.ultimatepermissions";
    }

    public BandanaContext getParentContext() {
        return null;
    }

    public boolean hasParentContext() {
        return false;
    }

    public boolean isWhoCanViewButtonAllowed() {
        return AuthenticatedUserThreadLocal.get() != null && this.addonLicenseManager.getLicenseInfo().isValid() && this.isAllowedInConfig();
    }

    private boolean isAllowedInConfig() {
        String allowed = (String)this.bandanaManager.getValue((BandanaContext)this, ALLOW_WHO_CAN_VIEW_BUTTON);
        return allowed == null || Boolean.parseBoolean(allowed);
    }

    public void setWhoCanViewButtonAllowed(boolean allowed) {
        this.bandanaManager.setValue((BandanaContext)this, ALLOW_WHO_CAN_VIEW_BUTTON, (Object)Boolean.toString(allowed));
    }
}

