/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.xbl;

import org.apache.batik.dom.xbl.XBLShadowTreeElement;
import org.w3c.dom.events.Event;

public interface ShadowTreeEvent
extends Event {
    public XBLShadowTreeElement getXblShadowTree();

    public void initShadowTreeEvent(String var1, boolean var2, boolean var3, XBLShadowTreeElement var4);

    public void initShadowTreeEventNS(String var1, String var2, boolean var3, boolean var4, XBLShadowTreeElement var5);
}

