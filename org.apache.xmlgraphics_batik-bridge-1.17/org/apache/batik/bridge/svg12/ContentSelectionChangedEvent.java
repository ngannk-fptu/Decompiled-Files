/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.XBLOMContentElement
 */
package org.apache.batik.bridge.svg12;

import java.util.EventObject;
import org.apache.batik.anim.dom.XBLOMContentElement;

public class ContentSelectionChangedEvent
extends EventObject {
    public ContentSelectionChangedEvent(XBLOMContentElement c) {
        super(c);
    }

    public XBLOMContentElement getContentElement() {
        return (XBLOMContentElement)this.source;
    }
}

