/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.xml.mapping;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.xml.namespace.QName;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedParameter;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredMethod;
import org.hibernate.validator.internal.xml.mapping.AbstractConstrainedExecutableElementStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.ClassLoadingHelper;
import org.hibernate.validator.internal.xml.mapping.ConstrainedParameterStaxBuilder;
import org.hibernate.validator.internal.xml.mapping.DefaultPackageStaxBuilder;

class ConstrainedMethodStaxBuilder
extends AbstractConstrainedExecutableElementStaxBuilder {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final String METHOD_QNAME_LOCAL_PART = "method";
    private static final QName NAME_QNAME = new QName("name");

    ConstrainedMethodStaxBuilder(ClassLoadingHelper classLoadingHelper, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, DefaultPackageStaxBuilder defaultPackageStaxBuilder, AnnotationProcessingOptionsImpl annotationProcessingOptions) {
        super(classLoadingHelper, constraintHelper, typeResolutionHelper, valueExtractorManager, defaultPackageStaxBuilder, annotationProcessingOptions);
    }

    @Override
    Optional<QName> getMainAttributeValueQname() {
        return Optional.of(NAME_QNAME);
    }

    @Override
    protected String getAcceptableQName() {
        return METHOD_QNAME_LOCAL_PART;
    }

    public String getMethodName() {
        return this.mainAttributeValue;
    }

    ConstrainedExecutable build(Class<?> beanClass, List<Method> alreadyProcessedMethods) {
        Class[] parameterTypes = (Class[])this.constrainedParameterStaxBuilders.stream().map(builder -> builder.getParameterType(beanClass)).toArray(Class[]::new);
        String methodName = this.getMethodName();
        Method method = ConstrainedMethodStaxBuilder.run(GetDeclaredMethod.action(beanClass, methodName, parameterTypes));
        if (method == null) {
            throw LOG.getBeanDoesNotContainMethodException(beanClass, methodName, parameterTypes);
        }
        if (alreadyProcessedMethods.contains(method)) {
            throw LOG.getMethodIsDefinedTwiceInMappingXmlForBeanException(method, beanClass);
        }
        alreadyProcessedMethods.add(method);
        if (this.ignoreAnnotations.isPresent()) {
            this.annotationProcessingOptions.ignoreConstraintAnnotationsOnMember(method, (Boolean)this.ignoreAnnotations.get());
        }
        ArrayList<ConstrainedParameter> constrainedParameters = CollectionHelper.newArrayList(this.constrainedParameterStaxBuilders.size());
        for (int index = 0; index < this.constrainedParameterStaxBuilders.size(); ++index) {
            ConstrainedParameterStaxBuilder builder2 = (ConstrainedParameterStaxBuilder)this.constrainedParameterStaxBuilders.get(index);
            constrainedParameters.add(builder2.build(method, index));
        }
        Set<MetaConstraint<?>> crossParameterConstraints = this.getCrossParameterStaxBuilder().map(builder -> builder.build(method)).orElse(Collections.emptySet());
        HashSet returnValueConstraints = new HashSet();
        HashSet returnValueTypeArgumentConstraints = new HashSet();
        CascadingMetaDataBuilder cascadingMetaDataBuilder = this.getReturnValueStaxBuilder().map(builder -> builder.build(method, returnValueConstraints, returnValueTypeArgumentConstraints)).orElse(CascadingMetaDataBuilder.nonCascading());
        return new ConstrainedExecutable(ConfigurationSource.XML, method, constrainedParameters, crossParameterConstraints, returnValueConstraints, returnValueTypeArgumentConstraints, cascadingMetaDataBuilder);
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}

