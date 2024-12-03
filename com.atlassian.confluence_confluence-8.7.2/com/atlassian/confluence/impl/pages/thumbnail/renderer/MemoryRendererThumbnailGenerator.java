/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.thumbnail.Thumber
 *  com.atlassian.core.util.thumbnail.Thumbnail
 *  com.atlassian.core.util.thumbnail.Thumbnail$MimeType
 *  com.atlassian.core.util.thumbnail.ThumbnailDimension
 *  com.sun.media.jai.codec.SeekableStream
 *  javax.media.jai.JAI
 *  javax.media.jai.OpImage
 *  javax.media.jai.RenderedOp
 */
package com.atlassian.confluence.impl.pages.thumbnail.renderer;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.impl.pages.thumbnail.renderer.AbstractStreamedThumbnailGenerator;
import com.atlassian.confluence.util.io.InputStreamConsumer;
import com.atlassian.core.util.thumbnail.Thumber;
import com.atlassian.core.util.thumbnail.Thumbnail;
import com.atlassian.core.util.thumbnail.ThumbnailDimension;
import com.sun.media.jai.codec.SeekableStream;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedOp;

class MemoryRendererThumbnailGenerator
extends AbstractStreamedThumbnailGenerator {
    private final Thumber thumber;

    public MemoryRendererThumbnailGenerator(Thumber thumber) {
        this.thumber = thumber;
    }

    @Override
    protected InputStreamConsumer<Thumbnail> getInputStreamConsumer(File outputFile, int maxWidth, int maxHeight) {
        return is -> {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
            ImageDimensions dimensions = new JAIImageRenderer().renderThumbnail(bufferedInputStream, outputFile, maxWidth, maxHeight);
            return new Thumbnail(dimensions.getHeight(), dimensions.getWidth(), outputFile.getName(), 0L, Thumbnail.MimeType.PNG);
        };
    }

    private class JAIImageRenderer {
        private JAIImageRenderer() {
        }

        public ImageDimensions renderThumbnail(InputStream inputStream, File thumbnailFile, int maxWidth, int maxHeight) throws IOException {
            ImageDimensions imageDimensions;
            BufferedOutputStream thumbnailOutputStream = new BufferedOutputStream(new FileOutputStream(thumbnailFile));
            try {
                imageDimensions = this.scale(inputStream, thumbnailOutputStream, maxWidth, maxHeight);
            }
            catch (Throwable throwable) {
                try {
                    try {
                        ((OutputStream)thumbnailOutputStream).close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            ((OutputStream)thumbnailOutputStream).close();
            return imageDimensions;
        }

        private ImageDimensions scale(InputStream inputStream, OutputStream thumbnail, int maxWidth, int maxHeight) {
            RenderedImage image = this.loadImage(inputStream);
            ThumbnailDimension dimension = MemoryRendererThumbnailGenerator.this.thumber.determineScaleSize(maxWidth, maxHeight, image.getWidth(), image.getHeight());
            double scale = (double)dimension.getWidth() / (double)image.getWidth();
            ParameterBlock pb = new ParameterBlock();
            pb.addSource(image);
            pb.add(scale);
            pb.add(scale);
            pb.add(0.0f);
            pb.add(0.0f);
            pb.add(image);
            RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            image = JAI.create((String)"SubsampleAverage", (ParameterBlock)pb, (RenderingHints)qualityHints);
            JAI.create((String)"encode", (RenderedImage)image, (Object)thumbnail, (Object)"PNG");
            return new ImageDimensions(image.getWidth(), image.getHeight());
        }

        private RenderedImage loadImage(InputStream inputStream) {
            SeekableStream s = SeekableStream.wrapInputStream((InputStream)inputStream, (boolean)true);
            RenderedOp img = JAI.create((String)"stream", (Object)s);
            ((OpImage)img.getRendering()).setTileCache(null);
            return img;
        }
    }
}

