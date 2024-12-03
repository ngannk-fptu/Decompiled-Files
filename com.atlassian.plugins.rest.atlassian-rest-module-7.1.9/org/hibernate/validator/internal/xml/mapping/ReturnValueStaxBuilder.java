/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.xml.mapping;

import java.lang.reflect.Executable;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.xml.mapping.AbstractConstrainedElementStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ClassLoadingHelper;
import org.hibernate.validator.internal.xml.mapping.ContainerElementTypeConfigurationBuilder;
import org.hibernate.validator.internal.xml.mapping.DefaultPackageStaxBuilder;

class ReturnValueStaxBuilder
extends AbstractConstrainedElementStaxBuilder {
    private static final String RETURN_VALUE_QNAME_LOCAL_PART = "return-value";

    ReturnValueStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder, AnnotationProcessingOptionsImpl annotationProcessingOptions) {
        super(classLoadingHelper, constraintHelper, typeResolutionHelper, valueExtractorManager, defaultPackageStaxBuilder, annotationProcessingOptions);
    }

    @Override
    Optional<QName> getMainAttributeValueQname() {
        return Optional.empty();
    }

    @Override
    protected String getAcceptableQName() {
        return RETURN_VALUE_QNAME_LOCAL_PART;
    }

    CascadingMetaDataBuilder build(Executable executable, Set<MetaConstraint<?>> returnValueConstraints, Set<MetaConstraint<?>> returnValueTypeArgumentConstraints) {
        ConstraintLocation constraintLocation = ConstraintLocation.forReturnValue(executable);
        returnValueConstraints.addAll(this.constraintTypeStaxBuilders.stream().map(builder -> builder.build(constraintLocation, ExecutableHelper.getElementType(executable), ConstraintDescriptorImpl.ConstraintType.GENERIC)).collect(Collectors.toSet()));
        ContainerElementTypeConfigurationBuilder.ContainerElementTypeConfiguration containerElementTypeConfiguration = this.getContainerElementTypeConfiguration(ReflectionHelper.typeOf(executable), constraintLocation);
        returnValueTypeArgumentConstraints.addAll(containerElementTypeConfiguration.getMetaConstraints());
        if (this.ignoreAnnotations.isPresent()) {
            this.annotationProcessingOptions.ignoreConstraintAnnotationsForReturnValue(executable, (Boolean)this.ignoreAnnotations.get());
        }
        return this.getCascadingMetaData(containerElementTypeConfiguration.getTypeParametersCascadingMetaData(), ReflectionHelper.typeOf(executable));
    }
}

