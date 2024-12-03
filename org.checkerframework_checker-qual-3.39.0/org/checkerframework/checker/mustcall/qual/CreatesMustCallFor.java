/*
 * Decompiled with CFR 0.152.
 */
package org.checkerframework.checker.mustcall.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.InheritedAnnotation;
import org.checkerframework.framework.qual.JavaExpression;

@Target(value={ElementType.METHOD})
@InheritedAnnotation
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=List.class)
public @interface CreatesMustCallFor {
    @JavaExpression
    public String value() default "this";

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.METHOD})
    @InheritedAnnotation
    public static @interface List {
        public CreatesMustCallFor[] value();
    }
}

