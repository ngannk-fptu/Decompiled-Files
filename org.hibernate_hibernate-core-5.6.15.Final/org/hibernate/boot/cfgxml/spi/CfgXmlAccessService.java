/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.cfgxml.spi;

import org.hibernate.boot.cfgxml.spi.LoadedConfig;
import org.hibernate.service.Service;

public interface CfgXmlAccessService
extends Service {
    public static final String LOADED_CONFIG_KEY = "hibernate.boot.CfgXmlAccessService.key";

    public LoadedConfig getAggregatedConfig();
}

