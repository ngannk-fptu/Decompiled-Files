/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.bridge.svg12;

import org.apache.batik.bridge.BridgeUpdateHandler;
import org.apache.batik.bridge.svg12.ContentSelectionChangedEvent;
import org.w3c.dom.Element;

public interface SVG12BridgeUpdateHandler
extends BridgeUpdateHandler {
    public void handleBindingEvent(Element var1, Element var2);

    public void handleContentSelectionChangedEvent(ContentSelectionChangedEvent var1);
}

