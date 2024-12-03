/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.criteria;

import com.atlassian.confluence.admin.criteria.AdminConfigurationCriteria;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseURLIsValidCriteria
implements AdminConfigurationCriteria {
    private static final Logger log = LoggerFactory.getLogger(BaseURLIsValidCriteria.class);
    private final SettingsManager settingsManager;

    public BaseURLIsValidCriteria(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public boolean isMet() {
        URL addy = null;
        boolean valid = true;
        try {
            addy = new URL(this.getValue());
            valid = valid && !this.containsLocalhost(addy);
            valid = valid && !this.containsIpAddress(addy);
        }
        catch (Exception e) {
            log.debug("Testing Base URL failed: " + e.getMessage());
        }
        return valid;
    }

    @Override
    public boolean getIgnored() {
        return this.settingsManager.getGlobalSettings().isBaseUrlAdminMessageOff();
    }

    @Override
    public void setIgnored(boolean ignored) {
        Settings settings = this.settingsManager.getGlobalSettings();
        settings.setBaseUrlAdminMessageOff(ignored);
        this.settingsManager.updateGlobalSettings(settings);
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public String getValue() {
        return this.settingsManager.getGlobalSettings().getBaseUrl();
    }

    @Override
    public boolean hasLiveValue() {
        return true;
    }

    private boolean containsLocalhost(URL url) {
        boolean isLocal = false;
        String host = url.getHost();
        if (!StringUtils.isBlank((CharSequence)host)) {
            isLocal = isLocal || host.equalsIgnoreCase("localhost");
            isLocal = isLocal || host.endsWith(".local");
            isLocal = isLocal || host.equals("0.0.0.0");
            isLocal = isLocal || host.equals("127.0.0.1");
        }
        return isLocal;
    }

    private boolean containsIpAddress(URL url) {
        String[] parts = url.getHost().split("\\.");
        boolean result = parts.length == 4;
        try {
            for (String s : parts) {
                int i = Integer.parseInt(s);
                result = result && i >= 0 && i <= 255;
            }
        }
        catch (NumberFormatException nfe) {
            result = false;
        }
        return result;
    }
}

