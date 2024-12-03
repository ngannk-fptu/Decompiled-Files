/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ElementKind
 *  javax.validation.metadata.ParameterDescriptor
 */
package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.annotation.ElementType;
import java.lang.reflect.Executable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import javax.validation.ElementKind;
import javax.validation.metadata.ParameterDescriptor;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.AbstractConstraintMetaData;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.aggregated.MetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.descriptor.ParameterDescriptorImpl;
import org.hibernate.validator.internal.metadata.facets.Cascadable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedParameter;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.TypeResolutionHelper;

public class ParameterMetaData
extends AbstractConstraintMetaData
implements Cascadable {
    private final int index;
    private final CascadingMetaData cascadingMetaData;

    private ParameterMetaData(int index, String name, Type type, Set<MetaConstraint<?>> constraints, Set<MetaConstraint<?>> containerElementsConstraints, CascadingMetaData cascadingMetaData) {
        super(name, type, constraints, containerElementsConstraints, cascadingMetaData.isMarkedForCascadingOnAnnotatedObjectOrContainerElements(), !constraints.isEmpty() || !containerElementsConstraints.isEmpty() || cascadingMetaData.isMarkedForCascadingOnAnnotatedObjectOrContainerElements());
        this.index = index;
        this.cascadingMetaData = cascadingMetaData;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.PARAMETER;
    }

    public ParameterDescriptor asDescriptor(boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        return new ParameterDescriptorImpl(this.getType(), this.index, this.getName(), this.asDescriptors(this.getDirectConstraints()), this.asContainerElementTypeDescriptors(this.getContainerElementsConstraints(), this.cascadingMetaData, defaultGroupSequenceRedefined, defaultGroupSequence), this.cascadingMetaData.isCascading(), defaultGroupSequenceRedefined, defaultGroupSequence, this.cascadingMetaData.getGroupConversionDescriptors());
    }

    @Override
    public Object getValue(Object parent) {
        return ((Object[])parent)[this.getIndex()];
    }

    @Override
    public Type getCascadableType() {
        return this.getType();
    }

    @Override
    public void appendTo(PathImpl path) {
        path.addParameterNode(this.getName(), this.getIndex());
    }

    @Override
    public CascadingMetaData getCascadingMetaData() {
        return this.cascadingMetaData;
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.PARAMETER;
    }

    public static class Builder
    extends MetaDataBuilder {
        private final ExecutableParameterNameProvider parameterNameProvider;
        private final Type parameterType;
        private final int parameterIndex;
        private Executable executableForNameRetrieval;
        private CascadingMetaDataBuilder cascadingMetaDataBuilder;

        public Builder(Class<?> beanClass, ConstrainedParameter constrainedParameter, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, ExecutableParameterNameProvider parameterNameProvider) {
            super(beanClass, constraintHelper, typeResolutionHelper, valueExtractorManager);
            this.parameterNameProvider = parameterNameProvider;
            this.parameterType = constrainedParameter.getType();
            this.parameterIndex = constrainedParameter.getIndex();
            this.add(constrainedParameter);
        }

        @Override
        public boolean accepts(ConstrainedElement constrainedElement) {
            if (constrainedElement.getKind() != ConstrainedElement.ConstrainedElementKind.PARAMETER) {
                return false;
            }
            return ((ConstrainedParameter)constrainedElement).getIndex() == this.parameterIndex;
        }

        @Override
        public void add(ConstrainedElement constrainedElement) {
            super.add(constrainedElement);
            ConstrainedParameter newConstrainedParameter = (ConstrainedParameter)constrainedElement;
            this.cascadingMetaDataBuilder = this.cascadingMetaDataBuilder == null ? newConstrainedParameter.getCascadingMetaDataBuilder() : this.cascadingMetaDataBuilder.merge(newConstrainedParameter.getCascadingMetaDataBuilder());
            if (this.executableForNameRetrieval == null || newConstrainedParameter.getExecutable().getDeclaringClass().isAssignableFrom(this.executableForNameRetrieval.getDeclaringClass())) {
                this.executableForNameRetrieval = newConstrainedParameter.getExecutable();
            }
        }

        @Override
        public ParameterMetaData build() {
            return new ParameterMetaData(this.parameterIndex, this.parameterNameProvider.getParameterNames(this.executableForNameRetrieval).get(this.parameterIndex), this.parameterType, this.adaptOriginsAndImplicitGroups(this.getDirectConstraints()), this.adaptOriginsAndImplicitGroups(this.getContainerElementConstraints()), this.cascadingMetaDataBuilder.build(this.valueExtractorManager, this.executableForNameRetrieval));
        }
    }
}

