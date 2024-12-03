/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.w3c.dom.Location
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.BridgeContext;

public class Location
implements org.apache.batik.w3c.dom.Location {
    private BridgeContext bridgeContext;

    public Location(BridgeContext ctx) {
        this.bridgeContext = ctx;
    }

    public void assign(String url) {
        this.bridgeContext.getUserAgent().loadDocument(url);
    }

    public void reload() {
        String url = this.bridgeContext.getDocument().getDocumentURI();
        this.bridgeContext.getUserAgent().loadDocument(url);
    }

    public String toString() {
        return this.bridgeContext.getDocument().getDocumentURI();
    }
}

