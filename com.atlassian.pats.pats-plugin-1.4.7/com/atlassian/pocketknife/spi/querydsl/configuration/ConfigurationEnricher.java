/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.pocketknife.spi.querydsl.configuration;

import com.atlassian.annotations.PublicSpi;
import com.querydsl.sql.Configuration;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@PublicSpi
public interface ConfigurationEnricher {
    public Configuration enrich(Configuration var1);
}

