/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfiles;

@Target(value={ElementType.TYPE, ElementType.PACKAGE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=FetchProfiles.class)
public @interface FetchProfile {
    public String name();

    public FetchOverride[] fetchOverrides();

    @Target(value={ElementType.TYPE, ElementType.PACKAGE})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface FetchOverride {
        public Class<?> entity();

        public String association();

        public FetchMode mode();
    }
}

