/*
 * Decompiled with CFR 0.152.
 */
package javax.validation.metadata;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintTarget;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import javax.validation.metadata.ValidateUnwrappedValue;

public interface ConstraintDescriptor<T extends Annotation> {
    public T getAnnotation();

    public String getMessageTemplate();

    public Set<Class<?>> getGroups();

    public Set<Class<? extends Payload>> getPayload();

    public ConstraintTarget getValidationAppliesTo();

    public List<Class<? extends ConstraintValidator<T, ?>>> getConstraintValidatorClasses();

    public Map<String, Object> getAttributes();

    public Set<ConstraintDescriptor<?>> getComposingConstraints();

    public boolean isReportAsSingleViolation();

    public ValidateUnwrappedValue getValueUnwrapping();

    public <U> U unwrap(Class<U> var1);
}

