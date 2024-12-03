/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.AttributeSource;
import org.hibernate.boot.model.source.spi.NaturalIdMutability;
import org.hibernate.boot.model.source.spi.SingularAttributeNature;
import org.hibernate.tuple.GenerationTiming;

public interface SingularAttributeSource
extends AttributeSource {
    public boolean isVirtualAttribute();

    public SingularAttributeNature getSingularAttributeNature();

    public GenerationTiming getGenerationTiming();

    public Boolean isInsertable();

    public Boolean isUpdatable();

    public boolean isBytecodeLazy();

    public NaturalIdMutability getNaturalIdMutability();
}

