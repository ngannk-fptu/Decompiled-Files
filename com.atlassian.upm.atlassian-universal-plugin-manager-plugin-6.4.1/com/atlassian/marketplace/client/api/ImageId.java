/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.ResourceId;
import java.net.URI;

public final class ImageId
extends ResourceId {
    private ImageId(URI value) {
        super(value);
    }

    public static ImageId fromUri(URI uri) {
        return new ImageId(uri);
    }
}

