/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.cfg.context;

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
import java.util.stream.Collectors;
import org.hibernate.validator.cfg.context.Cascadable;
import org.hibernate.validator.cfg.context.ContainerElementConstraintMappingContext;
import org.hibernate.validator.cfg.context.ContainerElementTarget;
import org.hibernate.validator.cfg.context.GroupConversionTargetContext;
import org.hibernate.validator.internal.cfg.context.ConstraintMappingContextImplBase;
import org.hibernate.validator.internal.cfg.context.ContainerElementConstraintMappingContextImpl;
import org.hibernate.validator.internal.cfg.context.DefaultConstraintMapping;
import org.hibernate.validator.internal.cfg.context.GroupConversionTargetContextImpl;
import org.hibernate.validator.internal.cfg.context.TypeConstraintMappingContextImpl;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

abstract class CascadableConstraintMappingContextImplBase<C extends Cascadable<C>>
extends ConstraintMappingContextImplBase
implements Cascadable<C> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Type configuredType;
    protected boolean isCascading;
    protected final Map<Class<?>, Class<?>> groupConversions = CollectionHelper.newHashMap();
    private final Map<Integer, ContainerElementConstraintMappingContextImpl> containerElementContexts = new HashMap<Integer, ContainerElementConstraintMappingContextImpl>();
    private final Set<ContainerElementPathKey> configuredPaths = new HashSet<ContainerElementPathKey>();

    CascadableConstraintMappingContextImplBase(DefaultConstraintMapping mapping, Type configuredType) {
        super(mapping);
        this.configuredType = configuredType;
    }

    protected abstract C getThis();

    public void addGroupConversion(Class<?> from, Class<?> to) {
        this.groupConversions.put(from, to);
    }

    @Override
    public C valid() {
        this.isCascading = true;
        return this.getThis();
    }

    @Override
    public GroupConversionTargetContext<C> convertGroup(Class<?> from) {
        return new GroupConversionTargetContextImpl<C>(from, this.getThis(), this);
    }

    public ContainerElementConstraintMappingContext containerElement(ContainerElementTarget parent, TypeConstraintMappingContextImpl<?> typeContext, ConstraintLocation location) {
        if (TypeHelper.isArray(this.configuredType)) {
            throw LOG.getContainerElementConstraintsAndCascadedValidationNotSupportedOnArraysException(this.configuredType);
        }
        if (this.configuredType instanceof ParameterizedType) {
            if (((ParameterizedType)this.configuredType).getActualTypeArguments().length > 1) {
                throw LOG.getNoTypeArgumentIndexIsGivenForTypeWithMultipleTypeArgumentsException(this.configuredType);
            }
        } else if (!TypeHelper.isArray(this.configuredType)) {
            throw LOG.getTypeIsNotAParameterizedNorArrayTypeException(this.configuredType);
        }
        return this.containerElement(parent, typeContext, location, 0, new int[0]);
    }

    public ContainerElementConstraintMappingContext containerElement(ContainerElementTarget parent, TypeConstraintMappingContextImpl<?> typeContext, ConstraintLocation location, int index, int ... nestedIndexes) {
        boolean configuredBefore;
        Contracts.assertTrue(index >= 0, "Type argument index must not be negative");
        if (TypeHelper.isArray(this.configuredType)) {
            throw LOG.getContainerElementConstraintsAndCascadedValidationNotSupportedOnArraysException(this.configuredType);
        }
        if (!(this.configuredType instanceof ParameterizedType) && !TypeHelper.isArray(this.configuredType)) {
            throw LOG.getTypeIsNotAParameterizedNorArrayTypeException(this.configuredType);
        }
        ContainerElementPathKey key = new ContainerElementPathKey(index, nestedIndexes);
        boolean bl = configuredBefore = !this.configuredPaths.add(key);
        if (configuredBefore) {
            throw LOG.getContainerElementTypeHasAlreadyBeenConfiguredViaProgrammaticApiException(location.getTypeForValidatorResolution());
        }
        ContainerElementConstraintMappingContextImpl containerElementContext = this.containerElementContexts.get(index);
        if (containerElementContext == null) {
            containerElementContext = new ContainerElementConstraintMappingContextImpl(typeContext, parent, location, index);
            this.containerElementContexts.put(index, containerElementContext);
        }
        if (nestedIndexes.length > 0) {
            return containerElementContext.nestedContainerElement(nestedIndexes);
        }
        return containerElementContext;
    }

    public boolean isCascading() {
        return this.isCascading;
    }

    protected Set<MetaConstraint<?>> getTypeArgumentConstraints(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        return this.containerElementContexts.values().stream().map(t -> t.build(constraintHelper, typeResolutionHelper, valueExtractorManager)).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    protected CascadingMetaDataBuilder getCascadingMetaDataBuilder() {
        Map<TypeVariable<?>, CascadingMetaDataBuilder> typeParametersCascadingMetaData = this.containerElementContexts.values().stream().filter(c -> c.getContainerElementCascadingMetaDataBuilder() != null).collect(Collectors.toMap(c -> c.getContainerElementCascadingMetaDataBuilder().getTypeParameter(), c -> c.getContainerElementCascadingMetaDataBuilder()));
        for (ContainerElementConstraintMappingContextImpl typeArgumentContext : this.containerElementContexts.values()) {
            CascadingMetaDataBuilder cascadingMetaDataBuilder = typeArgumentContext.getContainerElementCascadingMetaDataBuilder();
            if (cascadingMetaDataBuilder == null) continue;
            typeParametersCascadingMetaData.put(cascadingMetaDataBuilder.getTypeParameter(), cascadingMetaDataBuilder);
        }
        return CascadingMetaDataBuilder.annotatedObject(this.configuredType, this.isCascading, typeParametersCascadingMetaData, this.groupConversions);
    }

    private static class ContainerElementPathKey {
        private final int index;
        private final int[] nestedIndexes;

        public ContainerElementPathKey(int index, int[] nestedIndexes) {
            this.index = index;
            this.nestedIndexes = nestedIndexes;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + this.index;
            result = 31 * result + Arrays.hashCode(this.nestedIndexes);
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            ContainerElementPathKey other = (ContainerElementPathKey)obj;
            if (this.index != other.index) {
                return false;
            }
            return Arrays.equals(this.nestedIndexes, other.nestedIndexes);
        }
    }
}

