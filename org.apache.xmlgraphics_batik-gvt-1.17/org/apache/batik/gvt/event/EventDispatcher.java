/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.event;

import java.awt.event.InputEvent;
import java.awt.geom.AffineTransform;
import java.util.EventListener;
import java.util.EventObject;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.event.GraphicsNodeKeyListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseWheelListener;

public interface EventDispatcher {
    public void setRootNode(GraphicsNode var1);

    public GraphicsNode getRootNode();

    public void setBaseTransform(AffineTransform var1);

    public AffineTransform getBaseTransform();

    public void dispatchEvent(EventObject var1);

    public void addGraphicsNodeMouseListener(GraphicsNodeMouseListener var1);

    public void removeGraphicsNodeMouseListener(GraphicsNodeMouseListener var1);

    public void addGraphicsNodeMouseWheelListener(GraphicsNodeMouseWheelListener var1);

    public void removeGraphicsNodeMouseWheelListener(GraphicsNodeMouseWheelListener var1);

    public void addGraphicsNodeKeyListener(GraphicsNodeKeyListener var1);

    public void removeGraphicsNodeKeyListener(GraphicsNodeKeyListener var1);

    public EventListener[] getListeners(Class var1);

    public void setNodeIncrementEvent(InputEvent var1);

    public void setNodeDecrementEvent(InputEvent var1);
}

