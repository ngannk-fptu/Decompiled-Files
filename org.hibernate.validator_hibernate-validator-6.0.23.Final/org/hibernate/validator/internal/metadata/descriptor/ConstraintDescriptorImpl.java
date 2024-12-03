/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Constraint
 *  javax.validation.ConstraintTarget
 *  javax.validation.ConstraintValidator
 *  javax.validation.OverridesAttribute
 *  javax.validation.OverridesAttribute$List
 *  javax.validation.Payload
 *  javax.validation.ReportAsSingleViolation
 *  javax.validation.constraintvalidation.SupportedValidationTarget
 *  javax.validation.constraintvalidation.ValidationTarget
 *  javax.validation.groups.Default
 *  javax.validation.metadata.ConstraintDescriptor
 *  javax.validation.metadata.ValidateUnwrappedValue
 *  javax.validation.valueextraction.Unwrapping$Skip
 *  javax.validation.valueextraction.Unwrapping$Unwrap
 */
package org.hibernate.validator.internal.metadata.descriptor;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Constraint;
import javax.validation.ConstraintTarget;
import javax.validation.ConstraintValidator;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import javax.validation.groups.Default;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ValidateUnwrappedValue;
import javax.validation.valueextraction.Unwrapping;
import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.ConstraintOrigin;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.annotation.ConstraintAnnotationDescriptor;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetAnnotationAttributes;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredMethods;
import org.hibernate.validator.internal.util.privilegedactions.GetMethod;

public class ConstraintDescriptorImpl<T extends Annotation>
implements ConstraintDescriptor<T>,
Serializable {
    private static final long serialVersionUID = -2563102960314069246L;
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final int OVERRIDES_PARAMETER_DEFAULT_INDEX = -1;
    private static final List<String> NON_COMPOSING_CONSTRAINT_ANNOTATIONS = Arrays.asList(Documented.class.getName(), Retention.class.getName(), Target.class.getName(), Constraint.class.getName(), ReportAsSingleViolation.class.getName(), Repeatable.class.getName(), Deprecated.class.getName());
    private final ConstraintAnnotationDescriptor<T> annotationDescriptor;
    private final List<Class<? extends ConstraintValidator<T, ?>>> constraintValidatorClasses;
    private final transient List<ConstraintValidatorDescriptor<T>> matchingConstraintValidatorDescriptors;
    private final Set<Class<?>> groups;
    private final Set<Class<? extends Payload>> payloads;
    private final Set<ConstraintDescriptorImpl<?>> composingConstraints;
    private final boolean isReportAsSingleInvalidConstraint;
    private final ElementType elementType;
    private final ConstraintOrigin definedOn;
    private final ConstraintType constraintType;
    private final ValidateUnwrappedValue valueUnwrapping;
    private final ConstraintTarget validationAppliesTo;
    private final CompositionType compositionType;
    private final int hashCode;

    public ConstraintDescriptorImpl(ConstraintHelper constraintHelper, Member member, ConstraintAnnotationDescriptor<T> annotationDescriptor, ElementType type, Class<?> implicitGroup, ConstraintOrigin definedOn, ConstraintType externalConstraintType) {
        this.annotationDescriptor = annotationDescriptor;
        this.elementType = type;
        this.definedOn = definedOn;
        this.isReportAsSingleInvalidConstraint = annotationDescriptor.getType().isAnnotationPresent(ReportAsSingleViolation.class);
        this.groups = ConstraintDescriptorImpl.buildGroupSet(annotationDescriptor, implicitGroup);
        this.payloads = ConstraintDescriptorImpl.buildPayloadSet(annotationDescriptor);
        this.valueUnwrapping = ConstraintDescriptorImpl.determineValueUnwrapping(this.payloads, member, annotationDescriptor.getType());
        this.validationAppliesTo = ConstraintDescriptorImpl.determineValidationAppliesTo(annotationDescriptor);
        this.constraintValidatorClasses = constraintHelper.getAllValidatorDescriptors(annotationDescriptor.getType()).stream().map(ConstraintValidatorDescriptor::getValidatorClass).collect(Collectors.collectingAndThen(Collectors.toList(), CollectionHelper::toImmutableList));
        List crossParameterValidatorDescriptors = CollectionHelper.toImmutableList(constraintHelper.findValidatorDescriptors(annotationDescriptor.getType(), ValidationTarget.PARAMETERS));
        List genericValidatorDescriptors = CollectionHelper.toImmutableList(constraintHelper.findValidatorDescriptors(annotationDescriptor.getType(), ValidationTarget.ANNOTATED_ELEMENT));
        if (crossParameterValidatorDescriptors.size() > 1) {
            throw LOG.getMultipleCrossParameterValidatorClassesException(annotationDescriptor.getType());
        }
        this.constraintType = this.determineConstraintType(annotationDescriptor.getType(), member, type, !genericValidatorDescriptors.isEmpty(), !crossParameterValidatorDescriptors.isEmpty(), externalConstraintType);
        this.composingConstraints = this.parseComposingConstraints(constraintHelper, member, this.constraintType);
        this.compositionType = this.parseCompositionType(constraintHelper);
        this.validateComposingConstraintTypes();
        this.matchingConstraintValidatorDescriptors = this.constraintType == ConstraintType.GENERIC ? CollectionHelper.toImmutableList(genericValidatorDescriptors) : CollectionHelper.toImmutableList(crossParameterValidatorDescriptors);
        this.hashCode = annotationDescriptor.hashCode();
    }

    public ConstraintDescriptorImpl(ConstraintHelper constraintHelper, Member member, ConstraintAnnotationDescriptor<T> annotationDescriptor, ElementType type) {
        this(constraintHelper, member, annotationDescriptor, type, null, ConstraintOrigin.DEFINED_LOCALLY, null);
    }

    public ConstraintDescriptorImpl(ConstraintHelper constraintHelper, Member member, ConstraintAnnotationDescriptor<T> annotationDescriptor, ElementType type, ConstraintType constraintType) {
        this(constraintHelper, member, annotationDescriptor, type, null, ConstraintOrigin.DEFINED_LOCALLY, constraintType);
    }

    public ConstraintAnnotationDescriptor<T> getAnnotationDescriptor() {
        return this.annotationDescriptor;
    }

    public T getAnnotation() {
        return (T)this.annotationDescriptor.getAnnotation();
    }

    public Class<T> getAnnotationType() {
        return this.annotationDescriptor.getType();
    }

    public String getMessageTemplate() {
        return this.annotationDescriptor.getMessage();
    }

    public Set<Class<?>> getGroups() {
        return this.groups;
    }

    public Set<Class<? extends Payload>> getPayload() {
        return this.payloads;
    }

    public ConstraintTarget getValidationAppliesTo() {
        return this.validationAppliesTo;
    }

    public ValidateUnwrappedValue getValueUnwrapping() {
        return this.valueUnwrapping;
    }

    public List<Class<? extends ConstraintValidator<T, ?>>> getConstraintValidatorClasses() {
        return this.constraintValidatorClasses;
    }

    public List<ConstraintValidatorDescriptor<T>> getMatchingConstraintValidatorDescriptors() {
        return this.matchingConstraintValidatorDescriptors;
    }

    public Map<String, Object> getAttributes() {
        return this.annotationDescriptor.getAttributes();
    }

    public Set<ConstraintDescriptor<?>> getComposingConstraints() {
        return this.composingConstraints;
    }

    public Set<ConstraintDescriptorImpl<?>> getComposingConstraintImpls() {
        return this.composingConstraints;
    }

    public boolean isReportAsSingleViolation() {
        return this.isReportAsSingleInvalidConstraint;
    }

    public ElementType getElementType() {
        return this.elementType;
    }

    public ConstraintOrigin getDefinedOn() {
        return this.definedOn;
    }

    public ConstraintType getConstraintType() {
        return this.constraintType;
    }

    public <U> U unwrap(Class<U> type) {
        throw LOG.getUnwrappingOfConstraintDescriptorNotSupportedYetException();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ConstraintDescriptorImpl that = (ConstraintDescriptorImpl)o;
        return !(this.annotationDescriptor != null ? !this.annotationDescriptor.equals(that.annotationDescriptor) : that.annotationDescriptor != null);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ConstraintDescriptorImpl");
        sb.append("{annotation=").append(StringHelper.toShortString(this.annotationDescriptor.getType()));
        sb.append(", payloads=").append(this.payloads);
        sb.append(", hasComposingConstraints=").append(this.composingConstraints.isEmpty());
        sb.append(", isReportAsSingleInvalidConstraint=").append(this.isReportAsSingleInvalidConstraint);
        sb.append(", elementType=").append((Object)this.elementType);
        sb.append(", definedOn=").append((Object)this.definedOn);
        sb.append(", groups=").append(this.groups);
        sb.append(", attributes=").append(this.annotationDescriptor.getAttributes());
        sb.append(", constraintType=").append((Object)this.constraintType);
        sb.append(", valueUnwrapping=").append(this.valueUnwrapping);
        sb.append('}');
        return sb.toString();
    }

    private ConstraintType determineConstraintType(Class<? extends Annotation> constraintAnnotationType, Member member, ElementType elementType, boolean hasGenericValidators, boolean hasCrossParameterValidator, ConstraintType externalConstraintType) {
        ConstraintTarget constraintTarget = this.validationAppliesTo;
        ConstraintType constraintType = null;
        boolean isExecutable = this.isExecutable(elementType);
        if (constraintTarget == ConstraintTarget.RETURN_VALUE) {
            if (!isExecutable) {
                throw LOG.getParametersOrReturnValueConstraintTargetGivenAtNonExecutableException(this.annotationDescriptor.getType(), ConstraintTarget.RETURN_VALUE);
            }
            constraintType = ConstraintType.GENERIC;
        } else if (constraintTarget == ConstraintTarget.PARAMETERS) {
            if (!isExecutable) {
                throw LOG.getParametersOrReturnValueConstraintTargetGivenAtNonExecutableException(this.annotationDescriptor.getType(), ConstraintTarget.PARAMETERS);
            }
            constraintType = ConstraintType.CROSS_PARAMETER;
        } else if (externalConstraintType != null) {
            constraintType = externalConstraintType;
        } else if (hasGenericValidators && !hasCrossParameterValidator) {
            constraintType = ConstraintType.GENERIC;
        } else if (!hasGenericValidators && hasCrossParameterValidator) {
            constraintType = ConstraintType.CROSS_PARAMETER;
        } else if (!isExecutable) {
            constraintType = ConstraintType.GENERIC;
        } else if (constraintAnnotationType.isAnnotationPresent(SupportedValidationTarget.class)) {
            SupportedValidationTarget supportedValidationTarget = constraintAnnotationType.getAnnotation(SupportedValidationTarget.class);
            if (supportedValidationTarget.value().length == 1) {
                constraintType = supportedValidationTarget.value()[0] == ValidationTarget.ANNOTATED_ELEMENT ? ConstraintType.GENERIC : ConstraintType.CROSS_PARAMETER;
            }
        } else {
            boolean hasParameters = this.hasParameters(member);
            boolean hasReturnValue = this.hasReturnValue(member);
            if (!hasParameters && hasReturnValue) {
                constraintType = ConstraintType.GENERIC;
            } else if (hasParameters && !hasReturnValue) {
                constraintType = ConstraintType.CROSS_PARAMETER;
            }
        }
        if (constraintType == null) {
            throw LOG.getImplicitConstraintTargetInAmbiguousConfigurationException(this.annotationDescriptor.getType());
        }
        if (constraintType == ConstraintType.CROSS_PARAMETER) {
            this.validateCrossParameterConstraintType(member, hasCrossParameterValidator);
        }
        return constraintType;
    }

    private static ValidateUnwrappedValue determineValueUnwrapping(Set<Class<? extends Payload>> payloads, Member member, Class<? extends Annotation> annotationType) {
        if (payloads.contains(Unwrapping.Unwrap.class)) {
            if (payloads.contains(Unwrapping.Skip.class)) {
                throw LOG.getInvalidUnwrappingConfigurationForConstraintException(member, annotationType);
            }
            return ValidateUnwrappedValue.UNWRAP;
        }
        if (payloads.contains(Unwrapping.Skip.class)) {
            return ValidateUnwrappedValue.SKIP;
        }
        return ValidateUnwrappedValue.DEFAULT;
    }

    private static ConstraintTarget determineValidationAppliesTo(ConstraintAnnotationDescriptor<?> annotationDescriptor) {
        return annotationDescriptor.getValidationAppliesTo();
    }

    private void validateCrossParameterConstraintType(Member member, boolean hasCrossParameterValidator) {
        if (!hasCrossParameterValidator) {
            throw LOG.getCrossParameterConstraintHasNoValidatorException(this.annotationDescriptor.getType());
        }
        if (member == null) {
            throw LOG.getCrossParameterConstraintOnClassException(this.annotationDescriptor.getType());
        }
        if (member instanceof Field) {
            throw LOG.getCrossParameterConstraintOnFieldException(this.annotationDescriptor.getType(), member);
        }
        if (!this.hasParameters(member)) {
            throw LOG.getCrossParameterConstraintOnMethodWithoutParametersException(this.annotationDescriptor.getType(), (Executable)member);
        }
    }

    private void validateComposingConstraintTypes() {
        for (ConstraintDescriptorImpl<?> composingConstraint : this.getComposingConstraintImpls()) {
            if (composingConstraint.constraintType == this.constraintType) continue;
            throw LOG.getComposedAndComposingConstraintsHaveDifferentTypesException(this.annotationDescriptor.getType(), composingConstraint.annotationDescriptor.getType(), this.constraintType, composingConstraint.constraintType);
        }
    }

    private boolean hasParameters(Member member) {
        boolean hasParameters = false;
        if (member instanceof Constructor) {
            Constructor constructor = (Constructor)member;
            hasParameters = constructor.getParameterTypes().length > 0;
        } else if (member instanceof Method) {
            Method method = (Method)member;
            hasParameters = method.getParameterTypes().length > 0;
        }
        return hasParameters;
    }

    private boolean hasReturnValue(Member member) {
        Method method;
        boolean hasReturnValue = member instanceof Constructor ? true : (member instanceof Method ? (method = (Method)member).getGenericReturnType() != Void.TYPE : false);
        return hasReturnValue;
    }

    private boolean isExecutable(ElementType elementType) {
        return elementType == ElementType.METHOD || elementType == ElementType.CONSTRUCTOR;
    }

    private static Set<Class<? extends Payload>> buildPayloadSet(ConstraintAnnotationDescriptor<?> annotationDescriptor) {
        HashSet payloadSet = CollectionHelper.newHashSet();
        Class<Payload>[] payloadFromAnnotation = annotationDescriptor.getPayload();
        if (payloadFromAnnotation != null) {
            payloadSet.addAll(Arrays.asList(payloadFromAnnotation));
        }
        return CollectionHelper.toImmutableSet(payloadSet);
    }

    private static Set<Class<?>> buildGroupSet(ConstraintAnnotationDescriptor<?> annotationDescriptor, Class<?> implicitGroup) {
        HashSet groupSet = CollectionHelper.newHashSet();
        Class<?>[] groupsFromAnnotation = annotationDescriptor.getGroups();
        if (groupsFromAnnotation.length == 0) {
            groupSet.add(Default.class);
        } else {
            groupSet.addAll(Arrays.asList(groupsFromAnnotation));
        }
        if (implicitGroup != null && groupSet.contains(Default.class)) {
            groupSet.add(implicitGroup);
        }
        return CollectionHelper.toImmutableSet(groupSet);
    }

    private Map<ClassIndexWrapper, Map<String, Object>> parseOverrideParameters() {
        Method[] methods;
        HashMap<ClassIndexWrapper, Map<String, Object>> overrideParameters = CollectionHelper.newHashMap();
        for (Method m : methods = ConstraintDescriptorImpl.run(GetDeclaredMethods.action(this.annotationDescriptor.getType()))) {
            if (m.getAnnotation(OverridesAttribute.class) != null) {
                this.addOverrideAttributes(overrideParameters, m, m.getAnnotation(OverridesAttribute.class));
                continue;
            }
            if (m.getAnnotation(OverridesAttribute.List.class) == null) continue;
            this.addOverrideAttributes(overrideParameters, m, m.getAnnotation(OverridesAttribute.List.class).value());
        }
        return overrideParameters;
    }

    private void addOverrideAttributes(Map<ClassIndexWrapper, Map<String, Object>> overrideParameters, Method m, OverridesAttribute ... attributes) {
        Object value = this.annotationDescriptor.getAttribute(m.getName());
        for (OverridesAttribute overridesAttribute : attributes) {
            String overridesAttributeName = overridesAttribute.name().length() > 0 ? overridesAttribute.name() : m.getName();
            this.ensureAttributeIsOverridable(m, overridesAttribute, overridesAttributeName);
            ClassIndexWrapper wrapper = new ClassIndexWrapper(overridesAttribute.constraint(), overridesAttribute.constraintIndex());
            Map<String, Object> map = overrideParameters.get(wrapper);
            if (map == null) {
                map = CollectionHelper.newHashMap();
                overrideParameters.put(wrapper, map);
            }
            map.put(overridesAttributeName, value);
        }
    }

    private void ensureAttributeIsOverridable(Method m, OverridesAttribute overridesAttribute, String overridesAttributeName) {
        Method method = ConstraintDescriptorImpl.run(GetMethod.action(overridesAttribute.constraint(), overridesAttributeName));
        if (method == null) {
            throw LOG.getOverriddenConstraintAttributeNotFoundException(overridesAttributeName);
        }
        Class<?> returnTypeOfOverriddenConstraint = method.getReturnType();
        if (!returnTypeOfOverriddenConstraint.equals(m.getReturnType())) {
            throw LOG.getWrongAttributeTypeForOverriddenConstraintException(returnTypeOfOverriddenConstraint, m.getReturnType());
        }
    }

    private Set<ConstraintDescriptorImpl<?>> parseComposingConstraints(ConstraintHelper constraintHelper, Member member, ConstraintType constraintType) {
        HashSet composingConstraintsSet = CollectionHelper.newHashSet();
        Map<ClassIndexWrapper, Map<String, Object>> overrideParameters = this.parseOverrideParameters();
        HashMap<Class<? extends Annotation>, ComposingConstraintAnnotationLocation> composingConstraintLocations = new HashMap<Class<? extends Annotation>, ComposingConstraintAnnotationLocation>();
        for (Annotation declaredAnnotation : this.annotationDescriptor.getType().getDeclaredAnnotations()) {
            Class<? extends Annotation> declaredAnnotationType = declaredAnnotation.annotationType();
            if (NON_COMPOSING_CONSTRAINT_ANNOTATIONS.contains(declaredAnnotationType.getName())) continue;
            if (constraintHelper.isConstraintAnnotation(declaredAnnotationType)) {
                if (composingConstraintLocations.containsKey(declaredAnnotationType) && !ComposingConstraintAnnotationLocation.DIRECT.equals(composingConstraintLocations.get(declaredAnnotationType))) {
                    throw LOG.getCannotMixDirectAnnotationAndListContainerOnComposedConstraintException(this.annotationDescriptor.getType(), declaredAnnotationType);
                }
                ConstraintDescriptorImpl<Annotation> descriptor = this.createComposingConstraintDescriptor(constraintHelper, member, overrideParameters, -1, declaredAnnotation, constraintType);
                composingConstraintsSet.add(descriptor);
                composingConstraintLocations.put(declaredAnnotationType, ComposingConstraintAnnotationLocation.DIRECT);
                LOG.debugf("Adding composing constraint: %s.", descriptor);
                continue;
            }
            if (!constraintHelper.isMultiValueConstraint(declaredAnnotationType)) continue;
            List<Annotation> multiValueConstraints = constraintHelper.getConstraintsFromMultiValueConstraint(declaredAnnotation);
            int index = 0;
            for (Annotation constraintAnnotation : multiValueConstraints) {
                if (composingConstraintLocations.containsKey(constraintAnnotation.annotationType()) && !ComposingConstraintAnnotationLocation.IN_CONTAINER.equals(composingConstraintLocations.get(constraintAnnotation.annotationType()))) {
                    throw LOG.getCannotMixDirectAnnotationAndListContainerOnComposedConstraintException(this.annotationDescriptor.getType(), constraintAnnotation.annotationType());
                }
                ConstraintDescriptorImpl<Annotation> descriptor = this.createComposingConstraintDescriptor(constraintHelper, member, overrideParameters, index, constraintAnnotation, constraintType);
                composingConstraintsSet.add(descriptor);
                composingConstraintLocations.put(constraintAnnotation.annotationType(), ComposingConstraintAnnotationLocation.IN_CONTAINER);
                LOG.debugf("Adding composing constraint: %s.", descriptor);
                ++index;
            }
        }
        return CollectionHelper.toImmutableSet(composingConstraintsSet);
    }

    private CompositionType parseCompositionType(ConstraintHelper constraintHelper) {
        for (Annotation declaredAnnotation : this.annotationDescriptor.getType().getDeclaredAnnotations()) {
            Class<? extends Annotation> declaredAnnotationType = declaredAnnotation.annotationType();
            if (NON_COMPOSING_CONSTRAINT_ANNOTATIONS.contains(declaredAnnotationType.getName()) || !constraintHelper.isConstraintComposition(declaredAnnotationType)) continue;
            if (LOG.isDebugEnabled()) {
                LOG.debugf("Adding Bool %s.", declaredAnnotationType.getName());
            }
            return ((ConstraintComposition)declaredAnnotation).value();
        }
        return CompositionType.AND;
    }

    private <U extends Annotation> ConstraintDescriptorImpl<U> createComposingConstraintDescriptor(ConstraintHelper constraintHelper, Member member, Map<ClassIndexWrapper, Map<String, Object>> overrideParameters, int index, U constraintAnnotation, ConstraintType constraintType) {
        Class<? extends Annotation> annotationType = constraintAnnotation.annotationType();
        ConstraintAnnotationDescriptor.Builder<? extends Annotation> annotationDescriptorBuilder = new ConstraintAnnotationDescriptor.Builder<Annotation>(annotationType, ConstraintDescriptorImpl.run(GetAnnotationAttributes.action(constraintAnnotation)));
        Map<String, Object> overrides = overrideParameters.get(new ClassIndexWrapper(annotationType, index));
        if (overrides != null) {
            for (Map.Entry<String, Object> entry : overrides.entrySet()) {
                annotationDescriptorBuilder.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        annotationDescriptorBuilder.setGroups(this.groups.toArray(new Class[this.groups.size()]));
        annotationDescriptorBuilder.setPayload(this.payloads.toArray(new Class[this.payloads.size()]));
        if (annotationDescriptorBuilder.hasAttribute("validationAppliesTo")) {
            ConstraintTarget validationAppliesTo = this.getValidationAppliesTo();
            if (validationAppliesTo == null) {
                validationAppliesTo = constraintType == ConstraintType.CROSS_PARAMETER ? ConstraintTarget.PARAMETERS : ConstraintTarget.IMPLICIT;
            }
            annotationDescriptorBuilder.setAttribute("validationAppliesTo", validationAppliesTo);
        }
        return new ConstraintDescriptorImpl<T>(constraintHelper, member, annotationDescriptorBuilder.build(), this.elementType, null, this.definedOn, constraintType);
    }

    private static <P> P run(PrivilegedAction<P> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }

    public CompositionType getCompositionType() {
        return this.compositionType;
    }

    private static enum ComposingConstraintAnnotationLocation {
        DIRECT,
        IN_CONTAINER;

    }

    public static enum ConstraintType {
        GENERIC,
        CROSS_PARAMETER;

    }

    private static class ClassIndexWrapper {
        final Class<?> clazz;
        final int index;

        ClassIndexWrapper(Class<?> clazz, int index) {
            this.clazz = clazz;
            this.index = index;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ClassIndexWrapper that = (ClassIndexWrapper)o;
            if (this.index != that.index) {
                return false;
            }
            return this.clazz.equals(that.clazz);
        }

        public int hashCode() {
            int result = this.clazz != null ? this.clazz.hashCode() : 0;
            result = 31 * result + this.index;
            return result;
        }

        public String toString() {
            return "ClassIndexWrapper [clazz=" + StringHelper.toShortString(this.clazz) + ", index=" + this.index + "]";
        }
    }
}

