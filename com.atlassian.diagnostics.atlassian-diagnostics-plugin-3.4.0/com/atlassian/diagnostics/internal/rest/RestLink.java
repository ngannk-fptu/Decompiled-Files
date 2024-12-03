/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.internal.rest.RestEntity;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public class RestLink
extends RestEntity {
    public RestLink(@Nonnull URI href, @Nonnull String rel, Map<String, String> properties) {
        if (properties != null) {
            this.putAll(properties);
        }
        this.put("href", Objects.requireNonNull(href, "href").toASCIIString());
        this.put("rel", Objects.requireNonNull(rel, "rel"));
    }

    @Nonnull
    public static RestLink next(@Nonnull URI href) {
        return new RestLink(href, "next", null);
    }

    @Nonnull
    public static RestLink previous(@Nonnull URI href) {
        return new RestLink(href, "prev", null);
    }
}

