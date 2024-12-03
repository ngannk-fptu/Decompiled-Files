/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.webresource.api.prebake.Coordinate
 *  com.atlassian.webresource.api.prebake.DimensionAwareUrlReadingCondition
 *  com.atlassian.webresource.api.prebake.Dimensions
 *  javax.annotation.Nonnull
 */
package com.atlassian.zdu;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.DimensionAwareUrlReadingCondition;
import com.atlassian.webresource.api.prebake.Dimensions;
import com.atlassian.zdu.LicenseService;
import com.atlassian.zdu.api.ZduService;
import com.atlassian.zdu.rest.dto.ClusterState;
import java.util.Map;
import javax.annotation.Nonnull;

public class LoadZduResourcesCondition
implements DimensionAwareUrlReadingCondition {
    private static final String ZDU_MODE_QUERY_PARAM = "isZduModeAvailable";
    private final ZduService zduService;
    private final PermissionEnforcer permissionEnforcer;
    private final LicenseService licenseService;

    public LoadZduResourcesCondition(@Nonnull LicenseService licenseService, @Nonnull PermissionEnforcer permissionEnforcer, @Nonnull ZduService zduService) {
        this.licenseService = licenseService;
        this.permissionEnforcer = permissionEnforcer;
        this.zduService = zduService;
    }

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        if (this.isZduModeAvailable()) {
            urlBuilder.addToQueryString(ZDU_MODE_QUERY_PARAM, String.valueOf(true));
        }
    }

    private boolean isZduModeAvailable() {
        return this.licenseService.isDataCenter() && this.permissionEnforcer.isSystemAdmin() && this.zduService.getState() != ClusterState.STABLE;
    }

    public boolean shouldDisplay(QueryParams queryParams) {
        return Boolean.valueOf(queryParams.get(ZDU_MODE_QUERY_PARAM));
    }

    public Dimensions computeDimensions() {
        return Dimensions.empty().andExactly(ZDU_MODE_QUERY_PARAM, new String[]{String.valueOf(true)}).andAbsent(ZDU_MODE_QUERY_PARAM);
    }

    public void addToUrl(UrlBuilder urlBuilder, Coordinate coordinate) {
        coordinate.copyTo(urlBuilder, ZDU_MODE_QUERY_PARAM);
    }
}

