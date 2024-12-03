/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ReusableBufferedInputStream
 *  com.atlassian.core.util.thumbnail.Thumber
 *  com.atlassian.core.util.thumbnail.Thumbnail
 *  com.google.common.base.Predicate
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.pages.thumbnail.renderer;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.content.render.image.ImageRenderUtils;
import com.atlassian.confluence.impl.pages.thumbnail.renderer.MemoryRendererThumbnailGenerator;
import com.atlassian.confluence.impl.pages.thumbnail.renderer.StreamRendererThumbnailGenerator;
import com.atlassian.confluence.impl.pages.thumbnail.renderer.ThumberThumbnailGenerator;
import com.atlassian.confluence.pages.thumbnail.Dimensions;
import com.atlassian.confluence.pages.thumbnail.ThumbnailRenderException;
import com.atlassian.confluence.util.io.InputStreamConsumer;
import com.atlassian.core.util.ReusableBufferedInputStream;
import com.atlassian.core.util.thumbnail.Thumber;
import com.atlassian.core.util.thumbnail.Thumbnail;
import java.awt.color.CMMException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThumbnailRenderer {
    private static final Logger log = LoggerFactory.getLogger(ThumbnailRenderer.class);
    private static final Integer PERMITS_SIZE = Integer.getInteger("thumbnail.generator.permits.size", Runtime.getRuntime().availableProcessors());
    private final Semaphore semaphore = new Semaphore(PERMITS_SIZE);
    private final Thumber thumber;
    private final Predicate<ImageDimensions> rasterBasedRenderingThreshold;

    @Deprecated
    public ThumbnailRenderer(Thumber thumber, com.google.common.base.Predicate<Dimensions> treshold) {
        this.thumber = thumber;
        this.rasterBasedRenderingThreshold = imageDimensions -> treshold.apply((Object)new Dimensions((ImageDimensions)imageDimensions));
    }

    public ThumbnailRenderer(Thumber thumber, Predicate<ImageDimensions> rasterBasedRenderingThreshold) {
        this.thumber = thumber;
        this.rasterBasedRenderingThreshold = rasterBasedRenderingThreshold;
    }

    public Thumbnail createThumbnail(File inputFile, File outputFile, int maxWidth, int maxHeight) {
        Thumbnail thumbnail;
        FileInputStream fileReader = new FileInputStream(inputFile);
        try {
            thumbnail = this.createThumbnail(fileReader, outputFile, maxWidth, maxHeight);
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((InputStream)fileReader).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                throw new ThumbnailRenderException(e);
            }
        }
        ((InputStream)fileReader).close();
        return thumbnail;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Thumbnail createThumbnail(InputStream inputStream, File outputFile, int maxWidth, int maxHeight) {
        this.checkOutputFileCreation(outputFile);
        ReusableBufferedInputStream reusableInputStream = new ReusableBufferedInputStream(inputStream);
        ImageDimensions originalImageDimensions = ThumbnailRenderer.dimensions((InputStream)reusableInputStream);
        if (originalImageDimensions.getHeight() <= maxHeight && originalImageDimensions.getWidth() <= maxWidth) {
            Thumbnail thumbnail = ThumbnailRenderer.withStreamConsumer((InputStream)reusableInputStream, is -> {
                try (FileOutputStream outputStream = new FileOutputStream(outputFile);){
                    IOUtils.copy((InputStream)is, (OutputStream)outputStream);
                }
                return new Thumbnail(originalImageDimensions.getHeight(), originalImageDimensions.getWidth(), outputFile.getName(), 0L);
            });
            return thumbnail;
        }
        if (!this.rasterBasedRenderingThreshold.test(originalImageDimensions)) {
            log.debug("Image dimensions ({}) exceed the threshold for raster based image manipulation. Using stream based renderer.", (Object)originalImageDimensions);
            try {
                Thumbnail thumbnail = this.generateWithStreamRenderer((InputStream)reusableInputStream, outputFile, maxWidth, maxHeight);
                return thumbnail;
            }
            catch (CMMException cme) {
                throw new ThumbnailRenderException(cme);
            }
        }
        Thumbnail cme = this.generateWithThumber((InputStream)reusableInputStream, outputFile, maxWidth, maxHeight);
        return cme;
        catch (CMMException ce) {
            log.debug("Failed to create thumbnail, delegating to JAI based thumbnail renderer: CMMException ({})", (Object)ce.getLocalizedMessage());
            Thumbnail thumbnail = this.generateWithInMemoryRenderer((InputStream)reusableInputStream, outputFile, maxWidth, maxHeight);
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (IOException ioe) {
                log.warn("Failed to close input stream", (Throwable)ioe);
            }
            try {
                reusableInputStream.destroy();
            }
            catch (IOException e) {
                log.warn("Failed to close input stream", (Throwable)e);
            }
            return thumbnail;
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (IOException ioe) {
                log.warn("Failed to close input stream", (Throwable)ioe);
            }
            try {
                reusableInputStream.destroy();
            }
            catch (IOException e) {
                log.warn("Failed to close input stream", (Throwable)e);
            }
        }
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

    private Thumbnail generateWithThumber(InputStream inputStream, File outputFile, int maxWidth, int maxHeight) {
        return new ThumberThumbnailGenerator(this.thumber).generate(inputStream, outputFile, maxWidth, maxHeight);
    }

    private Thumbnail generateWithInMemoryRenderer(InputStream inputStream, File outputFile, int maxWidth, int maxHeight) {
        return new MemoryRendererThumbnailGenerator(this.thumber).generate(inputStream, outputFile, maxWidth, maxHeight);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Thumbnail generateWithStreamRenderer(InputStream inputStream, File outputFile, int maxWidth, int maxHeight) {
        this.semaphore.acquireUninterruptibly();
        try {
            Thumbnail thumbnail = new StreamRendererThumbnailGenerator(this.thumber).generate(inputStream, outputFile, maxWidth, maxHeight);
            return thumbnail;
        }
        finally {
            this.semaphore.release();
        }
    }

    @Deprecated
    public static Dimensions imageDimensions(File inputFile) {
        try {
            return ThumbnailRenderer.imageDimensions(new FileInputStream(inputFile));
        }
        catch (FileNotFoundException e) {
            return null;
        }
    }

    public static ImageDimensions dimensions(File inputFile) {
        try {
            return ThumbnailRenderer.dimensions(new FileInputStream(inputFile));
        }
        catch (FileNotFoundException e) {
            return null;
        }
    }

    @Deprecated
    public static Dimensions imageDimensions(InputStream inputStream) {
        return ThumbnailRenderer.withStreamConsumer(inputStream, is -> new Dimensions(ImageRenderUtils.dimensionsForImage(is)));
    }

    public static ImageDimensions dimensions(InputStream inputStream) {
        return ThumbnailRenderer.withStreamConsumer(inputStream, ImageRenderUtils::dimensionsForImage);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static <T> T withStreamConsumer(InputStream inputStream, InputStreamConsumer<T> sc) {
        try (InputStream is = inputStream;){
            T t = sc.withInputStream(is);
            return t;
        }
        catch (IOException e) {
            throw new ThumbnailRenderException(e);
        }
    }
}

