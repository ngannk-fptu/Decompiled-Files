/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.engine.valueextraction;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.hibernate.validator.internal.engine.valueextraction.MapKeyExtractor;
import org.hibernate.validator.internal.engine.valueextraction.MapValueExtractor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorHelper;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.TypeVariableBindings;
import org.hibernate.validator.internal.util.TypeVariables;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class ValueExtractorResolver {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Set<ValueExtractorDescriptor> registeredValueExtractors;
    private final ConcurrentHashMap<ValueExtractorCacheKey, Set<ValueExtractorDescriptor>> possibleValueExtractorsByRuntimeTypeAndTypeParameter = new ConcurrentHashMap();
    private final ConcurrentHashMap<Class<?>, Set<ValueExtractorDescriptor>> possibleValueExtractorsByRuntimeType = new ConcurrentHashMap();
    private final Set<Class<?>> nonContainerTypes = Collections.newSetFromMap(new ConcurrentHashMap());

    ValueExtractorResolver(Set<ValueExtractorDescriptor> valueExtractors) {
        this.registeredValueExtractors = CollectionHelper.toImmutableSet(valueExtractors);
    }

    public Set<ValueExtractorDescriptor> getMaximallySpecificValueExtractors(Class<?> declaredType) {
        return this.getRuntimeCompliantValueExtractors(declaredType, this.registeredValueExtractors);
    }

    public ValueExtractorDescriptor getMaximallySpecificAndContainerElementCompliantValueExtractor(Class<?> declaredType, TypeVariable<?> typeParameter) {
        return this.getUniqueValueExtractorOrThrowException(declaredType, this.getRuntimeAndContainerElementCompliantValueExtractorsFromPossibleCandidates(declaredType, typeParameter, declaredType, this.registeredValueExtractors));
    }

    public ValueExtractorDescriptor getMaximallySpecificAndRuntimeContainerElementCompliantValueExtractor(Type declaredType, TypeVariable<?> typeParameter, Class<?> runtimeType, Collection<ValueExtractorDescriptor> valueExtractorCandidates) {
        Contracts.assertNotEmpty(valueExtractorCandidates, "Value extractor candidates cannot be empty");
        if (valueExtractorCandidates.size() == 1) {
            return valueExtractorCandidates.iterator().next();
        }
        return this.getUniqueValueExtractorOrThrowException(runtimeType, this.getRuntimeAndContainerElementCompliantValueExtractorsFromPossibleCandidates(declaredType, typeParameter, runtimeType, valueExtractorCandidates));
    }

    public ValueExtractorDescriptor getMaximallySpecificValueExtractorForAllContainerElements(Class<?> runtimeType, Set<ValueExtractorDescriptor> potentialValueExtractorDescriptors) {
        if (TypeHelper.isAssignable(Map.class, runtimeType)) {
            return MapValueExtractor.DESCRIPTOR;
        }
        return this.getUniqueValueExtractorOrThrowException(runtimeType, this.getRuntimeCompliantValueExtractors(runtimeType, potentialValueExtractorDescriptors));
    }

    public Set<ValueExtractorDescriptor> getValueExtractorCandidatesForCascadedValidation(Type declaredType, TypeVariable<?> typeParameter) {
        HashSet<ValueExtractorDescriptor> valueExtractorDescriptors = new HashSet<ValueExtractorDescriptor>();
        valueExtractorDescriptors.addAll(this.getRuntimeAndContainerElementCompliantValueExtractorsFromPossibleCandidates(declaredType, typeParameter, TypeHelper.getErasedReferenceType(declaredType), this.registeredValueExtractors));
        valueExtractorDescriptors.addAll(this.getPotentiallyRuntimeTypeCompliantAndContainerElementCompliantValueExtractors(declaredType, typeParameter));
        return CollectionHelper.toImmutableSet(valueExtractorDescriptors);
    }

    public Set<ValueExtractorDescriptor> getValueExtractorCandidatesForContainerDetectionOfGlobalCascadedValidation(Type enclosingType) {
        boolean mapAssignable = TypeHelper.isAssignable(Map.class, enclosingType);
        Class<?> enclosingClass = ReflectionHelper.getClassFromType(enclosingType);
        return this.getRuntimeCompliantValueExtractors(enclosingClass, this.registeredValueExtractors).stream().filter(ved -> !mapAssignable || !ved.equals(MapKeyExtractor.DESCRIPTOR)).collect(Collectors.collectingAndThen(Collectors.toSet(), CollectionHelper::toImmutableSet));
    }

    public Set<ValueExtractorDescriptor> getPotentialValueExtractorCandidatesForCascadedValidation(Type declaredType) {
        return this.registeredValueExtractors.stream().filter(e -> TypeHelper.isAssignable(declaredType, e.getContainerType())).collect(Collectors.collectingAndThen(Collectors.toSet(), CollectionHelper::toImmutableSet));
    }

    public void clear() {
        this.nonContainerTypes.clear();
        this.possibleValueExtractorsByRuntimeType.clear();
        this.possibleValueExtractorsByRuntimeTypeAndTypeParameter.clear();
    }

    private Set<ValueExtractorDescriptor> getPotentiallyRuntimeTypeCompliantAndContainerElementCompliantValueExtractors(Type declaredType, TypeVariable<?> typeParameter) {
        boolean isInternal = TypeVariables.isInternal(typeParameter);
        Class<?> erasedDeclaredType = TypeHelper.getErasedReferenceType(declaredType);
        Set typeCompatibleExtractors = this.registeredValueExtractors.stream().filter(e -> TypeHelper.isAssignable(erasedDeclaredType, e.getContainerType())).collect(Collectors.toSet());
        HashSet<ValueExtractorDescriptor> containerElementCompliantExtractors = new HashSet<ValueExtractorDescriptor>();
        for (ValueExtractorDescriptor extractorDescriptor : typeCompatibleExtractors) {
            TypeVariable<?> typeParameterBoundToExtractorType;
            if (!isInternal) {
                Map<Class<?>, Map<TypeVariable<?>, TypeVariable<?>>> allBindings = TypeVariableBindings.getTypeVariableBindings(extractorDescriptor.getContainerType());
                Map<TypeVariable<?>, TypeVariable<?>> bindingsForExtractorType = allBindings.get(erasedDeclaredType);
                typeParameterBoundToExtractorType = this.bind(extractorDescriptor.getExtractedTypeParameter(), bindingsForExtractorType);
            } else {
                typeParameterBoundToExtractorType = typeParameter;
            }
            if (!Objects.equals(typeParameter, typeParameterBoundToExtractorType)) continue;
            containerElementCompliantExtractors.add(extractorDescriptor);
        }
        return containerElementCompliantExtractors;
    }

    private ValueExtractorDescriptor getUniqueValueExtractorOrThrowException(Class<?> runtimeType, Set<ValueExtractorDescriptor> maximallySpecificContainerElementCompliantValueExtractors) {
        if (maximallySpecificContainerElementCompliantValueExtractors.size() == 1) {
            return maximallySpecificContainerElementCompliantValueExtractors.iterator().next();
        }
        if (maximallySpecificContainerElementCompliantValueExtractors.isEmpty()) {
            return null;
        }
        throw LOG.getUnableToGetMostSpecificValueExtractorDueToSeveralMaximallySpecificValueExtractorsDeclaredException(runtimeType, ValueExtractorHelper.toValueExtractorClasses(maximallySpecificContainerElementCompliantValueExtractors));
    }

    private Set<ValueExtractorDescriptor> getMaximallySpecificValueExtractors(Set<ValueExtractorDescriptor> possibleValueExtractors) {
        HashSet<ValueExtractorDescriptor> valueExtractorDescriptors = CollectionHelper.newHashSet(possibleValueExtractors.size());
        for (ValueExtractorDescriptor descriptor : possibleValueExtractors) {
            if (valueExtractorDescriptors.isEmpty()) {
                valueExtractorDescriptors.add(descriptor);
                continue;
            }
            Iterator candidatesIterator = valueExtractorDescriptors.iterator();
            boolean isNewRoot = true;
            while (candidatesIterator.hasNext()) {
                ValueExtractorDescriptor candidate = (ValueExtractorDescriptor)candidatesIterator.next();
                if (candidate.getContainerType().equals(descriptor.getContainerType())) continue;
                if (TypeHelper.isAssignable(candidate.getContainerType(), descriptor.getContainerType())) {
                    candidatesIterator.remove();
                    continue;
                }
                if (!TypeHelper.isAssignable(descriptor.getContainerType(), candidate.getContainerType())) continue;
                isNewRoot = false;
            }
            if (!isNewRoot) continue;
            valueExtractorDescriptors.add(descriptor);
        }
        return valueExtractorDescriptors;
    }

    private Set<ValueExtractorDescriptor> getRuntimeCompliantValueExtractors(Class<?> runtimeType, Set<ValueExtractorDescriptor> potentialValueExtractorDescriptors) {
        if (this.nonContainerTypes.contains(runtimeType)) {
            return Collections.emptySet();
        }
        Set<ValueExtractorDescriptor> valueExtractorDescriptors = this.possibleValueExtractorsByRuntimeType.get(runtimeType);
        if (valueExtractorDescriptors != null) {
            return valueExtractorDescriptors;
        }
        Set<ValueExtractorDescriptor> possibleValueExtractors = potentialValueExtractorDescriptors.stream().filter(e -> TypeHelper.isAssignable(e.getContainerType(), runtimeType)).collect(Collectors.toSet());
        valueExtractorDescriptors = this.getMaximallySpecificValueExtractors(possibleValueExtractors);
        if (valueExtractorDescriptors.isEmpty()) {
            this.nonContainerTypes.add(runtimeType);
            return Collections.emptySet();
        }
        Set<ValueExtractorDescriptor> valueExtractorDescriptorsToCache = CollectionHelper.toImmutableSet(valueExtractorDescriptors);
        Set<ValueExtractorDescriptor> cachedValueExtractorDescriptors = this.possibleValueExtractorsByRuntimeType.putIfAbsent(runtimeType, valueExtractorDescriptorsToCache);
        return cachedValueExtractorDescriptors != null ? cachedValueExtractorDescriptors : valueExtractorDescriptorsToCache;
    }

    private Set<ValueExtractorDescriptor> getRuntimeAndContainerElementCompliantValueExtractorsFromPossibleCandidates(Type declaredType, TypeVariable<?> typeParameter, Class<?> runtimeType, Collection<ValueExtractorDescriptor> valueExtractorCandidates) {
        if (this.nonContainerTypes.contains(runtimeType)) {
            return Collections.emptySet();
        }
        ValueExtractorCacheKey cacheKey = new ValueExtractorCacheKey(runtimeType, typeParameter);
        Set<ValueExtractorDescriptor> valueExtractorDescriptors = this.possibleValueExtractorsByRuntimeTypeAndTypeParameter.get(cacheKey);
        if (valueExtractorDescriptors != null) {
            return valueExtractorDescriptors;
        }
        boolean isInternal = TypeVariables.isInternal(typeParameter);
        Class<?> erasedDeclaredType = TypeHelper.getErasedReferenceType(declaredType);
        Set<ValueExtractorDescriptor> possibleValueExtractors = valueExtractorCandidates.stream().filter(e -> TypeHelper.isAssignable(e.getContainerType(), runtimeType)).filter(extractorDescriptor -> this.checkValueExtractorTypeCompatibility(typeParameter, isInternal, erasedDeclaredType, (ValueExtractorDescriptor)extractorDescriptor)).collect(Collectors.toSet());
        valueExtractorDescriptors = this.getMaximallySpecificValueExtractors(possibleValueExtractors);
        if (valueExtractorDescriptors.isEmpty()) {
            this.nonContainerTypes.add(runtimeType);
            return Collections.emptySet();
        }
        Set<ValueExtractorDescriptor> valueExtractorDescriptorsToCache = CollectionHelper.toImmutableSet(valueExtractorDescriptors);
        Set<ValueExtractorDescriptor> cachedValueExtractorDescriptors = this.possibleValueExtractorsByRuntimeTypeAndTypeParameter.putIfAbsent(cacheKey, valueExtractorDescriptorsToCache);
        return cachedValueExtractorDescriptors != null ? cachedValueExtractorDescriptors : valueExtractorDescriptorsToCache;
    }

    private boolean checkValueExtractorTypeCompatibility(TypeVariable<?> typeParameter, boolean isInternal, Class<?> erasedDeclaredType, ValueExtractorDescriptor extractorDescriptor) {
        return TypeHelper.isAssignable(extractorDescriptor.getContainerType(), erasedDeclaredType) ? this.validateValueExtractorCompatibility(isInternal, erasedDeclaredType, extractorDescriptor.getContainerType(), typeParameter, extractorDescriptor.getExtractedTypeParameter()) : this.validateValueExtractorCompatibility(isInternal, extractorDescriptor.getContainerType(), erasedDeclaredType, extractorDescriptor.getExtractedTypeParameter(), typeParameter);
    }

    private boolean validateValueExtractorCompatibility(boolean isInternal, Class<?> typeForBinding, Class<?> typeToBind, TypeVariable<?> typeParameterForBinding, TypeVariable<?> typeParameterToCompare) {
        TypeVariable<?> typeParameterBoundToExtractorType;
        if (!isInternal) {
            Map<Class<?>, Map<TypeVariable<?>, TypeVariable<?>>> allBindings = TypeVariableBindings.getTypeVariableBindings(typeForBinding);
            Map<TypeVariable<?>, TypeVariable<?>> bindingsForExtractorType = allBindings.get(typeToBind);
            typeParameterBoundToExtractorType = this.bind(typeParameterForBinding, bindingsForExtractorType);
        } else {
            typeParameterBoundToExtractorType = typeParameterForBinding;
        }
        return Objects.equals(typeParameterToCompare, typeParameterBoundToExtractorType);
    }

    private TypeVariable<?> bind(TypeVariable<?> typeParameter, Map<TypeVariable<?>, TypeVariable<?>> bindings) {
        return bindings != null ? bindings.get(typeParameter) : null;
    }

    private static class ValueExtractorCacheKey {
        private Class<?> type;
        private TypeVariable<?> typeParameter;
        private int hashCode;

        ValueExtractorCacheKey(Class<?> type, TypeVariable<?> typeParameter) {
            this.type = type;
            this.typeParameter = typeParameter;
            this.hashCode = this.buildHashCode();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            ValueExtractorCacheKey that = (ValueExtractorCacheKey)o;
            return Objects.equals(this.type, that.type) && Objects.equals(this.typeParameter, that.typeParameter);
        }

        public int hashCode() {
            return this.hashCode;
        }

        private int buildHashCode() {
            int result = this.type.hashCode();
            result = 31 * result + (this.typeParameter != null ? this.typeParameter.hashCode() : 0);
            return result;
        }
    }
}

