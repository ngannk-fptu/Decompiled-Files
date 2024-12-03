/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.List;
import org.hibernate.boot.model.source.spi.CompositeIdentifierSource;
import org.hibernate.boot.model.source.spi.MapsIdSource;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceEmbedded;

public interface IdentifierSourceAggregatedComposite
extends CompositeIdentifierSource {
    public SingularAttributeSourceEmbedded getIdentifierAttributeSource();

    public List<MapsIdSource> getMapsIdSources();
}

