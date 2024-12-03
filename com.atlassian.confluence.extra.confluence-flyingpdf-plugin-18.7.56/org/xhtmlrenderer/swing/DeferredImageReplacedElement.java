/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.swing.AWTFSImage;
import org.xhtmlrenderer.swing.ImageReplacedElement;
import org.xhtmlrenderer.swing.RepaintListener;
import org.xhtmlrenderer.util.ImageUtil;
import org.xhtmlrenderer.util.XRLog;

public class DeferredImageReplacedElement
extends ImageReplacedElement {
    private Point _location = new Point(0, 0);
    private final RepaintListener repaintListener;
    private final int _targetHeight;
    private final int _targetWidth;
    private boolean _doScaleImage;
    private boolean _loaded;
    private final ImageResource _imageResource;

    public DeferredImageReplacedElement(ImageResource imageResource, RepaintListener repaintListener, int w, int h) {
        this._imageResource = imageResource;
        this._loaded = false;
        this.repaintListener = repaintListener;
        if (w == -1 && h == -1) {
            this._doScaleImage = false;
            this._targetHeight = 1;
            this._targetWidth = 1;
        } else {
            this._doScaleImage = true;
            this._targetHeight = Math.max(1, h);
            this._targetWidth = Math.max(1, w);
        }
        this._image = ImageUtil.createCompatibleBufferedImage(this._targetWidth, this._targetHeight);
    }

    @Override
    public void detach(LayoutContext c) {
    }

    @Override
    public int getIntrinsicHeight() {
        return this._loaded ? this._image.getHeight(null) : this._targetHeight;
    }

    @Override
    public int getIntrinsicWidth() {
        return this._loaded ? this._image.getWidth(null) : this._targetWidth;
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

    @Override
    public Image getImage() {
        if (!this._loaded && this._imageResource.isLoaded()) {
            BufferedImage image = ((AWTFSImage)this._imageResource.getImage()).getImage();
            if (this._doScaleImage && (this._targetWidth > 0 || this._targetHeight > 0)) {
                int w = ((Image)image).getWidth(null);
                int h = ((Image)image).getHeight(null);
                int newW = this._targetWidth;
                int newH = this._targetHeight;
                if (newW == -1) {
                    newW = (int)((double)w * ((double)newH / (double)h));
                }
                if (newH == -1) {
                    newH = (int)((double)h * ((double)newW / (double)w));
                }
                if (w != newW || h != newH) {
                    if (image instanceof BufferedImage) {
                        image = ImageUtil.getScaledInstance(image, newW, newH);
                    } else {
                        throw new RuntimeException("image is not a buffered image! " + this._imageResource.getImageUri());
                    }
                }
                this._image = image;
            } else {
                this._image = image;
            }
            this._loaded = true;
            XRLog.load(Level.FINE, "Icon: replaced image " + this._imageResource.getImageUri() + ", repaint requested");
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    DeferredImageReplacedElement.this.repaintListener.repaintRequested(DeferredImageReplacedElement.this._doScaleImage);
                }
            });
        }
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

