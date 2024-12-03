/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  javax.annotation.Nonnull
 */
package com.atlassian.core.util.thumbnail;

import com.atlassian.core.util.thumbnail.GIFUtils;
import com.atlassian.core.util.thumbnail.ThumbnailDimension;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public final class ThumbnailUtil {
    private static final List<String> THUMBNAIL_MIME_TYPES = Collections.unmodifiableList(Arrays.asList(ImageIO.getReaderMIMETypes()));
    private static final List<String> THUMBNAIL_FORMATS = Collections.unmodifiableList(Arrays.asList(ImageIO.getReaderFormatNames()));

    public static List<String> getThumbnailMimeTypes() {
        return THUMBNAIL_MIME_TYPES;
    }

    public static List<String> getThumbnailFormats() {
        return THUMBNAIL_FORMATS;
    }

    public static boolean isMimeTypeSupported(String mimeType) {
        if (mimeType != null && !mimeType.trim().equalsIgnoreCase("")) {
            for (String supportedFormat : THUMBNAIL_MIME_TYPES) {
                if (!mimeType.equalsIgnoreCase(supportedFormat)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean isFormatSupported(String formatName) {
        if (formatName != null && !formatName.trim().equalsIgnoreCase("")) {
            for (String supportedFormat : THUMBNAIL_FORMATS) {
                if (!formatName.equalsIgnoreCase(supportedFormat)) continue;
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ThumbnailDimension dimensionsForImage(@Nonnull InputStream inputStream) throws IOException {
        Throwable throwable = null;
        try (ImageInputStream imageInputStream = ThumbnailUtil.getImageInputStream(inputStream);){
            Optional<ImageReader> imageReader = ThumbnailUtil.findFirstImageReader(imageInputStream);
            if (!imageReader.isPresent()) {
                throw new IOException("There is no ImageReader available for the given ImageInputStream");
            }
            ImageReader reader = imageReader.get();
            try {
                reader.setInput(imageInputStream);
                ThumbnailDimension thumbnailDimension = new ThumbnailDimension(reader.getWidth(0), reader.getHeight(0));
                reader.dispose();
                return thumbnailDimension;
            }
            catch (Throwable throwable2) {
                try {
                    reader.dispose();
                    throw throwable2;
                }
                catch (Throwable throwable3) {
                    throwable = throwable3;
                    throw throwable3;
                }
            }
        }
    }

    public static ThumbnailDimension determineScaledDimensions(int maxWidth, int maxHeight, @Nonnull Image image) {
        return ThumbnailUtil.determineScaledDimensions(maxWidth, maxHeight, image.getWidth(null), image.getHeight(null));
    }

    public static ThumbnailDimension determineScaledDimensions(int maxWidth, int maxHeight, int imageWidth, int imageHeight) {
        if (maxHeight > imageHeight && maxWidth > imageWidth) {
            return new ThumbnailDimension(imageWidth, imageHeight);
        }
        double thumbRatio = (double)maxWidth / (double)maxHeight;
        double imageRatio = (double)imageWidth / (double)imageHeight;
        if (thumbRatio < imageRatio) {
            return new ThumbnailDimension(maxWidth, (int)Math.max(1.0, (double)maxWidth / imageRatio));
        }
        return new ThumbnailDimension((int)Math.max(1.0, (double)maxHeight * imageRatio), maxHeight);
    }

    public static ImageInputStream getImageInputStream(@Nonnull InputStream inputStream) throws IOException {
        ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);
        if (GIFUtils.isGif(imageInputStream)) {
            return GIFUtils.sanitize(imageInputStream);
        }
        return imageInputStream;
    }

    @Deprecated
    public static com.google.common.base.Optional<ImageReader> getFirstImageReader(@Nonnull ImageInputStream imageInputStream) throws IOException {
        return com.google.common.base.Optional.fromJavaUtil(ThumbnailUtil.findFirstImageReader(imageInputStream));
    }

    public static Optional<ImageReader> findFirstImageReader(@Nonnull ImageInputStream imageInputStream) throws IOException {
        Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
        return readers.hasNext() ? Optional.of(readers.next()) : Optional.empty();
    }

    public static int computeSubsamplingRatio(ThumbnailDimension original, ThumbnailDimension scaled, int samplingFactor) {
        double widthRatio = (double)original.getWidth() / (double)scaled.getWidth();
        double heightRatio = (double)original.getHeight() / (double)scaled.getHeight();
        double maxRatio = Math.max(widthRatio, heightRatio);
        double ratio = maxRatio / (double)samplingFactor;
        return Math.max(1, (int)Math.floor(ratio));
    }
}

