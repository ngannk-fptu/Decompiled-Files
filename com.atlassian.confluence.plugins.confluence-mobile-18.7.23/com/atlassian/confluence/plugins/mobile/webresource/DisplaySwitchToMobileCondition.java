/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.mobile.webresource;

import com.atlassian.confluence.plugins.mobile.MobileUtils;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisplaySwitchToMobileCondition
implements Condition {
    private static final Logger log = LoggerFactory.getLogger(DisplaySwitchToMobileCondition.class);

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        HttpServletRequest request = ServletContextThreadLocal.getRequest();
        if (request == null) {
            log.warn("No servlet request retrieved so defaulting to not serving 'switch to mobile' resources");
            return false;
        }
        return MobileUtils.isSupportedUserAgent(request) && MobileUtils.isDesktopSwitchRequired(request);
    }
}

