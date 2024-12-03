/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.MobileUtils
 *  com.atlassian.confluence.util.MobileUtils$MobileOS
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.mobile.contextprovider;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.MobileUtils;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class MobileAppBannerContextProvider
implements ContextProvider {
    private static final String CONFLUENCE_MOBILE_SCHEME = "confluence-server://";
    private static final String VIEW_PAGE_URL_REG = "^/pages/viewpage\\.action?.*";
    private static final String ANDROID_MANIFEST_PATH = "download/resources/com.atlassian.confluence.plugins.confluence-mobile-plugin:mobile-app-banner-web-resource/manifest.json";
    private static final String IOS_APP_ID = "1288365159";
    private static final String OS_DESTINATION = "os_destination";
    private final SettingsManager settingsManager;

    public MobileAppBannerContextProvider(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        HttpServletRequest request = ServletContextThreadLocal.getRequest();
        if (request == null) {
            return context;
        }
        MobileUtils.MobileOS os = MobileUtils.getMobileOS((HttpServletRequest)request);
        if (os == MobileUtils.MobileOS.ANDROID) {
            context.put("os", MobileUtils.MobileOS.ANDROID.getValue());
            context.put("manifest", ANDROID_MANIFEST_PATH);
        } else if (os == MobileUtils.MobileOS.IOS) {
            context.put("os", MobileUtils.MobileOS.IOS.getValue());
            context.put("appId", IOS_APP_ID);
            context.put("appArgument", this.getAppArgument(request));
        }
        return context;
    }

    private String getAppArgument(HttpServletRequest request) {
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        if (StringUtils.isBlank((CharSequence)baseUrl)) {
            return CONFLUENCE_MOBILE_SCHEME;
        }
        String appArgument = baseUrl.replaceFirst("http://|https://", CONFLUENCE_MOBILE_SCHEME);
        String osDestination = request.getParameter(OS_DESTINATION);
        if (StringUtils.isBlank((CharSequence)osDestination)) {
            return appArgument;
        }
        if (osDestination.matches(VIEW_PAGE_URL_REG)) {
            return appArgument + osDestination;
        }
        return appArgument + "?os_destination=" + osDestination;
    }
}

