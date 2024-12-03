/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import java.net.URI;
import org.xhtmlrenderer.extend.FSImage;

public class PDFAsImage
implements FSImage {
    private URI _source;
    private float _width;
    private float _height;
    private float _unscaledWidth;
    private float _unscaledHeight;

    public PDFAsImage(URI source) {
        this._source = source;
    }

    @Override
    public int getWidth() {
        return (int)this._width;
    }

    @Override
    public int getHeight() {
        return (int)this._height;
    }

    @Override
    public void scale(int width, int height) {
        float targetWidth = width;
        float targetHeight = height;
        if (width == -1) {
            targetWidth = this.getWidthAsFloat() * (targetHeight / (float)this.getHeight());
        }
        if (height == -1) {
            targetHeight = this.getHeightAsFloat() * (targetWidth / (float)this.getWidth());
        }
        this._width = targetWidth;
        this._height = targetHeight;
    }

    public URI getURI() {
        return this._source;
    }

    public void setInitialWidth(float width) {
        if (this._width == 0.0f) {
            this._width = width;
            this._unscaledWidth = width;
        }
    }

    public void setInitialHeight(float height) {
        if (this._height == 0.0f) {
            this._height = height;
            this._unscaledHeight = height;
        }
    }

    public float getWidthAsFloat() {
        return this._width;
    }

    public float getHeightAsFloat() {
        return this._height;
    }

    public float getUnscaledHeight() {
        return this._unscaledHeight;
    }

    public void setUnscaledHeight(float unscaledHeight) {
        this._unscaledHeight = unscaledHeight;
    }

    public float getUnscaledWidth() {
        return this._unscaledWidth;
    }

    public void setUnscaledWidth(float unscaledWidth) {
        this._unscaledWidth = unscaledWidth;
    }

    public float scaleHeight() {
        return this._height / this._unscaledHeight;
    }

    public float scaleWidth() {
        return this._width / this._unscaledWidth;
    }
}

