/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.ImageWriterBase
 *  com.twelvemonkeys.imageio.util.ProgressListenerBase
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.ImageWriterBase;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGImage10Metadata;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageWriterSpi;
import com.twelvemonkeys.imageio.util.ProgressListenerBase;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.spi.ImageWriterSpi;

public final class JPEGImageWriter
extends ImageWriterBase {
    private final ImageWriter delegate;
    private final ProgressDelegator progressDelegator;

    public JPEGImageWriter(JPEGImageWriterSpi jPEGImageWriterSpi, ImageWriter imageWriter) {
        super((ImageWriterSpi)((Object)jPEGImageWriterSpi));
        this.delegate = imageWriter;
        this.progressDelegator = new ProgressDelegator();
    }

    private void installListeners() {
        this.delegate.addIIOWriteProgressListener((IIOWriteProgressListener)((Object)this.progressDelegator));
        this.delegate.addIIOWriteWarningListener(this.progressDelegator);
    }

    protected void resetMembers() {
        this.delegate.reset();
        this.installListeners();
    }

    public void setOutput(Object object) {
        super.setOutput(object);
        this.delegate.setOutput(object);
    }

    public Object getOutput() {
        return this.delegate.getOutput();
    }

    public Locale[] getAvailableLocales() {
        return this.delegate.getAvailableLocales();
    }

    public void setLocale(Locale locale) {
        this.delegate.setLocale(locale);
    }

    public Locale getLocale() {
        return this.delegate.getLocale();
    }

    public ImageWriteParam getDefaultWriteParam() {
        return this.delegate.getDefaultWriteParam();
    }

    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam imageWriteParam) {
        return this.delegate.getDefaultStreamMetadata(imageWriteParam);
    }

    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam) {
        return this.delegate.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
    }

    public IIOMetadata convertStreamMetadata(IIOMetadata iIOMetadata, ImageWriteParam imageWriteParam) {
        return this.delegate.convertStreamMetadata(iIOMetadata, imageWriteParam);
    }

    public IIOMetadata convertImageMetadata(IIOMetadata iIOMetadata, ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam) {
        return this.delegate.convertImageMetadata(iIOMetadata, imageTypeSpecifier, imageWriteParam);
    }

    public int getNumThumbnailsSupported(ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam, IIOMetadata iIOMetadata, IIOMetadata iIOMetadata2) {
        return this.delegate.getNumThumbnailsSupported(imageTypeSpecifier, imageWriteParam, iIOMetadata, iIOMetadata2);
    }

    public Dimension[] getPreferredThumbnailSizes(ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam, IIOMetadata iIOMetadata, IIOMetadata iIOMetadata2) {
        return this.delegate.getPreferredThumbnailSizes(imageTypeSpecifier, imageWriteParam, iIOMetadata, iIOMetadata2);
    }

    public boolean canWriteRasters() {
        return this.delegate.canWriteRasters();
    }

    public void write(IIOMetadata iIOMetadata, IIOImage iIOImage, ImageWriteParam imageWriteParam) throws IOException {
        if (this.isDestinationCMYK(iIOImage, imageWriteParam)) {
            this.writeCMYK(iIOMetadata, iIOImage, imageWriteParam);
        } else {
            if (iIOImage.getMetadata() instanceof JPEGImage10Metadata) {
                ImageTypeSpecifier imageTypeSpecifier = iIOImage.hasRaster() ? null : ImageTypeSpecifier.createFromRenderedImage(iIOImage.getRenderedImage());
                IIOMetadata iIOMetadata2 = this.delegate.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
                JPEGImage10Metadata jPEGImage10Metadata = (JPEGImage10Metadata)((Object)iIOImage.getMetadata());
                iIOMetadata2.setFromTree(jPEGImage10Metadata.getNativeMetadataFormatName(), jPEGImage10Metadata.getNativeTree());
                iIOImage.setMetadata(iIOMetadata2);
            }
            this.delegate.write(iIOMetadata, iIOImage, imageWriteParam);
        }
    }

    private boolean isDestinationCMYK(IIOImage iIOImage, ImageWriteParam imageWriteParam) {
        return !iIOImage.hasRaster() && iIOImage.getRenderedImage().getColorModel().getColorSpace().getType() == 9 || imageWriteParam != null && imageWriteParam.getDestinationType() != null && imageWriteParam.getDestinationType().getColorModel().getColorSpace().getType() == 9;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeCMYK(IIOMetadata iIOMetadata, IIOImage iIOImage, ImageWriteParam imageWriteParam) throws IOException {
        Object object;
        RenderedImage renderedImage = iIOImage.getRenderedImage();
        boolean bl = imageWriteParam != null && imageWriteParam.getDestinationType() != null;
        ImageTypeSpecifier imageTypeSpecifier = bl ? imageWriteParam.getDestinationType() : ImageTypeSpecifier.createFromRenderedImage(renderedImage);
        ColorSpace colorSpace = imageTypeSpecifier.getColorModel().getColorSpace();
        IIOMetadata iIOMetadata2 = this.delegate.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("javax_imageio_jpeg_image_1.0");
        iIOMetadataNode.appendChild(new IIOMetadataNode("JPEGVariety"));
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("markerSequence");
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("app14Adobe");
        iIOMetadataNode3.setAttribute("transform", "0");
        iIOMetadataNode2.appendChild(iIOMetadataNode3);
        if (colorSpace instanceof ICC_ColorSpace) {
            object = ((ICC_ColorSpace)colorSpace).getProfile();
            byte[] byArray = ((ICC_Profile)object).getData();
            String string = "ICC_PROFILE";
            int n = string.length();
            byte[] byArray2 = string.getBytes(StandardCharsets.US_ASCII);
            int n2 = 65535 - n - 3 - 2;
            int n3 = (int)Math.ceil((float)byArray.length / (float)n2);
            for (int i = 0; i < n3; ++i) {
                IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("unknown");
                iIOMetadataNode4.setAttribute("MarkerTag", String.valueOf(226));
                int n4 = Math.min(n2, byArray.length - i * n2);
                byte[] byArray3 = new byte[n + 3 + n4];
                System.arraycopy(byArray2, 0, byArray3, 0, n);
                byArray3[n] = 0;
                byArray3[n + 1] = (byte)(1 + i);
                byArray3[n + 2] = (byte)n3;
                System.arraycopy(byArray, i * n2, byArray3, n + 3, n4);
                iIOMetadataNode4.setUserObject(byArray3);
                iIOMetadataNode2.appendChild(iIOMetadataNode4);
            }
        }
        iIOMetadata2.mergeTree("javax_imageio_jpeg_image_1.0", iIOMetadataNode);
        object = new InvertedRaster(JPEGImageWriter.getRaster(renderedImage));
        if (bl) {
            imageWriteParam.setDestinationType(null);
        }
        try {
            this.delegate.write(iIOMetadata, new IIOImage((Raster)object, null, iIOMetadata2), imageWriteParam);
        }
        finally {
            if (bl) {
                imageWriteParam.setDestinationType(imageTypeSpecifier);
            }
        }
    }

    private static Raster getRaster(RenderedImage renderedImage) {
        return renderedImage instanceof BufferedImage ? ((BufferedImage)renderedImage).getRaster() : (renderedImage.getNumXTiles() == 1 && renderedImage.getNumYTiles() == 1 ? renderedImage.getTile(0, 0) : renderedImage.getData());
    }

    public boolean canWriteSequence() {
        return this.delegate.canWriteSequence();
    }

    public void prepareWriteSequence(IIOMetadata iIOMetadata) throws IOException {
        this.delegate.prepareWriteSequence(iIOMetadata);
    }

    public void writeToSequence(IIOImage iIOImage, ImageWriteParam imageWriteParam) throws IOException {
        this.delegate.writeToSequence(iIOImage, imageWriteParam);
    }

    public void endWriteSequence() throws IOException {
        this.delegate.endWriteSequence();
    }

    public boolean canReplaceStreamMetadata() throws IOException {
        return this.delegate.canReplaceStreamMetadata();
    }

    public void replaceStreamMetadata(IIOMetadata iIOMetadata) throws IOException {
        this.delegate.replaceStreamMetadata(iIOMetadata);
    }

    public boolean canReplaceImageMetadata(int n) throws IOException {
        return this.delegate.canReplaceImageMetadata(n);
    }

    public void replaceImageMetadata(int n, IIOMetadata iIOMetadata) throws IOException {
        this.delegate.replaceImageMetadata(n, iIOMetadata);
    }

    public boolean canInsertImage(int n) throws IOException {
        return this.delegate.canInsertImage(n);
    }

    public void writeInsert(int n, IIOImage iIOImage, ImageWriteParam imageWriteParam) throws IOException {
        this.delegate.writeInsert(n, iIOImage, imageWriteParam);
    }

    public boolean canRemoveImage(int n) throws IOException {
        return this.delegate.canRemoveImage(n);
    }

    public void removeImage(int n) throws IOException {
        this.delegate.removeImage(n);
    }

    public boolean canWriteEmpty() throws IOException {
        return this.delegate.canWriteEmpty();
    }

    public void prepareWriteEmpty(IIOMetadata iIOMetadata, ImageTypeSpecifier imageTypeSpecifier, int n, int n2, IIOMetadata iIOMetadata2, List<? extends BufferedImage> list, ImageWriteParam imageWriteParam) throws IOException {
        this.delegate.prepareWriteEmpty(iIOMetadata, imageTypeSpecifier, n, n2, iIOMetadata2, list, imageWriteParam);
    }

    public void endWriteEmpty() throws IOException {
        this.delegate.endWriteEmpty();
    }

    public boolean canInsertEmpty(int n) throws IOException {
        return this.delegate.canInsertEmpty(n);
    }

    public void prepareInsertEmpty(int n, ImageTypeSpecifier imageTypeSpecifier, int n2, int n3, IIOMetadata iIOMetadata, List<? extends BufferedImage> list, ImageWriteParam imageWriteParam) throws IOException {
        this.delegate.prepareInsertEmpty(n, imageTypeSpecifier, n2, n3, iIOMetadata, list, imageWriteParam);
    }

    public void endInsertEmpty() throws IOException {
        this.delegate.endInsertEmpty();
    }

    public boolean canReplacePixels(int n) throws IOException {
        return this.delegate.canReplacePixels(n);
    }

    public void prepareReplacePixels(int n, Rectangle rectangle) throws IOException {
        this.delegate.prepareReplacePixels(n, rectangle);
    }

    public void replacePixels(RenderedImage renderedImage, ImageWriteParam imageWriteParam) throws IOException {
        this.delegate.replacePixels(renderedImage, imageWriteParam);
    }

    public void replacePixels(Raster raster, ImageWriteParam imageWriteParam) throws IOException {
        this.delegate.replacePixels(raster, imageWriteParam);
    }

    public void endReplacePixels() throws IOException {
        this.delegate.endReplacePixels();
    }

    public void abort() {
        super.abort();
        this.delegate.abort();
    }

    public void reset() {
        super.reset();
        this.delegate.reset();
    }

    public void dispose() {
        super.dispose();
        this.delegate.dispose();
    }

    private class ProgressDelegator
    extends ProgressListenerBase
    implements IIOWriteWarningListener {
        private ProgressDelegator() {
        }

        public void imageComplete(ImageWriter imageWriter) {
            JPEGImageWriter.this.processImageComplete();
        }

        public void imageProgress(ImageWriter imageWriter, float f) {
            JPEGImageWriter.this.processImageProgress(f);
        }

        public void imageStarted(ImageWriter imageWriter, int n) {
            JPEGImageWriter.this.processImageStarted(n);
        }

        public void thumbnailComplete(ImageWriter imageWriter) {
            JPEGImageWriter.this.processThumbnailComplete();
        }

        public void thumbnailProgress(ImageWriter imageWriter, float f) {
            JPEGImageWriter.this.processThumbnailProgress(f);
        }

        public void thumbnailStarted(ImageWriter imageWriter, int n, int n2) {
            JPEGImageWriter.this.processThumbnailStarted(n, n2);
        }

        public void writeAborted(ImageWriter imageWriter) {
            JPEGImageWriter.this.processWriteAborted();
        }

        @Override
        public void warningOccurred(ImageWriter imageWriter, int n, String string) {
            JPEGImageWriter.this.processWarningOccurred(n, string);
        }
    }

    private static class InvertedRaster
    extends WritableRaster {
        InvertedRaster(final Raster raster) {
            super(raster.getSampleModel(), new DataBuffer(raster.getDataBuffer().getDataType(), raster.getDataBuffer().getSize()){
                private final DataBuffer delegate;
                {
                    super(n, n2);
                    this.delegate = raster.getDataBuffer();
                }

                @Override
                public int getElem(int n, int n2) {
                    return 255 - this.delegate.getElem(n, n2);
                }

                @Override
                public void setElem(int n, int n2, int n3) {
                    throw new UnsupportedOperationException("setElem");
                }
            }, new Point());
        }
    }
}

