/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge;

import java.awt.Paint;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public interface PaintBridge
extends Bridge {
    public Paint createPaint(BridgeContext var1, Element var2, Element var3, GraphicsNode var4, float var5);
}

