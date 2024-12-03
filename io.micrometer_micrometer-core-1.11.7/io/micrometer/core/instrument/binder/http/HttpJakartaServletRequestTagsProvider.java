/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 */
package io.micrometer.core.instrument.binder.http;

import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@FunctionalInterface
@Incubating(since="1.10.0")
public interface HttpJakartaServletRequestTagsProvider {
    public Iterable<Tag> getTags(HttpServletRequest var1, HttpServletResponse var2);
}

