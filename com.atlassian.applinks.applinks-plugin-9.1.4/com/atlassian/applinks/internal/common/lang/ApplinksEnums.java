/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.lang;

import com.google.common.base.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ApplinksEnums {
    private ApplinksEnums() {
    }

    @Nonnull
    public static <E extends Enum<E>> Function<String, E> fromName(final @Nonnull Class<E> enumType) {
        return new Function<String, E>(){

            @Nullable
            public E apply(String stringValue) {
                return Enum.valueOf(enumType, stringValue);
            }
        };
    }

    @Nonnull
    public static <E extends Enum<E>> Function<String, E> fromNameSafe(@Nonnull Class<E> enumType) {
        final Function<String, E> fromName = ApplinksEnums.fromName(enumType);
        return new Function<String, E>(){

            @Nullable
            public E apply(@Nullable String stringValue) {
                try {
                    return (Enum)fromName.apply((Object)stringValue);
                }
                catch (IllegalArgumentException | NullPointerException e) {
                    return null;
                }
            }
        };
    }

    @Nonnull
    public static Function<Enum<?>, String> toName() {
        return new Function<Enum<?>, String>(){

            @Nullable
            public String apply(Enum<?> anEnum) {
                return anEnum.name();
            }
        };
    }
}

