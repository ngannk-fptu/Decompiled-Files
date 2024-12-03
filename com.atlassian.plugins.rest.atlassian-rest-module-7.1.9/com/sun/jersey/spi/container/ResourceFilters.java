/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.spi.container.ResourceFilter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ResourceFilters {
    public Class<? extends ResourceFilter>[] value();
}

