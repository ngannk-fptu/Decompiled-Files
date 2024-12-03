/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.prebake.Coordinate
 *  com.atlassian.webresource.api.prebake.DimensionAwareUrlReadingCondition
 *  com.atlassian.webresource.api.prebake.Dimensions
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.mobile.webresource;

import com.atlassian.confluence.plugins.mobile.MobileUtils;
import com.atlassian.confluence.plugins.mobile.webresource.DisplaySwitchToMobileCondition;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.DimensionAwareUrlReadingCondition;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisplaySwitchToMobileUrlReadingCondition
implements DimensionAwareUrlReadingCondition {
    private static final Logger log = LoggerFactory.getLogger(DisplaySwitchToMobileCondition.class);
    private static final String QUERY_PARAM = "ismobile";

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        HttpServletRequest request = ServletContextThreadLocal.getRequest();
        if (request == null) {
            log.warn("No servlet request retrieved so defaulting to not serving 'switch to mobile' resources");
            return;
        }
        Boolean isMobile = MobileUtils.isSupportedUserAgent(request) && MobileUtils.isDesktopSwitchRequired(request);
        if (isMobile.booleanValue()) {
            urlBuilder.addToQueryString(QUERY_PARAM, String.valueOf(true));
        }
    }

    public boolean shouldDisplay(QueryParams queryParams) {
        String isMobile = queryParams.get(QUERY_PARAM);
        if (!StringUtils.isEmpty((CharSequence)isMobile)) {
            return Boolean.parseBoolean(isMobile);
        }
        return false;
    }

    public Dimensions computeDimensions() {
        return Dimensions.empty().andExactly(QUERY_PARAM, new String[]{String.valueOf(true)}).andAbsent(QUERY_PARAM);
    }

    public void addToUrl(UrlBuilder urlBuilder, Coordinate coordinate) {
        coordinate.copyTo(urlBuilder, QUERY_PARAM);
    }
}

