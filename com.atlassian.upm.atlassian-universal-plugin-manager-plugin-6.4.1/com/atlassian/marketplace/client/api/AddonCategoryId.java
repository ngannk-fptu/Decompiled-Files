/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.ResourceId;
import java.net.URI;

public final class AddonCategoryId
extends ResourceId {
    private AddonCategoryId(URI uri) {
        super(uri);
    }

    public static AddonCategoryId fromUri(URI uri) {
        return new AddonCategoryId(uri);
    }
}

