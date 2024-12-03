/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import java.awt.Point;
import java.awt.Rectangle;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextReplacedElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.RenderingContext;

public class ITextImageElement
implements ITextReplacedElement {
    private FSImage _image;
    private Point _location = new Point(0, 0);

    public ITextImageElement(FSImage image) {
        this._image = image;
    }

    @Override
    public int getIntrinsicWidth() {
        return this._image.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return this._image.getHeight();
    }

    @Override
    public Point getLocation() {
        return this._location;
    }

    @Override
    public void setLocation(int x, int y) {
        this._location = new Point(x, y);
    }

    public FSImage getImage() {
        return this._image;
    }

    @Override
    public void detach(LayoutContext c) {
    }

    @Override
    public boolean isRequiresInteractivePaint() {
        return false;
    }

    @Override
    public void paint(RenderingContext c, ITextOutputDevice outputDevice, BlockBox box) {
        Rectangle contentBounds = box.getContentAreaEdge(box.getAbsX(), box.getAbsY(), c);
        ReplacedElement element = box.getReplacedElement();
        outputDevice.drawImage(((ITextImageElement)element).getImage(), contentBounds.x, contentBounds.y);
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

