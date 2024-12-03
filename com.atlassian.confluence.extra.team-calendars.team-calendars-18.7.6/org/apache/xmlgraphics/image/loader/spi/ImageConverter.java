/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.spi;

import java.io.IOException;
import java.util.Map;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;

public interface ImageConverter {
    public static final int NO_CONVERSION_PENALTY = 0;
    public static final int MINIMAL_CONVERSION_PENALTY = 1;
    public static final int MEDIUM_CONVERSION_PENALTY = 10;

    public Image convert(Image var1, Map var2) throws ImageException, IOException;

    public ImageFlavor getTargetFlavor();

    public ImageFlavor getSourceFlavor();

    public int getConversionPenalty();
}

