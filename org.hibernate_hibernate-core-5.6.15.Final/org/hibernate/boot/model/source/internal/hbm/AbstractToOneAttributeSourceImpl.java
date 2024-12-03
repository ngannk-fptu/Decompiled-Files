/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.spi.AttributeSource;
import org.hibernate.boot.model.source.spi.NaturalIdMutability;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceToOne;
import org.hibernate.tuple.GenerationTiming;

public abstract class AbstractToOneAttributeSourceImpl
extends AbstractHbmSourceNode
implements SingularAttributeSourceToOne {
    private final NaturalIdMutability naturalIdMutability;

    AbstractToOneAttributeSourceImpl(MappingDocument sourceMappingDocument, NaturalIdMutability naturalIdMutability) {
        super(sourceMappingDocument);
        this.naturalIdMutability = naturalIdMutability;
    }

    @Override
    public NaturalIdMutability getNaturalIdMutability() {
        return this.naturalIdMutability;
    }

    @Override
    public boolean isSingular() {
        return true;
    }

    @Override
    public boolean isVirtualAttribute() {
        return false;
    }

    @Override
    public GenerationTiming getGenerationTiming() {
        return GenerationTiming.NEVER;
    }

    @Override
    public boolean isIgnoreNotFound() {
        return false;
    }

    @Override
    public boolean isMappedBy() {
        return false;
    }

    @Override
    public AttributeSource getAttributeSource() {
        return this;
    }

    @Override
    public boolean createForeignKeyConstraint() {
        return true;
    }
}

