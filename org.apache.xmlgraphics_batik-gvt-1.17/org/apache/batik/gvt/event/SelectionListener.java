/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.event;

import java.util.EventListener;
import org.apache.batik.gvt.event.SelectionEvent;

public interface SelectionListener
extends EventListener {
    public void selectionChanged(SelectionEvent var1);

    public void selectionDone(SelectionEvent var1);

    public void selectionCleared(SelectionEvent var1);

    public void selectionStarted(SelectionEvent var1);
}

