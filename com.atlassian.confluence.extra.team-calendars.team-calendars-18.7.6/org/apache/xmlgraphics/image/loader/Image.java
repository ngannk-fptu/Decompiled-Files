/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSize;

public interface Image {
    public ImageInfo getInfo();

    public ImageSize getSize();

    public ImageFlavor getFlavor();

    public boolean isCacheable();

    public ICC_Profile getICCProfile();

    public ColorSpace getColorSpace();
}

