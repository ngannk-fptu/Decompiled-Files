/*
 * Decompiled with CFR 0.152.
 */
package org.checkerframework.checker.calledmethods.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.checker.calledmethods.qual.CalledMethods;
import org.checkerframework.framework.qual.PreconditionAnnotation;
import org.checkerframework.framework.qual.QualifierArgument;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@PreconditionAnnotation(qualifier=CalledMethods.class)
@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface RequiresCalledMethods {
    public String[] value();

    @QualifierArgument(value="value")
    public String[] methods();

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
    public static @interface List {
        public RequiresCalledMethods[] value();
    }
}

