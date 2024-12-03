/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.GroupSequence
 *  javax.validation.Valid
 *  javax.validation.groups.ConvertGroup
 *  javax.validation.groups.ConvertGroup$List
 */
package org.hibernate.validator.internal.metadata.provider;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;
import org.hibernate.validator.group.GroupSequenceProvider;
import org.hibernate.validator.internal.engine.valueextraction.ArrayElement;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.core.MetaConstraints;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.metadata.provider.MetaDataProvider;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedField;
import org.hibernate.validator.internal.metadata.raw.ConstrainedParameter;
import org.hibernate.validator.internal.metadata.raw.ConstrainedType;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.annotation.ConstraintAnnotationDescriptor;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.logging.Messages;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredConstructors;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredFields;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredMethods;
import org.hibernate.validator.internal.util.privilegedactions.GetMethods;
import org.hibernate.validator.internal.util.privilegedactions.NewInstance;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

public class AnnotationMetaDataProvider
implements MetaDataProvider {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final Annotation[] EMPTY_PARAMETER_ANNOTATIONS = new Annotation[0];
    private final ConstraintHelper constraintHelper;
    private final TypeResolutionHelper typeResolutionHelper;
    private final AnnotationProcessingOptions annotationProcessingOptions;
    private final ValueExtractorManager valueExtractorManager;
    private final BeanConfiguration<Object> objectBeanConfiguration;

    public AnnotationMetaDataProvider(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, AnnotationProcessingOptions annotationProcessingOptions) {
        this.constraintHelper = constraintHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.annotationProcessingOptions = annotationProcessingOptions;
        this.objectBeanConfiguration = this.retrieveBeanConfiguration(Object.class);
    }

    @Override
    public AnnotationProcessingOptions getAnnotationProcessingOptions() {
        return new AnnotationProcessingOptionsImpl();
    }

    public <T> BeanConfiguration<T> getBeanConfiguration(Class<T> beanClass) {
        if (Object.class.equals(beanClass)) {
            return this.objectBeanConfiguration;
        }
        return this.retrieveBeanConfiguration(beanClass);
    }

    private <T> BeanConfiguration<T> retrieveBeanConfiguration(Class<T> beanClass) {
        Set<ConstrainedElement> constrainedElements = this.getFieldMetaData(beanClass);
        constrainedElements.addAll(this.getMethodMetaData(beanClass));
        constrainedElements.addAll(this.getConstructorMetaData(beanClass));
        Set<MetaConstraint<?>> classLevelConstraints = this.getClassLevelConstraints(beanClass);
        if (!classLevelConstraints.isEmpty()) {
            ConstrainedType classLevelMetaData = new ConstrainedType(ConfigurationSource.ANNOTATION, beanClass, classLevelConstraints);
            constrainedElements.add(classLevelMetaData);
        }
        return new BeanConfiguration<T>(ConfigurationSource.ANNOTATION, beanClass, constrainedElements, this.getDefaultGroupSequence(beanClass), this.getDefaultGroupSequenceProvider(beanClass));
    }

    private List<Class<?>> getDefaultGroupSequence(Class<?> beanClass) {
        GroupSequence groupSequenceAnnotation = beanClass.getAnnotation(GroupSequence.class);
        return groupSequenceAnnotation != null ? Arrays.asList(groupSequenceAnnotation.value()) : null;
    }

    private <T> DefaultGroupSequenceProvider<? super T> getDefaultGroupSequenceProvider(Class<T> beanClass) {
        GroupSequenceProvider groupSequenceProviderAnnotation = beanClass.getAnnotation(GroupSequenceProvider.class);
        if (groupSequenceProviderAnnotation != null) {
            Class<? extends DefaultGroupSequenceProvider<?>> providerClass = groupSequenceProviderAnnotation.value();
            return this.newGroupSequenceProviderClassInstance(beanClass, providerClass);
        }
        return null;
    }

    private <T> DefaultGroupSequenceProvider<? super T> newGroupSequenceProviderClassInstance(Class<T> beanClass, Class<? extends DefaultGroupSequenceProvider<? super T>> providerClass) {
        Method[] providerMethods;
        for (Method method : providerMethods = this.run(GetMethods.action(providerClass))) {
            Class<?>[] paramTypes = method.getParameterTypes();
            if (!"getValidationGroups".equals(method.getName()) || method.isBridge() || paramTypes.length != 1 || !paramTypes[0].isAssignableFrom(beanClass)) continue;
            return this.run(NewInstance.action(providerClass, "the default group sequence provider"));
        }
        throw LOG.getWrongDefaultGroupSequenceProviderTypeException(beanClass);
    }

    private Set<MetaConstraint<?>> getClassLevelConstraints(Class<?> clazz) {
        if (this.annotationProcessingOptions.areClassLevelConstraintsIgnoredFor(clazz)) {
            return Collections.emptySet();
        }
        HashSet<MetaConstraint<?>> classLevelConstraints = CollectionHelper.newHashSet();
        List<ConstraintDescriptorImpl<?>> classMetaData = this.findClassLevelConstraints(clazz);
        ConstraintLocation location = ConstraintLocation.forClass(clazz);
        for (ConstraintDescriptorImpl<?> constraintDescription : classMetaData) {
            classLevelConstraints.add(MetaConstraints.create(this.typeResolutionHelper, this.valueExtractorManager, constraintDescription, location));
        }
        return classLevelConstraints;
    }

    private Set<ConstrainedElement> getFieldMetaData(Class<?> beanClass) {
        HashSet<ConstrainedElement> propertyMetaData = CollectionHelper.newHashSet();
        for (Field field : this.run(GetDeclaredFields.action(beanClass))) {
            if (Modifier.isStatic(field.getModifiers()) || this.annotationProcessingOptions.areMemberConstraintsIgnoredFor(field) || field.isSynthetic()) continue;
            propertyMetaData.add(this.findPropertyMetaData(field));
        }
        return propertyMetaData;
    }

    private ConstrainedField findPropertyMetaData(Field field) {
        Set<MetaConstraint<?>> constraints = this.convertToMetaConstraints(this.findConstraints(field, ElementType.FIELD), field);
        CascadingMetaDataBuilder cascadingMetaDataBuilder = this.findCascadingMetaData(field);
        Set<MetaConstraint<?>> typeArgumentsConstraints = this.findTypeAnnotationConstraints(field);
        return new ConstrainedField(ConfigurationSource.ANNOTATION, field, constraints, typeArgumentsConstraints, cascadingMetaDataBuilder);
    }

    private Set<MetaConstraint<?>> convertToMetaConstraints(List<ConstraintDescriptorImpl<?>> constraintDescriptors, Field field) {
        if (constraintDescriptors.isEmpty()) {
            return Collections.emptySet();
        }
        HashSet<MetaConstraint<?>> constraints = CollectionHelper.newHashSet();
        ConstraintLocation location = ConstraintLocation.forField(field);
        for (ConstraintDescriptorImpl<?> constraintDescription : constraintDescriptors) {
            constraints.add(MetaConstraints.create(this.typeResolutionHelper, this.valueExtractorManager, constraintDescription, location));
        }
        return constraints;
    }

    private Set<ConstrainedExecutable> getConstructorMetaData(Class<?> clazz) {
        Executable[] declaredConstructors = this.run(GetDeclaredConstructors.action(clazz));
        return this.getMetaData(declaredConstructors);
    }

    private Set<ConstrainedExecutable> getMethodMetaData(Class<?> clazz) {
        Executable[] declaredMethods = this.run(GetDeclaredMethods.action(clazz));
        return this.getMetaData(declaredMethods);
    }

    private Set<ConstrainedExecutable> getMetaData(Executable[] executableElements) {
        HashSet<ConstrainedExecutable> executableMetaData = CollectionHelper.newHashSet();
        for (Executable executable : executableElements) {
            if (Modifier.isStatic(executable.getModifiers()) || executable.isSynthetic()) continue;
            executableMetaData.add(this.findExecutableMetaData(executable));
        }
        return executableMetaData;
    }

    private ConstrainedExecutable findExecutableMetaData(Executable executable) {
        CascadingMetaDataBuilder cascadingMetaDataBuilder;
        Set<MetaConstraint<?>> typeArgumentsConstraints;
        Set<MetaConstraint<?>> returnValueConstraints;
        List<ConstrainedParameter> parameterConstraints = this.getParameterMetaData(executable);
        Map<ConstraintDescriptorImpl.ConstraintType, List<ConstraintDescriptorImpl>> executableConstraints = this.findConstraints(executable, ExecutableHelper.getElementType(executable)).stream().collect(Collectors.groupingBy(ConstraintDescriptorImpl::getConstraintType));
        Set<Object> crossParameterConstraints = this.annotationProcessingOptions.areCrossParameterConstraintsIgnoredFor(executable) ? Collections.emptySet() : this.convertToMetaConstraints(executableConstraints.get((Object)ConstraintDescriptorImpl.ConstraintType.CROSS_PARAMETER), executable);
        if (this.annotationProcessingOptions.areReturnValueConstraintsIgnoredFor(executable)) {
            returnValueConstraints = Collections.emptySet();
            typeArgumentsConstraints = Collections.emptySet();
            cascadingMetaDataBuilder = CascadingMetaDataBuilder.nonCascading();
        } else {
            AnnotatedType annotatedReturnType = executable.getAnnotatedReturnType();
            typeArgumentsConstraints = this.findTypeAnnotationConstraints(executable, annotatedReturnType);
            returnValueConstraints = this.convertToMetaConstraints(executableConstraints.get((Object)ConstraintDescriptorImpl.ConstraintType.GENERIC), executable);
            cascadingMetaDataBuilder = this.findCascadingMetaData(executable, annotatedReturnType);
        }
        return new ConstrainedExecutable(ConfigurationSource.ANNOTATION, executable, parameterConstraints, crossParameterConstraints, returnValueConstraints, typeArgumentsConstraints, cascadingMetaDataBuilder);
    }

    private Set<MetaConstraint<?>> convertToMetaConstraints(List<ConstraintDescriptorImpl<?>> constraintsDescriptors, Executable executable) {
        if (constraintsDescriptors == null) {
            return Collections.emptySet();
        }
        HashSet<MetaConstraint<?>> constraints = CollectionHelper.newHashSet();
        ConstraintLocation returnValueLocation = ConstraintLocation.forReturnValue(executable);
        ConstraintLocation crossParameterLocation = ConstraintLocation.forCrossParameter(executable);
        for (ConstraintDescriptorImpl<?> constraintDescriptor : constraintsDescriptors) {
            ConstraintLocation location = constraintDescriptor.getConstraintType() == ConstraintDescriptorImpl.ConstraintType.GENERIC ? returnValueLocation : crossParameterLocation;
            constraints.add(MetaConstraints.create(this.typeResolutionHelper, this.valueExtractorManager, constraintDescriptor, location));
        }
        return constraints;
    }

    private List<ConstrainedParameter> getParameterMetaData(Executable executable) {
        if (executable.getParameterCount() == 0) {
            return Collections.emptyList();
        }
        Parameter[] parameters = executable.getParameters();
        ArrayList<ConstrainedParameter> metaData = new ArrayList<ConstrainedParameter>(parameters.length);
        int i = 0;
        for (Parameter parameter : parameters) {
            Annotation[] parameterAnnotations;
            try {
                parameterAnnotations = parameter.getAnnotations();
            }
            catch (ArrayIndexOutOfBoundsException ex) {
                LOG.warn(Messages.MESSAGES.constraintOnConstructorOfNonStaticInnerClass(), ex);
                parameterAnnotations = EMPTY_PARAMETER_ANNOTATIONS;
            }
            HashSet<MetaConstraint<?>> parameterConstraints = CollectionHelper.newHashSet();
            if (this.annotationProcessingOptions.areParameterConstraintsIgnoredFor(executable, i)) {
                Type type = ReflectionHelper.typeOf(executable, i);
                metaData.add(new ConstrainedParameter(ConfigurationSource.ANNOTATION, executable, type, i, parameterConstraints, Collections.emptySet(), CascadingMetaDataBuilder.nonCascading()));
                ++i;
                continue;
            }
            ConstraintLocation location = ConstraintLocation.forParameter(executable, i);
            for (Annotation parameterAnnotation : parameterAnnotations) {
                List<ConstraintDescriptorImpl<?>> constraints = this.findConstraintAnnotations(executable, parameterAnnotation, ElementType.PARAMETER);
                for (ConstraintDescriptorImpl<?> constraintDescriptorImpl : constraints) {
                    parameterConstraints.add(MetaConstraints.create(this.typeResolutionHelper, this.valueExtractorManager, constraintDescriptorImpl, location));
                }
            }
            AnnotatedType parameterAnnotatedType = parameter.getAnnotatedType();
            Set<MetaConstraint<?>> typeArgumentsConstraints = this.findTypeAnnotationConstraintsForExecutableParameter(executable, i, parameterAnnotatedType);
            CascadingMetaDataBuilder cascadingMetaData = this.findCascadingMetaData(executable, parameters, i, parameterAnnotatedType);
            metaData.add(new ConstrainedParameter(ConfigurationSource.ANNOTATION, executable, ReflectionHelper.typeOf(executable, i), i, parameterConstraints, typeArgumentsConstraints, cascadingMetaData));
            ++i;
        }
        return metaData;
    }

    private List<ConstraintDescriptorImpl<?>> findConstraints(Member member, ElementType type) {
        ArrayList<ConstraintDescriptorImpl<?>> metaData = CollectionHelper.newArrayList();
        for (Annotation annotation : ((AccessibleObject)((Object)member)).getDeclaredAnnotations()) {
            metaData.addAll(this.findConstraintAnnotations(member, annotation, type));
        }
        return metaData;
    }

    private List<ConstraintDescriptorImpl<?>> findClassLevelConstraints(Class<?> beanClass) {
        ArrayList<ConstraintDescriptorImpl<?>> metaData = CollectionHelper.newArrayList();
        for (Annotation annotation : beanClass.getDeclaredAnnotations()) {
            metaData.addAll(this.findConstraintAnnotations(null, annotation, ElementType.TYPE));
        }
        return metaData;
    }

    protected <A extends Annotation> List<ConstraintDescriptorImpl<?>> findConstraintAnnotations(Member member, A annotation, ElementType type) {
        if (this.constraintHelper.isJdkAnnotation(annotation.annotationType())) {
            return Collections.emptyList();
        }
        ArrayList constraints = CollectionHelper.newArrayList();
        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (this.constraintHelper.isConstraintAnnotation(annotationType)) {
            constraints.add(annotation);
        } else if (this.constraintHelper.isMultiValueConstraint(annotationType)) {
            constraints.addAll(this.constraintHelper.getConstraintsFromMultiValueConstraint(annotation));
        }
        return constraints.stream().map(c -> this.buildConstraintDescriptor(member, c, type)).collect(Collectors.toList());
    }

    private Map<Class<?>, Class<?>> getGroupConversions(AnnotatedElement annotatedElement) {
        return this.getGroupConversions(annotatedElement.getAnnotation(ConvertGroup.class), annotatedElement.getAnnotation(ConvertGroup.List.class));
    }

    private Map<Class<?>, Class<?>> getGroupConversions(ConvertGroup groupConversion, ConvertGroup.List groupConversionList) {
        if (groupConversion == null && (groupConversionList == null || groupConversionList.value().length == 0)) {
            return Collections.emptyMap();
        }
        HashMap<Class<?>, Class<?>> groupConversions = CollectionHelper.newHashMap();
        if (groupConversion != null) {
            groupConversions.put(groupConversion.from(), groupConversion.to());
        }
        if (groupConversionList != null) {
            for (ConvertGroup conversion : groupConversionList.value()) {
                if (groupConversions.containsKey(conversion.from())) {
                    throw LOG.getMultipleGroupConversionsForSameSourceException(conversion.from(), CollectionHelper.asSet((Class)groupConversions.get(conversion.from()), conversion.to()));
                }
                groupConversions.put(conversion.from(), conversion.to());
            }
        }
        return groupConversions;
    }

    private <A extends Annotation> ConstraintDescriptorImpl<A> buildConstraintDescriptor(Member member, A annotation, ElementType type) {
        return new ConstraintDescriptorImpl<A>(this.constraintHelper, member, new ConstraintAnnotationDescriptor<A>(annotation), type);
    }

    private <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }

    protected Set<MetaConstraint<?>> findTypeAnnotationConstraints(Field field) {
        return this.findTypeArgumentsConstraints(field, new TypeArgumentFieldLocation(field), field.getAnnotatedType());
    }

    protected Set<MetaConstraint<?>> findTypeAnnotationConstraints(Executable executable, AnnotatedType annotatedReturnType) {
        return this.findTypeArgumentsConstraints(executable, new TypeArgumentReturnValueLocation(executable), annotatedReturnType);
    }

    private CascadingMetaDataBuilder findCascadingMetaData(Executable executable, Parameter[] parameters, int i, AnnotatedType parameterAnnotatedType) {
        Parameter parameter = parameters[i];
        TypeVariable<Class<?>>[] typeParameters = parameter.getType().getTypeParameters();
        Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData = this.getTypeParametersCascadingMetadata(parameterAnnotatedType, typeParameters);
        try {
            return this.getCascadingMetaData(ReflectionHelper.typeOf(parameter.getDeclaringExecutable(), i), parameter, containerElementTypesCascadingMetaData);
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            LOG.warn(Messages.MESSAGES.constraintOnConstructorOfNonStaticInnerClass(), ex);
            return CascadingMetaDataBuilder.nonCascading();
        }
    }

    private CascadingMetaDataBuilder findCascadingMetaData(Field field) {
        TypeVariable<Class<?>>[] typeParameters = field.getType().getTypeParameters();
        AnnotatedType annotatedType = field.getAnnotatedType();
        Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData = this.getTypeParametersCascadingMetadata(annotatedType, typeParameters);
        return this.getCascadingMetaData(ReflectionHelper.typeOf(field), field, containerElementTypesCascadingMetaData);
    }

    private CascadingMetaDataBuilder findCascadingMetaData(Executable executable, AnnotatedType annotatedReturnType) {
        TypeVariable<Class<Object>>[] typeParameters = executable instanceof Method ? ((Method)executable).getReturnType().getTypeParameters() : ((Constructor)executable).getDeclaringClass().getTypeParameters();
        Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData = this.getTypeParametersCascadingMetadata(annotatedReturnType, typeParameters);
        return this.getCascadingMetaData(ReflectionHelper.typeOf(executable), executable, containerElementTypesCascadingMetaData);
    }

    private Map<TypeVariable<?>, CascadingMetaDataBuilder> getTypeParametersCascadingMetadata(AnnotatedType annotatedType, TypeVariable<?>[] typeParameters) {
        if (annotatedType instanceof AnnotatedArrayType) {
            return this.getTypeParametersCascadingMetaDataForArrayType((AnnotatedArrayType)annotatedType);
        }
        if (annotatedType instanceof AnnotatedParameterizedType) {
            return this.getTypeParametersCascadingMetaDataForParameterizedType((AnnotatedParameterizedType)annotatedType, typeParameters);
        }
        return Collections.emptyMap();
    }

    private Map<TypeVariable<?>, CascadingMetaDataBuilder> getTypeParametersCascadingMetaDataForParameterizedType(AnnotatedParameterizedType annotatedParameterizedType, TypeVariable<?>[] typeParameters) {
        HashMap<TypeVariable<?>, CascadingMetaDataBuilder> typeParametersCascadingMetadata = CollectionHelper.newHashMap(typeParameters.length);
        AnnotatedType[] annotatedTypeArguments = annotatedParameterizedType.getAnnotatedActualTypeArguments();
        int i = 0;
        for (AnnotatedType annotatedTypeArgument : annotatedTypeArguments) {
            Map<TypeVariable<?>, CascadingMetaDataBuilder> nestedTypeParametersCascadingMetadata = this.getTypeParametersCascadingMetaDataForAnnotatedType(annotatedTypeArgument);
            typeParametersCascadingMetadata.put(typeParameters[i], new CascadingMetaDataBuilder(annotatedParameterizedType.getType(), typeParameters[i], annotatedTypeArgument.isAnnotationPresent(Valid.class), nestedTypeParametersCascadingMetadata, this.getGroupConversions(annotatedTypeArgument)));
            ++i;
        }
        return typeParametersCascadingMetadata;
    }

    private Map<TypeVariable<?>, CascadingMetaDataBuilder> getTypeParametersCascadingMetaDataForArrayType(AnnotatedArrayType annotatedArrayType) {
        return Collections.emptyMap();
    }

    private Map<TypeVariable<?>, CascadingMetaDataBuilder> getTypeParametersCascadingMetaDataForAnnotatedType(AnnotatedType annotatedType) {
        if (annotatedType instanceof AnnotatedArrayType) {
            return this.getTypeParametersCascadingMetaDataForArrayType((AnnotatedArrayType)annotatedType);
        }
        if (annotatedType instanceof AnnotatedParameterizedType) {
            return this.getTypeParametersCascadingMetaDataForParameterizedType((AnnotatedParameterizedType)annotatedType, ReflectionHelper.getClassFromType(annotatedType.getType()).getTypeParameters());
        }
        return Collections.emptyMap();
    }

    protected Set<MetaConstraint<?>> findTypeAnnotationConstraintsForExecutableParameter(Executable executable, int i, AnnotatedType parameterAnnotatedType) {
        try {
            return this.findTypeArgumentsConstraints(executable, new TypeArgumentExecutableParameterLocation(executable, i), parameterAnnotatedType);
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            LOG.warn(Messages.MESSAGES.constraintOnConstructorOfNonStaticInnerClass(), ex);
            return Collections.emptySet();
        }
    }

    private Set<MetaConstraint<?>> findTypeArgumentsConstraints(Member member, TypeArgumentLocation location, AnnotatedType annotatedType) {
        if (!(annotatedType instanceof AnnotatedParameterizedType)) {
            return Collections.emptySet();
        }
        HashSet typeArgumentConstraints = new HashSet();
        if (annotatedType instanceof AnnotatedArrayType) {
            AnnotatedArrayType annotatedArrayType = (AnnotatedArrayType)annotatedType;
            Type validatedType = annotatedArrayType.getAnnotatedGenericComponentType().getType();
            ArrayElement arrayElementTypeArgument = new ArrayElement(annotatedArrayType);
            typeArgumentConstraints.addAll(this.findTypeUseConstraints(member, annotatedArrayType, arrayElementTypeArgument, location, validatedType));
            typeArgumentConstraints.addAll(this.findTypeArgumentsConstraints(member, new NestedTypeArgumentLocation(location, arrayElementTypeArgument, validatedType), annotatedArrayType.getAnnotatedGenericComponentType()));
        } else if (annotatedType instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType)annotatedType;
            int i = 0;
            for (TypeVariable<Class<?>> typeVariable : ReflectionHelper.getClassFromType(annotatedType.getType()).getTypeParameters()) {
                AnnotatedType annotatedTypeParameter = annotatedParameterizedType.getAnnotatedActualTypeArguments()[i];
                Type validatedType = annotatedTypeParameter.getType();
                typeArgumentConstraints.addAll(this.findTypeUseConstraints(member, annotatedTypeParameter, typeVariable, location, validatedType));
                if (validatedType instanceof ParameterizedType) {
                    typeArgumentConstraints.addAll(this.findTypeArgumentsConstraints(member, new NestedTypeArgumentLocation(location, typeVariable, validatedType), annotatedTypeParameter));
                }
                ++i;
            }
        }
        return typeArgumentConstraints.isEmpty() ? Collections.emptySet() : typeArgumentConstraints;
    }

    private Set<MetaConstraint<?>> findTypeUseConstraints(Member member, AnnotatedType typeArgument, TypeVariable<?> typeVariable, TypeArgumentLocation location, Type type) {
        Set<MetaConstraint<?>> constraints = Arrays.stream(typeArgument.getAnnotations()).flatMap(a -> this.findConstraintAnnotations(member, a, ElementType.TYPE_USE).stream()).map(d -> this.createTypeArgumentMetaConstraint((ConstraintDescriptorImpl)d, location, typeVariable, type)).collect(Collectors.toSet());
        return constraints;
    }

    private <A extends Annotation> MetaConstraint<?> createTypeArgumentMetaConstraint(ConstraintDescriptorImpl<A> descriptor, TypeArgumentLocation location, TypeVariable<?> typeVariable, Type type) {
        ConstraintLocation constraintLocation = ConstraintLocation.forTypeArgument(location.toConstraintLocation(), typeVariable, type);
        return MetaConstraints.create(this.typeResolutionHelper, this.valueExtractorManager, descriptor, constraintLocation);
    }

    private CascadingMetaDataBuilder getCascadingMetaData(Type type, AnnotatedElement annotatedElement, Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData) {
        return CascadingMetaDataBuilder.annotatedObject(type, annotatedElement.isAnnotationPresent(Valid.class), containerElementTypesCascadingMetaData, this.getGroupConversions(annotatedElement));
    }

    private static class NestedTypeArgumentLocation
    implements TypeArgumentLocation {
        private final TypeArgumentLocation parentLocation;
        private final TypeVariable<?> typeParameter;
        private final Type typeOfAnnotatedElement;

        private NestedTypeArgumentLocation(TypeArgumentLocation parentLocation, TypeVariable<?> typeParameter, Type typeOfAnnotatedElement) {
            this.parentLocation = parentLocation;
            this.typeParameter = typeParameter;
            this.typeOfAnnotatedElement = typeOfAnnotatedElement;
        }

        @Override
        public ConstraintLocation toConstraintLocation() {
            return ConstraintLocation.forTypeArgument(this.parentLocation.toConstraintLocation(), this.typeParameter, this.typeOfAnnotatedElement);
        }
    }

    private static class TypeArgumentReturnValueLocation
    implements TypeArgumentLocation {
        private final Executable executable;

        private TypeArgumentReturnValueLocation(Executable executable) {
            this.executable = executable;
        }

        @Override
        public ConstraintLocation toConstraintLocation() {
            return ConstraintLocation.forReturnValue(this.executable);
        }
    }

    private static class TypeArgumentFieldLocation
    implements TypeArgumentLocation {
        private final Field field;

        private TypeArgumentFieldLocation(Field field) {
            this.field = field;
        }

        @Override
        public ConstraintLocation toConstraintLocation() {
            return ConstraintLocation.forField(this.field);
        }
    }

    private static class TypeArgumentExecutableParameterLocation
    implements TypeArgumentLocation {
        private final Executable executable;
        private final int index;

        private TypeArgumentExecutableParameterLocation(Executable executable, int index) {
            this.executable = executable;
            this.index = index;
        }

        @Override
        public ConstraintLocation toConstraintLocation() {
            return ConstraintLocation.forParameter(this.executable, this.index);
        }
    }

    private static interface TypeArgumentLocation {
        public ConstraintLocation toConstraintLocation();
    }
}

