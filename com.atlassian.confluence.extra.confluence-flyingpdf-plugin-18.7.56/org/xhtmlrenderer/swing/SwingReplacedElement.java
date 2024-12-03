/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JComponent;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.swing.RootPanel;

public class SwingReplacedElement
implements ReplacedElement {
    private JComponent _component;
    private Dimension intrinsicSize;

    public SwingReplacedElement(JComponent component) {
        this._component = component;
    }

    public JComponent getJComponent() {
        return this._component;
    }

    public void setIntrinsicSize(Dimension intrinsicSize) {
        this.intrinsicSize = intrinsicSize;
    }

    @Override
    public int getIntrinsicHeight() {
        return this.intrinsicSize == null ? this._component.getSize().height : this.intrinsicSize.height;
    }

    @Override
    public int getIntrinsicWidth() {
        return this.intrinsicSize == null ? this._component.getSize().width : this.intrinsicSize.width;
    }

    @Override
    public void setLocation(int x, int y) {
        this._component.setLocation(x, y);
    }

    @Override
    public Point getLocation() {
        return this._component.getLocation();
    }

    @Override
    public void detach(LayoutContext c) {
        if (c.isInteractive()) {
            ((RootPanel)c.getCanvas()).remove(this.getJComponent());
        }
    }

    @Override
    public boolean isRequiresInteractivePaint() {
        return false;
    }

    @Override
    public int getBaseline() {
        return 0;
    }

    @Override
    public boolean hasBaseline() {
        return false;
    }
}

