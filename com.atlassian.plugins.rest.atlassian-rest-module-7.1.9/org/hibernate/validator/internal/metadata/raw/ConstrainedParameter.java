/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.raw;

import java.lang.reflect.Executable;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.raw.AbstractConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.util.CollectionHelper;

public class ConstrainedParameter
extends AbstractConstrainedElement {
    private final Executable executable;
    private final Type type;
    private final int index;

    public ConstrainedParameter(ConfigurationSource source, Executable executable, Type type, int index) {
        this(source, executable, type, index, Collections.emptySet(), Collections.emptySet(), CascadingMetaDataBuilder.nonCascading());
    }

    public ConstrainedParameter(ConfigurationSource source, Executable executable, Type type, int index, Set<MetaConstraint<?>> constraints, Set<MetaConstraint<?>> typeArgumentConstraints, CascadingMetaDataBuilder cascadingMetaDataBuilder) {
        super(source, ConstrainedElement.ConstrainedElementKind.PARAMETER, constraints, typeArgumentConstraints, cascadingMetaDataBuilder);
        this.executable = executable;
        this.type = type;
        this.index = index;
    }

    public Type getType() {
        return this.type;
    }

    public Executable getExecutable() {
        return this.executable;
    }

    public int getIndex() {
        return this.index;
    }

    public ConstrainedParameter merge(ConstrainedParameter other) {
        ConfigurationSource mergedSource = ConfigurationSource.max(this.source, other.source);
        HashSet<MetaConstraint<?>> mergedConstraints = CollectionHelper.newHashSet(this.constraints);
        mergedConstraints.addAll(other.constraints);
        HashSet mergedTypeArgumentConstraints = new HashSet(this.typeArgumentConstraints);
        mergedTypeArgumentConstraints.addAll(other.typeArgumentConstraints);
        CascadingMetaDataBuilder mergedCascadingMetaData = this.cascadingMetaDataBuilder.merge(other.cascadingMetaDataBuilder);
        return new ConstrainedParameter(mergedSource, this.executable, this.type, this.index, mergedConstraints, mergedTypeArgumentConstraints, mergedCascadingMetaData);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (MetaConstraint<?> oneConstraint : this.getConstraints()) {
            sb.append(oneConstraint.getDescriptor().getAnnotation().annotationType().getSimpleName());
            sb.append(", ");
        }
        String constraintsAsString = sb.length() > 0 ? sb.substring(0, sb.length() - 2) : sb.toString();
        return "ParameterMetaData [executable=" + this.executable + ", index=" + this.index + "], constraints=[" + constraintsAsString + "]";
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + this.index;
        result = 31 * result + (this.executable == null ? 0 : this.executable.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ConstrainedParameter other = (ConstrainedParameter)obj;
        if (this.index != other.index) {
            return false;
        }
        return !(this.executable == null ? other.executable != null : !this.executable.equals(other.executable));
    }
}

