/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import java.awt.Point;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextReplacedElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.RenderingContext;

public class BookmarkElement
implements ITextReplacedElement {
    private Point _location = new Point(0, 0);
    private String _anchorName;

    @Override
    public int getIntrinsicWidth() {
        return 0;
    }

    @Override
    public int getIntrinsicHeight() {
        return 0;
    }

    @Override
    public Point getLocation() {
        return this._location;
    }

    @Override
    public void setLocation(int x, int y) {
        this._location = new Point(x, y);
    }

    @Override
    public void detach(LayoutContext c) {
        c.removeBoxId(this.getAnchorName());
    }

    public String getAnchorName() {
        return this._anchorName;
    }

    public void setAnchorName(String anchorName) {
        this._anchorName = anchorName;
    }

    @Override
    public boolean isRequiresInteractivePaint() {
        return false;
    }

    @Override
    public void paint(RenderingContext c, ITextOutputDevice outputDevice, BlockBox box) {
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

