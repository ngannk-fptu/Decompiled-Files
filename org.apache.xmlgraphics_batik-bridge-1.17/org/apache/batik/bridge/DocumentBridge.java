/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.RootGraphicsNode
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.gvt.RootGraphicsNode;
import org.w3c.dom.Document;

public interface DocumentBridge
extends Bridge {
    public RootGraphicsNode createGraphicsNode(BridgeContext var1, Document var2);

    public void buildGraphicsNode(BridgeContext var1, Document var2, RootGraphicsNode var3);
}

