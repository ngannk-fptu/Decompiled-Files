/*
 * Decompiled with CFR 0.152.
 */
package org.checkerframework.common.initializedfields.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.common.initializedfields.qual.InitializedFields;
import org.checkerframework.framework.qual.InheritedAnnotation;
import org.checkerframework.framework.qual.PostconditionAnnotation;
import org.checkerframework.framework.qual.QualifierArgument;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
@PostconditionAnnotation(qualifier=InitializedFields.class)
@InheritedAnnotation
@Repeatable(value=List.class)
public @interface EnsuresInitializedFields {
    public String[] value() default {"this"};

    @QualifierArgument(value="value")
    public String[] fields();

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
    @PostconditionAnnotation(qualifier=InitializedFields.class)
    @InheritedAnnotation
    public static @interface List {
        public EnsuresInitializedFields[] value();
    }
}

