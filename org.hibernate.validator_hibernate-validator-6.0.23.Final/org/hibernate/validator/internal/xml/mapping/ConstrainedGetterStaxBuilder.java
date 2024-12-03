/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.xml.mapping;

import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
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
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetMethodFromPropertyName;
import org.hibernate.validator.internal.xml.mapping.AbstractConstrainedElementStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ClassLoadingHelper;
import org.hibernate.validator.internal.xml.mapping.ContainerElementTypeConfigurationBuilder;
import org.hibernate.validator.internal.xml.mapping.DefaultPackageStaxBuilder;

class ConstrainedGetterStaxBuilder
extends AbstractConstrainedElementStaxBuilder {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final QName NAME_QNAME = new QName("name");
    private static final String GETTER_QNAME_LOCAL_PART = "getter";

    ConstrainedGetterStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder, AnnotationProcessingOptionsImpl annotationProcessingOptions) {
        super(classLoadingHelper, constraintHelper, typeResolutionHelper, valueExtractorManager, defaultPackageStaxBuilder, annotationProcessingOptions);
    }

    @Override
    Optional<QName> getMainAttributeValueQname() {
        return Optional.of(NAME_QNAME);
    }

    @Override
    protected String getAcceptableQName() {
        return GETTER_QNAME_LOCAL_PART;
    }

    ConstrainedExecutable build(Class<?> beanClass, List<String> alreadyProcessedGetterNames) {
        if (alreadyProcessedGetterNames.contains(this.mainAttributeValue)) {
            throw LOG.getIsDefinedTwiceInMappingXmlForBeanException(this.mainAttributeValue, beanClass);
        }
        alreadyProcessedGetterNames.add(this.mainAttributeValue);
        Method getter = ConstrainedGetterStaxBuilder.findGetter(beanClass, this.mainAttributeValue);
        ConstraintLocation constraintLocation = ConstraintLocation.forGetter(beanClass, getter);
        Set<MetaConstraint<?>> metaConstraints = this.constraintTypeStaxBuilders.stream().map(builder -> builder.build(constraintLocation, ElementType.METHOD, null)).collect(Collectors.toSet());
        ContainerElementTypeConfigurationBuilder.ContainerElementTypeConfiguration containerElementTypeConfiguration = this.getContainerElementTypeConfiguration(ReflectionHelper.typeOf(getter), constraintLocation);
        ConstrainedExecutable constrainedGetter = new ConstrainedExecutable(ConfigurationSource.XML, getter, Collections.emptyList(), Collections.emptySet(), metaConstraints, containerElementTypeConfiguration.getMetaConstraints(), this.getCascadingMetaData(containerElementTypeConfiguration.getTypeParametersCascadingMetaData(), ReflectionHelper.typeOf(getter)));
        if (this.ignoreAnnotations.isPresent()) {
            this.annotationProcessingOptions.ignoreConstraintAnnotationsOnMember(getter, (Boolean)this.ignoreAnnotations.get());
        }
        return constrainedGetter;
    }

    private static Method findGetter(Class<?> beanClass, String getterName) {
        Method method = ConstrainedGetterStaxBuilder.run(GetMethodFromPropertyName.action(beanClass, getterName));
        if (method == null) {
            throw LOG.getBeanDoesNotContainThePropertyException(beanClass, getterName);
        }
        return method;
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}

