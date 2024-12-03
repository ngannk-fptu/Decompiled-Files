/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.google.common.base.Strings
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.analytics.client;

import com.atlassian.sal.api.license.LicenseHandler;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

public class ServerIdProvider {
    private String serverId = "";
    private final LicenseHandler licenseHandler;

    public ServerIdProvider(LicenseHandler licenseHandler) {
        this.licenseHandler = licenseHandler;
    }

    public String getServerId() {
        if (StringUtils.isBlank((CharSequence)this.serverId)) {
            this.serverId = Strings.nullToEmpty((String)this.licenseHandler.getServerId());
        }
        return this.serverId;
    }
}

