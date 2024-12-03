/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.glassfish.jersey.server.monitoring.RequestEvent
 */
package io.micrometer.core.instrument.binder.jersey.server;

import io.micrometer.core.instrument.Tag;
import org.glassfish.jersey.server.monitoring.RequestEvent;

public interface JerseyTagsProvider {
    public Iterable<Tag> httpRequestTags(RequestEvent var1);

    public Iterable<Tag> httpLongRequestTags(RequestEvent var1);
}

