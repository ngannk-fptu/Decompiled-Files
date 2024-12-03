/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.content.render.image;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.google.common.base.Preconditions;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class ImageRenderUtils {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ImageDimensions dimensionsForImage(ImageInputStream inputStream) throws IOException {
        Iterator<ImageReader> readers = ImageIO.getImageReaders(inputStream);
        if (!readers.hasNext()) {
            throw new IOException("There is not ImageReader available for the given ImageInputStream");
        }
        ImageReader reader = null;
        try {
            reader = readers.next();
            reader.setInput(inputStream);
            ImageDimensions imageDimensions = new ImageDimensions(reader.getWidth(0), reader.getHeight(0));
            return imageDimensions;
        }
        finally {
            if (reader != null) {
                reader.dispose();
            }
        }
    }

    public static ImageDimensions dimensionsForImage(InputStream is) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
        return ImageRenderUtils.dimensionsForImage(ImageIO.createImageInputStream(bufferedInputStream));
    }

    public static void writePngToStream(BufferedImage image, HttpServletResponse response) throws IOException {
        Preconditions.checkNotNull((Object)image);
        Preconditions.checkNotNull((Object)response);
        response.setContentType("image/png");
        try (ServletOutputStream outputStream = response.getOutputStream();){
            ImageIO.write((RenderedImage)image, "png", (OutputStream)outputStream);
            outputStream.flush();
        }
    }
}

