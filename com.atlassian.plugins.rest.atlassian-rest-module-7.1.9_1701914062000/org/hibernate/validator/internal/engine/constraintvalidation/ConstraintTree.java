/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintDeclarationException
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.ConstraintViolation
 *  javax.validation.ValidationException
 */
package org.hibernate.validator.internal.engine.constraintvalidation;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import org.hibernate.validator.internal.engine.ValidationContext;
import org.hibernate.validator.internal.engine.ValueContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ComposingConstraintTree;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManager;
import org.hibernate.validator.internal.engine.constraintvalidation.SimpleConstraintTree;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public abstract class ConstraintTree<A extends Annotation> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    protected final ConstraintDescriptorImpl<A> descriptor;
    private final Type validatedValueType;
    private volatile ConstraintValidator<A, ?> constraintValidatorForDefaultConstraintValidatorFactoryAndInitializationContext;

    protected ConstraintTree(ConstraintDescriptorImpl<A> descriptor, Type validatedValueType) {
        this.descriptor = descriptor;
        this.validatedValueType = validatedValueType;
    }

    public static <U extends Annotation> ConstraintTree<U> of(ConstraintDescriptorImpl<U> composingDescriptor, Type validatedValueType) {
        if (composingDescriptor.getComposingConstraintImpls().isEmpty()) {
            return new SimpleConstraintTree<U>(composingDescriptor, validatedValueType);
        }
        return new ComposingConstraintTree<U>(composingDescriptor, validatedValueType);
    }

    public final <T> boolean validateConstraints(ValidationContext<T> executionContext, ValueContext<?, ?> valueContext) {
        HashSet<ConstraintViolation<T>> constraintViolations = CollectionHelper.newHashSet(5);
        this.validateConstraints(executionContext, valueContext, constraintViolations);
        if (!constraintViolations.isEmpty()) {
            executionContext.addConstraintFailures(constraintViolations);
            return false;
        }
        return true;
    }

    protected abstract <T> void validateConstraints(ValidationContext<T> var1, ValueContext<?, ?> var2, Set<ConstraintViolation<T>> var3);

    public final ConstraintDescriptorImpl<A> getDescriptor() {
        return this.descriptor;
    }

    public final Type getValidatedValueType() {
        return this.validatedValueType;
    }

    private ValidationException getExceptionForNullValidator(Type validatedValueType, String path) {
        if (this.descriptor.getConstraintType() == ConstraintDescriptorImpl.ConstraintType.CROSS_PARAMETER) {
            return LOG.getValidatorForCrossParameterConstraintMustEitherValidateObjectOrObjectArrayException(this.descriptor.getAnnotationType());
        }
        String className = validatedValueType.toString();
        if (validatedValueType instanceof Class) {
            Class clazz = (Class)validatedValueType;
            className = clazz.isArray() ? clazz.getComponentType().toString() + "[]" : clazz.getName();
        }
        return LOG.getNoValidatorFoundForTypeException(this.descriptor.getAnnotationType(), className, path);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final <T> ConstraintValidator<A, ?> getInitializedConstraintValidator(ValidationContext<T> validationContext, ValueContext<?, ?> valueContext) {
        Object validator;
        if (validationContext.getConstraintValidatorFactory() == validationContext.getConstraintValidatorManager().getDefaultConstraintValidatorFactory() && validationContext.getConstraintValidatorInitializationContext() == validationContext.getConstraintValidatorManager().getDefaultConstraintValidatorInitializationContext()) {
            validator = this.constraintValidatorForDefaultConstraintValidatorFactoryAndInitializationContext;
            if (validator == null) {
                ConstraintTree constraintTree = this;
                synchronized (constraintTree) {
                    validator = this.constraintValidatorForDefaultConstraintValidatorFactoryAndInitializationContext;
                    if (validator == null) {
                        validator = this.getInitializedConstraintValidator(validationContext);
                        this.constraintValidatorForDefaultConstraintValidatorFactoryAndInitializationContext = validator;
                    }
                }
            }
        } else {
            validator = this.getInitializedConstraintValidator(validationContext);
        }
        if (validator == ConstraintValidatorManager.DUMMY_CONSTRAINT_VALIDATOR) {
            throw this.getExceptionForNullValidator(this.validatedValueType, valueContext.getPropertyPath().asString());
        }
        return validator;
    }

    private ConstraintValidator<A, ?> getInitializedConstraintValidator(ValidationContext<?> validationContext) {
        ConstraintValidator<A, ?> validator = validationContext.getConstraintValidatorManager().getInitializedValidator(this.validatedValueType, this.descriptor, validationContext.getConstraintValidatorFactory(), validationContext.getConstraintValidatorInitializationContext());
        if (validator != null) {
            return validator;
        }
        return ConstraintValidatorManager.DUMMY_CONSTRAINT_VALIDATOR;
    }

    protected final <T, V> Set<ConstraintViolation<T>> validateSingleConstraint(ValidationContext<T> executionContext, ValueContext<?, ?> valueContext, ConstraintValidatorContextImpl constraintValidatorContext, ConstraintValidator<A, V> validator) {
        boolean isValid;
        try {
            Object validatedValue = valueContext.getCurrentValidatedValue();
            isValid = validator.isValid(validatedValue, (ConstraintValidatorContext)constraintValidatorContext);
        }
        catch (RuntimeException e) {
            if (e instanceof ConstraintDeclarationException) {
                throw e;
            }
            throw LOG.getExceptionDuringIsValidCallException(e);
        }
        if (!isValid) {
            return executionContext.createConstraintViolations(valueContext, constraintValidatorContext);
        }
        return Collections.emptySet();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ConstraintTree");
        sb.append("{ descriptor=").append(this.descriptor);
        sb.append('}');
        return sb.toString();
    }
}

