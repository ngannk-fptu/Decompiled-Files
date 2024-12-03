/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.filter.Mask
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Mask;
import org.w3c.dom.Element;

public interface MaskBridge
extends Bridge {
    public Mask createMask(BridgeContext var1, Element var2, Element var3, GraphicsNode var4);
}

