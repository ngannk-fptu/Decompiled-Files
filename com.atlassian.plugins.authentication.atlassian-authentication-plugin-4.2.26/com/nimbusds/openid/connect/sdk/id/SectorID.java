/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.id;

import com.nimbusds.oauth2.sdk.id.Identifier;
import java.net.URI;
import net.jcip.annotations.Immutable;

@Immutable
public final class SectorID
extends Identifier {
    public static void ensureHTTPScheme(URI sectorURI) {
        if (!"https".equalsIgnoreCase(sectorURI.getScheme())) {
            throw new IllegalArgumentException("The URI must have a https scheme");
        }
    }

    public static String ensureHostComponent(URI sectorURI) {
        String host = sectorURI.getHost();
        if (host == null) {
            throw new IllegalArgumentException("The URI must contain a host component");
        }
        return host;
    }

    public SectorID(String host) {
        super(host);
    }

    public SectorID(URI sectorURI) {
        super(SectorID.ensureHostComponent(sectorURI));
    }
}

