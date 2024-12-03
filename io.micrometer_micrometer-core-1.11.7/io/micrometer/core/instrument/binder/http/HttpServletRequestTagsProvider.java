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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FunctionalInterface
@Incubating(since="1.4.0")
public interface HttpServletRequestTagsProvider {
    public Iterable<Tag> getTags(HttpServletRequest var1, HttpServletResponse var2);
}

