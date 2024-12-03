/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.stereotype.Component
 */
package com.atlassian.pocketknife.internal.querydsl.configuration;

import com.atlassian.pocketknife.api.querydsl.configuration.ConfigurationEnrichment;
import com.atlassian.pocketknife.spi.querydsl.configuration.ConfigurationEnricher;
import com.google.common.base.Preconditions;
import com.querydsl.sql.Configuration;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.stereotype.Component;

@Component
@ParametersAreNonnullByDefault
public class ConfigurationEnrichmentImpl
implements ConfigurationEnrichment {
    private final AtomicReference<ConfigurationEnricher> enricher = new AtomicReference<NoopEnricher>(new NoopEnricher());

    @Override
    public ConfigurationEnricher getEnricher() {
        return this.enricher.get();
    }

    @Override
    public void setEnricher(ConfigurationEnricher enricher) {
        Preconditions.checkNotNull((Object)enricher);
        this.enricher.set(enricher);
    }

    @ParametersAreNonnullByDefault
    private class NoopEnricher
    implements ConfigurationEnricher {
        private NoopEnricher() {
        }

        @Override
        public Configuration enrich(Configuration configuration) {
            return configuration;
        }
    }
}

