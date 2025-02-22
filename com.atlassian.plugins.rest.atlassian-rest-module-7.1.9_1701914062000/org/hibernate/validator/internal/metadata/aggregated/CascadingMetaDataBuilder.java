/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.GroupSequence
 */
package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.GroupSequence;
import org.hibernate.validator.internal.engine.valueextraction.AnnotatedObject;
import org.hibernate.validator.internal.engine.valueextraction.ArrayElement;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorHelper;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData;
import org.hibernate.validator.internal.metadata.aggregated.ContainerCascadingMetaData;
import org.hibernate.validator.internal.metadata.aggregated.NonContainerCascadingMetaData;
import org.hibernate.validator.internal.metadata.aggregated.PotentiallyContainerCascadingMetaData;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.TypeVariableBindings;
import org.hibernate.validator.internal.util.TypeVariables;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class CascadingMetaDataBuilder {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final CascadingMetaDataBuilder NON_CASCADING = new CascadingMetaDataBuilder(null, null, null, null, false, Collections.emptyMap(), Collections.emptyMap());
    private final Type enclosingType;
    private final TypeVariable<?> typeParameter;
    private final Class<?> declaredContainerClass;
    private final TypeVariable<?> declaredTypeParameter;
    private final Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData;
    private final boolean cascading;
    private final Map<Class<?>, Class<?>> groupConversions;
    private final boolean hasContainerElementsMarkedForCascading;
    private final boolean hasGroupConversionsOnAnnotatedObjectOrContainerElements;

    public CascadingMetaDataBuilder(Type enclosingType, TypeVariable<?> typeParameter, boolean cascading, Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData, Map<Class<?>, Class<?>> groupConversions) {
        this(enclosingType, typeParameter, TypeVariables.getContainerClass(typeParameter), TypeVariables.getActualTypeParameter(typeParameter), cascading, containerElementTypesCascadingMetaData, groupConversions);
    }

    private CascadingMetaDataBuilder(Type enclosingType, TypeVariable<?> typeParameter, Class<?> declaredContainerClass, TypeVariable<?> declaredTypeParameter, boolean cascading, Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData, Map<Class<?>, Class<?>> groupConversions) {
        this.enclosingType = enclosingType;
        this.typeParameter = typeParameter;
        this.declaredContainerClass = declaredContainerClass;
        this.declaredTypeParameter = declaredTypeParameter;
        this.cascading = cascading;
        this.groupConversions = CollectionHelper.toImmutableMap(groupConversions);
        this.containerElementTypesCascadingMetaData = CollectionHelper.toImmutableMap(containerElementTypesCascadingMetaData);
        boolean tmpHasContainerElementsMarkedForCascading = false;
        boolean tmpHasGroupConversionsOnAnnotatedObjectOrContainerElements = !groupConversions.isEmpty();
        for (CascadingMetaDataBuilder nestedCascadingTypeParameter : containerElementTypesCascadingMetaData.values()) {
            tmpHasContainerElementsMarkedForCascading = tmpHasContainerElementsMarkedForCascading || nestedCascadingTypeParameter.cascading || nestedCascadingTypeParameter.hasContainerElementsMarkedForCascading;
            tmpHasGroupConversionsOnAnnotatedObjectOrContainerElements = tmpHasGroupConversionsOnAnnotatedObjectOrContainerElements || nestedCascadingTypeParameter.hasGroupConversionsOnAnnotatedObjectOrContainerElements;
        }
        this.hasContainerElementsMarkedForCascading = tmpHasContainerElementsMarkedForCascading;
        this.hasGroupConversionsOnAnnotatedObjectOrContainerElements = tmpHasGroupConversionsOnAnnotatedObjectOrContainerElements;
    }

    public static CascadingMetaDataBuilder nonCascading() {
        return NON_CASCADING;
    }

    public static CascadingMetaDataBuilder annotatedObject(Type cascadableType, boolean cascading, Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData, Map<Class<?>, Class<?>> groupConversions) {
        return new CascadingMetaDataBuilder(cascadableType, AnnotatedObject.INSTANCE, cascading, containerElementTypesCascadingMetaData, groupConversions);
    }

    public TypeVariable<?> getTypeParameter() {
        return this.typeParameter;
    }

    public Type getEnclosingType() {
        return this.enclosingType;
    }

    public Class<?> getDeclaredContainerClass() {
        return this.declaredContainerClass;
    }

    public TypeVariable<?> getDeclaredTypeParameter() {
        return this.declaredTypeParameter;
    }

    public boolean isCascading() {
        return this.cascading;
    }

    public Map<Class<?>, Class<?>> getGroupConversions() {
        return this.groupConversions;
    }

    public boolean hasContainerElementsMarkedForCascading() {
        return this.hasContainerElementsMarkedForCascading;
    }

    public boolean isMarkedForCascadingOnAnnotatedObjectOrContainerElements() {
        return this.cascading || this.hasContainerElementsMarkedForCascading;
    }

    public boolean hasGroupConversionsOnAnnotatedObjectOrContainerElements() {
        return this.hasGroupConversionsOnAnnotatedObjectOrContainerElements;
    }

    public Map<TypeVariable<?>, CascadingMetaDataBuilder> getContainerElementTypesCascadingMetaData() {
        return this.containerElementTypesCascadingMetaData;
    }

    public CascadingMetaDataBuilder merge(CascadingMetaDataBuilder otherCascadingTypeParameter) {
        if (this == NON_CASCADING) {
            return otherCascadingTypeParameter;
        }
        if (otherCascadingTypeParameter == NON_CASCADING) {
            return this;
        }
        boolean cascading = this.cascading || otherCascadingTypeParameter.cascading;
        Map<Class<?>, Class<?>> groupConversions = CascadingMetaDataBuilder.mergeGroupConversion(this.groupConversions, otherCascadingTypeParameter.groupConversions);
        Map<TypeVariable<?>, CascadingMetaDataBuilder> nestedCascadingTypeParameterMap = Stream.concat(this.containerElementTypesCascadingMetaData.entrySet().stream(), otherCascadingTypeParameter.containerElementTypesCascadingMetaData.entrySet().stream()).collect(Collectors.toMap(entry -> (TypeVariable)entry.getKey(), entry -> (CascadingMetaDataBuilder)entry.getValue(), (value1, value2) -> value1.merge((CascadingMetaDataBuilder)value2)));
        return new CascadingMetaDataBuilder(this.enclosingType, this.typeParameter, cascading, nestedCascadingTypeParameterMap, groupConversions);
    }

    public CascadingMetaData build(ValueExtractorManager valueExtractorManager, Object context) {
        this.validateGroupConversions(context);
        if (!this.cascading) {
            if (!this.containerElementTypesCascadingMetaData.isEmpty() && this.hasContainerElementsMarkedForCascading) {
                return ContainerCascadingMetaData.of(valueExtractorManager, this, context);
            }
            return NonContainerCascadingMetaData.of(this, context);
        }
        Set<ValueExtractorDescriptor> containerDetectionValueExtractorCandidates = valueExtractorManager.getResolver().getValueExtractorCandidatesForContainerDetectionOfGlobalCascadedValidation(this.enclosingType);
        if (!containerDetectionValueExtractorCandidates.isEmpty()) {
            if (containerDetectionValueExtractorCandidates.size() > 1) {
                throw LOG.getUnableToGetMostSpecificValueExtractorDueToSeveralMaximallySpecificValueExtractorsDeclaredException(ReflectionHelper.getClassFromType(this.enclosingType), ValueExtractorHelper.toValueExtractorClasses(containerDetectionValueExtractorCandidates));
            }
            return ContainerCascadingMetaData.of(valueExtractorManager, new CascadingMetaDataBuilder(this.enclosingType, this.typeParameter, this.cascading, CascadingMetaDataBuilder.addCascadingMetaDataBasedOnContainerDetection(this.enclosingType, this.containerElementTypesCascadingMetaData, this.groupConversions, containerDetectionValueExtractorCandidates.iterator().next()), this.groupConversions), context);
        }
        Set<ValueExtractorDescriptor> potentialValueExtractorCandidates = valueExtractorManager.getResolver().getPotentialValueExtractorCandidatesForCascadedValidation(this.enclosingType);
        if (!potentialValueExtractorCandidates.isEmpty()) {
            return PotentiallyContainerCascadingMetaData.of(this, potentialValueExtractorCandidates, context);
        }
        return NonContainerCascadingMetaData.of(this, context);
    }

    private void validateGroupConversions(Object context) {
        if (!this.cascading && !this.groupConversions.isEmpty()) {
            throw LOG.getGroupConversionOnNonCascadingElementException(context);
        }
        for (Class<?> group : this.groupConversions.keySet()) {
            if (!group.isAnnotationPresent(GroupSequence.class)) continue;
            throw LOG.getGroupConversionForSequenceException(group);
        }
        for (CascadingMetaDataBuilder containerElementCascadingTypeParameter : this.containerElementTypesCascadingMetaData.values()) {
            containerElementCascadingTypeParameter.validateGroupConversions(context);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(" [");
        sb.append("enclosingType=").append(StringHelper.toShortString(this.enclosingType)).append(", ");
        sb.append("typeParameter=").append(this.typeParameter).append(", ");
        sb.append("cascading=").append(this.cascading).append(", ");
        sb.append("groupConversions=").append(this.groupConversions).append(", ");
        sb.append("containerElementTypesCascadingMetaData=").append(this.containerElementTypesCascadingMetaData);
        sb.append("]");
        return sb.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.typeParameter.hashCode();
        result = 31 * result + (this.cascading ? 1 : 0);
        result = 31 * result + this.groupConversions.hashCode();
        result = 31 * result + this.containerElementTypesCascadingMetaData.hashCode();
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
        CascadingMetaDataBuilder other = (CascadingMetaDataBuilder)obj;
        if (!this.typeParameter.equals(other.typeParameter)) {
            return false;
        }
        if (this.cascading != other.cascading) {
            return false;
        }
        if (!this.groupConversions.equals(other.groupConversions)) {
            return false;
        }
        return this.containerElementTypesCascadingMetaData.equals(other.containerElementTypesCascadingMetaData);
    }

    private static Map<Class<?>, Class<?>> mergeGroupConversion(Map<Class<?>, Class<?>> groupConversions, Map<Class<?>, Class<?>> otherGroupConversions) {
        if (groupConversions.isEmpty() && otherGroupConversions.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap mergedGroupConversions = new HashMap(groupConversions.size() + otherGroupConversions.size());
        for (Map.Entry<Class<?>, Class<?>> otherGroupConversionEntry : otherGroupConversions.entrySet()) {
            if (!groupConversions.containsKey(otherGroupConversionEntry.getKey())) continue;
            throw LOG.getMultipleGroupConversionsForSameSourceException(otherGroupConversionEntry.getKey(), CollectionHelper.asSet(groupConversions.get(otherGroupConversionEntry.getKey()), otherGroupConversionEntry.getValue()));
        }
        mergedGroupConversions.putAll(groupConversions);
        mergedGroupConversions.putAll(otherGroupConversions);
        return mergedGroupConversions;
    }

    private static Map<TypeVariable<?>, CascadingMetaDataBuilder> addCascadingMetaDataBasedOnContainerDetection(Type cascadableType, Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData, Map<Class<?>, Class<?>> groupConversions, ValueExtractorDescriptor possibleValueExtractor) {
        Class<?> cascadableClass = ReflectionHelper.getClassFromType(cascadableType);
        if (cascadableClass.isArray()) {
            return CascadingMetaDataBuilder.addArrayElementCascadingMetaData(cascadableClass, containerElementTypesCascadingMetaData, groupConversions);
        }
        Map<TypeVariable<?>, CascadingMetaDataBuilder> cascadingMetaData = containerElementTypesCascadingMetaData;
        cascadingMetaData = CascadingMetaDataBuilder.addCascadingMetaData(cascadableClass, possibleValueExtractor.getContainerType(), possibleValueExtractor.getExtractedTypeParameter(), cascadingMetaData, groupConversions);
        return cascadingMetaData;
    }

    private static Map<TypeVariable<?>, CascadingMetaDataBuilder> addCascadingMetaData(Class<?> enclosingType, Class<?> referenceType, TypeVariable<?> typeParameter, Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData, Map<Class<?>, Class<?>> groupConversions) {
        TypeVariable<?> cascadableTypeParameter;
        Class<?> cascadableClass;
        Map<Class<?>, Map<TypeVariable<?>, TypeVariable<?>>> typeVariableBindings = TypeVariableBindings.getTypeVariableBindings(enclosingType);
        TypeVariable<?> correspondingTypeParameter = typeVariableBindings.get(referenceType).entrySet().stream().filter(e -> Objects.equals(((TypeVariable)e.getKey()).getGenericDeclaration(), enclosingType)).collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)).get(typeParameter);
        if (correspondingTypeParameter != null) {
            cascadableClass = enclosingType;
            cascadableTypeParameter = correspondingTypeParameter;
        } else {
            cascadableClass = referenceType;
            cascadableTypeParameter = typeParameter;
        }
        HashMap<TypeVariable<?>, CascadingMetaDataBuilder> amendedCascadingMetadata = CollectionHelper.newHashMap(containerElementTypesCascadingMetaData.size() + 1);
        amendedCascadingMetadata.putAll(containerElementTypesCascadingMetaData);
        if (containerElementTypesCascadingMetaData.containsKey(cascadableTypeParameter)) {
            amendedCascadingMetadata.put(cascadableTypeParameter, CascadingMetaDataBuilder.makeCascading(containerElementTypesCascadingMetaData.get(cascadableTypeParameter), groupConversions));
        } else {
            amendedCascadingMetadata.put(cascadableTypeParameter, new CascadingMetaDataBuilder(cascadableClass, cascadableTypeParameter, enclosingType, correspondingTypeParameter, true, Collections.emptyMap(), groupConversions));
        }
        return amendedCascadingMetadata;
    }

    private static Map<TypeVariable<?>, CascadingMetaDataBuilder> addArrayElementCascadingMetaData(Class<?> enclosingType, Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData, Map<Class<?>, Class<?>> groupConversions) {
        HashMap<TypeVariable<?>, CascadingMetaDataBuilder> amendedCascadingMetadata = CollectionHelper.newHashMap(containerElementTypesCascadingMetaData.size() + 1);
        amendedCascadingMetadata.putAll(containerElementTypesCascadingMetaData);
        ArrayElement cascadableTypeParameter = new ArrayElement(enclosingType);
        amendedCascadingMetadata.put(cascadableTypeParameter, new CascadingMetaDataBuilder(enclosingType, cascadableTypeParameter, true, Collections.emptyMap(), groupConversions));
        return amendedCascadingMetadata;
    }

    private static CascadingMetaDataBuilder makeCascading(CascadingMetaDataBuilder cascadingTypeParameter, Map<Class<?>, Class<?>> groupConversions) {
        return new CascadingMetaDataBuilder(cascadingTypeParameter.enclosingType, cascadingTypeParameter.typeParameter, true, cascadingTypeParameter.containerElementTypesCascadingMetaData, cascadingTypeParameter.groupConversions.isEmpty() ? groupConversions : cascadingTypeParameter.groupConversions);
    }
}

