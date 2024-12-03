/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.internal.rest;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public final class RestUrl {
    public static final RestUrl EMPTY = new RestUrl(Collections.emptyList());
    public static final String COMPONENT_SEPARATOR = "/";
    private final List<String> components;

    @Nonnull
    public static RestUrl forPath(@Nonnull String path) {
        return EMPTY.add(path);
    }

    private RestUrl(List<String> components) {
        this.components = components;
    }

    @Nonnull
    public RestUrl add(@Nonnull String component) {
        Objects.requireNonNull(component, "component");
        String sanitized = StringUtils.strip((String)component);
        sanitized = StringUtils.strip((String)sanitized, (String)COMPONENT_SEPARATOR);
        return sanitized.isEmpty() ? this : new RestUrl((List<String>)ImmutableList.builder().addAll(this.components).add((Object)sanitized).build());
    }

    @Nonnull
    public RestUrl add(@Nonnull RestUrl other) {
        Objects.requireNonNull(other, "other");
        return new RestUrl((List<String>)ImmutableList.builder().addAll(this.components).addAll(other.components).build());
    }

    @Nonnull
    public String toString() {
        return StringUtils.join(this.components, (String)COMPONENT_SEPARATOR);
    }
}

