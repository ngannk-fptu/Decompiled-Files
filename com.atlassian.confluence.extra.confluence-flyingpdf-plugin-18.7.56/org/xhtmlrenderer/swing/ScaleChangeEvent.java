/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import org.xhtmlrenderer.simple.XHTMLPanel;

public class ScaleChangeEvent {
    private XHTMLPanel pane;
    private double scale;

    public ScaleChangeEvent(XHTMLPanel pane, double scale) {
        this.pane = pane;
        this.scale = scale;
    }

    public XHTMLPanel getComponent() {
        return this.pane;
    }

    public double getScale() {
        return this.scale;
    }
}

