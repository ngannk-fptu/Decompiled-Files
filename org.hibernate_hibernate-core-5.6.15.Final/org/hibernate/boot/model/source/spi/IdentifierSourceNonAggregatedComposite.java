/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.List;
import org.hibernate.boot.model.source.spi.CompositeIdentifierSource;
import org.hibernate.boot.model.source.spi.EmbeddableSource;
import org.hibernate.boot.model.source.spi.SingularAttributeSource;

public interface IdentifierSourceNonAggregatedComposite
extends CompositeIdentifierSource {
    public List<SingularAttributeSource> getAttributeSourcesMakingUpIdentifier();

    public EmbeddableSource getIdClassSource();
}

