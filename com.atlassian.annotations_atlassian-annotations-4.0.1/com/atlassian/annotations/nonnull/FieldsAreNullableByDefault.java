/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.meta.TypeQualifierDefault
 */
package com.atlassian.annotations.nonnull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.Nullable;
import javax.annotation.meta.TypeQualifierDefault;

@Documented
@Nullable
@TypeQualifierDefault(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Deprecated
public @interface FieldsAreNullableByDefault {
}

