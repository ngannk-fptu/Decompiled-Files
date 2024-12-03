/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.event;

import java.util.EventListener;
import org.apache.batik.gvt.event.GraphicsNodeChangeEvent;

public interface GraphicsNodeChangeListener
extends EventListener {
    public void changeStarted(GraphicsNodeChangeEvent var1);

    public void changeCompleted(GraphicsNodeChangeEvent var1);
}

