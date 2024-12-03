/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt;

import java.awt.Shape;

public interface Selectable {
    public boolean selectAt(double var1, double var3);

    public boolean selectTo(double var1, double var3);

    public boolean selectAll(double var1, double var3);

    public Object getSelection();

    public Shape getHighlightShape();
}

