/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.plugins.gadgets.events;

import com.atlassian.analytics.api.annotations.EventName;
import java.net.URI;

@EventName(value="gadget.placeholder.rendered")
public class GadgetPlaceholderRenderedEvent {
    private final URI gadgetUri;

    public GadgetPlaceholderRenderedEvent(URI gadgetUri) {
        this.gadgetUri = gadgetUri;
    }

    public URI getGadgetUri() {
        return this.gadgetUri;
    }

    public String getGadgetUriPath() {
        return this.gadgetUri.getPath();
    }
}

