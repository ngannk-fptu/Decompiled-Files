/*
 * Decompiled with CFR 0.152.
 */
package org.checkerframework.checker.mustcall.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.checker.mustcall.qual.MustCallUnknown;
import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.TypeUseLocation;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf(value={MustCallUnknown.class})
@DefaultQualifierInHierarchy
@DefaultFor(value={TypeUseLocation.EXCEPTION_PARAMETER, TypeUseLocation.UPPER_BOUND})
public @interface MustCall {
    public String[] value() default {};
}

