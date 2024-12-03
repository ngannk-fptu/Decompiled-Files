/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.event;

import java.util.EventListener;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;

public interface GraphicsNodeKeyListener
extends EventListener {
    public void keyPressed(GraphicsNodeKeyEvent var1);

    public void keyReleased(GraphicsNodeKeyEvent var1);

    public void keyTyped(GraphicsNodeKeyEvent var1);
}

