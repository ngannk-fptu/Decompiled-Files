/*
 * Decompiled with CFR 0.152.
 */
package org.checkerframework.checker.calledmethods.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.checker.calledmethods.qual.CalledMethods;
import org.checkerframework.framework.qual.ConditionalPostconditionAnnotation;
import org.checkerframework.framework.qual.InheritedAnnotation;
import org.checkerframework.framework.qual.QualifierArgument;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
@ConditionalPostconditionAnnotation(qualifier=CalledMethods.class)
@InheritedAnnotation
@Repeatable(value=List.class)
public @interface EnsuresCalledMethodsIf {
    public String[] expression();

    public boolean result();

    @QualifierArgument(value="value")
    public String[] methods();

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
    @ConditionalPostconditionAnnotation(qualifier=CalledMethods.class)
    @InheritedAnnotation
    public static @interface List {
        public EnsuresCalledMethodsIf[] value();
    }
}

