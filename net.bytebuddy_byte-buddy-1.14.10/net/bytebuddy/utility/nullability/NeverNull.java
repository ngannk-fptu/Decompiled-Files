/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.meta.TypeQualifierDefault
 *  javax.annotation.meta.TypeQualifierNickname
 */
package net.bytebuddy.utility.nullability;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import javax.annotation.meta.TypeQualifierNickname;

@Documented
@Target(value={ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
@Nonnull
@TypeQualifierNickname
public @interface NeverNull {

    @Documented
    @Target(value={ElementType.PACKAGE})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Nonnull
    @TypeQualifierDefault(value={ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    public static @interface ByDefault {
    }
}

