/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import javax.validation.ConstraintValidator;

public interface ConstraintValidatorFactory {
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> var1);

    public void releaseInstance(ConstraintValidator<?, ?> var1);
}

