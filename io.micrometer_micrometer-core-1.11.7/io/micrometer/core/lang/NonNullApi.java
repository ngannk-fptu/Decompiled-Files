/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.meta.TypeQualifierDefault
 */
package io.micrometer.core.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;

@Target(value={ElementType.PACKAGE, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Nonnull
@TypeQualifierDefault(value={ElementType.METHOD, ElementType.PARAMETER})
@Deprecated
public @interface NonNullApi {
}

