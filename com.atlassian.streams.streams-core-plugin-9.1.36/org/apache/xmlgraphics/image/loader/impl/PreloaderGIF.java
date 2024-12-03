/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.xml.transform.Source;
import org.apache.xmlgraphics.image.loader.ImageContext;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSize;
import org.apache.xmlgraphics.image.loader.impl.AbstractImagePreloader;
import org.apache.xmlgraphics.image.loader.util.ImageUtil;

public class PreloaderGIF
extends AbstractImagePreloader {
    private static final int GIF_SIG_LENGTH = 10;

    @Override
    public ImageInfo preloadImage(String uri, Source src, ImageContext context) throws IOException {
        boolean supported;
        if (!ImageUtil.hasImageInputStream(src)) {
            return null;
        }
        ImageInputStream in = ImageUtil.needImageInputStream(src);
        byte[] header = this.getHeader(in, 10);
        boolean bl = supported = header[0] == 71 && header[1] == 73 && header[2] == 70 && header[3] == 56 && (header[4] == 55 || header[4] == 57) && header[5] == 97;
        if (supported) {
            ImageInfo info = new ImageInfo(uri, "image/gif");
            info.setSize(this.determineSize(header, context, in));
            return info;
        }
        return null;
    }

    private ImageSize determineSize(byte[] header, ImageContext context, ImageInputStream in) throws IOException {
        int[] dim = this.extractImageMetadata(in);
        ImageSize size = new ImageSize(dim[0], dim[1], context.getSourceResolution());
        size.calcSizeFromPixels();
        return size;
    }

    private int[] extractImageMetadata(ImageInputStream in) throws IOException {
        long startPos = in.getStreamPosition();
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
        ImageReader reader = readers.next();
        reader.setInput(in, true);
        int width = reader.getWidth(0);
        int height = reader.getHeight(0);
        int[] dim = new int[]{width, height};
        in.seek(startPos);
        return dim;
    }
}

