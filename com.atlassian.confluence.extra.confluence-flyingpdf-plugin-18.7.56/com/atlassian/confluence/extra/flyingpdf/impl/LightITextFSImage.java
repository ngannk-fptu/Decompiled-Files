/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.transcoder.TranscoderException
 *  org.apache.batik.transcoder.TranscoderInput
 *  org.apache.batik.transcoder.TranscoderOutput
 *  org.apache.batik.transcoder.image.ImageTranscoder
 *  org.apache.batik.transcoder.image.PNGTranscoder
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.extra.flyingpdf.util.ImageFileCacheUtils;
import com.atlassian.confluence.extra.flyingpdf.util.ImageInformation;
import com.atlassian.confluence.extra.flyingpdf.util.ImageInformationURICacheUtil;
import com.atlassian.confluence.extra.flyingpdf.util.ImageTranscoderCacheUtil;
import com.lowagie.text.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.xhtmlrenderer.pdf.ITextFSImage;

public class LightITextFSImage
extends ITextFSImage {
    private static final int DEFAULT_SVG_HEIGHT = 32;
    private static final int DEFAULT_SVG_WIDTH = 32;
    private static final String FILE_PREFIX = "file:";
    private final Supplier<InputStream> imgStreamSupplier;
    private int width;
    private int height;
    private final String uri;
    private final String baseUrl;

    public LightITextFSImage(Supplier<InputStream> imgStreamSupplier, float dotsPerPixel, String baseUrl, String uri) throws IOException {
        super(null);
        this.imgStreamSupplier = imgStreamSupplier;
        this.baseUrl = baseUrl;
        this.uri = uri;
        this.updateMeasurements(dotsPerPixel);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateMeasurements(float dotsPerPixel) throws IOException {
        ImageInformation imageInfo = ImageInformationURICacheUtil.getCacheURI(this.uri);
        if (imageInfo != null && imageInfo.getTempFileName() == null) {
            throw new IOException("Unknown image format");
        }
        if (imageInfo != null) {
            this.width = imageInfo.getWidth();
            this.height = imageInfo.getHeight();
        } else {
            File tempFile = this.createTempFileFromInputStream();
            try (ImageInputStream in = ImageIO.createImageInputStream(new FileInputStream(tempFile));){
                ImageInformation cacheInformation;
                Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
                if (readers.hasNext()) {
                    ImageReader reader = readers.next();
                    try {
                        reader.setInput(in);
                        this.width = (int)(dotsPerPixel * (float)reader.getWidth(0));
                        this.height = (int)(dotsPerPixel * (float)reader.getHeight(0));
                        cacheInformation = new ImageInformation(this.height, this.width, tempFile.getAbsolutePath(), false);
                    }
                    finally {
                        reader.dispose();
                    }
                }
                try (FileInputStream imgSvgStream = new FileInputStream(tempFile);){
                    PNGTranscoder transcoder = new PNGTranscoder();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    transcoder.transcode(new TranscoderInput((Reader)new InputStreamReader((InputStream)imgSvgStream, "UTF-8")), new TranscoderOutput((OutputStream)out));
                    this.width = (int)(dotsPerPixel * 32.0f);
                    this.height = (int)(dotsPerPixel * 32.0f);
                    cacheInformation = new ImageInformation(this.height, this.width, tempFile.getAbsolutePath(), true);
                }
                catch (TranscoderException te) {
                    ImageInformation cacheInformation2 = new ImageInformation(0, 0, null, false);
                    ImageInformationURICacheUtil.setCacheURI(this.uri, cacheInformation2);
                    throw new IOException("Unknown image format", te);
                }
                catch (MalformedURLException te) {
                    ImageInformation cacheInformation3 = new ImageInformation(0, 0, null, false);
                    ImageInformationURICacheUtil.setCacheURI(this.uri, cacheInformation3);
                    throw new IOException("Malformed url " + this.uri, te);
                }
                ImageInformationURICacheUtil.setCacheURI(this.uri, cacheInformation);
            }
        }
    }

    private File createTempFileFromInputStream() throws IOException {
        InputStream imgStream = this.imgStreamSupplier.get();
        try {
            File file = ImageFileCacheUtils.createTempFile(imgStream);
            return file;
        }
        finally {
            IOUtils.closeQuietly((InputStream)imgStream);
        }
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void scale(int width, int height) {
        int targetWidth = width;
        int targetHeight = height;
        if (targetWidth == -1) {
            targetWidth = (int)((double)this.getWidth() * ((double)targetHeight / (double)this.getHeight()));
        }
        if (targetHeight == -1) {
            targetHeight = (int)((double)this.getHeight() * ((double)targetWidth / (double)this.getWidth()));
        }
        this.width = targetWidth != 0 ? targetWidth : this.getWidth();
        this.height = targetHeight != 0 ? targetHeight : this.getHeight();
    }

    /*
     * Enabled aggressive exception aggregation
     */
    @Override
    public Image getImage() {
        Object out;
        String imageTempFile;
        block29: {
            imageTempFile = ImageTranscoderCacheUtil.getCacheImage(this.uri);
            if (imageTempFile == null) {
                ImageInformation imageInformation = ImageInformationURICacheUtil.getCacheURI(this.uri);
                if (imageInformation.isSVGImage()) {
                    try (FileInputStream imgSvgStream = new FileInputStream(imageInformation.getTempFileName());){
                        PNGTranscoder transcoder = new PNGTranscoder();
                        transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, (Object)Float.valueOf(this.width));
                        transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (Object)Float.valueOf(this.height));
                        out = new ByteArrayOutputStream();
                        transcoder.transcode(new TranscoderInput((Reader)new InputStreamReader((InputStream)imgSvgStream, "UTF-8")), new TranscoderOutput((OutputStream)out));
                        File transcoderTempFile = ImageFileCacheUtils.createTempFile(((ByteArrayOutputStream)out).toByteArray());
                        ImageTranscoderCacheUtil.setCacheImage(this.uri, transcoderTempFile.getAbsolutePath());
                        imageTempFile = transcoderTempFile.getAbsolutePath();
                        break block29;
                    }
                    catch (Exception tx) {
                        throw new RuntimeException("Failed to read image", tx);
                    }
                }
                imageTempFile = imageInformation.getTempFileName();
            }
        }
        try {
            InputStream imageInputStream;
            if (imageTempFile != null) {
                imageInputStream = new FileInputStream(imageTempFile);
            } else if (!this.isExternalResource()) {
                imageInputStream = this.imgStreamSupplier.get();
            } else {
                throw new RuntimeException("Failed to read image");
            }
            try {
                BufferedImage jImage = ImageIO.read(imageInputStream);
                Image image = Image.getInstance(jImage, null);
                image.scaleAbsolute(this.width, this.height);
                out = image;
                return out;
            }
            catch (Exception e) {
                Image image;
                block30: {
                    InputStream imgSvgStream = new URL(this.uri).openStream();
                    try {
                        PNGTranscoder transcoder = new PNGTranscoder();
                        transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, (Object)Float.valueOf(this.width));
                        transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (Object)Float.valueOf(this.height));
                        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                        transcoder.transcode(new TranscoderInput((Reader)new InputStreamReader(imgSvgStream, "UTF-8")), new TranscoderOutput((OutputStream)out2));
                        Image svgImage = Image.getInstance(out2.toByteArray());
                        svgImage.scaleAbsolute(this.width, this.height);
                        image = svgImage;
                        if (imgSvgStream == null) break block30;
                    }
                    catch (Throwable throwable) {
                        try {
                            if (imgSvgStream != null) {
                                try {
                                    imgSvgStream.close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        catch (Exception tx) {
                            throw new RuntimeException("Failed to read image", tx);
                        }
                    }
                    imgSvgStream.close();
                }
                return image;
            }
            finally {
                IOUtils.closeQuietly((InputStream)imageInputStream);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to read image", e);
        }
    }

    private boolean isExternalResource() {
        return this.uri != null && !this.uri.startsWith(FILE_PREFIX) && !this.uri.startsWith(this.baseUrl) && !this.uri.startsWith("/");
    }
}

