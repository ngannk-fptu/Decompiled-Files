/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.http.mime;

import com.atlassian.http.mime.StringUtils;
import com.atlassian.http.mime.UserAgentUtil;
import com.atlassian.http.mime.UserAgentUtilImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserUtils {
    private static final Logger log = LoggerFactory.getLogger(BrowserUtils.class);

    public static boolean isIE8OrGreater(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return false;
        }
        UserAgentUtil.Browser browser = BrowserUtils.getBrowserObject(userAgent);
        return BrowserUtils.isIE(userAgent) && browser.getBrowserMajorVersion().compareTo(UserAgentUtil.BrowserMajorVersion.MSIE7) >= 1;
    }

    public static boolean isIE(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return false;
        }
        UserAgentUtilImpl userAgentUtil = new UserAgentUtilImpl();
        UserAgentUtil.BrowserFamily family = userAgentUtil.getBrowserFamily(userAgent);
        return family == UserAgentUtil.BrowserFamily.MSIE || family == UserAgentUtil.BrowserFamily.MSIE_TRIDENT;
    }

    private static UserAgentUtil.Browser getBrowserObject(String userAgent) {
        UserAgentUtilImpl userAgentUtil = new UserAgentUtilImpl();
        UserAgentUtil.UserAgent userAgentInfo = userAgentUtil.getUserAgentInfo(userAgent);
        return userAgentInfo.getBrowser();
    }
}

