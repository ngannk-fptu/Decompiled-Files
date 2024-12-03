/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.ImageUtil;

public class ImageReplacedElement
implements ReplacedElement {
    protected Image _image;
    private Point _location = new Point(0, 0);

    protected ImageReplacedElement() {
    }

    public ImageReplacedElement(Image image, int targetWidth, int targetHeight) {
        if (targetWidth > 0 || targetHeight > 0) {
            int w = image.getWidth(null);
            int h = image.getHeight(null);
            int newW = targetWidth;
            int newH = targetHeight;
            if (newW == -1) {
                newW = (int)((double)w * ((double)newH / (double)h));
            }
            if (newH == -1) {
                newH = (int)((double)h * ((double)newW / (double)w));
            }
            if (w != newW || h != newH) {
                String scalingType;
                image = image instanceof BufferedImage ? ImageUtil.getScaledInstance((BufferedImage)image, newW, newH) : ((scalingType = Configuration.valueFor("xr.image.scale", "HIGH").trim()).equalsIgnoreCase("HIGH") || scalingType.equalsIgnoreCase("MID") ? image.getScaledInstance(newW, newH, 4) : image.getScaledInstance(newW, newH, 2));
            }
        }
        this._image = image;
    }

    @Override
    public void detach(LayoutContext c) {
    }

    @Override
    public int getIntrinsicHeight() {
        return this._image.getHeight(null);
    }

    @Override
    public int getIntrinsicWidth() {
        return this._image.getWidth(null);
    }

    @Override
    public Point getLocation() {
        return this._location;
    }

    @Override
    public boolean isRequiresInteractivePaint() {
        return true;
    }

    @Override
    public void setLocation(int x, int y) {
        this._location = new Point(x, y);
    }

    public Image getImage() {
        return this._image;
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

