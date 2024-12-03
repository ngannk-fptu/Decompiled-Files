/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.ConstraintValidator;

@Documented
@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Constraint {
    public Class<? extends ConstraintValidator<?, ?>>[] validatedBy();
}

