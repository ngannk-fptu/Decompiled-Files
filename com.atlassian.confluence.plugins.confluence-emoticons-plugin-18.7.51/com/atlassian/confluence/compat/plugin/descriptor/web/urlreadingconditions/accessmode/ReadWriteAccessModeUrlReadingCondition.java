/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.prebake.Coordinate
 *  com.atlassian.webresource.api.prebake.DimensionAwareUrlReadingCondition
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.confluence.compat.plugin.descriptor.web.urlreadingconditions.accessmode;

import com.atlassian.confluence.compat.api.service.accessmode.AccessModeCompatService;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.DimensionAwareUrlReadingCondition;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.Map;

public class ReadWriteAccessModeUrlReadingCondition
implements DimensionAwareUrlReadingCondition {
    private final AccessModeCompatService accessModeCompatService;

    public ReadWriteAccessModeUrlReadingCondition(AccessModeCompatService accessModeCompatService) {
        this.accessModeCompatService = accessModeCompatService;
    }

    public Dimensions computeDimensions() {
        return Dimensions.empty();
    }

    public void addToUrl(UrlBuilder urlBuilder, Coordinate coordinate) {
    }

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public void addToUrl(UrlBuilder urlBuilder) {
    }

    public boolean shouldDisplay(QueryParams queryParams) {
        return !this.accessModeCompatService.isReadOnlyAccessModeEnabled();
    }
}

