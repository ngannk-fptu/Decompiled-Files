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
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.http.HttpJakartaServletRequestTagsProvider;
import io.micrometer.core.instrument.binder.http.HttpRequestTags;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Incubating(since="1.10.0")
public class DefaultHttpJakartaServletRequestTagsProvider
implements HttpJakartaServletRequestTagsProvider {
    @Override
    public Iterable<Tag> getTags(HttpServletRequest request, HttpServletResponse response) {
        return Tags.of(HttpRequestTags.method(request), HttpRequestTags.status(response), HttpRequestTags.outcome(response));
    }
}

