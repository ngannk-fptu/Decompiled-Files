/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.io.IOException;
import java.nio.ByteOrder;
import javax.imageio.stream.ImageInputStream;
import javax.xml.transform.Source;
import org.apache.xmlgraphics.image.loader.ImageContext;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSize;
import org.apache.xmlgraphics.image.loader.impl.AbstractImagePreloader;
import org.apache.xmlgraphics.image.loader.util.ImageUtil;
import org.apache.xmlgraphics.util.UnitConv;

public class PreloaderBMP
extends AbstractImagePreloader {
    protected static final int BMP_SIG_LENGTH = 2;
    private static final int WIDTH_OFFSET = 18;

    @Override
    public ImageInfo preloadImage(String uri, Source src, ImageContext context) throws IOException, ImageException {
        boolean supported;
        if (!ImageUtil.hasImageInputStream(src)) {
            return null;
        }
        ImageInputStream in = ImageUtil.needImageInputStream(src);
        byte[] header = this.getHeader(in, 2);
        boolean bl = supported = header[0] == 66 && header[1] == 77;
        if (supported) {
            ImageInfo info = new ImageInfo(uri, "image/bmp");
            info.setSize(this.determineSize(in, context));
            return info;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ImageSize determineSize(ImageInputStream in, ImageContext context) throws IOException, ImageException {
        in.mark();
        ByteOrder oldByteOrder = in.getByteOrder();
        try {
            int yRes;
            double yResDPI;
            ImageSize size = new ImageSize();
            in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            in.skipBytes(18);
            int width = in.readInt();
            int height = in.readInt();
            size.setSizeInPixels(width, height);
            in.skipBytes(12);
            int xRes = in.readInt();
            double xResDPI = UnitConv.in2mm((double)xRes / 1000.0);
            if (xResDPI == 0.0) {
                xResDPI = context.getSourceResolution();
            }
            if ((yResDPI = UnitConv.in2mm((double)(yRes = in.readInt()) / 1000.0)) == 0.0) {
                yResDPI = context.getSourceResolution();
            }
            size.setResolution(xResDPI, yResDPI);
            size.calcSizeFromPixels();
            ImageSize imageSize = size;
            return imageSize;
        }
        finally {
            in.setByteOrder(oldByteOrder);
            in.reset();
        }
    }
}

