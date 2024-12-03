/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.spi;

import java.io.IOException;
import java.util.Map;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;

public interface ImageLoader {
    public static final int NO_LOADING_PENALTY = 0;
    public static final int MEDIUM_LOADING_PENALTY = 10;

    public Image loadImage(ImageInfo var1, Map var2, ImageSessionContext var3) throws ImageException, IOException;

    public Image loadImage(ImageInfo var1, ImageSessionContext var2) throws ImageException, IOException;

    public ImageFlavor getTargetFlavor();

    public int getUsagePenalty();
}

