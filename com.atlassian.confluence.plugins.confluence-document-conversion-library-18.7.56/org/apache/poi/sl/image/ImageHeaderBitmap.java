/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 */
package org.apache.poi.sl.image;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Units;

@Internal
public class ImageHeaderBitmap {
    private static final Logger LOG = LogManager.getLogger(ImageHeaderBitmap.class);
    private final Dimension size;

    public ImageHeaderBitmap(byte[] data, int offset) {
        BufferedImage img = null;
        try {
            img = ImageIO.read((InputStream)new UnsynchronizedByteArrayInputStream(data, offset, data.length - offset));
        }
        catch (IOException e) {
            LOG.atWarn().withThrowable(e).log("Can't determine image dimensions");
        }
        this.size = img == null ? new Dimension(200, 200) : new Dimension((int)Units.pixelToPoints(img.getWidth()), (int)Units.pixelToPoints(img.getHeight()));
    }

    public Dimension getSize() {
        return this.size;
    }
}

