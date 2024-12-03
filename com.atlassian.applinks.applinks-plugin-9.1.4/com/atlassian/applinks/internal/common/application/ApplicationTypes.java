/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.application.generic.GenericApplicationType
 *  com.atlassian.applinks.spi.application.IdentifiableType
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.application;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.application.generic.GenericApplicationType;
import com.atlassian.applinks.application.BuiltinApplinksType;
import com.atlassian.applinks.spi.application.IdentifiableType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ApplicationTypes {
    private ApplicationTypes() {
        throw new AssertionError((Object)("Do not instantiate " + this.getClass().getSimpleName()));
    }

    @Nonnull
    public static String resolveApplicationTypeId(@Nonnull ApplicationType type) {
        return ApplicationTypes.isIdentifiable(type) ? ApplicationTypes.asIdentifiableType(type).getId().get() : type.getI18nKey();
    }

    @Nonnull
    public static String resolveEntityTypeId(@Nonnull EntityType type) {
        return ApplicationTypes.isIdentifiable(type) ? ApplicationTypes.asIdentifiableType(type).getId().get() : type.getI18nKey();
    }

    public static boolean isIdentifiable(@Nullable Object type) {
        return type instanceof IdentifiableType;
    }

    public static IdentifiableType asIdentifiableType(@Nullable Object type) {
        return (IdentifiableType)type;
    }

    public static boolean isBuiltIn(@Nullable Object type) {
        return type instanceof BuiltinApplinksType;
    }

    public static boolean isGeneric(@Nullable Object type) {
        return type instanceof GenericApplicationType;
    }

    public static boolean isAtlassian(@Nullable Object type) {
        return ApplicationTypes.isBuiltIn(type) && !ApplicationTypes.isGeneric(type);
    }
}

