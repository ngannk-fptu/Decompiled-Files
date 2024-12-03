/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.stream.SubImageOutputStream
 *  com.twelvemonkeys.imageio.util.ProgressListenerBase
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.DIBImageWriter;
import com.twelvemonkeys.imageio.plugins.bmp.DirectoryEntry;
import com.twelvemonkeys.imageio.plugins.bmp.ICOImageWriteParam;
import com.twelvemonkeys.imageio.stream.SubImageOutputStream;
import com.twelvemonkeys.imageio.util.ProgressListenerBase;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

public final class ICOImageWriter
extends DIBImageWriter {
    private static final int ENTRY_SIZE = 16;
    private static final int ICO_MAX_DIMENSION = 256;
    private static final int INITIAL_ENTRY_COUNT = 8;
    private int sequenceIndex = -1;
    private ImageWriter pngDelegate;

    protected ICOImageWriter(ImageWriterSpi imageWriterSpi) {
        super(imageWriterSpi);
    }

    protected void resetMembers() {
        this.sequenceIndex = -1;
        if (this.pngDelegate != null) {
            this.pngDelegate.dispose();
            this.pngDelegate = null;
        }
    }

    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam) {
        return null;
    }

    public IIOMetadata convertImageMetadata(IIOMetadata iIOMetadata, ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam) {
        return null;
    }

    public void write(IIOMetadata iIOMetadata, IIOImage iIOImage, ImageWriteParam imageWriteParam) throws IOException {
        this.prepareWriteSequence(iIOMetadata);
        this.writeToSequence(iIOImage, imageWriteParam);
        this.endWriteSequence();
    }

    public boolean canWriteSequence() {
        return true;
    }

    public void prepareWriteSequence(IIOMetadata iIOMetadata) throws IOException {
        this.assertOutput();
        if (this.sequenceIndex >= 0) {
            throw new IllegalStateException("writeSequence already started");
        }
        this.writeICOHeader();
        this.imageOutput.writeShort(0);
        this.sequenceIndex = 0;
        this.imageOutput.write(new byte[128]);
    }

    public void endWriteSequence() throws IOException {
        this.assertOutput();
        if (this.sequenceIndex < 0) {
            throw new IllegalStateException("prepareWriteSequence not called");
        }
        this.sequenceIndex = -1;
    }

    public void writeToSequence(IIOImage iIOImage, ImageWriteParam imageWriteParam) throws IOException {
        Object object;
        this.assertOutput();
        if (this.sequenceIndex < 0) {
            throw new IllegalStateException("prepareWriteSequence not called");
        }
        if (iIOImage.hasRaster()) {
            throw new UnsupportedOperationException("Raster not supported");
        }
        if (this.sequenceIndex >= 8) {
            this.growIfNecessary();
        }
        int n = iIOImage.getRenderedImage().getWidth();
        int n2 = iIOImage.getRenderedImage().getHeight();
        ColorModel colorModel = iIOImage.getRenderedImage().getColorModel();
        if (n > 256 || n2 > 256) {
            throw new IIOException(String.format("ICO maximum width or height (%d) exceeded", 256));
        }
        long l = this.imageOutput.getStreamPosition();
        if (l > Integer.MAX_VALUE) {
            throw new IIOException("ICO file too large");
        }
        boolean bl = imageWriteParam != null && "BI_PNG".equals(imageWriteParam.getCompressionType());
        this.processImageStarted(this.sequenceIndex);
        if (bl) {
            object = this.getPNGDelegate();
            ((ImageWriter)object).setOutput(new SubImageOutputStream(this.imageOutput));
            ((ImageWriter)object).write(null, iIOImage, this.copyParam(imageWriteParam, (ImageWriter)object));
        } else {
            object = iIOImage.getRenderedImage();
            this.writeDIBHeader(40, object.getWidth(), object.getHeight() * 2, false, object.getColorModel().getPixelSize(), 0);
            this.writeUncompressed(false, (BufferedImage)object, object.getWidth(), object.getHeight());
            this.imageOutput.write(new byte[(n * n2 + 31) / 32 * 4]);
        }
        this.processImageComplete();
        long l2 = this.imageOutput.getStreamPosition();
        this.imageOutput.seek(4L);
        this.imageOutput.writeShort(this.sequenceIndex + 1);
        int n3 = 6 + this.sequenceIndex * 16;
        this.imageOutput.seek(n3);
        long l3 = l2 - l;
        this.writeEntry(n, n2, colorModel, (int)l3, (int)l);
        ++this.sequenceIndex;
        this.imageOutput.seek(l2);
    }

    private void writeICOHeader() throws IOException {
        if (this.imageOutput.getStreamPosition() != 0L) {
            throw new IllegalStateException("Stream already written to");
        }
        this.imageOutput.writeShort(0);
        this.imageOutput.writeShort(1);
        this.imageOutput.flushBefore(this.imageOutput.getStreamPosition());
    }

    private void growIfNecessary() {
        throw new IllegalStateException(String.format("Maximum number of icons supported (%d) exceeded", 8));
    }

    public ImageWriteParam getDefaultWriteParam() {
        return new ICOImageWriteParam(this.getLocale());
    }

    private ImageWriteParam copyParam(ImageWriteParam imageWriteParam, ImageWriter imageWriter) {
        if (imageWriteParam == null) {
            return null;
        }
        ImageWriteParam imageWriteParam2 = imageWriter.getDefaultWriteParam();
        imageWriteParam2.setSourceSubsampling(imageWriteParam.getSourceXSubsampling(), imageWriteParam.getSourceYSubsampling(), imageWriteParam.getSubsamplingXOffset(), imageWriteParam.getSubsamplingYOffset());
        imageWriteParam2.setSourceRegion(imageWriteParam.getSourceRegion());
        imageWriteParam2.setSourceBands(imageWriteParam.getSourceBands());
        return imageWriteParam2;
    }

    private ImageWriter getPNGDelegate() {
        if (this.pngDelegate == null) {
            this.pngDelegate = ImageIO.getImageWritersByFormatName("PNG").next();
            this.pngDelegate.setLocale(this.getLocale());
            this.pngDelegate.addIIOWriteProgressListener((IIOWriteProgressListener)new ProgressListenerBase(){

                public void imageProgress(ImageWriter imageWriter, float f) {
                    ICOImageWriter.this.processImageProgress(f);
                }

                public void writeAborted(ImageWriter imageWriter) {
                    ICOImageWriter.this.processWriteAborted();
                }
            });
            this.pngDelegate.addIIOWriteWarningListener(new IIOWriteWarningListener(){

                @Override
                public void warningOccurred(ImageWriter imageWriter, int n, String string) {
                    ICOImageWriter.this.processWarningOccurred(ICOImageWriter.this.sequenceIndex, string);
                }
            });
        }
        return this.pngDelegate;
    }

    private void writeEntry(int n, int n2, ColorModel colorModel, int n3, int n4) throws IOException {
        new DirectoryEntry.ICOEntry(n, n2, colorModel, n3, n4).write(this.imageOutput);
    }

    public static void main(String[] stringArray) throws IOException {
        int n;
        boolean bl = false;
        for (n = 0; stringArray.length > n && stringArray[n].charAt(0) == '-'; ++n) {
            if (!stringArray[n].equals("-p") && !stringArray[n].equals("--png")) continue;
            bl = true;
        }
        if (stringArray.length - n < 2) {
            System.err.println("Usage: command [-p|--png] <output.ico> <input> [<input>...]");
            System.exit(1);
        }
        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(new File(stringArray[n++]));){
            ICOImageWriter iCOImageWriter = new ICOImageWriter(null);
            ((ImageWriter)((Object)iCOImageWriter)).setOutput(imageOutputStream);
            ImageWriteParam imageWriteParam = ((ImageWriter)((Object)iCOImageWriter)).getDefaultWriteParam();
            imageWriteParam.setCompressionMode(2);
            imageWriteParam.setCompressionType(bl ? "BI_PNG" : "BI_RGB");
            ((ImageWriter)((Object)iCOImageWriter)).prepareWriteSequence(null);
            for (int i = n; i < stringArray.length; ++i) {
                File file = new File(stringArray[i]);
                try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(file);){
                    Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInputStream);
                    if (!iterator.hasNext()) {
                        System.err.printf("Can't read %s\n", file.getAbsolutePath());
                        continue;
                    }
                    ImageReader imageReader = iterator.next();
                    imageReader.setInput(imageInputStream);
                    for (int j = 0; j < imageReader.getNumImages(true); ++j) {
                        IIOImage iIOImage = imageReader.readAll(j, null);
                        ((ImageWriter)((Object)iCOImageWriter)).writeToSequence(iIOImage, imageWriteParam);
                    }
                    continue;
                }
            }
            ((ImageWriter)((Object)iCOImageWriter)).endWriteSequence();
            ((ImageWriter)((Object)iCOImageWriter)).dispose();
        }
    }
}

