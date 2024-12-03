/*
 * Decompiled with CFR 0.152.
 */
package org.checkerframework.checker.index.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.checker.index.qual.LTLengthOf;
import org.checkerframework.framework.qual.InheritedAnnotation;
import org.checkerframework.framework.qual.JavaExpression;
import org.checkerframework.framework.qual.PostconditionAnnotation;
import org.checkerframework.framework.qual.QualifierArgument;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
@PostconditionAnnotation(qualifier=LTLengthOf.class)
@InheritedAnnotation
@Repeatable(value=List.class)
public @interface EnsuresLTLengthOf {
    @JavaExpression
    public String[] value();

    @JavaExpression
    @QualifierArgument(value="value")
    public String[] targetValue();

    @JavaExpression
    @QualifierArgument(value="offset")
    public String[] offset() default {};

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
    @PostconditionAnnotation(qualifier=LTLengthOf.class)
    @InheritedAnnotation
    public static @interface List {
        public EnsuresLTLengthOf[] value();
    }
}

