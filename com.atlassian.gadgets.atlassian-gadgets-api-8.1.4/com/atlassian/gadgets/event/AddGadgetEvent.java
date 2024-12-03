/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.event;

import java.net.URI;

public class AddGadgetEvent {
    private final URI gadgetUri;

    public AddGadgetEvent(URI gadgetUri) {
        this.gadgetUri = gadgetUri;
    }

    public URI getGadgetUri() {
        return this.gadgetUri;
    }
}

