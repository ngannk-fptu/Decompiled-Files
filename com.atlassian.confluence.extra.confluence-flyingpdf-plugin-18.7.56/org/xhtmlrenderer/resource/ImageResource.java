/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.resource;

import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.resource.AbstractResource;
import org.xhtmlrenderer.swing.AWTFSImage;
import org.xhtmlrenderer.swing.MutableFSImage;
import org.xml.sax.InputSource;

public class ImageResource
extends AbstractResource {
    private final String _imageUri;
    private FSImage _img;

    public ImageResource(String uri, FSImage img) {
        super((InputSource)null);
        this._imageUri = uri;
        this._img = img;
    }

    public FSImage getImage() {
        return this._img;
    }

    public boolean isLoaded() {
        return this._img instanceof MutableFSImage ? ((MutableFSImage)this._img).isLoaded() : true;
    }

    public String getImageUri() {
        return this._imageUri;
    }

    public boolean hasDimensions(int width, int height) {
        if (this.isLoaded()) {
            if (this._img instanceof AWTFSImage) {
                AWTFSImage awtfi = (AWTFSImage)this._img;
                return awtfi.getWidth() == width && awtfi.getHeight() == height;
            }
            return false;
        }
        return false;
    }
}

