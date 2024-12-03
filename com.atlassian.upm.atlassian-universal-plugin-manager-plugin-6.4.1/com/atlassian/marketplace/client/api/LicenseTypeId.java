/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.ResourceId;
import java.net.URI;

public final class LicenseTypeId
extends ResourceId {
    private LicenseTypeId(URI uri) {
        super(uri);
    }

    public static LicenseTypeId fromUri(URI uri) {
        return new LicenseTypeId(uri);
    }
}

