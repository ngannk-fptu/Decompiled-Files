/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.thumbnail.Thumber
 *  com.atlassian.core.util.thumbnail.Thumbnail
 *  com.atlassian.core.util.thumbnail.Thumbnail$MimeType
 *  com.atlassian.core.util.thumbnail.ThumbnailDimension
 */
package com.atlassian.confluence.impl.pages.thumbnail.renderer;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.impl.pages.thumbnail.renderer.AbstractStreamedThumbnailGenerator;
import com.atlassian.confluence.util.io.InputStreamConsumer;
import com.atlassian.core.util.thumbnail.Thumber;
import com.atlassian.core.util.thumbnail.Thumbnail;
import com.atlassian.core.util.thumbnail.ThumbnailDimension;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

class StreamRendererThumbnailGenerator
extends AbstractStreamedThumbnailGenerator {
    private final Thumber thumber;

    public StreamRendererThumbnailGenerator(Thumber thumber) {
        this.thumber = thumber;
    }

    @Override
    protected InputStreamConsumer<Thumbnail> getInputStreamConsumer(File outputFile, int maxWidth, int maxHeight) {
        return is -> {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
            ImageDimensions dimensions = new StreamingImageRenderer(this.thumber).renderThumbnail(bufferedInputStream, outputFile, maxWidth, maxHeight);
            return new Thumbnail(dimensions.getHeight(), dimensions.getWidth(), outputFile.getName(), 0L, Thumbnail.MimeType.PNG);
        };
    }

    private static class StreamingImageRenderer {
        private final Thumber thumber;

        public StreamingImageRenderer(Thumber thumber) {
            this.thumber = thumber;
        }

        public ImageDimensions renderThumbnail(InputStream inputStream, File thumbnailFile, int maxWidth, int maxHeight) throws IOException {
            ImageInputStream iis = ImageIO.createImageInputStream(inputStream);
            BufferedImage bi = this.scaleDown(iis, maxWidth, maxHeight);
            ImageIO.write((RenderedImage)bi, "png", thumbnailFile);
            return new ImageDimensions(bi.getWidth(), bi.getHeight());
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private BufferedImage scaleDown(ImageInputStream inputStream, int maxWidth, int maxHeight) throws IOException {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(inputStream);
            if (!readers.hasNext()) {
                throw new IOException("There is not ImageReader available for the given ImageInputStream");
            }
            ImageReader reader = null;
            try {
                reader = readers.next();
                ImageReadParam param = reader.getDefaultReadParam();
                reader.setInput(inputStream);
                ThumbnailDimension dimension = this.thumber.determineScaleSize(maxWidth, maxHeight, reader.getWidth(0), reader.getHeight(0));
                int ratio = (int)Math.round((double)reader.getWidth(0) / (double)dimension.getWidth());
                param.setSourceSubsampling(ratio, ratio, 0, 0);
                BufferedImage bufferedImage = reader.read(0, param);
                return bufferedImage;
            }
            finally {
                if (reader != null) {
                    reader.dispose();
                }
            }
        }
    }
}

