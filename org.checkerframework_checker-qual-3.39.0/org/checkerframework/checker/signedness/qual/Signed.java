/*
 * Decompiled with CFR 0.152.
 */
package org.checkerframework.checker.signedness.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.checker.signedness.qual.UnknownSignedness;
import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.TypeKind;
import org.checkerframework.framework.qual.TypeUseLocation;
import org.checkerframework.framework.qual.UpperBoundFor;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf(value={UnknownSignedness.class})
@DefaultQualifierInHierarchy
@DefaultFor(typeKinds={TypeKind.BYTE, TypeKind.INT, TypeKind.LONG, TypeKind.SHORT, TypeKind.FLOAT, TypeKind.DOUBLE}, types={Byte.class, Integer.class, Long.class, Short.class, Float.class, Double.class}, value={TypeUseLocation.EXCEPTION_PARAMETER})
@UpperBoundFor(typeKinds={TypeKind.FLOAT, TypeKind.DOUBLE}, types={Float.class, Double.class})
public @interface Signed {
}

