/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt;

import org.apache.batik.gvt.event.GraphicsNodeChangeListener;
import org.apache.batik.gvt.event.GraphicsNodeKeyListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.apache.batik.gvt.event.SelectionListener;

public interface Selector
extends GraphicsNodeMouseListener,
GraphicsNodeKeyListener,
GraphicsNodeChangeListener {
    public Object getSelection();

    public boolean isEmpty();

    public void addSelectionListener(SelectionListener var1);

    public void removeSelectionListener(SelectionListener var1);
}

