/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.model.Links;
import java.net.URI;

public interface Entity {
    public Links getLinks();

    public URI getSelfUri();
}

