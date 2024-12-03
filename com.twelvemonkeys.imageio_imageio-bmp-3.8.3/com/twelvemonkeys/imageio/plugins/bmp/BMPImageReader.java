/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.ImageReaderBase
 *  com.twelvemonkeys.imageio.stream.SubImageInputStream
 *  com.twelvemonkeys.imageio.util.IIOUtil
 *  com.twelvemonkeys.imageio.util.ImageTypeSpecifiers
 *  com.twelvemonkeys.imageio.util.ProgressListenerBase
 *  com.twelvemonkeys.io.LittleEndianDataInputStream
 *  com.twelvemonkeys.io.enc.Decoder
 *  com.twelvemonkeys.io.enc.DecoderStream
 *  com.twelvemonkeys.xml.XMLSerializer
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.ImageReaderBase;
import com.twelvemonkeys.imageio.plugins.bmp.BMPImageReaderSpi;
import com.twelvemonkeys.imageio.plugins.bmp.BMPMetadata;
import com.twelvemonkeys.imageio.plugins.bmp.DIBHeader;
import com.twelvemonkeys.imageio.plugins.bmp.RLE4Decoder;
import com.twelvemonkeys.imageio.plugins.bmp.RLE8Decoder;
import com.twelvemonkeys.imageio.stream.SubImageInputStream;
import com.twelvemonkeys.imageio.util.IIOUtil;
import com.twelvemonkeys.imageio.util.ImageTypeSpecifiers;
import com.twelvemonkeys.imageio.util.ProgressListenerBase;
import com.twelvemonkeys.io.LittleEndianDataInputStream;
import com.twelvemonkeys.io.enc.Decoder;
import com.twelvemonkeys.io.enc.DecoderStream;
import com.twelvemonkeys.xml.XMLSerializer;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public final class BMPImageReader
extends ImageReaderBase {
    private long pixelOffset;
    private DIBHeader header;
    private int[] colors;
    private IndexColorModel colorMap;
    private ImageReader jpegReaderDelegate;
    private ImageReader pngReaderDelegate;

    public BMPImageReader() {
        super((ImageReaderSpi)((Object)new BMPImageReaderSpi()));
    }

    BMPImageReader(ImageReaderSpi imageReaderSpi) {
        super(imageReaderSpi);
    }

    protected void resetMembers() {
        this.pixelOffset = 0L;
        this.header = null;
        this.colors = null;
        this.colorMap = null;
        if (this.pngReaderDelegate != null) {
            this.pngReaderDelegate.dispose();
            this.pngReaderDelegate = null;
        }
        if (this.jpegReaderDelegate != null) {
            this.jpegReaderDelegate.dispose();
            this.jpegReaderDelegate = null;
        }
    }

    public int getNumImages(boolean bl) throws IOException {
        this.readHeader();
        return 1;
    }

    private void readHeader() throws IOException {
        this.assertInput();
        if (this.header == null) {
            this.imageInput.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            byte[] byArray = new byte[10];
            this.imageInput.readFully(byArray);
            if (byArray[0] != 66 || byArray[1] != 77) {
                throw new IIOException("Not a BMP");
            }
            this.pixelOffset = this.imageInput.readUnsignedInt();
            this.header = DIBHeader.read(this.imageInput);
            if (this.pixelOffset < (long)(this.header.size + 14)) {
                throw new IIOException("Invalid pixel offset: " + this.pixelOffset);
            }
        }
    }

    private IndexColorModel readColorMap() throws IOException {
        this.readHeader();
        if (this.colors == null) {
            if (this.header.getBitCount() > 8 && this.header.colorsUsed == 0) {
                this.colors = new int[0];
            } else {
                int n;
                int n2;
                int n3 = 14 + this.header.getSize();
                if ((long)n3 != this.imageInput.getStreamPosition()) {
                    this.imageInput.seek(n3);
                }
                if (this.header.getSize() == 12) {
                    this.colors = new int[Math.min(this.header.getColorsUsed(), (int)(this.pixelOffset - 14L - (long)this.header.getSize()) / 3)];
                    for (n2 = 0; n2 < this.colors.length; ++n2) {
                        n = this.imageInput.readUnsignedByte();
                        int n4 = this.imageInput.readUnsignedByte();
                        int n5 = this.imageInput.readUnsignedByte();
                        this.colors[n2] = n5 << 16 | n4 << 8 | n | 0xFF000000;
                    }
                } else {
                    this.colors = new int[Math.min(this.header.getColorsUsed(), (int)(this.pixelOffset - 14L - (long)this.header.getSize()) / 4)];
                    for (n2 = 0; n2 < this.colors.length; ++n2) {
                        this.colors[n2] = this.imageInput.readInt() & 0xFFFFFF | 0xFF000000;
                    }
                }
                if (this.colors.length > 0) {
                    n2 = Math.min(this.colors.length, 1 << this.header.getBitCount());
                    n = this.header.getBitCount() <= 8 ? this.header.getBitCount() : (n2 <= 256 ? 8 : 16);
                    this.colorMap = new IndexColorModel(n, n2, this.colors, 0, false, -1, 0);
                }
            }
        }
        return this.colorMap;
    }

    public int getWidth(int n) throws IOException {
        this.checkBounds(n);
        return this.header.getWidth();
    }

    public int getHeight(int n) throws IOException {
        this.checkBounds(n);
        return this.header.getHeight();
    }

    public Iterator<ImageTypeSpecifier> getImageTypes(int n) throws IOException {
        this.checkBounds(n);
        return Collections.singletonList(this.getRawImageType(n)).iterator();
    }

    public ImageTypeSpecifier getRawImageType(int n) throws IOException {
        this.checkBounds(n);
        if (this.header.getPlanes() != 1) {
            throw new IIOException("Multiple planes not supported");
        }
        try {
            switch (this.header.getBitCount()) {
                case 1: 
                case 2: 
                case 4: 
                case 8: {
                    return ImageTypeSpecifiers.createFromIndexColorModel((IndexColorModel)this.readColorMap());
                }
                case 16: {
                    if (this.header.hasMasks()) {
                        return ImageTypeSpecifiers.createPacked((ColorSpace)ColorSpace.getInstance(1000), (int)this.header.masks[0], (int)this.header.masks[1], (int)this.header.masks[2], (int)this.header.masks[3], (int)1, (boolean)false);
                    }
                    return ImageTypeSpecifiers.createFromBufferedImageType((int)9);
                }
                case 24: {
                    if (this.header.getCompression() != 0) {
                        throw new IIOException("Unsupported compression for RGB: " + this.header.getCompression());
                    }
                    return ImageTypeSpecifiers.createFromBufferedImageType((int)5);
                }
                case 32: {
                    if (this.header.hasMasks()) {
                        return ImageTypeSpecifiers.createPacked((ColorSpace)ColorSpace.getInstance(1000), (int)this.header.masks[0], (int)this.header.masks[1], (int)this.header.masks[2], (int)this.header.masks[3], (int)3, (boolean)false);
                    }
                    return ImageTypeSpecifiers.createFromBufferedImageType((int)1);
                }
                case 0: {
                    if (this.header.getCompression() != 4 && this.header.getCompression() != 5) break;
                    return this.initReaderDelegate(this.header.getCompression()).getRawImageType(0);
                }
            }
            throw new IIOException("Unsupported bit count: " + this.header.getBitCount());
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new IIOException(illegalArgumentException.getMessage(), illegalArgumentException);
        }
    }

    public BufferedImage read(int n, ImageReadParam imageReadParam) throws IOException {
        WritableRaster writableRaster;
        ImageInputStream imageInputStream;
        this.checkBounds(n);
        if (this.header.getCompression() == 4 || this.header.getCompression() == 5) {
            return this.readUsingDelegate(this.header.getCompression(), imageReadParam);
        }
        int n2 = this.getWidth(n);
        int n3 = this.getHeight(n);
        ImageTypeSpecifier imageTypeSpecifier = this.getRawImageType(n);
        BufferedImage bufferedImage = BMPImageReader.getDestination((ImageReadParam)imageReadParam, this.getImageTypes(n), (int)n2, (int)n3);
        ColorModel colorModel = bufferedImage.getColorModel();
        if (colorModel instanceof IndexColorModel && ((IndexColorModel)colorModel).getMapSize() < this.header.getColorsUsed()) {
            this.processWarningOccurred(String.format("Color map contains more colors than raster allows (%d). Ignoring entries above %d.", this.header.getColorsUsed(), ((IndexColorModel)colorModel).getMapSize()));
        }
        int n4 = (this.header.getBitCount() * n2 + 31) / 32 * 4;
        this.imageInput.seek(this.pixelOffset);
        switch (this.header.getCompression()) {
            case 2: {
                if (this.header.getBitCount() != 4) {
                    throw new IIOException(String.format("Unsupported combination of bitCount/compression: %s/%s", this.header.getBitCount(), this.header.getCompression()));
                }
                imageInputStream = new LittleEndianDataInputStream((InputStream)new DecoderStream(IIOUtil.createStreamAdapter((ImageInputStream)this.imageInput), (Decoder)new RLE4Decoder(n2), n4));
                break;
            }
            case 1: {
                if (this.header.getBitCount() != 8) {
                    throw new IIOException(String.format("Unsupported combination of bitCount/compression: %s/%s", this.header.getBitCount(), this.header.getCompression()));
                }
                imageInputStream = new LittleEndianDataInputStream((InputStream)new DecoderStream(IIOUtil.createStreamAdapter((ImageInputStream)this.imageInput), (Decoder)new RLE8Decoder(n2), n4));
                break;
            }
            case 0: 
            case 3: 
            case 6: {
                imageInputStream = this.imageInput;
                break;
            }
            default: {
                throw new IIOException("Unsupported compression: " + this.header.getCompression());
            }
        }
        Rectangle rectangle = new Rectangle();
        Rectangle rectangle2 = new Rectangle();
        BMPImageReader.computeRegions((ImageReadParam)imageReadParam, (int)n2, (int)n3, (BufferedImage)bufferedImage, (Rectangle)rectangle, (Rectangle)rectangle2);
        WritableRaster writableRaster2 = this.clipToRect(bufferedImage.getRaster(), rectangle2, imageReadParam != null ? imageReadParam.getDestinationBands() : null);
        BMPImageReader.checkReadParamBandSettings((ImageReadParam)imageReadParam, (int)imageTypeSpecifier.getNumBands(), (int)writableRaster2.getNumBands());
        switch (this.header.getBitCount()) {
            case 1: 
            case 2: 
            case 4: {
                writableRaster = Raster.createPackedRaster(new DataBufferByte(n4), n2, 1, this.header.getBitCount(), null);
                break;
            }
            case 8: 
            case 24: {
                writableRaster = Raster.createInterleavedRaster(new DataBufferByte(n4), n2, 1, n4, this.header.getBitCount() / 8, this.createOffsets(imageTypeSpecifier.getNumBands()), null);
                break;
            }
            case 16: 
            case 32: {
                writableRaster = imageTypeSpecifier.createBufferedImage(n2, 1).getRaster();
                break;
            }
            default: {
                throw new IIOException("Unsupported pixel depth: " + this.header.getBitCount());
            }
        }
        Raster raster = this.clipRowToRect(writableRaster, rectangle, imageReadParam != null ? imageReadParam.getSourceBands() : null, imageReadParam != null ? imageReadParam.getSourceXSubsampling() : 1);
        int n5 = imageReadParam != null ? imageReadParam.getSourceXSubsampling() : 1;
        int n6 = imageReadParam != null ? imageReadParam.getSourceYSubsampling() : 1;
        this.processImageStarted(n);
        for (int i = 0; i < n3; ++i) {
            int n7 = this.header.getBitCount();
            switch (n7) {
                case 1: 
                case 2: 
                case 4: 
                case 8: 
                case 24: {
                    byte[] byArray = ((DataBufferByte)writableRaster.getDataBuffer()).getData();
                    int n8 = n7 == 24 ? 8 : n7;
                    int n9 = n7 == 24 ? 3 : 1;
                    this.readRowByte(imageInputStream, n3, rectangle, n5, n6, n8, n9, byArray, writableRaster2, raster, i);
                    break;
                }
                case 16: {
                    short[] sArray = ((DataBufferUShort)writableRaster.getDataBuffer()).getData();
                    this.readRowUShort(imageInputStream, n3, rectangle, n5, n6, sArray, writableRaster2, raster, i);
                    break;
                }
                case 32: {
                    int[] nArray = ((DataBufferInt)writableRaster.getDataBuffer()).getData();
                    this.readRowInt(imageInputStream, n3, rectangle, n5, n6, nArray, writableRaster2, raster, i);
                    break;
                }
                default: {
                    throw new AssertionError((Object)("Unsupported pixel depth: " + n7));
                }
            }
            this.processImageProgress(100.0f * (float)i / (float)n3);
            if (n3 - 1 - i < rectangle.y) break;
            if (!this.abortRequested()) continue;
            this.processReadAborted();
            break;
        }
        this.processImageComplete();
        return bufferedImage;
    }

    private BufferedImage readUsingDelegate(int n, ImageReadParam imageReadParam) throws IOException {
        return this.initReaderDelegate(n).read(0, imageReadParam);
    }

    private ImageReader initReaderDelegate(int n) throws IOException {
        ImageReader imageReader = this.getImageReaderDelegate(n);
        imageReader.reset();
        ListenerDelegator listenerDelegator = new ListenerDelegator();
        imageReader.addIIOReadWarningListener(listenerDelegator);
        imageReader.addIIOReadProgressListener((IIOReadProgressListener)((Object)listenerDelegator));
        imageReader.addIIOReadUpdateListener(listenerDelegator);
        this.imageInput.seek(this.pixelOffset);
        imageReader.setInput(new SubImageInputStream(this.imageInput, (long)this.header.getImageSize()));
        return imageReader;
    }

    private ImageReader getImageReaderDelegate(int n) throws IIOException {
        String string;
        switch (n) {
            case 4: {
                if (this.jpegReaderDelegate != null) {
                    return this.jpegReaderDelegate;
                }
                string = "JPEG";
                break;
            }
            case 5: {
                if (this.pngReaderDelegate != null) {
                    return this.pngReaderDelegate;
                }
                string = "PNG";
                break;
            }
            default: {
                throw new AssertionError((Object)("Unsupported BMP compression: " + n));
            }
        }
        Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(string);
        if (!iterator.hasNext()) {
            throw new IIOException(String.format("Delegate ImageReader for %s format not found", string));
        }
        ImageReader imageReader = iterator.next();
        switch (n) {
            case 4: {
                this.jpegReaderDelegate = imageReader;
                break;
            }
            case 5: {
                this.pngReaderDelegate = imageReader;
            }
        }
        return imageReader;
    }

    private int[] createOffsets(int n) {
        int[] nArray = new int[n];
        for (int i = 0; i < n; ++i) {
            nArray[i] = n - i - 1;
        }
        return nArray;
    }

    private void readRowByte(DataInput dataInput, int n, Rectangle rectangle, int n2, int n3, int n4, int n5, byte[] byArray, WritableRaster writableRaster, Raster raster, int n6) throws IOException {
        int n7 = !this.header.topDown ? n - 1 - n6 : n6;
        int n8 = (n7 - rectangle.y) / n3;
        if (n7 % n3 != 0 || n7 < rectangle.y || n7 >= rectangle.y + rectangle.height) {
            dataInput.skipBytes(byArray.length);
            return;
        }
        dataInput.readFully(byArray, 0, byArray.length);
        if (n2 != 1) {
            IIOUtil.subsampleRow((byte[])byArray, (int)rectangle.x, (int)rectangle.width, (byte[])byArray, (int)0, (int)n5, (int)n4, (int)n2);
        }
        writableRaster.setDataElements(0, n8, raster);
    }

    private void readRowUShort(DataInput dataInput, int n, Rectangle rectangle, int n2, int n3, short[] sArray, WritableRaster writableRaster, Raster raster, int n4) throws IOException {
        int n5 = !this.header.topDown ? n - 1 - n4 : n4;
        int n6 = (n5 - rectangle.y) / n3;
        if (n5 % n3 != 0 || n5 < rectangle.y || n5 >= rectangle.y + rectangle.height) {
            dataInput.skipBytes(sArray.length * 2 + sArray.length % 2 * 2);
            return;
        }
        BMPImageReader.readFully(dataInput, sArray);
        if (sArray.length % 2 != 0) {
            dataInput.skipBytes(2);
        }
        if (n2 != 1) {
            for (int i = 0; i < rectangle.width / n2; ++i) {
                sArray[rectangle.x + i] = sArray[rectangle.x + i * n2];
            }
        }
        writableRaster.setDataElements(0, n6, raster);
    }

    private void readRowInt(DataInput dataInput, int n, Rectangle rectangle, int n2, int n3, int[] nArray, WritableRaster writableRaster, Raster raster, int n4) throws IOException {
        int n5 = !this.header.topDown ? n - 1 - n4 : n4;
        int n6 = (n5 - rectangle.y) / n3;
        if (n5 % n3 != 0 || n5 < rectangle.y || n5 >= rectangle.y + rectangle.height) {
            dataInput.skipBytes(nArray.length * 4);
            return;
        }
        BMPImageReader.readFully(dataInput, nArray);
        if (n2 != 1) {
            for (int i = 0; i < rectangle.width / n2; ++i) {
                nArray[rectangle.x + i] = nArray[rectangle.x + i * n2];
            }
        }
        writableRaster.setDataElements(0, n6, raster);
    }

    private static void readFully(DataInput dataInput, short[] sArray) throws IOException {
        if (dataInput instanceof ImageInputStream) {
            ((ImageInputStream)dataInput).readFully(sArray, 0, sArray.length);
        } else {
            for (int i = 0; i < sArray.length; ++i) {
                sArray[i] = dataInput.readShort();
            }
        }
    }

    private static void readFully(DataInput dataInput, int[] nArray) throws IOException {
        if (dataInput instanceof ImageInputStream) {
            ((ImageInputStream)dataInput).readFully(nArray, 0, nArray.length);
        } else {
            for (int i = 0; i < nArray.length; ++i) {
                nArray[i] = dataInput.readInt();
            }
        }
    }

    private Raster clipRowToRect(Raster raster, Rectangle rectangle, int[] nArray, int n) {
        if (rectangle.contains(raster.getMinX(), 0, raster.getWidth(), 1) && n == 1 && nArray == null) {
            return raster;
        }
        return raster.createChild(rectangle.x / n, 0, rectangle.width / n, 1, 0, 0, nArray);
    }

    private WritableRaster clipToRect(WritableRaster writableRaster, Rectangle rectangle, int[] nArray) {
        if (rectangle.contains(writableRaster.getMinX(), writableRaster.getMinY(), writableRaster.getWidth(), writableRaster.getHeight()) && nArray == null) {
            return writableRaster;
        }
        return writableRaster.createWritableChild(rectangle.x, rectangle.y, rectangle.width, rectangle.height, 0, 0, nArray);
    }

    public IIOMetadata getImageMetadata(int n) throws IOException {
        this.readHeader();
        switch (this.header.getBitCount()) {
            case 1: 
            case 2: 
            case 4: 
            case 8: {
                this.readColorMap();
                break;
            }
            default: {
                if (this.header.colorsUsed <= 0) break;
                this.readColorMap();
            }
        }
        return new BMPMetadata(this.header, this.colors);
    }

    public static void main(String[] stringArray) {
        BMPImageReaderSpi bMPImageReaderSpi = new BMPImageReaderSpi();
        BMPImageReader bMPImageReader = new BMPImageReader((ImageReaderSpi)((Object)bMPImageReaderSpi));
        for (String string : stringArray) {
            try {
                File file = new File(string);
                ImageInputStream imageInputStream = ImageIO.createImageInputStream(file);
                System.err.println("Can read?: " + bMPImageReaderSpi.canDecodeInput(imageInputStream));
                bMPImageReader.reset();
                bMPImageReader.setInput(imageInputStream);
                ImageReadParam imageReadParam = bMPImageReader.getDefaultReadParam();
                imageReadParam.setDestinationType(bMPImageReader.getImageTypes(0).next());
                System.err.println("reader.header: " + bMPImageReader.header);
                BufferedImage bufferedImage = bMPImageReader.read(0, imageReadParam);
                System.err.println("image: " + bufferedImage);
                BMPImageReader.showIt((BufferedImage)bufferedImage, (String)file.getName());
                IIOMetadata iIOMetadata = bMPImageReader.getImageMetadata(0);
                if (iIOMetadata == null) continue;
                new XMLSerializer((OutputStream)System.out, System.getProperty("file.encoding")).serialize(iIOMetadata.getAsTree("javax_imageio_1.0"), false);
            }
            catch (Throwable throwable) {
                if (stringArray.length > 1) {
                    System.err.println("---");
                    System.err.println("---> " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage() + " for " + string);
                    System.err.println("---");
                    continue;
                }
                BMPImageReader.throwAs(RuntimeException.class, throwable);
            }
        }
    }

    static <T extends Throwable> void throwAs(Class<T> clazz, Throwable throwable) throws T {
        throw throwable;
    }

    private class ListenerDelegator
    extends ProgressListenerBase
    implements IIOReadUpdateListener,
    IIOReadWarningListener {
        private ListenerDelegator() {
        }

        public void imageComplete(ImageReader imageReader) {
            BMPImageReader.this.processImageComplete();
        }

        public void imageProgress(ImageReader imageReader, float f) {
            BMPImageReader.this.processImageProgress(f);
        }

        public void imageStarted(ImageReader imageReader, int n) {
            BMPImageReader.this.processImageStarted(n);
        }

        public void readAborted(ImageReader imageReader) {
            BMPImageReader.this.processReadAborted();
        }

        public void sequenceComplete(ImageReader imageReader) {
            BMPImageReader.this.processSequenceComplete();
        }

        public void sequenceStarted(ImageReader imageReader, int n) {
            BMPImageReader.this.processSequenceStarted(n);
        }

        public void thumbnailComplete(ImageReader imageReader) {
            BMPImageReader.this.processThumbnailComplete();
        }

        public void thumbnailProgress(ImageReader imageReader, float f) {
            BMPImageReader.this.processThumbnailProgress(f);
        }

        public void thumbnailStarted(ImageReader imageReader, int n, int n2) {
            BMPImageReader.this.processThumbnailStarted(n, n2);
        }

        @Override
        public void passStarted(ImageReader imageReader, BufferedImage bufferedImage, int n, int n2, int n3, int n4, int n5, int n6, int n7, int[] nArray) {
            BMPImageReader.this.processPassStarted(bufferedImage, n, n2, n3, n4, n5, n6, n7, nArray);
        }

        @Override
        public void imageUpdate(ImageReader imageReader, BufferedImage bufferedImage, int n, int n2, int n3, int n4, int n5, int n6, int[] nArray) {
            BMPImageReader.this.processImageUpdate(bufferedImage, n, n2, n3, n4, n5, n6, nArray);
        }

        @Override
        public void passComplete(ImageReader imageReader, BufferedImage bufferedImage) {
            BMPImageReader.this.processPassComplete(bufferedImage);
        }

        @Override
        public void thumbnailPassStarted(ImageReader imageReader, BufferedImage bufferedImage, int n, int n2, int n3, int n4, int n5, int n6, int n7, int[] nArray) {
            BMPImageReader.this.processThumbnailPassStarted(bufferedImage, n, n2, n3, n4, n5, n6, n7, nArray);
        }

        @Override
        public void thumbnailUpdate(ImageReader imageReader, BufferedImage bufferedImage, int n, int n2, int n3, int n4, int n5, int n6, int[] nArray) {
            BMPImageReader.this.processThumbnailUpdate(bufferedImage, n, n2, n3, n4, n5, n6, nArray);
        }

        @Override
        public void thumbnailPassComplete(ImageReader imageReader, BufferedImage bufferedImage) {
            BMPImageReader.this.processThumbnailPassComplete(bufferedImage);
        }

        @Override
        public void warningOccurred(ImageReader imageReader, String string) {
            BMPImageReader.this.processWarningOccurred(string);
        }
    }
}

