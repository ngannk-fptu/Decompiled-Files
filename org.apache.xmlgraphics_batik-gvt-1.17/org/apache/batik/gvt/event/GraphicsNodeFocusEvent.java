/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.event;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.event.GraphicsNodeEvent;

public class GraphicsNodeFocusEvent
extends GraphicsNodeEvent {
    static final int FOCUS_FIRST = 1004;
    public static final int FOCUS_GAINED = 1004;
    public static final int FOCUS_LOST = 1005;

    public GraphicsNodeFocusEvent(GraphicsNode source, int id) {
        super(source, id);
    }
}

