/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.cfg.context;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.cfg.context.ConstructorConstraintMappingContext;
import org.hibernate.validator.cfg.context.ContainerElementConstraintMappingContext;
import org.hibernate.validator.cfg.context.ContainerElementTarget;
import org.hibernate.validator.cfg.context.MethodConstraintMappingContext;
import org.hibernate.validator.cfg.context.ParameterConstraintMappingContext;
import org.hibernate.validator.cfg.context.ParameterTarget;
import org.hibernate.validator.cfg.context.PropertyConstraintMappingContext;
import org.hibernate.validator.cfg.context.ReturnValueConstraintMappingContext;
import org.hibernate.validator.cfg.context.ReturnValueTarget;
import org.hibernate.validator.internal.cfg.context.CascadableConstraintMappingContextImplBase;
import org.hibernate.validator.internal.cfg.context.ConfiguredConstraint;
import org.hibernate.validator.internal.cfg.context.TypeConstraintMappingContextImpl;
import org.hibernate.validator.internal.engine.valueextraction.ArrayElement;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.core.MetaConstraints;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class ContainerElementConstraintMappingContextImpl
extends CascadableConstraintMappingContextImplBase<ContainerElementConstraintMappingContext>
implements ContainerElementConstraintMappingContext {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final TypeConstraintMappingContextImpl<?> typeContext;
    private final ContainerElementTarget parentContainerElementTarget;
    private final ConstraintLocation parentLocation;
    private final Type configuredType;
    private final int index;
    private final TypeVariable<?> typeParameter;
    protected final Map<Integer, ContainerElementConstraintMappingContextImpl> nestedContainerElementContexts;
    private final Set<ConfiguredConstraint<?>> constraints;

    ContainerElementConstraintMappingContextImpl(TypeConstraintMappingContextImpl<?> typeContext, ContainerElementTarget parentContainerElementTarget, ConstraintLocation parentLocation, int index) {
        super(typeContext.getConstraintMapping(), parentLocation.getTypeForValidatorResolution());
        this.typeContext = typeContext;
        this.parentContainerElementTarget = parentContainerElementTarget;
        this.parentLocation = parentLocation;
        this.configuredType = parentLocation.getTypeForValidatorResolution();
        if (TypeHelper.isArray(this.configuredType)) {
            throw LOG.getContainerElementConstraintsAndCascadedValidationNotSupportedOnArraysException(this.configuredType);
        }
        if (this.configuredType instanceof ParameterizedType) {
            TypeVariable<Class<?>>[] typeParameters = ReflectionHelper.getClassFromType(this.configuredType).getTypeParameters();
            if (index > typeParameters.length - 1) {
                throw LOG.getInvalidTypeArgumentIndexException(this.configuredType, index);
            }
            this.typeParameter = typeParameters[index];
        } else {
            this.typeParameter = new ArrayElement(this.configuredType);
        }
        this.index = index;
        this.constraints = new HashSet();
        this.nestedContainerElementContexts = new HashMap<Integer, ContainerElementConstraintMappingContextImpl>();
    }

    @Override
    protected ContainerElementConstraintMappingContext getThis() {
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
    public ParameterConstraintMappingContext parameter(int index) {
        if (this.parentContainerElementTarget instanceof ParameterTarget) {
            return ((ParameterTarget)((Object)this.parentContainerElementTarget)).parameter(index);
        }
        throw LOG.getParameterIsNotAValidCallException();
    }

    @Override
    public ReturnValueConstraintMappingContext returnValue() {
        if (this.parentContainerElementTarget instanceof ReturnValueTarget) {
            return ((ReturnValueTarget)((Object)this.parentContainerElementTarget)).returnValue();
        }
        throw LOG.getReturnValueIsNotAValidCallException();
    }

    @Override
    public ContainerElementConstraintMappingContext containerElementType() {
        return this.parentContainerElementTarget.containerElementType(0, new int[0]);
    }

    @Override
    public ContainerElementConstraintMappingContext containerElementType(int index, int ... nestedIndexes) {
        return this.parentContainerElementTarget.containerElementType(index, nestedIndexes);
    }

    ContainerElementConstraintMappingContext nestedContainerElement(int[] nestedIndexes) {
        if (!(this.configuredType instanceof ParameterizedType) && !TypeHelper.isArray(this.configuredType)) {
            throw LOG.getTypeIsNotAParameterizedNorArrayTypeException(this.configuredType);
        }
        ContainerElementConstraintMappingContextImpl nestedContext = this.nestedContainerElementContexts.get(nestedIndexes[0]);
        if (nestedContext == null) {
            nestedContext = new ContainerElementConstraintMappingContextImpl(this.typeContext, this.parentContainerElementTarget, ConstraintLocation.forTypeArgument(this.parentLocation, this.typeParameter, this.getContainerElementType()), nestedIndexes[0]);
            this.nestedContainerElementContexts.put(nestedIndexes[0], nestedContext);
        }
        if (nestedIndexes.length > 1) {
            return nestedContext.nestedContainerElement(Arrays.copyOfRange(nestedIndexes, 1, nestedIndexes.length));
        }
        return nestedContext;
    }

    @Override
    public ContainerElementConstraintMappingContext constraint(ConstraintDef<?, ?> definition) {
        this.constraints.add(ConfiguredConstraint.forTypeArgument(definition, this.parentLocation, this.typeParameter, this.getContainerElementType()));
        return this;
    }

    private Type getContainerElementType() {
        if (this.configuredType instanceof ParameterizedType) {
            return ((ParameterizedType)this.configuredType).getActualTypeArguments()[this.index];
        }
        return TypeHelper.getComponentType(this.configuredType);
    }

    @Override
    protected ConstraintDescriptorImpl.ConstraintType getConstraintType() {
        return ConstraintDescriptorImpl.ConstraintType.GENERIC;
    }

    CascadingMetaDataBuilder getContainerElementCascadingMetaDataBuilder() {
        return new CascadingMetaDataBuilder(this.parentLocation.getTypeForValidatorResolution(), this.typeParameter, this.isCascading, this.nestedContainerElementContexts.values().stream().map(ContainerElementConstraintMappingContextImpl::getContainerElementCascadingMetaDataBuilder).collect(Collectors.toMap(CascadingMetaDataBuilder::getTypeParameter, Function.identity())), this.groupConversions);
    }

    Set<MetaConstraint<?>> build(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        return Stream.concat(this.constraints.stream().map(c -> this.asMetaConstraint((ConfiguredConstraint)c, constraintHelper, typeResolutionHelper, valueExtractorManager)), this.nestedContainerElementContexts.values().stream().map(c -> c.build(constraintHelper, typeResolutionHelper, valueExtractorManager)).flatMap(Collection::stream)).collect(Collectors.toSet());
    }

    private <A extends Annotation> MetaConstraint<A> asMetaConstraint(ConfiguredConstraint<A> config, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        ConstraintDescriptorImpl<A> constraintDescriptor = new ConstraintDescriptorImpl<A>(constraintHelper, config.getLocation().getMember(), config.createAnnotationDescriptor(), config.getElementType(), this.getConstraintType());
        return MetaConstraints.create(typeResolutionHelper, valueExtractorManager, constraintDescriptor, config.getLocation());
    }

    public String toString() {
        return "TypeArgumentConstraintMappingContextImpl [configuredType=" + StringHelper.toShortString(this.configuredType) + ", typeParameter=" + this.typeParameter + "]";
    }
}

