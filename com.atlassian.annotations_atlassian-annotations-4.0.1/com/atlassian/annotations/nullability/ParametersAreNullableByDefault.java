/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.meta.TypeQualifierDefault
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.annotations.nullability;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifierDefault;
import org.checkerframework.checker.nullness.qual.Nullable;

@Documented
@Nullable
@TypeQualifierDefault(value={ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ParametersAreNullableByDefault {
}

