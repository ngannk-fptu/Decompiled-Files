/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.event;

import java.awt.Shape;

public class SelectionEvent {
    public static final int SELECTION_CHANGED = 1;
    public static final int SELECTION_CLEARED = 3;
    public static final int SELECTION_STARTED = 4;
    public static final int SELECTION_DONE = 2;
    protected Shape highlightShape;
    protected Object selection;
    protected int id;

    public SelectionEvent(Object selection, int id, Shape highlightShape) {
        this.id = id;
        this.selection = selection;
        this.highlightShape = highlightShape;
    }

    public Shape getHighlightShape() {
        return this.highlightShape;
    }

    public Object getSelection() {
        return this.selection;
    }

    public int getID() {
        return this.id;
    }
}

