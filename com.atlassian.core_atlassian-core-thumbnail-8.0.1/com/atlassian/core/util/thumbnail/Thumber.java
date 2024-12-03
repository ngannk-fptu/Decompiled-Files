/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.exception.FailedPredicateException
 *  com.atlassian.core.util.ImageInfo
 *  com.atlassian.core.util.ReusableBufferedInputStream
 *  javax.annotation.Nonnull
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.core.util.thumbnail;

import com.atlassian.core.exception.FailedPredicateException;
import com.atlassian.core.util.ImageInfo;
import com.atlassian.core.util.ReusableBufferedInputStream;
import com.atlassian.core.util.thumbnail.Thumbnail;
import com.atlassian.core.util.thumbnail.ThumbnailDimension;
import com.atlassian.core.util.thumbnail.ThumbnailRenderException;
import com.atlassian.core.util.thumbnail.ThumbnailUtil;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Thumber {
    private static final Logger log = LoggerFactory.getLogger(Thumber.class);
    private static final Predicate<ReusableBufferedInputStream> ALWAYS_TRUE = x -> true;
    private float encodingQuality = 0.8f;
    private final Thumbnail.MimeType mimeType;
    private final int samplingFactor;
    public static final int DEFAULT_SAMPLING_FACTOR = 4;

    public Thumber() {
        this(Thumbnail.MimeType.JPG);
    }

    public Thumber(Thumbnail.MimeType mimeType) {
        this(mimeType, 4);
    }

    public Thumber(Thumbnail.MimeType mimeType, int samplingFactor) {
        if (mimeType == null) {
            throw new IllegalArgumentException("mimeType cannot be null");
        }
        if (samplingFactor < 1) {
            throw new IllegalArgumentException("Sampling factor must be a positive number");
        }
        this.mimeType = mimeType;
        this.samplingFactor = samplingFactor;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Thumbnail retrieveOrCreateThumbNail(File originalFile, File thumbnailFile, int maxWidth, int maxHeight, long thumbnailId) throws MalformedURLException {
        Thumbnail thumbnail;
        FileInputStream originalFileStream = null;
        try {
            originalFileStream = new FileInputStream(originalFile);
            thumbnail = this.retrieveOrCreateThumbNail(originalFileStream, originalFile.getName(), thumbnailFile, maxWidth, maxHeight, thumbnailId);
        }
        catch (FileNotFoundException e) {
            try {
                log.error("Unable to create thumbnail: file not found: " + originalFile.getAbsolutePath());
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(originalFileStream);
                throw throwable;
            }
            IOUtils.closeQuietly((InputStream)originalFileStream);
            return null;
        }
        IOUtils.closeQuietly((InputStream)originalFileStream);
        return thumbnail;
    }

    public void storeImage(BufferedImage scaledImage, File file) throws FileNotFoundException, ThumbnailRenderException {
        if (scaledImage == null) {
            log.warn("Can't store a null scaledImage.");
            return;
        }
        this.checkOutputFileCreation(file);
        if (this.mimeType == Thumbnail.MimeType.JPG) {
            BufferedImage newImage = this.removeAlphaIfExists(scaledImage);
            this.storeImageAsJpeg(newImage, file);
        } else {
            this.storeImageAsPng(scaledImage, file);
        }
    }

    private BufferedImage removeAlphaIfExists(BufferedImage image) {
        if (image.getTransparency() == 1) {
            return image;
        }
        int type = this.determineImageType(image.getType());
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, type);
        Graphics2D g = newImage.createGraphics();
        g.drawImage((Image)newImage, 0, 0, null);
        g.dispose();
        return newImage;
    }

    private int determineImageType(int srcType) {
        if (srcType == 6 || srcType == 7) {
            return 4;
        }
        return 1;
    }

    private void checkOutputFileCreation(File outputFile) {
        try {
            FileUtils.touch((File)outputFile);
            FileUtils.deleteQuietly((File)outputFile);
        }
        catch (IOException ioe) {
            throw new ThumbnailRenderException(ioe);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void storeImageAsJpeg(BufferedImage scaledImage, File file) {
        FileImageOutputStream flout = null;
        ImageWriter writer = null;
        try {
            flout = new FileImageOutputStream(file);
            writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(2);
            param.setCompressionQuality(this.encodingQuality);
            writer.setOutput(flout);
            writer.write(null, new IIOImage(scaledImage, null, null), param);
        }
        catch (IOException e) {
            log.error("Error encoding the thumbnail image to JPEG", (Throwable)e);
        }
        finally {
            if (writer != null) {
                writer.dispose();
            }
            try {
                if (flout != null) {
                    flout.close();
                }
            }
            catch (IOException iOException) {}
        }
    }

    private void storeImageAsPng(BufferedImage image, File file) throws FileNotFoundException {
        try {
            ImageIO.write((RenderedImage)image, "png", file);
        }
        catch (IOException e) {
            log.error("Error encoding the thumbnail image to PNG", (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BufferedImage scaleImage(Image imageToScale, ThumbnailDimension newDimensions) {
        BufferedImage sourceImage = Pictures.toBufferedImage(imageToScale);
        Image scaledInstance = sourceImage.getScaledInstance(newDimensions.getWidth(), newDimensions.getHeight(), 4);
        BufferedImage bufferedScaledImage = new BufferedImage(newDimensions.getWidth(), newDimensions.getHeight(), Pictures.hasAlpha(imageToScale) ? 2 : 1);
        Graphics graphics = bufferedScaledImage.getGraphics();
        try {
            graphics.drawImage(scaledInstance, 0, 0, null);
        }
        finally {
            graphics.dispose();
        }
        return bufferedScaledImage;
    }

    public BufferedImage scaleImage(int maxWidth, int maxHeight, InputStream imageStream) throws IOException {
        try (ImageInputStream imageInputStream = ThumbnailUtil.getImageInputStream(imageStream);){
            BufferedImage bufferedImage = this.scaleImage(maxWidth, maxHeight, imageInputStream);
            return bufferedImage;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BufferedImage scaleImage(int maxWidth, int maxHeight, ImageInputStream imageInputStream) throws IOException {
        Optional<ImageReader> imageReader = ThumbnailUtil.findFirstImageReader(imageInputStream);
        if (!imageReader.isPresent()) {
            throw new IOException("Cannot read the image");
        }
        ImageReader reader = imageReader.get();
        try {
            reader.setInput(imageInputStream, false, true);
            ThumbnailDimension originalImageDimensions = new ThumbnailDimension(reader.getWidth(0), reader.getHeight(0));
            ThumbnailDimension scaledImageDimension = ThumbnailUtil.determineScaledDimensions(maxWidth, maxHeight, originalImageDimensions.getWidth(), originalImageDimensions.getHeight());
            int ratio = ThumbnailUtil.computeSubsamplingRatio(originalImageDimensions, scaledImageDimension, this.samplingFactor);
            ImageReadParam param = reader.getDefaultReadParam();
            param.setSourceSubsampling(ratio, ratio, 0, 0);
            BufferedImage scaledImage = reader.read(0, param);
            BufferedImage bufferedImage = this.scaleImage(scaledImage, scaledImageDimension);
            return bufferedImage;
        }
        finally {
            reader.dispose();
        }
    }

    public Thumbnail retrieveOrCreateThumbNail(InputStream originalFileStream, String fileName, File thumbnailFile, int maxWidth, int maxHeight, long thumbnailId) {
        Thumbnail thumbnail;
        try {
            thumbnail = this.getThumbnail(thumbnailFile, fileName, thumbnailId);
        }
        catch (IOException e) {
            log.error("Unable to get thumbnail image for id " + thumbnailId, (Throwable)e);
            return null;
        }
        if (thumbnail == null) {
            try {
                thumbnail = this.createThumbnail(originalFileStream, thumbnailFile, maxWidth, maxHeight, thumbnailId, fileName);
            }
            catch (ThumbnailRenderException | IOException e) {
                log.error("Unable to create thumbnail image for id " + thumbnailId, (Throwable)e);
                return null;
            }
        }
        return thumbnail;
    }

    private Thumbnail createThumbnail(InputStream inputStream, File thumbnailFile, int maxWidth, int maxHeight, long thumbId, String fileName) throws IOException, ThumbnailRenderException {
        BufferedImage thumbnailImage = this.scaleImage(maxWidth, maxHeight, inputStream);
        int height = thumbnailImage.getHeight();
        int width = thumbnailImage.getWidth();
        this.storeImage(thumbnailImage, thumbnailFile);
        return new Thumbnail(height, width, fileName, thumbId, this.mimeType);
    }

    private Thumbnail getThumbnail(File thumbnailFile, String filename, long thumbId) throws IOException {
        if (thumbnailFile.exists()) {
            BufferedImage thumbImage = this.getImage(thumbnailFile, ALWAYS_TRUE);
            return new Thumbnail(((Image)thumbImage).getHeight(null), ((Image)thumbImage).getWidth(null), filename, thumbId, this.mimeType);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BufferedImage getImage(File file, @Nonnull Predicate<ReusableBufferedInputStream> predicate) throws IOException {
        ReusableBufferedInputStream reusableInputStream = new ReusableBufferedInputStream((InputStream)new FileInputStream(file));
        try {
            BufferedImage bufferedImage = this.getImage(reusableInputStream, predicate);
            return bufferedImage;
        }
        finally {
            reusableInputStream.destroy();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BufferedImage getImage(InputStream is) throws IOException {
        ReusableBufferedInputStream reusableInputStream = new ReusableBufferedInputStream(is);
        try {
            BufferedImage bufferedImage = this.getImage(reusableInputStream, ALWAYS_TRUE);
            return bufferedImage;
        }
        finally {
            reusableInputStream.destroy();
        }
    }

    public BufferedImage getImage(ReusableBufferedInputStream is, @Nonnull Predicate<ReusableBufferedInputStream> predicate) throws IOException {
        if (!predicate.test(is)) {
            throw new FailedPredicateException();
        }
        try {
            ImageInputStream stream = ThumbnailUtil.getImageInputStream((InputStream)is);
            return ImageIO.read(stream);
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void setEncodingQuality(float f) {
        if (f > 1.0f || f < 0.0f) {
            throw new IllegalArgumentException("Invalid quality setting '" + f + "', value must be between 0 and 1. ");
        }
        this.encodingQuality = f;
    }

    public ThumbnailDimension determineScaleSize(int maxWidth, int maxHeight, int imageWidth, int imageHeight) {
        return ThumbnailUtil.determineScaledDimensions(maxWidth, maxHeight, imageWidth, imageHeight);
    }

    public boolean isFileSupportedImage(File file) {
        try {
            return this.isFileSupportedImage(new FileInputStream(file));
        }
        catch (FileNotFoundException e) {
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isFileSupportedImage(InputStream inputStream) {
        ImageInfo imageInfo = new ImageInfo();
        try {
            imageInfo.setInput(inputStream);
            imageInfo.check();
            boolean bl = ThumbnailUtil.isFormatSupported(imageInfo.getFormatName());
            return bl;
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (Exception e) {
                log.error("Failed to close InputStream for image", (Throwable)e);
            }
        }
    }

    static class Pictures {
        Pictures() {
        }

        public static BufferedImage toBufferedImage(Image image) {
            if (image instanceof BufferedImage) {
                return (BufferedImage)image;
            }
            boolean hasAlpha = Pictures.hasAlpha(image = new ImageIcon(image).getImage());
            int type = hasAlpha ? 2 : 1;
            BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
            Graphics2D g = bimage.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            return bimage;
        }

        public static boolean hasAlpha(Image image) {
            if (image instanceof BufferedImage) {
                return ((BufferedImage)image).getColorModel().hasAlpha();
            }
            PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
            try {
                pg.grabPixels();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            return pg.getColorModel().hasAlpha();
        }
    }
}

