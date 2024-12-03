/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintViolation
 */
package org.hibernate.validator.internal.engine.constraintvalidation;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintViolation;
import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.internal.engine.ValidationContext;
import org.hibernate.validator.internal.engine.ValueContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintTree;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintViolationCreationContext;
import org.hibernate.validator.internal.engine.constraintvalidation.SimpleConstraintTree;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

class ComposingConstraintTree<B extends Annotation>
extends ConstraintTree<B> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final List<ConstraintTree<?>> children;

    public ComposingConstraintTree(ConstraintDescriptorImpl<B> descriptor, Type validatedValueType) {
        super(descriptor, validatedValueType);
        this.children = descriptor.getComposingConstraintImpls().stream().map(desc -> this.createConstraintTree((ConstraintDescriptorImpl)desc)).collect(Collectors.collectingAndThen(Collectors.toList(), CollectionHelper::toImmutableList));
    }

    private <U extends Annotation> ConstraintTree<U> createConstraintTree(ConstraintDescriptorImpl<U> composingDescriptor) {
        if (composingDescriptor.getComposingConstraintImpls().isEmpty()) {
            return new SimpleConstraintTree<U>(composingDescriptor, this.getValidatedValueType());
        }
        return new ComposingConstraintTree<U>(composingDescriptor, this.getValidatedValueType());
    }

    @Override
    protected <T> void validateConstraints(ValidationContext<T> validationContext, ValueContext<?, ?> valueContext, Set<ConstraintViolation<T>> constraintViolations) {
        Set<ConstraintViolation<T>> localViolations;
        CompositionResult compositionResult = this.validateComposingConstraints(validationContext, valueContext, constraintViolations);
        if (this.mainConstraintNeedsEvaluation(validationContext, constraintViolations)) {
            if (LOG.isTraceEnabled()) {
                LOG.tracef("Validating value %s against constraint defined by %s.", valueContext.getCurrentValidatedValue(), (Object)this.descriptor);
            }
            ConstraintValidator validator = this.getInitializedConstraintValidator(validationContext, valueContext);
            ConstraintValidatorContextImpl constraintValidatorContext = new ConstraintValidatorContextImpl(validationContext.getParameterNames(), validationContext.getClockProvider(), valueContext.getPropertyPath(), this.descriptor, validationContext.getConstraintValidatorPayload());
            localViolations = this.validateSingleConstraint(validationContext, valueContext, constraintValidatorContext, validator);
            if (localViolations.isEmpty()) {
                compositionResult.setAtLeastOneTrue(true);
            } else {
                compositionResult.setAllTrue(false);
            }
        } else {
            localViolations = Collections.emptySet();
        }
        if (!this.passesCompositionTypeRequirement(constraintViolations, compositionResult)) {
            this.prepareFinalConstraintViolations(validationContext, valueContext, constraintViolations, localViolations);
        }
    }

    private <T> boolean mainConstraintNeedsEvaluation(ValidationContext<T> executionContext, Set<ConstraintViolation<T>> constraintViolations) {
        if (!this.descriptor.getComposingConstraints().isEmpty() && this.descriptor.getMatchingConstraintValidatorDescriptors().isEmpty()) {
            return false;
        }
        if (constraintViolations.isEmpty()) {
            return true;
        }
        if (this.descriptor.isReportAsSingleViolation() && this.descriptor.getCompositionType() == CompositionType.AND) {
            return false;
        }
        return !executionContext.isFailFastModeEnabled();
    }

    private <T> void prepareFinalConstraintViolations(ValidationContext<T> executionContext, ValueContext<?, ?> valueContext, Set<ConstraintViolation<T>> constraintViolations, Set<ConstraintViolation<T>> localViolations) {
        if (this.reportAsSingleViolation()) {
            constraintViolations.clear();
            if (localViolations.isEmpty()) {
                String message = this.getDescriptor().getMessageTemplate();
                ConstraintViolationCreationContext constraintViolationCreationContext = new ConstraintViolationCreationContext(message, valueContext.getPropertyPath());
                ConstraintViolation<T> violation = executionContext.createConstraintViolation(valueContext, constraintViolationCreationContext, this.descriptor);
                constraintViolations.add(violation);
            }
        }
        constraintViolations.addAll(localViolations);
    }

    private <T> CompositionResult validateComposingConstraints(ValidationContext<T> executionContext, ValueContext<?, ?> valueContext, Set<ConstraintViolation<T>> constraintViolations) {
        CompositionResult compositionResult = new CompositionResult(true, false);
        for (ConstraintTree<?> tree : this.children) {
            HashSet tmpViolations = CollectionHelper.newHashSet(5);
            tree.validateConstraints(executionContext, valueContext, tmpViolations);
            constraintViolations.addAll(tmpViolations);
            if (tmpViolations.isEmpty()) {
                compositionResult.setAtLeastOneTrue(true);
                if (this.descriptor.getCompositionType() != CompositionType.OR) continue;
                break;
            }
            compositionResult.setAllTrue(false);
            if (this.descriptor.getCompositionType() != CompositionType.AND || !executionContext.isFailFastModeEnabled() && !this.descriptor.isReportAsSingleViolation()) continue;
            break;
        }
        return compositionResult;
    }

    private boolean passesCompositionTypeRequirement(Set<?> constraintViolations, CompositionResult compositionResult) {
        CompositionType compositionType = this.getDescriptor().getCompositionType();
        boolean passedValidation = false;
        switch (compositionType) {
            case OR: {
                passedValidation = compositionResult.isAtLeastOneTrue();
                break;
            }
            case AND: {
                passedValidation = compositionResult.isAllTrue();
                break;
            }
            case ALL_FALSE: {
                boolean bl = passedValidation = !compositionResult.isAtLeastOneTrue();
            }
        }
        assert (!passedValidation || compositionType != CompositionType.AND || constraintViolations.isEmpty());
        if (passedValidation) {
            constraintViolations.clear();
        }
        return passedValidation;
    }

    private boolean reportAsSingleViolation() {
        return this.getDescriptor().isReportAsSingleViolation() || this.getDescriptor().getCompositionType() == CompositionType.ALL_FALSE;
    }

    private static final class CompositionResult {
        private boolean allTrue;
        private boolean atLeastOneTrue;

        CompositionResult(boolean allTrue, boolean atLeastOneTrue) {
            this.allTrue = allTrue;
            this.atLeastOneTrue = atLeastOneTrue;
        }

        public boolean isAllTrue() {
            return this.allTrue;
        }

        public boolean isAtLeastOneTrue() {
            return this.atLeastOneTrue;
        }

        public void setAllTrue(boolean allTrue) {
            this.allTrue = allTrue;
        }

        public void setAtLeastOneTrue(boolean atLeastOneTrue) {
            this.atLeastOneTrue = atLeastOneTrue;
        }
    }
}

