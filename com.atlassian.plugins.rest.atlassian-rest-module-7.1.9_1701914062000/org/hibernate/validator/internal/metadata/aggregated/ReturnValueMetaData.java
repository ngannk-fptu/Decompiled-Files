/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ElementKind
 *  javax.validation.metadata.ReturnValueDescriptor
 */
package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.validation.ElementKind;
import javax.validation.metadata.ReturnValueDescriptor;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.aggregated.AbstractConstraintMetaData;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.descriptor.ReturnValueDescriptorImpl;
import org.hibernate.validator.internal.metadata.facets.Cascadable;
import org.hibernate.validator.internal.metadata.facets.Validatable;

public class ReturnValueMetaData
extends AbstractConstraintMetaData
implements Validatable,
Cascadable {
    private static final String RETURN_VALUE_NODE_NAME = null;
    private final List<Cascadable> cascadables = this.isCascading() ? Collections.singletonList(this) : Collections.emptyList();
    private final CascadingMetaData cascadingMetaData;

    public ReturnValueMetaData(Type type, Set<MetaConstraint<?>> constraints, Set<MetaConstraint<?>> containerElementsConstraints, CascadingMetaData cascadingMetaData) {
        super(RETURN_VALUE_NODE_NAME, type, constraints, containerElementsConstraints, cascadingMetaData.isMarkedForCascadingOnAnnotatedObjectOrContainerElements(), !constraints.isEmpty() || containerElementsConstraints.isEmpty() || cascadingMetaData.isMarkedForCascadingOnAnnotatedObjectOrContainerElements());
        this.cascadingMetaData = cascadingMetaData;
    }

    @Override
    public Iterable<Cascadable> getCascadables() {
        return this.cascadables;
    }

    @Override
    public boolean hasCascadables() {
        return !this.cascadables.isEmpty();
    }

    @Override
    public ElementType getElementType() {
        return ElementType.METHOD;
    }

    public ReturnValueDescriptor asDescriptor(boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        return new ReturnValueDescriptorImpl(this.getType(), this.asDescriptors(this.getDirectConstraints()), this.asContainerElementTypeDescriptors(this.getContainerElementsConstraints(), this.cascadingMetaData, defaultGroupSequenceRedefined, defaultGroupSequence), this.cascadingMetaData.isCascading(), defaultGroupSequenceRedefined, defaultGroupSequence, this.cascadingMetaData.getGroupConversionDescriptors());
    }

    @Override
    public Object getValue(Object parent) {
        return parent;
    }

    @Override
    public Type getCascadableType() {
        return this.getType();
    }

    @Override
    public void appendTo(PathImpl path) {
        path.addReturnValueNode();
    }

    @Override
    public CascadingMetaData getCascadingMetaData() {
        return this.cascadingMetaData;
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.RETURN_VALUE;
    }
}

