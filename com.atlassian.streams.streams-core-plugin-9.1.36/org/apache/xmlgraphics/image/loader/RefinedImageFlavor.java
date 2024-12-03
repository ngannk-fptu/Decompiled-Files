/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader;

import org.apache.xmlgraphics.image.loader.ImageFlavor;

public abstract class RefinedImageFlavor
extends ImageFlavor {
    private ImageFlavor parentFlavor;

    protected RefinedImageFlavor(ImageFlavor parentFlavor) {
        this(parentFlavor.getName(), parentFlavor);
    }

    protected RefinedImageFlavor(String name, ImageFlavor parentFlavor) {
        super(name);
        this.parentFlavor = parentFlavor;
    }

    public ImageFlavor getParentFlavor() {
        return this.parentFlavor;
    }

    @Override
    public String getMimeType() {
        return this.parentFlavor.getMimeType();
    }

    @Override
    public String getNamespace() {
        return this.parentFlavor.getNamespace();
    }

    @Override
    public boolean isCompatible(ImageFlavor flavor) {
        return this.getParentFlavor().isCompatible(flavor) || super.isCompatible(flavor);
    }
}

