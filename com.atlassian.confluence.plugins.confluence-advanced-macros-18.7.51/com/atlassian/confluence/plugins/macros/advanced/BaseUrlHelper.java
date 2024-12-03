/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.core.filters.ServletContextThreadLocal;
import javax.servlet.http.HttpServletRequest;

@Internal
public class BaseUrlHelper {
    public static String calculateBaseUrl(SettingsManager settingsManager) {
        HttpServletRequest request = ServletContextThreadLocal.getRequest();
        if (request != null) {
            return request.getContextPath();
        }
        return settingsManager.getGlobalSettings().getBaseUrl();
    }
}

