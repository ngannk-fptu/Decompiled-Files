/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.cfg.context;

import java.lang.annotation.ElementType;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.cfg.context.ConstructorConstraintMappingContext;
import org.hibernate.validator.cfg.context.ContainerElementConstraintMappingContext;
import org.hibernate.validator.cfg.context.MethodConstraintMappingContext;
import org.hibernate.validator.cfg.context.PropertyConstraintMappingContext;
import org.hibernate.validator.internal.cfg.context.CascadableConstraintMappingContextImplBase;
import org.hibernate.validator.internal.cfg.context.ConfiguredConstraint;
import org.hibernate.validator.internal.cfg.context.TypeConstraintMappingContextImpl;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedField;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;

final class PropertyConstraintMappingContextImpl
extends CascadableConstraintMappingContextImplBase<PropertyConstraintMappingContext>
implements PropertyConstraintMappingContext {
    private final TypeConstraintMappingContextImpl<?> typeContext;
    private final Member member;
    private final ConstraintLocation location;

    PropertyConstraintMappingContextImpl(TypeConstraintMappingContextImpl<?> typeContext, Member member) {
        super(typeContext.getConstraintMapping(), ReflectionHelper.typeOf(member));
        this.typeContext = typeContext;
        this.member = member;
        this.location = member instanceof Field ? ConstraintLocation.forField((Field)member) : ConstraintLocation.forGetter((Method)member);
    }

    @Override
    protected PropertyConstraintMappingContextImpl getThis() {
        return this;
    }

    @Override
    public PropertyConstraintMappingContext constraint(ConstraintDef<?, ?> definition) {
        if (this.member instanceof Field) {
            super.addConstraint(ConfiguredConstraint.forProperty(definition, this.member));
        } else {
            super.addConstraint(ConfiguredConstraint.forExecutable(definition, (Method)this.member));
        }
        return this;
    }

    @Override
    public PropertyConstraintMappingContext ignoreAnnotations() {
        return this.ignoreAnnotations(true);
    }

    @Override
    public PropertyConstraintMappingContext ignoreAnnotations(boolean ignoreAnnotations) {
        this.mapping.getAnnotationProcessingOptions().ignoreConstraintAnnotationsOnMember(this.member, ignoreAnnotations);
        return this;
    }

    @Override
    public PropertyConstraintMappingContext property(String property, ElementType elementType) {
        return this.typeContext.property(property, elementType);
    }

    @Override
    public ConstructorConstraintMappingContext constructor(Class<?> ... parameterTypes) {
        return this.typeContext.constructor(parameterTypes);
    }

    @Override
    public MethodConstraintMappingContext method(String name, Class<?> ... parameterTypes) {
        return this.typeContext.method(name, parameterTypes);
    }

    @Override
    public ContainerElementConstraintMappingContext containerElementType() {
        return super.containerElement(this, this.typeContext, this.location);
    }

    @Override
    public ContainerElementConstraintMappingContext containerElementType(int index, int ... nestedIndexes) {
        return super.containerElement(this, this.typeContext, this.location, index, nestedIndexes);
    }

    ConstrainedElement build(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        if (this.member instanceof Field) {
            return new ConstrainedField(ConfigurationSource.API, (Field)this.member, this.getConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager), this.getTypeArgumentConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager), this.getCascadingMetaDataBuilder());
        }
        return new ConstrainedExecutable(ConfigurationSource.API, (Executable)this.member, this.getConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager), this.getTypeArgumentConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager), this.getCascadingMetaDataBuilder());
    }

    @Override
    protected ConstraintDescriptorImpl.ConstraintType getConstraintType() {
        return ConstraintDescriptorImpl.ConstraintType.GENERIC;
    }
}

