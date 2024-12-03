/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.Marker
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.gvt.Marker;
import org.w3c.dom.Element;

public interface MarkerBridge
extends Bridge {
    public Marker createMarker(BridgeContext var1, Element var2, Element var3);
}

