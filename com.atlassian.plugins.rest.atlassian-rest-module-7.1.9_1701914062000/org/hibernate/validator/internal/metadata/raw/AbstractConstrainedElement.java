/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.raw;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.util.CollectionHelper;

public abstract class AbstractConstrainedElement
implements ConstrainedElement {
    private final ConstrainedElement.ConstrainedElementKind kind;
    protected final ConfigurationSource source;
    protected final Set<MetaConstraint<?>> constraints;
    protected final CascadingMetaDataBuilder cascadingMetaDataBuilder;
    protected final Set<MetaConstraint<?>> typeArgumentConstraints;

    public AbstractConstrainedElement(ConfigurationSource source, ConstrainedElement.ConstrainedElementKind kind, Set<MetaConstraint<?>> constraints, Set<MetaConstraint<?>> typeArgumentConstraints, CascadingMetaDataBuilder cascadingMetaDataBuilder) {
        this.kind = kind;
        this.source = source;
        this.constraints = constraints != null ? CollectionHelper.toImmutableSet(constraints) : Collections.emptySet();
        this.typeArgumentConstraints = typeArgumentConstraints != null ? CollectionHelper.toImmutableSet(typeArgumentConstraints) : Collections.emptySet();
        this.cascadingMetaDataBuilder = cascadingMetaDataBuilder;
    }

    @Override
    public ConstrainedElement.ConstrainedElementKind getKind() {
        return this.kind;
    }

    @Override
    public Iterator<MetaConstraint<?>> iterator() {
        return this.constraints.iterator();
    }

    @Override
    public Set<MetaConstraint<?>> getConstraints() {
        return this.constraints;
    }

    @Override
    public Set<MetaConstraint<?>> getTypeArgumentConstraints() {
        return this.typeArgumentConstraints;
    }

    @Override
    public CascadingMetaDataBuilder getCascadingMetaDataBuilder() {
        return this.cascadingMetaDataBuilder;
    }

    @Override
    public boolean isConstrained() {
        return this.cascadingMetaDataBuilder.isMarkedForCascadingOnAnnotatedObjectOrContainerElements() || this.cascadingMetaDataBuilder.hasGroupConversionsOnAnnotatedObjectOrContainerElements() || !this.constraints.isEmpty() || !this.typeArgumentConstraints.isEmpty();
    }

    @Override
    public ConfigurationSource getSource() {
        return this.source;
    }

    public String toString() {
        return "AbstractConstrainedElement [kind=" + (Object)((Object)this.kind) + ", source=" + (Object)((Object)this.source) + ", constraints=" + this.constraints + ", cascadingMetaDataBuilder=" + this.cascadingMetaDataBuilder + "]";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.source == null ? 0 : this.source.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        AbstractConstrainedElement other = (AbstractConstrainedElement)obj;
        return this.source == other.source;
    }
}

