/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.event;

import java.util.EventListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;

public interface GraphicsNodeMouseListener
extends EventListener {
    public void mouseClicked(GraphicsNodeMouseEvent var1);

    public void mousePressed(GraphicsNodeMouseEvent var1);

    public void mouseReleased(GraphicsNodeMouseEvent var1);

    public void mouseEntered(GraphicsNodeMouseEvent var1);

    public void mouseExited(GraphicsNodeMouseEvent var1);

    public void mouseDragged(GraphicsNodeMouseEvent var1);

    public void mouseMoved(GraphicsNodeMouseEvent var1);
}

