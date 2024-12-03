/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.ResourceId;
import java.net.URI;

public final class ArtifactId
extends ResourceId {
    private ArtifactId(URI value) {
        super(value);
    }

    public static ArtifactId fromUri(URI uri) {
        return new ArtifactId(uri);
    }
}

