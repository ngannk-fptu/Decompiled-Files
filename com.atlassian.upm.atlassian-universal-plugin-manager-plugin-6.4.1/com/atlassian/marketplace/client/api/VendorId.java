/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.ResourceId;
import java.net.URI;

public final class VendorId
extends ResourceId {
    private VendorId(URI uri) {
        super(uri);
    }

    public static VendorId fromUri(URI uri) {
        return new VendorId(uri);
    }
}

