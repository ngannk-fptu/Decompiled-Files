/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.metadata.ValidateUnwrappedValue
 */
package org.hibernate.validator.internal.metadata.core;

import com.fasterxml.classmate.ResolvedType;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.metadata.ValidateUnwrappedValue;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorHelper;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.metadata.location.TypeArgumentConstraintLocation;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.TypeVariableBindings;
import org.hibernate.validator.internal.util.TypeVariables;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class MetaConstraints {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    private MetaConstraints() {
    }

    public static <A extends Annotation> MetaConstraint<A> create(TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, ConstraintDescriptorImpl<A> constraintDescriptor, ConstraintLocation location) {
        ArrayList<MetaConstraint.ContainerClassTypeParameterAndExtractor> valueExtractionPath = new ArrayList<MetaConstraint.ContainerClassTypeParameterAndExtractor>();
        Type typeOfValidatedElement = MetaConstraints.addValueExtractorDescriptorForWrappedValue(typeResolutionHelper, valueExtractorManager, constraintDescriptor, valueExtractionPath, location);
        ConstraintLocation current = location;
        do {
            if (current instanceof TypeArgumentConstraintLocation) {
                MetaConstraints.addValueExtractorDescriptorForTypeArgumentLocation(valueExtractorManager, valueExtractionPath, (TypeArgumentConstraintLocation)current);
                current = ((TypeArgumentConstraintLocation)current).getDelegate();
                continue;
            }
            current = null;
        } while (current != null);
        Collections.reverse(valueExtractionPath);
        return new MetaConstraint<A>(constraintDescriptor, location, valueExtractionPath, typeOfValidatedElement);
    }

    private static <A extends Annotation> Type addValueExtractorDescriptorForWrappedValue(TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, ConstraintDescriptorImpl<A> constraintDescriptor, List<MetaConstraint.ContainerClassTypeParameterAndExtractor> valueExtractionPath, ConstraintLocation location) {
        ValueExtractorDescriptor selectedValueExtractorDescriptor;
        Class<?> declaredType;
        block11: {
            Set<ValueExtractorDescriptor> valueExtractorDescriptorCandidates;
            block10: {
                if (ValidateUnwrappedValue.SKIP.equals((Object)constraintDescriptor.getValueUnwrapping())) {
                    return location.getTypeForValidatorResolution();
                }
                declaredType = TypeHelper.getErasedReferenceType(location.getTypeForValidatorResolution());
                valueExtractorDescriptorCandidates = valueExtractorManager.getResolver().getMaximallySpecificValueExtractors(declaredType);
                if (!ValidateUnwrappedValue.UNWRAP.equals((Object)constraintDescriptor.getValueUnwrapping())) break block10;
                switch (valueExtractorDescriptorCandidates.size()) {
                    case 0: {
                        throw LOG.getNoValueExtractorFoundForUnwrapException(declaredType);
                    }
                    case 1: {
                        selectedValueExtractorDescriptor = valueExtractorDescriptorCandidates.iterator().next();
                        break block11;
                    }
                    default: {
                        throw LOG.getUnableToGetMostSpecificValueExtractorDueToSeveralMaximallySpecificValueExtractorsDeclaredException(declaredType, ValueExtractorHelper.toValueExtractorClasses(valueExtractorDescriptorCandidates));
                    }
                }
            }
            Set<ValueExtractorDescriptor> unwrapByDefaultValueExtractorDescriptorCandidates = valueExtractorDescriptorCandidates.stream().filter(ved -> ved.isUnwrapByDefault()).collect(Collectors.toSet());
            switch (unwrapByDefaultValueExtractorDescriptorCandidates.size()) {
                case 0: {
                    return location.getTypeForValidatorResolution();
                }
                case 1: {
                    selectedValueExtractorDescriptor = (ValueExtractorDescriptor)unwrapByDefaultValueExtractorDescriptorCandidates.iterator().next();
                    break;
                }
                default: {
                    throw LOG.getImplicitUnwrappingNotAllowedWhenSeveralMaximallySpecificValueExtractorsMarkedWithUnwrapByDefaultDeclaredException(declaredType, ValueExtractorHelper.toValueExtractorClasses(unwrapByDefaultValueExtractorDescriptorCandidates));
                }
            }
        }
        if (selectedValueExtractorDescriptor.getExtractedType().isPresent()) {
            valueExtractionPath.add(new MetaConstraint.ContainerClassTypeParameterAndExtractor(declaredType, null, null, selectedValueExtractorDescriptor));
            return selectedValueExtractorDescriptor.getExtractedType().get();
        }
        Class<?> wrappedValueType = MetaConstraints.getWrappedValueType(typeResolutionHelper, location.getTypeForValidatorResolution(), selectedValueExtractorDescriptor);
        TypeVariable<?> typeParameter = MetaConstraints.getContainerClassTypeParameter(declaredType, selectedValueExtractorDescriptor);
        valueExtractionPath.add(new MetaConstraint.ContainerClassTypeParameterAndExtractor(declaredType, typeParameter, TypeVariables.getTypeParameterIndex(typeParameter), selectedValueExtractorDescriptor));
        return wrappedValueType;
    }

    private static void addValueExtractorDescriptorForTypeArgumentLocation(ValueExtractorManager valueExtractorManager, List<MetaConstraint.ContainerClassTypeParameterAndExtractor> valueExtractionPath, TypeArgumentConstraintLocation typeArgumentConstraintLocation) {
        Class<?> declaredType = typeArgumentConstraintLocation.getContainerClass();
        TypeVariable<?> typeParameter = typeArgumentConstraintLocation.getTypeParameter();
        ValueExtractorDescriptor valueExtractorDescriptor = valueExtractorManager.getResolver().getMaximallySpecificAndContainerElementCompliantValueExtractor(declaredType, typeParameter);
        if (valueExtractorDescriptor == null) {
            throw LOG.getNoValueExtractorFoundForTypeException(declaredType, typeParameter);
        }
        TypeVariable<?> actualTypeParameter = TypeVariables.getActualTypeParameter(typeParameter);
        valueExtractionPath.add(new MetaConstraint.ContainerClassTypeParameterAndExtractor(TypeVariables.getContainerClass(typeParameter), actualTypeParameter, TypeVariables.getTypeParameterIndex(actualTypeParameter), valueExtractorDescriptor));
    }

    private static Class<?> getWrappedValueType(TypeResolutionHelper typeResolutionHelper, Type declaredType, ValueExtractorDescriptor valueExtractorDescriptor) {
        ResolvedType resolvedType = typeResolutionHelper.getTypeResolver().resolve(declaredType, new Type[0]);
        List<ResolvedType> resolvedTypeParameters = resolvedType.typeParametersFor(valueExtractorDescriptor.getContainerType());
        if (resolvedTypeParameters == null || resolvedTypeParameters.isEmpty()) {
            throw LOG.getNoValueExtractorFoundForUnwrapException(declaredType);
        }
        return resolvedTypeParameters.get(TypeVariables.getTypeParameterIndex(valueExtractorDescriptor.getExtractedTypeParameter())).getErasedType();
    }

    private static TypeVariable<?> getContainerClassTypeParameter(Class<?> declaredType, ValueExtractorDescriptor selectedValueExtractorDescriptor) {
        if (selectedValueExtractorDescriptor.getExtractedTypeParameter() == null) {
            return null;
        }
        Map<Class<?>, Map<TypeVariable<?>, TypeVariable<?>>> allBindings = TypeVariableBindings.getTypeVariableBindings(declaredType);
        Map<TypeVariable<?>, TypeVariable<?>> extractorTypeBindings = allBindings.get(selectedValueExtractorDescriptor.getContainerType());
        if (extractorTypeBindings == null) {
            return null;
        }
        return extractorTypeBindings.entrySet().stream().filter(e -> Objects.equals(((TypeVariable)e.getKey()).getGenericDeclaration(), declaredType)).collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)).get(selectedValueExtractorDescriptor.getExtractedTypeParameter());
    }
}

