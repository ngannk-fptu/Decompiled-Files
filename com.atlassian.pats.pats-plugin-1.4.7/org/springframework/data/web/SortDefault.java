/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.web;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.data.domain.Sort;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface SortDefault {
    public String[] value() default {};

    public String[] sort() default {};

    public Sort.Direction direction() default Sort.Direction.ASC;

    public boolean caseSensitive() default true;

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.PARAMETER})
    public static @interface SortDefaults {
        public SortDefault[] value();
    }
}

