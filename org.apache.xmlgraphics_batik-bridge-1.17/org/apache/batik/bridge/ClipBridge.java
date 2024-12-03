/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.renderable.ClipRable
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public interface ClipBridge
extends Bridge {
    public ClipRable createClip(BridgeContext var1, Element var2, Element var3, GraphicsNode var4);
}

