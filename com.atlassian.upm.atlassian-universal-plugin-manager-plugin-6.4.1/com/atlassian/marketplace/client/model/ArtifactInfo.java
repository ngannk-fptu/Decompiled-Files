/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.model.Entity;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.RequiredLink;
import io.atlassian.fugue.Option;
import java.net.URI;

public final class ArtifactInfo
implements Entity {
    Links _links;
    @RequiredLink(rel="self")
    URI selfUri;
    @RequiredLink(rel="binary")
    URI binaryUri;

    public URI getBinaryUri() {
        return this.binaryUri;
    }

    @Override
    public URI getSelfUri() {
        return this.selfUri;
    }

    public Option<URI> getRemoteDescriptorUri() {
        return this._links.getUri("remote");
    }

    @Override
    public Links getLinks() {
        return this._links;
    }
}

