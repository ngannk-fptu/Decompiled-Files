/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import org.xhtmlrenderer.swing.ImageResourceLoader;
import org.xhtmlrenderer.swing.MutableFSImage;

class ImageLoadItem {
    final ImageResourceLoader _imageResourceLoader;
    final String _uri;
    final MutableFSImage _mfsImage;
    final int _targetWidth;
    final int _targetHeight;

    public ImageLoadItem(ImageResourceLoader imageResourceLoader, String uri, MutableFSImage fsi, int width, int height) {
        this._imageResourceLoader = imageResourceLoader;
        this._uri = uri;
        this._mfsImage = fsi;
        this._targetWidth = width;
        this._targetHeight = height;
    }

    public boolean haveTargetDimensions() {
        return this._targetWidth > -1 && this._targetHeight > -1;
    }
}

