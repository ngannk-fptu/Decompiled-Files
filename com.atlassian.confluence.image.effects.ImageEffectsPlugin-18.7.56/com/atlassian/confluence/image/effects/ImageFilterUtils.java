/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.DarkFeatures
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.image.effects;

import com.atlassian.confluence.image.effects.ImageDimensionsHelper;
import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.imageeffects.core.BaseEffect;
import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
final class ImageFilterUtils {
    private static final int DIMENSION_MAX = Integer.getInteger("atlassian.image_filter.transform.max.pixel", 4000);
    private static final int DROP_SHADOW_MAX = Integer.getInteger("atlassian.image_filter.transform.max.pixel.drop_shadow", Math.min(DIMENSION_MAX, 2000));
    private static Pattern exifDownloadPattern = Pattern.compile("/download/(attachments|thumbnails)/(\\d+)/([^\\?\\n\\r]+(\\.(?i)(jpg|tif)))(\\?version\\=(\\d+))?");

    private ImageFilterUtils() {
    }

    static boolean isImageTooBig(InputStream in, String contextName) throws IOException {
        try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(in);){
            Dimension imageDims = ImageDimensionsHelper.dimensionsForImage(imageInputStream);
            int dimSquared = (int)(imageDims.getHeight() * imageDims.getWidth());
            if ("drop-shadow".equals(contextName)) {
                boolean bl = dimSquared > DROP_SHADOW_MAX * DROP_SHADOW_MAX;
                return bl;
            }
            boolean bl = dimSquared > DIMENSION_MAX * DIMENSION_MAX;
            return bl;
        }
    }

    static byte[] renderImage(BufferedImage img) throws IOException {
        return ImageFilterUtils.renderImage(img, "jpg");
    }

    static byte[] renderImage(BufferedImage img, String format) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName(format);
        ImageWriter writer = imageWriters.next();
        try (ImageOutputStream output = ImageIO.createImageOutputStream(out);){
            byte[] byArray;
            block13: {
                Closeable ignored = writer::dispose;
                try {
                    ImageWriteParam iwp = writer.getDefaultWriteParam();
                    if (iwp.canWriteCompressed()) {
                        iwp.setCompressionMode(2);
                        iwp.setCompressionQuality(0.95f);
                    }
                    writer.setOutput(output);
                    IIOImage iiomage = new IIOImage(img, null, null);
                    writer.write(null, iiomage, iwp);
                    byArray = out.toByteArray();
                    if (ignored == null) break block13;
                }
                catch (Throwable throwable) {
                    if (ignored != null) {
                        try {
                            ignored.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ignored.close();
            }
            return byArray;
        }
    }

    static String getImageFormat(InputStream inputStream) throws IOException {
        inputStream.mark(Integer.MAX_VALUE);
        try {
            String string;
            block10: {
                ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);
                try {
                    Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
                    if (!readers.hasNext()) {
                        throw new IOException("Could not find the image reader.");
                    }
                    string = readers.next().getFormatName();
                    if (imageInputStream == null) break block10;
                }
                catch (Throwable throwable) {
                    if (imageInputStream != null) {
                        try {
                            imageInputStream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                imageInputStream.close();
            }
            return string;
        }
        finally {
            inputStream.reset();
        }
    }

    static <T extends AutoCloseable> QuietlyCloseableResource<T> quietlyCloseable(T resource) {
        return new QuietlyCloseableResource<T>(resource);
    }

    static ImageEffectFunction imageEffectFunction(String label) {
        return new ImageEffectFunction(label);
    }

    static String applyExifRotateEffect(HttpServletRequest request, String effects) {
        Matcher matcher;
        String download = request.getParameter("download");
        if (download == null && !DarkFeatures.isDarkFeatureEnabled((String)"imageFilter.exif.rotate.disabled") && (matcher = exifDownloadPattern.matcher(request.getRequestURL().append("?").append(request.getQueryString()))).find()) {
            effects = StringUtils.isEmpty((CharSequence)effects) ? "exif-rotate" : "exif-rotate," + (String)effects;
        }
        return effects;
    }

    public static class ImageEffectFunction
    implements BiFunction<BufferedImage, BaseEffect, BufferedImage> {
        private final String label;

        ImageEffectFunction(String label) {
            this.label = label;
        }

        @Override
        public BufferedImage apply(BufferedImage bufferedImage, BaseEffect baseEffect) {
            try {
                return baseEffect.processEffect(bufferedImage, this.label);
            }
            catch (FontFormatException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class QuietlyCloseableResource<T extends AutoCloseable>
    implements AutoCloseable {
        private static final Logger LOG = LoggerFactory.getLogger(QuietlyCloseableResource.class);
        private final T resource;

        QuietlyCloseableResource(T resource) {
            this.resource = resource;
        }

        public T get() {
            return this.resource;
        }

        @Override
        public void close() {
            try {
                this.resource.close();
            }
            catch (Exception e) {
                LOG.warn("Unexpected error whilst closing resource", (Throwable)e);
            }
        }
    }
}

