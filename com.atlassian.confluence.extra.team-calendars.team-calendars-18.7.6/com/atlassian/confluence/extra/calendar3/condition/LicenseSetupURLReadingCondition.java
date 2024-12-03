/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.condition;

import com.atlassian.confluence.extra.calendar3.license.LicenseAccessor;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseSetupURLReadingCondition
implements UrlReadingCondition {
    private static final Logger LOG = LoggerFactory.getLogger(LicenseSetupURLReadingCondition.class);
    static final String TC_LICENSE_SETUP_QUERY_PARAM_KEY = "tc-license-setup";
    private LicenseAccessor licenseAccessor;

    public LicenseSetupURLReadingCondition(LicenseAccessor licenseAccessor) {
        this.licenseAccessor = licenseAccessor;
    }

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        boolean licenseSetup = this.licenseAccessor.isLicenseSetup();
        urlBuilder.addToQueryString(TC_LICENSE_SETUP_QUERY_PARAM_KEY, Boolean.toString(licenseSetup));
    }

    public boolean shouldDisplay(QueryParams queryParams) {
        String isLicenseSetupQueryParam = queryParams.get(TC_LICENSE_SETUP_QUERY_PARAM_KEY);
        if (StringUtils.isBlank(isLicenseSetupQueryParam)) {
            return true;
        }
        if (isLicenseSetupQueryParam.equalsIgnoreCase("true")) {
            return true;
        }
        if (isLicenseSetupQueryParam.equalsIgnoreCase("false")) {
            return false;
        }
        LOG.warn("TC license query parameter value is unknown: {}. Ignoring restriction.", (Object)isLicenseSetupQueryParam);
        return true;
    }
}

