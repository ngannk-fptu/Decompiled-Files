/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.ImageWriterBase
 *  com.twelvemonkeys.imageio.stream.SubImageOutputStream
 *  com.twelvemonkeys.imageio.util.ProgressListenerBase
 */
package com.twelvemonkeys.imageio.plugins.icns;

import com.twelvemonkeys.imageio.ImageWriterBase;
import com.twelvemonkeys.imageio.plugins.icns.IconResource;
import com.twelvemonkeys.imageio.stream.SubImageOutputStream;
import com.twelvemonkeys.imageio.util.ProgressListenerBase;
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

public final class ICNSImageWriter
extends ImageWriterBase {
    private int sequenceIndex = -1;
    private ImageWriter pngDelegate;

    ICNSImageWriter(ImageWriterSpi imageWriterSpi) {
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
        this.writeICNSHeader();
        this.sequenceIndex = 0;
    }

    public void endWriteSequence() throws IOException {
        this.assertOutput();
        if (this.sequenceIndex < 0) {
            throw new IllegalStateException("prepareWriteSequence not called");
        }
        this.sequenceIndex = -1;
    }

    public void writeToSequence(IIOImage iIOImage, ImageWriteParam imageWriteParam) throws IOException {
        this.assertOutput();
        if (this.sequenceIndex < 0) {
            throw new IllegalStateException("prepareWriteSequence not called");
        }
        if (iIOImage.hasRaster()) {
            throw new UnsupportedOperationException("image has a Raster");
        }
        long l = this.imageOutput.getStreamPosition();
        this.imageOutput.writeInt(IconResource.typeFromImage(iIOImage.getRenderedImage(), "PNG"));
        this.imageOutput.writeInt(0);
        this.processImageStarted(this.sequenceIndex);
        ImageWriter imageWriter = this.getPNGDelegate();
        imageWriter.setOutput(new SubImageOutputStream(this.imageOutput));
        imageWriter.write(null, iIOImage, this.copyParam(imageWriteParam, imageWriter));
        this.processImageComplete();
        long l2 = this.imageOutput.getStreamPosition();
        if (l2 > Integer.MAX_VALUE) {
            throw new IIOException("File too large for ICNS");
        }
        int n = (int)(l2 - l);
        this.imageOutput.seek(4L);
        this.imageOutput.writeInt((int)l2);
        this.imageOutput.seek(l + 4L);
        this.imageOutput.writeInt(n);
        this.imageOutput.seek(l2);
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
                    ICNSImageWriter.this.processImageProgress(f);
                }

                public void writeAborted(ImageWriter imageWriter) {
                    ICNSImageWriter.this.processWriteAborted();
                }
            });
            this.pngDelegate.addIIOWriteWarningListener(new IIOWriteWarningListener(){

                @Override
                public void warningOccurred(ImageWriter imageWriter, int n, String string) {
                    ICNSImageWriter.this.processWarningOccurred(ICNSImageWriter.this.sequenceIndex, string);
                }
            });
        }
        return this.pngDelegate;
    }

    private void writeICNSHeader() throws IOException {
        if (this.imageOutput.getStreamPosition() != 0L) {
            throw new IllegalStateException("Stream already written to");
        }
        this.imageOutput.writeInt(1768124019);
        this.imageOutput.writeInt(8);
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
            ICNSImageWriter iCNSImageWriter = new ICNSImageWriter(null);
            ((ImageWriter)((Object)iCNSImageWriter)).setOutput(imageOutputStream);
            ImageWriteParam imageWriteParam = ((ImageWriter)((Object)iCNSImageWriter)).getDefaultWriteParam();
            ((ImageWriter)((Object)iCNSImageWriter)).prepareWriteSequence(null);
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
                        ((ImageWriter)((Object)iCNSImageWriter)).writeToSequence(iIOImage, imageWriteParam);
                    }
                    continue;
                }
            }
            ((ImageWriter)((Object)iCNSImageWriter)).endWriteSequence();
            ((ImageWriter)((Object)iCNSImageWriter)).dispose();
        }
    }
}

