/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.xml.mapping;

import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedField;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredField;
import org.hibernate.validator.internal.xml.mapping.AbstractConstrainedElementStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ClassLoadingHelper;
import org.hibernate.validator.internal.xml.mapping.ContainerElementTypeConfigurationBuilder;
import org.hibernate.validator.internal.xml.mapping.DefaultPackageStaxBuilder;

class ConstrainedFieldStaxBuilder
extends AbstractConstrainedElementStaxBuilder {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final String FIELD_QNAME_LOCAL_PART = "field";
    private static final QName NAME_QNAME = new QName("name");

    ConstrainedFieldStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder, AnnotationProcessingOptionsImpl annotationProcessingOptions) {
        super(classLoadingHelper, constraintHelper, typeResolutionHelper, valueExtractorManager, defaultPackageStaxBuilder, annotationProcessingOptions);
    }

    @Override
    Optional<QName> getMainAttributeValueQname() {
        return Optional.of(NAME_QNAME);
    }

    @Override
    protected String getAcceptableQName() {
        return FIELD_QNAME_LOCAL_PART;
    }

    ConstrainedField build(Class<?> beanClass, List<String> alreadyProcessedFieldNames) {
        if (alreadyProcessedFieldNames.contains(this.mainAttributeValue)) {
            throw LOG.getIsDefinedTwiceInMappingXmlForBeanException(this.mainAttributeValue, beanClass);
        }
        alreadyProcessedFieldNames.add(this.mainAttributeValue);
        Field field = ConstrainedFieldStaxBuilder.findField(beanClass, this.mainAttributeValue);
        ConstraintLocation constraintLocation = ConstraintLocation.forField(field);
        Set<MetaConstraint<?>> metaConstraints = this.constraintTypeStaxBuilders.stream().map(builder -> builder.build(constraintLocation, ElementType.FIELD, null)).collect(Collectors.toSet());
        ContainerElementTypeConfigurationBuilder.ContainerElementTypeConfiguration containerElementTypeConfiguration = this.getContainerElementTypeConfiguration(ReflectionHelper.typeOf(field), constraintLocation);
        ConstrainedField constrainedField = new ConstrainedField(ConfigurationSource.XML, field, metaConstraints, containerElementTypeConfiguration.getMetaConstraints(), this.getCascadingMetaData(containerElementTypeConfiguration.getTypeParametersCascadingMetaData(), ReflectionHelper.typeOf(field)));
        if (this.ignoreAnnotations.isPresent()) {
            this.annotationProcessingOptions.ignoreConstraintAnnotationsOnMember(field, (Boolean)this.ignoreAnnotations.get());
        }
        return constrainedField;
    }

    private static Field findField(Class<?> beanClass, String fieldName) {
        Field field = ConstrainedFieldStaxBuilder.run(GetDeclaredField.action(beanClass, fieldName));
        if (field == null) {
            throw LOG.getBeanDoesNotContainTheFieldException(beanClass, fieldName);
        }
        return field;
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}

