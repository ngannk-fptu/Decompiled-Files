/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.cfg.context;

import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.cfg.context.ConstructorConstraintMappingContext;
import org.hibernate.validator.cfg.context.MethodConstraintMappingContext;
import org.hibernate.validator.cfg.context.PropertyConstraintMappingContext;
import org.hibernate.validator.cfg.context.TypeConstraintMappingContext;
import org.hibernate.validator.internal.cfg.context.ConfiguredConstraint;
import org.hibernate.validator.internal.cfg.context.ConstraintMappingContextImplBase;
import org.hibernate.validator.internal.cfg.context.ConstructorConstraintMappingContextImpl;
import org.hibernate.validator.internal.cfg.context.DefaultConstraintMapping;
import org.hibernate.validator.internal.cfg.context.ExecutableConstraintMappingContextImpl;
import org.hibernate.validator.internal.cfg.context.MethodConstraintMappingContextImpl;
import org.hibernate.validator.internal.cfg.context.PropertyConstraintMappingContextImpl;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedType;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.logging.Messages;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredConstructor;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredField;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredMethod;
import org.hibernate.validator.internal.util.privilegedactions.GetMethod;
import org.hibernate.validator.internal.util.privilegedactions.NewInstance;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

public final class TypeConstraintMappingContextImpl<C>
extends ConstraintMappingContextImplBase
implements TypeConstraintMappingContext<C> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Class<C> beanClass;
    private final Set<ExecutableConstraintMappingContextImpl> executableContexts = CollectionHelper.newHashSet();
    private final Set<PropertyConstraintMappingContextImpl> propertyContexts = CollectionHelper.newHashSet();
    private final Set<Member> configuredMembers = CollectionHelper.newHashSet();
    private List<Class<?>> defaultGroupSequence;
    private Class<? extends DefaultGroupSequenceProvider<? super C>> defaultGroupSequenceProviderClass;

    TypeConstraintMappingContextImpl(DefaultConstraintMapping mapping, Class<C> beanClass) {
        super(mapping);
        this.beanClass = beanClass;
        mapping.getAnnotationProcessingOptions().ignoreAnnotationConstraintForClass(beanClass, Boolean.FALSE);
    }

    @Override
    public TypeConstraintMappingContext<C> constraint(ConstraintDef<?, ?> definition) {
        this.addConstraint(ConfiguredConstraint.forType(definition, this.beanClass));
        return this;
    }

    @Override
    public TypeConstraintMappingContext<C> ignoreAnnotations() {
        return this.ignoreAnnotations(true);
    }

    @Override
    public TypeConstraintMappingContext<C> ignoreAnnotations(boolean ignoreAnnotations) {
        this.mapping.getAnnotationProcessingOptions().ignoreClassLevelConstraintAnnotations(this.beanClass, ignoreAnnotations);
        return this;
    }

    @Override
    public TypeConstraintMappingContext<C> ignoreAllAnnotations() {
        this.mapping.getAnnotationProcessingOptions().ignoreAnnotationConstraintForClass(this.beanClass, Boolean.TRUE);
        return this;
    }

    @Override
    public TypeConstraintMappingContext<C> defaultGroupSequence(Class<?> ... defaultGroupSequence) {
        this.defaultGroupSequence = Arrays.asList(defaultGroupSequence);
        return this;
    }

    @Override
    public TypeConstraintMappingContext<C> defaultGroupSequenceProviderClass(Class<? extends DefaultGroupSequenceProvider<? super C>> defaultGroupSequenceProviderClass) {
        this.defaultGroupSequenceProviderClass = defaultGroupSequenceProviderClass;
        return this;
    }

    @Override
    public PropertyConstraintMappingContext property(String property, ElementType elementType) {
        Contracts.assertNotNull(property, "The property name must not be null.");
        Contracts.assertNotNull((Object)elementType, "The element type must not be null.");
        Contracts.assertNotEmpty(property, Messages.MESSAGES.propertyNameMustNotBeEmpty());
        Member member = this.getMember(this.beanClass, property, elementType);
        if (member == null || member.getDeclaringClass() != this.beanClass) {
            throw LOG.getUnableToFindPropertyWithAccessException(this.beanClass, property, elementType);
        }
        if (this.configuredMembers.contains(member)) {
            throw LOG.getPropertyHasAlreadyBeConfiguredViaProgrammaticApiException(this.beanClass, property);
        }
        PropertyConstraintMappingContextImpl context = new PropertyConstraintMappingContextImpl(this, member);
        this.configuredMembers.add(member);
        this.propertyContexts.add(context);
        return context;
    }

    @Override
    public MethodConstraintMappingContext method(String name, Class<?> ... parameterTypes) {
        Contracts.assertNotNull(name, Messages.MESSAGES.methodNameMustNotBeNull());
        Method method = this.run(GetDeclaredMethod.action(this.beanClass, name, parameterTypes));
        if (method == null || method.getDeclaringClass() != this.beanClass) {
            throw LOG.getBeanDoesNotContainMethodException(this.beanClass, name, parameterTypes);
        }
        if (this.configuredMembers.contains(method)) {
            throw LOG.getMethodHasAlreadyBeConfiguredViaProgrammaticApiException(this.beanClass, ExecutableHelper.getExecutableAsString(name, parameterTypes));
        }
        MethodConstraintMappingContextImpl context = new MethodConstraintMappingContextImpl(this, method);
        this.configuredMembers.add(method);
        this.executableContexts.add(context);
        return context;
    }

    @Override
    public ConstructorConstraintMappingContext constructor(Class<?> ... parameterTypes) {
        Constructor constructor = (Constructor)this.run(GetDeclaredConstructor.action(this.beanClass, parameterTypes));
        if (constructor == null || constructor.getDeclaringClass() != this.beanClass) {
            throw LOG.getBeanDoesNotContainConstructorException(this.beanClass, parameterTypes);
        }
        if (this.configuredMembers.contains(constructor)) {
            throw LOG.getConstructorHasAlreadyBeConfiguredViaProgrammaticApiException(this.beanClass, ExecutableHelper.getExecutableAsString(this.beanClass.getSimpleName(), parameterTypes));
        }
        ConstructorConstraintMappingContextImpl context = new ConstructorConstraintMappingContextImpl(this, constructor);
        this.configuredMembers.add(constructor);
        this.executableContexts.add(context);
        return context;
    }

    BeanConfiguration<C> build(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        return new BeanConfiguration<C>(ConfigurationSource.API, this.beanClass, this.buildConstraintElements(constraintHelper, typeResolutionHelper, valueExtractorManager), this.defaultGroupSequence, this.getDefaultGroupSequenceProvider());
    }

    private Set<ConstrainedElement> buildConstraintElements(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        HashSet<ConstrainedElement> elements = CollectionHelper.newHashSet();
        elements.add(new ConstrainedType(ConfigurationSource.API, this.beanClass, this.getConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager)));
        for (ExecutableConstraintMappingContextImpl executableContext : this.executableContexts) {
            elements.add(executableContext.build(constraintHelper, typeResolutionHelper, valueExtractorManager));
        }
        for (PropertyConstraintMappingContextImpl propertyContext : this.propertyContexts) {
            elements.add(propertyContext.build(constraintHelper, typeResolutionHelper, valueExtractorManager));
        }
        return elements;
    }

    private DefaultGroupSequenceProvider<? super C> getDefaultGroupSequenceProvider() {
        return this.defaultGroupSequenceProviderClass != null ? this.run(NewInstance.action(this.defaultGroupSequenceProviderClass, "default group sequence provider")) : null;
    }

    Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Override
    protected ConstraintDescriptorImpl.ConstraintType getConstraintType() {
        return ConstraintDescriptorImpl.ConstraintType.GENERIC;
    }

    private Member getMember(Class<?> clazz, String property, ElementType elementType) {
        Contracts.assertNotNull(clazz, Messages.MESSAGES.classCannotBeNull());
        if (property == null || property.length() == 0) {
            throw LOG.getPropertyNameCannotBeNullOrEmptyException();
        }
        if (!ElementType.FIELD.equals((Object)elementType) && !ElementType.METHOD.equals((Object)elementType)) {
            throw LOG.getElementTypeHasToBeFieldOrMethodException();
        }
        Member member = null;
        if (ElementType.FIELD.equals((Object)elementType)) {
            member = this.run(GetDeclaredField.action(clazz, property));
        } else {
            String prefix;
            String methodName = property.substring(0, 1).toUpperCase(Locale.ROOT) + property.substring(1);
            String[] stringArray = ReflectionHelper.PROPERTY_ACCESSOR_PREFIXES;
            int n = stringArray.length;
            for (int i = 0; i < n && (member = (Member)this.run(GetMethod.action(clazz, (prefix = stringArray[i]) + methodName))) == null; ++i) {
            }
        }
        return member;
    }

    private <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}

