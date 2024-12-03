/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.event;

import java.util.EventListener;
import org.apache.batik.gvt.event.GraphicsNodeFocusEvent;

public interface GraphicsNodeFocusListener
extends EventListener {
    public void focusGained(GraphicsNodeFocusEvent var1);

    public void focusLost(GraphicsNodeFocusEvent var1);
}

