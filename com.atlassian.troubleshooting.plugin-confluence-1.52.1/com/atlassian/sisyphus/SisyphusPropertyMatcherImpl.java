/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 */
package com.atlassian.sisyphus;

import com.atlassian.sisyphus.SisyphusPropertyMatcher;
import com.atlassian.sisyphus.application.properties.PropertyParser;
import com.atlassian.sisyphus.dm.PropScanResult;
import com.atlassian.sisyphus.marketplace.MarketPlaceService;
import com.atlassian.sisyphus.plugin.compatiblity.CompatibilityData;
import java.io.BufferedReader;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SisyphusPropertyMatcherImpl
implements SisyphusPropertyMatcher {
    private final MarketPlaceService marketPlaceService;

    @Inject
    public SisyphusPropertyMatcherImpl(MarketPlaceService marketPlaceService) {
        this.marketPlaceService = marketPlaceService;
    }

    @Override
    public PropScanResult match(BufferedReader reader) {
        PropertyParser parser = new PropertyParser();
        PropScanResult result = parser.parse(reader);
        return new CompatibilityData(this.marketPlaceService).updatePluginCompatibility(result);
    }
}

