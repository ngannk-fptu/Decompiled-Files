/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import com.lowagie.text.Image;
import org.xhtmlrenderer.extend.FSImage;

public class ITextFSImage
implements FSImage,
Cloneable {
    private Image _image;

    public ITextFSImage(Image image) {
        this._image = image;
    }

    @Override
    public int getWidth() {
        return (int)this._image.getPlainWidth();
    }

    @Override
    public int getHeight() {
        return (int)this._image.getPlainHeight();
    }

    @Override
    public void scale(int width, int height) {
        if (width > 0 || height > 0) {
            int currentWith = this.getWidth();
            int currentHeight = this.getHeight();
            int targetWidth = width;
            int targetHeight = height;
            if (targetWidth == -1) {
                targetWidth = (int)((double)currentWith * ((double)targetHeight / (double)currentHeight));
            }
            if (targetHeight == -1) {
                targetHeight = (int)((double)currentHeight * ((double)targetWidth / (double)currentWith));
            }
            if (currentWith != targetWidth || currentHeight != targetHeight) {
                this._image.scaleAbsolute(targetWidth, targetHeight);
            }
        }
    }

    public Image getImage() {
        return this._image;
    }

    public Object clone() {
        return new ITextFSImage(Image.getInstance(this._image));
    }
}

