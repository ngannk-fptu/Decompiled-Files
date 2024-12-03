/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.client.api.Result
 */
package io.micrometer.core.instrument.binder.jetty;

import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jetty.JettyClientTags;
import org.eclipse.jetty.client.api.Result;

@Incubating(since="1.5.0")
public interface JettyClientTagsProvider {
    default public Iterable<Tag> httpRequestTags(Result result) {
        return Tags.of(JettyClientTags.method(result.getRequest()), JettyClientTags.host(result.getRequest()), JettyClientTags.uri(result, this::uriPattern), JettyClientTags.exception(result), JettyClientTags.status(result), JettyClientTags.outcome(result));
    }

    @Deprecated
    public String uriPattern(Result var1);
}

