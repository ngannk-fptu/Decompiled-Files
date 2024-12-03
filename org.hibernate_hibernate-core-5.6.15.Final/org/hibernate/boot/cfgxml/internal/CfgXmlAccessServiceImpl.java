/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.cfgxml.internal;

import java.util.Map;
import org.hibernate.boot.cfgxml.spi.CfgXmlAccessService;
import org.hibernate.boot.cfgxml.spi.LoadedConfig;

public class CfgXmlAccessServiceImpl
implements CfgXmlAccessService {
    private final LoadedConfig aggregatedCfgXml;

    public CfgXmlAccessServiceImpl(Map configurationValues) {
        this.aggregatedCfgXml = (LoadedConfig)configurationValues.get("hibernate.boot.CfgXmlAccessService.key");
    }

    @Override
    public LoadedConfig getAggregatedConfig() {
        return this.aggregatedCfgXml;
    }
}

