/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.glassfish.jersey.server.ContainerResponse
 *  org.glassfish.jersey.server.monitoring.RequestEvent
 */
package io.micrometer.core.instrument.binder.jersey.server;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jersey.server.JerseyTags;
import io.micrometer.core.instrument.binder.jersey.server.JerseyTagsProvider;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.monitoring.RequestEvent;

public final class DefaultJerseyTagsProvider
implements JerseyTagsProvider {
    @Override
    public Iterable<Tag> httpRequestTags(RequestEvent event) {
        ContainerResponse response = event.getContainerResponse();
        return Tags.of(JerseyTags.method(event.getContainerRequest()), JerseyTags.uri(event), JerseyTags.exception(event), JerseyTags.status(response), JerseyTags.outcome(response));
    }

    @Override
    public Iterable<Tag> httpLongRequestTags(RequestEvent event) {
        return Tags.of(JerseyTags.method(event.getContainerRequest()), JerseyTags.uri(event));
    }
}

