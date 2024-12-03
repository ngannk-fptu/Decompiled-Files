/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaPersister
 *  io.micrometer.core.instrument.MeterRegistry
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.confluence.impl.bandana;

import com.atlassian.bandana.BandanaPersister;
import com.atlassian.confluence.impl.bandana.MeteredBandanaPersister;
import com.atlassian.confluence.impl.metrics.ConfluenceMicrometer;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.FactoryBean;

public final class MeteredBandanaPersisterFactoryBean
implements FactoryBean<BandanaPersister> {
    private final BandanaPersister delegate;
    private final MeterRegistry micrometer;

    public MeteredBandanaPersisterFactoryBean(BandanaPersister delegate, MeterRegistry micrometer) {
        this.delegate = delegate;
        this.micrometer = micrometer;
    }

    public BandanaPersister getObject() {
        if (ConfluenceMicrometer.isMicrometerEnabled()) {
            return new MeteredBandanaPersister(this.delegate, this.micrometer);
        }
        return this.delegate;
    }

    public Class<?> getObjectType() {
        return BandanaPersister.class;
    }
}

