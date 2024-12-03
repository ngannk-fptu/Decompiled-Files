/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.raw;

import java.util.Set;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;

public interface ConstrainedElement
extends Iterable<MetaConstraint<?>> {
    public ConstrainedElementKind getKind();

    public Set<MetaConstraint<?>> getConstraints();

    public Set<MetaConstraint<?>> getTypeArgumentConstraints();

    public CascadingMetaDataBuilder getCascadingMetaDataBuilder();

    public boolean isConstrained();

    public ConfigurationSource getSource();

    public static enum ConstrainedElementKind {
        TYPE,
        FIELD,
        CONSTRUCTOR,
        METHOD,
        PARAMETER;

    }
}

