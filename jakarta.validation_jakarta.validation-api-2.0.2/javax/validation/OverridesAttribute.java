/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
@Repeatable(value=List.class)
public @interface OverridesAttribute {
    public Class<? extends Annotation> constraint();

    public String name() default "";

    public int constraintIndex() default -1;

    @Documented
    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface List {
        public OverridesAttribute[] value();
    }
}

