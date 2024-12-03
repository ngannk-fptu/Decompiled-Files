/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSize;

public abstract class AbstractImage
implements Image {
    private ImageInfo info;

    public AbstractImage(ImageInfo info) {
        this.info = info;
    }

    @Override
    public ImageInfo getInfo() {
        return this.info;
    }

    @Override
    public ImageSize getSize() {
        return this.getInfo().getSize();
    }

    @Override
    public ColorSpace getColorSpace() {
        return null;
    }

    @Override
    public ICC_Profile getICCProfile() {
        if (this.getColorSpace() instanceof ICC_ColorSpace) {
            return ((ICC_ColorSpace)this.getColorSpace()).getProfile();
        }
        return null;
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.getInfo();
    }
}

