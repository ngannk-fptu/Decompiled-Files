/*
 * Decompiled with CFR 0.152.
 */
package org.checkerframework.checker.calledmethods.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.checker.calledmethods.qual.CalledMethods;
import org.checkerframework.framework.qual.SubtypeOf;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf(value={CalledMethods.class})
public @interface CalledMethodsPredicate {
    public String value();
}

