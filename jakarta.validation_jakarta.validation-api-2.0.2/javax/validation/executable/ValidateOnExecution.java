/*
 * Decompiled with CFR 0.152.
 */
package javax.validation.executable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.executable.ExecutableType;

@Target(value={ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.TYPE, ElementType.PACKAGE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateOnExecution {
    public ExecutableType[] type() default {ExecutableType.IMPLICIT};
}

