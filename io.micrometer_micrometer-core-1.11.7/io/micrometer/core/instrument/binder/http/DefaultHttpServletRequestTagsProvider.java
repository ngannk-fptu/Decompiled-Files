/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package io.micrometer.core.instrument.binder.http;

import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.http.HttpRequestTags;
import io.micrometer.core.instrument.binder.http.HttpServletRequestTagsProvider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Incubating(since="1.4.0")
public class DefaultHttpServletRequestTagsProvider
implements HttpServletRequestTagsProvider {
    @Override
    public Iterable<Tag> getTags(HttpServletRequest request, HttpServletResponse response) {
        return Tags.of(HttpRequestTags.method(request), HttpRequestTags.status(response), HttpRequestTags.outcome(response));
    }
}

