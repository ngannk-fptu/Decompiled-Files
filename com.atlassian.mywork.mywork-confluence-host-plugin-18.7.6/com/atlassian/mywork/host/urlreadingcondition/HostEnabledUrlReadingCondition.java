/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.service.ServiceSelector
 *  com.atlassian.mywork.service.ServiceSelector$Target
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 */
package com.atlassian.mywork.host.urlreadingcondition;

import com.atlassian.mywork.service.ServiceSelector;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import java.util.Map;

public class HostEnabledUrlReadingCondition
implements UrlReadingCondition {
    private static final String IS_HOST_ENABLED_QUERY_PARAM = "hostenabled";
    private final ServiceSelector serviceSelector;

    public HostEnabledUrlReadingCondition(ServiceSelector serviceSelector) {
        this.serviceSelector = serviceSelector;
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        if (this.isHostEnabled()) {
            urlBuilder.addToQueryString(IS_HOST_ENABLED_QUERY_PARAM, String.valueOf(true));
        }
    }

    public boolean shouldDisplay(QueryParams queryParams) {
        return Boolean.valueOf(queryParams.get(IS_HOST_ENABLED_QUERY_PARAM));
    }

    private boolean isHostEnabled() {
        return this.serviceSelector.getEffectiveTarget() == ServiceSelector.Target.LOCAL;
    }
}

