/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.image.effects;

import java.awt.Dimension;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

final class ImageDimensionsHelper {
    private ImageDimensionsHelper() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Dimension dimensionsForImage(ImageInputStream inputStream) throws IOException {
        Iterator<ImageReader> readers = ImageIO.getImageReaders(inputStream);
        if (!readers.hasNext()) {
            throw new IOException("There is no ImageReader available for the given ImageInputStream");
        }
        ImageReader reader = null;
        try {
            reader = readers.next();
            reader.setInput(inputStream);
            Dimension dimension = new Dimension(reader.getWidth(0), reader.getHeight(0));
            return dimension;
        }
        finally {
            if (reader != null) {
                reader.dispose();
            }
        }
    }
}

