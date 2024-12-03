/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.pocketknife.api.querydsl.configuration;

import com.atlassian.annotations.PublicApi;
import com.atlassian.pocketknife.spi.querydsl.configuration.ConfigurationEnricher;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@PublicApi
public interface ConfigurationEnrichment {
    public ConfigurationEnricher getEnricher();

    public void setEnricher(ConfigurationEnricher var1);
}

