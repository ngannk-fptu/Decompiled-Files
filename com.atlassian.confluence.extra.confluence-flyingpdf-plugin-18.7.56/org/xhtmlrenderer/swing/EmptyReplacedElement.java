/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Point;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.layout.LayoutContext;

public class EmptyReplacedElement
implements ReplacedElement {
    private int _width;
    private int _height;
    private Point _location = new Point(0, 0);

    public EmptyReplacedElement(int width, int height) {
        this._width = width;
        this._height = height;
    }

    @Override
    public void detach(LayoutContext c) {
    }

    @Override
    public int getIntrinsicHeight() {
        return this._height;
    }

    @Override
    public int getIntrinsicWidth() {
        return this._width;
    }

    @Override
    public Point getLocation() {
        return this._location;
    }

    @Override
    public boolean isRequiresInteractivePaint() {
        return false;
    }

    @Override
    public void setLocation(int x, int y) {
        this._location = new Point(0, 0);
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

