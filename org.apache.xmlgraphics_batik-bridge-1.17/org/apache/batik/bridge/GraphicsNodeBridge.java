/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public interface GraphicsNodeBridge
extends Bridge {
    public GraphicsNode createGraphicsNode(BridgeContext var1, Element var2);

    public void buildGraphicsNode(BridgeContext var1, Element var2, GraphicsNode var3);

    public boolean isComposite();

    public boolean getDisplay(Element var1);

    @Override
    public Bridge getInstance();
}

