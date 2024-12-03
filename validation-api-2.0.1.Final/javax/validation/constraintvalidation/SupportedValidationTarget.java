/*
 * Decompiled with CFR 0.152.
 */
package javax.validation.constraintvalidation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.constraintvalidation.ValidationTarget;

@Documented
@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface SupportedValidationTarget {
    public ValidationTarget[] value();
}

