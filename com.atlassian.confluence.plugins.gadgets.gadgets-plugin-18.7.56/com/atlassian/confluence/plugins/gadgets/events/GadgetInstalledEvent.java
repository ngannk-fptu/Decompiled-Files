/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 */
package com.atlassian.confluence.plugins.gadgets.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import java.net.URI;

@EventName(value="gadget.installed")
public class GadgetInstalledEvent
extends ConfluenceEvent {
    private final URI gadgetUri;

    public GadgetInstalledEvent(Object src, URI gadgetUri) {
        super(src);
        this.gadgetUri = gadgetUri;
    }

    public URI getGadgetUri() {
        return this.gadgetUri;
    }

    public String getGadgetUriPath() {
        return this.gadgetUri.getPath();
    }
}

