/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.ImageReaderBase
 *  com.twelvemonkeys.imageio.color.ColorProfiles
 *  com.twelvemonkeys.imageio.color.ColorSpaces
 *  com.twelvemonkeys.imageio.color.YCbCrConverter
 *  com.twelvemonkeys.imageio.metadata.CompoundDirectory
 *  com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegment
 *  com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegmentUtil
 *  com.twelvemonkeys.imageio.metadata.tiff.TIFFReader
 *  com.twelvemonkeys.imageio.stream.SubImageInputStream
 *  com.twelvemonkeys.imageio.util.ImageTypeSpecifiers
 *  com.twelvemonkeys.imageio.util.ProgressListenerBase
 *  com.twelvemonkeys.lang.Validate
 *  com.twelvemonkeys.xml.XMLSerializer
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.ImageReaderBase;
import com.twelvemonkeys.imageio.color.ColorProfiles;
import com.twelvemonkeys.imageio.color.ColorSpaces;
import com.twelvemonkeys.imageio.color.YCbCrConverter;
import com.twelvemonkeys.imageio.metadata.CompoundDirectory;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegment;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegmentUtil;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFReader;
import com.twelvemonkeys.imageio.plugins.jpeg.AdobeDCT;
import com.twelvemonkeys.imageio.plugins.jpeg.Application;
import com.twelvemonkeys.imageio.plugins.jpeg.EXIF;
import com.twelvemonkeys.imageio.plugins.jpeg.EXIFThumbnail;
import com.twelvemonkeys.imageio.plugins.jpeg.FastCMYKToRGB;
import com.twelvemonkeys.imageio.plugins.jpeg.Frame;
import com.twelvemonkeys.imageio.plugins.jpeg.JFIF;
import com.twelvemonkeys.imageio.plugins.jpeg.JFIFThumbnail;
import com.twelvemonkeys.imageio.plugins.jpeg.JFXX;
import com.twelvemonkeys.imageio.plugins.jpeg.JFXXThumbnail;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGColorSpace;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGImage10Metadata;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGLosslessDecoderWrapper;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGSegmentImageInputStream;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGSegmentWarningListener;
import com.twelvemonkeys.imageio.plugins.jpeg.LuminanceToGray;
import com.twelvemonkeys.imageio.plugins.jpeg.Segment;
import com.twelvemonkeys.imageio.plugins.jpeg.ThumbnailReader;
import com.twelvemonkeys.imageio.stream.SubImageInputStream;
import com.twelvemonkeys.imageio.util.ImageTypeSpecifiers;
import com.twelvemonkeys.imageio.util.ProgressListenerBase;
import com.twelvemonkeys.lang.Validate;
import com.twelvemonkeys.xml.XMLSerializer;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javax.imageio.IIOException;
import javax.imageio.IIOParam;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.Node;

public final class JPEGImageReader
extends ImageReaderBase {
    static final boolean DEBUG = "true".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.imageio.plugins.jpeg.debug"));
    static final boolean FORCE_RASTER_CONVERSION = "force".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.imageio.plugins.jpeg.raster"));
    static final int ALL_APP_MARKERS = -1;
    private final ImageReader delegate;
    private final ProgressDelegator progressDelegator;
    private ImageReader thumbnailReader;
    private List<ThumbnailReader> thumbnails;
    private List<Segment> segments;
    private int currentStreamIndex = 0;
    private final List<Long> streamOffsets = new ArrayList<Long>();

    JPEGImageReader(ImageReaderSpi imageReaderSpi, ImageReader imageReader) {
        super(imageReaderSpi);
        this.delegate = (ImageReader)Validate.notNull((Object)imageReader);
        this.progressDelegator = new ProgressDelegator();
    }

    private void installListeners() {
        this.delegate.addIIOReadProgressListener((IIOReadProgressListener)((Object)this.progressDelegator));
        this.delegate.addIIOReadUpdateListener(this.progressDelegator);
        this.delegate.addIIOReadWarningListener(this.progressDelegator);
    }

    protected void resetMembers() {
        this.delegate.reset();
        this.currentStreamIndex = 0;
        this.streamOffsets.clear();
        this.segments = null;
        this.thumbnails = null;
        if (this.thumbnailReader != null) {
            this.thumbnailReader.reset();
        }
        this.installListeners();
    }

    public void dispose() {
        super.dispose();
        if (this.thumbnailReader != null) {
            this.thumbnailReader.dispose();
            this.thumbnailReader = null;
        }
        this.delegate.dispose();
    }

    public String getFormatName() throws IOException {
        return this.delegate.getFormatName();
    }

    private boolean isLossless() throws IOException {
        this.assertInput();
        return this.getSOF().marker == 65475;
    }

    public int getWidth(int n) throws IOException {
        this.checkBounds(n);
        this.initHeader(n);
        return this.getSOF().samplesPerLine;
    }

    public int getHeight(int n) throws IOException {
        this.checkBounds(n);
        this.initHeader(n);
        return this.getSOF().lines;
    }

    public Iterator<ImageTypeSpecifier> getImageTypes(int n) throws IOException {
        ImageTypeSpecifier imageTypeSpecifier = this.getRawImageType(n);
        ColorModel colorModel = imageTypeSpecifier.getColorModel();
        JPEGColorSpace jPEGColorSpace = JPEGImageReader.getSourceCSType(this.getJFIF(), this.getAdobeDCT(), this.getSOF());
        LinkedHashSet<ImageTypeSpecifier> linkedHashSet = new LinkedHashSet<ImageTypeSpecifier>();
        if (colorModel.getColorSpace().getType() != 6) {
            if (colorModel.hasAlpha()) {
                linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType((int)2));
                linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType((int)6));
                linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType((int)3));
                linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType((int)7));
            }
            linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType((int)5));
            linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType((int)1));
            linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType((int)4));
        }
        linkedHashSet.add(imageTypeSpecifier);
        if (jPEGColorSpace != JPEGColorSpace.RGB && jPEGColorSpace != JPEGColorSpace.RGBA && jPEGColorSpace != JPEGColorSpace.CMYK) {
            if (colorModel.hasAlpha()) {
                linkedHashSet.add(ImageTypeSpecifiers.createGrayscale((int)8, (int)0, (boolean)false));
            }
            linkedHashSet.add(ImageTypeSpecifiers.createFromBufferedImageType((int)10));
        }
        return linkedHashSet.iterator();
    }

    public ImageTypeSpecifier getRawImageType(int n) throws IOException {
        this.checkBounds(n);
        this.initHeader(n);
        JPEGColorSpace jPEGColorSpace = JPEGImageReader.getSourceCSType(this.getJFIF(), this.getAdobeDCT(), this.getSOF());
        ICC_Profile iCC_Profile = this.getEmbeddedICCProfile(false);
        boolean bl = false;
        switch (jPEGColorSpace) {
            case GrayA: {
                bl = true;
            }
            case Gray: {
                int[] nArray;
                ColorSpace colorSpace;
                ColorSpace colorSpace2 = colorSpace = iCC_Profile != null && iCC_Profile.getNumComponents() == 1 ? ColorSpaces.createColorSpace((ICC_Profile)iCC_Profile) : ColorSpaces.getColorSpace((int)1003);
                if (bl) {
                    int[] nArray2 = new int[2];
                    nArray2[0] = 1;
                    nArray = nArray2;
                    nArray2[1] = 0;
                } else {
                    int[] nArray3 = new int[1];
                    nArray = nArray3;
                    nArray3[0] = 0;
                }
                return ImageTypeSpecifiers.createInterleaved((ColorSpace)colorSpace, (int[])nArray, (int)0, (boolean)bl, (boolean)false);
            }
            case YCbCrA: 
            case RGBA: 
            case PhotoYCCA: {
                bl = true;
            }
            case YCbCr: 
            case RGB: 
            case PhotoYCC: {
                int[] nArray;
                ColorSpace colorSpace;
                if (jPEGColorSpace == JPEGColorSpace.PhotoYCC || jPEGColorSpace == JPEGColorSpace.PhotoYCCA) {
                    colorSpace = ColorSpaces.getColorSpace((int)1002);
                } else {
                    ColorSpace colorSpace3 = colorSpace = iCC_Profile != null && iCC_Profile.getNumComponents() == 3 ? ColorSpaces.createColorSpace((ICC_Profile)iCC_Profile) : ColorSpaces.getColorSpace((int)1000);
                }
                if (bl) {
                    int[] nArray4 = new int[4];
                    nArray4[0] = 3;
                    nArray4[1] = 2;
                    nArray4[2] = 1;
                    nArray = nArray4;
                    nArray4[3] = 0;
                } else {
                    int[] nArray5 = new int[3];
                    nArray5[0] = 2;
                    nArray5[1] = 1;
                    nArray = nArray5;
                    nArray5[2] = 0;
                }
                return ImageTypeSpecifiers.createInterleaved((ColorSpace)colorSpace, (int[])nArray, (int)0, (boolean)bl, (boolean)false);
            }
            case YCCK: 
            case CMYK: {
                ColorSpace colorSpace = iCC_Profile != null && iCC_Profile.getNumComponents() == 4 ? ColorSpaces.createColorSpace((ICC_Profile)iCC_Profile) : ColorSpaces.getColorSpace((int)5001);
                return ImageTypeSpecifiers.createInterleaved((ColorSpace)colorSpace, (int[])new int[]{3, 2, 1, 0}, (int)0, (boolean)false, (boolean)false);
            }
        }
        throw new IIOException("Could not determine JPEG source color space");
    }

    public BufferedImage read(int n, ImageReadParam imageReadParam) throws IOException {
        this.checkBounds(n);
        this.initHeader(n);
        Frame frame = this.getSOF();
        ICC_Profile iCC_Profile = this.getEmbeddedICCProfile(false);
        AdobeDCT adobeDCT = this.getAdobeDCT();
        boolean bl = false;
        if (adobeDCT != null && (adobeDCT.transform == 1 && frame.componentsInFrame() != 3 || adobeDCT.transform == 2 && frame.componentsInFrame() != 4)) {
            this.processWarningOccurred(String.format("Invalid Adobe App14 marker. Indicates %s data, but SOF%d has %d color component(s). Ignoring Adobe App14 marker.", adobeDCT.transform == 2 ? "YCCK/CMYK" : "YCC/RGB", frame.marker & 0xF, frame.componentsInFrame()));
            bl = true;
            adobeDCT = null;
        }
        JFIF jFIF = this.getJFIF();
        JPEGColorSpace jPEGColorSpace = JPEGImageReader.getSourceCSType(jFIF, adobeDCT, frame);
        if (frame.marker == 65475) {
            BufferedImage bufferedImage;
            if (DEBUG) {
                System.out.println("Reading using Lossless decoder");
            }
            BufferedImage bufferedImage2 = new JPEGLosslessDecoderWrapper(this).readImage(this.segments, this.imageInput);
            BufferedImage bufferedImage3 = bufferedImage = imageReadParam != null ? imageReadParam.getDestination() : null;
            if (bufferedImage != null) {
                bufferedImage.getRaster().setDataElements(0, 0, bufferedImage2.getRaster());
                return bufferedImage;
            }
            return bufferedImage2;
        }
        if (FORCE_RASTER_CONVERSION || bl || iCC_Profile != null && !ColorProfiles.isCS_sRGB((ICC_Profile)iCC_Profile) || (long)frame.lines * (long)frame.samplesPerLine > Integer.MAX_VALUE || this.delegateCSTypeMismatch(jFIF, adobeDCT, frame, jPEGColorSpace)) {
            if (DEBUG) {
                System.out.println("Reading using raster and extra conversion");
                System.out.println("ICC color profile: " + iCC_Profile);
            }
            return this.readImageAsRasterAndReplaceColorProfile(n, imageReadParam, frame, jPEGColorSpace, iCC_Profile);
        }
        if (DEBUG) {
            System.out.println("Reading using delegate");
        }
        return this.delegate.read(0, imageReadParam);
    }

    private boolean delegateCSTypeMismatch(JFIF jFIF, AdobeDCT adobeDCT, Frame frame, JPEGColorSpace jPEGColorSpace) throws IOException {
        switch (jPEGColorSpace) {
            case GrayA: 
            case YCbCrA: 
            case RGBA: 
            case PhotoYCCA: 
            case PhotoYCC: 
            case YCCK: 
            case CMYK: {
                return true;
            }
        }
        try {
            ImageTypeSpecifier imageTypeSpecifier = this.delegate.getRawImageType(0);
            switch (jPEGColorSpace) {
                case Gray: {
                    return imageTypeSpecifier == null || imageTypeSpecifier.getColorModel().getColorSpace().getType() != 6;
                }
                case YCbCr: {
                    if (imageTypeSpecifier == null) {
                        return false;
                    }
                    if (jFIF != null && (frame.components[0].id != 1 || frame.components[1].id != 2 || frame.components[2].id != 3)) {
                        return true;
                    }
                    if (!(adobeDCT != null || frame.components[0].id == 1 && frame.components[1].id == 2 && frame.components[2].id == 3 || frame.components[0].hSub != 1 && frame.components[0].vSub != 1 && frame.components[1].hSub != 1 && frame.components[1].vSub != 1 && frame.components[2].hSub != 1 && frame.components[2].vSub != 1)) {
                        return true;
                    }
                }
                case RGB: {
                    return imageTypeSpecifier == null || imageTypeSpecifier.getColorModel().getColorSpace().getType() != 5;
                }
            }
            return false;
        }
        catch (ArrayIndexOutOfBoundsException | NegativeArraySizeException | NullPointerException | IIOException exception) {
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BufferedImage readImageAsRasterAndReplaceColorProfile(int n, ImageReadParam imageReadParam, Frame frame, JPEGColorSpace jPEGColorSpace, ICC_Profile iCC_Profile) throws IOException {
        Serializable serializable;
        ICC_ColorSpace iCC_ColorSpace;
        int n2 = this.getWidth(n);
        int n3 = this.getHeight(n);
        Iterator<ImageTypeSpecifier> iterator = this.getImageTypes(n);
        BufferedImage bufferedImage = JPEGImageReader.getDestination((ImageReadParam)imageReadParam, iterator, (int)n2, (int)n3);
        WritableRaster writableRaster = bufferedImage.getRaster();
        RasterOp rasterOp = null;
        ICC_ColorSpace iCC_ColorSpace2 = iCC_ColorSpace = iCC_Profile != null ? ColorSpaces.createColorSpace((ICC_Profile)iCC_Profile) : null;
        if (writableRaster.getNumBands() <= 2 && jPEGColorSpace != JPEGColorSpace.Gray && jPEGColorSpace != JPEGColorSpace.GrayA) {
            rasterOp = new LuminanceToGray();
        } else if (iCC_Profile == null || jPEGColorSpace != JPEGColorSpace.Gray && jPEGColorSpace != JPEGColorSpace.GrayA) {
            if (iCC_ColorSpace != null) {
                if (frame.componentsInFrame() != iCC_ColorSpace.getNumComponents()) {
                    this.processWarningOccurred(String.format("Embedded ICC color profile is incompatible with image data. Profile indicates %d components, but SOF%d has %d color components. Ignoring ICC profile, assuming source color space %s.", new Object[]{iCC_ColorSpace.getNumComponents(), frame.marker & 0xF, frame.componentsInFrame(), jPEGColorSpace}));
                    if (jPEGColorSpace == JPEGColorSpace.CMYK && bufferedImage.getColorModel().getColorSpace().getType() != 9) {
                        rasterOp = new ColorConvertOp(ColorSpaces.getColorSpace((int)5001), bufferedImage.getColorModel().getColorSpace(), null);
                    }
                } else if (iCC_ColorSpace != bufferedImage.getColorModel().getColorSpace()) {
                    if (DEBUG) {
                        System.err.println("Converting from " + iCC_ColorSpace + " to " + (bufferedImage.getColorModel().getColorSpace().isCS_sRGB() ? "sRGB" : bufferedImage.getColorModel().getColorSpace()));
                    }
                    rasterOp = new ColorConvertOp(iCC_ColorSpace, bufferedImage.getColorModel().getColorSpace(), null);
                }
            } else if (jPEGColorSpace == JPEGColorSpace.YCCK || jPEGColorSpace == JPEGColorSpace.CMYK) {
                serializable = ColorSpaces.getColorSpace((int)5001);
                if (serializable instanceof ICC_ColorSpace) {
                    this.processWarningOccurred("No embedded ICC color profile, defaulting to \"generic\" CMYK ICC profile. Colors may look incorrect.");
                    if (serializable != bufferedImage.getColorModel().getColorSpace()) {
                        rasterOp = new ColorConvertOp((ColorSpace)serializable, bufferedImage.getColorModel().getColorSpace(), null);
                    }
                } else {
                    this.processWarningOccurred("No embedded ICC color profile, will convert using inaccurate CMYK to RGB conversion. Colors may look incorrect.");
                    rasterOp = new FastCMYKToRGB();
                }
            }
        }
        if (imageReadParam == null) {
            imageReadParam = this.delegate.getDefaultReadParam();
        }
        serializable = imageReadParam.getSourceRegion();
        Rectangle rectangle = new Rectangle();
        Rectangle rectangle2 = new Rectangle();
        JPEGImageReader.computeRegions((ImageReadParam)imageReadParam, (int)n2, (int)n3, (BufferedImage)bufferedImage, (Rectangle)rectangle, (Rectangle)rectangle2);
        int n4 = imageReadParam.getSubsamplingXOffset();
        int n5 = imageReadParam.getSubsamplingYOffset();
        rectangle.translate(-n4, -n5);
        rectangle.width += n4;
        rectangle.height += n5;
        try {
            imageReadParam.setSourceRegion(rectangle);
            Raster raster = this.delegate.readRaster(0, imageReadParam);
            if (jPEGColorSpace == JPEGColorSpace.YCbCr) {
                JPEGImageReader.convertYCbCr2RGB(raster, 3);
            } else if (jPEGColorSpace == JPEGColorSpace.YCbCrA) {
                JPEGImageReader.convertYCbCr2RGB(raster, 4);
            } else if (jPEGColorSpace == JPEGColorSpace.YCCK) {
                JPEGImageReader.convertYCCK2CMYK(raster);
            } else if (jPEGColorSpace == JPEGColorSpace.CMYK) {
                JPEGImageReader.invertCMYK(raster);
            }
            WritableRaster writableRaster2 = writableRaster.createWritableChild(rectangle2.x, rectangle2.y, raster.getWidth(), raster.getHeight(), 0, 0, imageReadParam.getDestinationBands());
            if (rasterOp != null) {
                rasterOp.filter(raster, writableRaster2);
            } else {
                writableRaster2.setRect(0, 0, raster);
            }
        }
        finally {
            imageReadParam.setSourceRegion((Rectangle)serializable);
        }
        return bufferedImage;
    }

    static JPEGColorSpace getSourceCSType(JFIF jFIF, AdobeDCT adobeDCT, Frame frame) throws IIOException {
        switch (frame.componentsInFrame()) {
            case 1: {
                return JPEGColorSpace.Gray;
            }
            case 2: {
                return JPEGColorSpace.GrayA;
            }
            case 3: {
                if (jFIF != null) {
                    return JPEGColorSpace.YCbCr;
                }
                if (adobeDCT != null) {
                    switch (adobeDCT.transform) {
                        case 0: {
                            return JPEGColorSpace.RGB;
                        }
                    }
                    return JPEGColorSpace.YCbCr;
                }
                int n = frame.components[0].id;
                int n2 = frame.components[1].id;
                int n3 = frame.components[2].id;
                if (n == 1 && n2 == 2 && n3 == 3) {
                    return JPEGColorSpace.YCbCr;
                }
                if (n == 82 && n2 == 71 && n3 == 66) {
                    return JPEGColorSpace.RGB;
                }
                if (n == 89 && n2 == 67 && n3 == 99) {
                    return JPEGColorSpace.PhotoYCC;
                }
                return JPEGColorSpace.YCbCr;
            }
            case 4: {
                if (adobeDCT != null) {
                    switch (adobeDCT.transform) {
                        case 0: {
                            return JPEGColorSpace.CMYK;
                        }
                    }
                    return JPEGColorSpace.YCCK;
                }
                int n = frame.components[0].id;
                int n4 = frame.components[1].id;
                int n5 = frame.components[2].id;
                int n6 = frame.components[3].id;
                if (n == 1 && n4 == 2 && n5 == 3 && n6 == 4) {
                    return JPEGColorSpace.YCbCrA;
                }
                if (n == 82 && n4 == 71 && n5 == 66 && n6 == 65) {
                    return JPEGColorSpace.RGBA;
                }
                if (n == 89 && n4 == 67 && n5 == 99 && n6 == 65) {
                    return JPEGColorSpace.PhotoYCCA;
                }
                return JPEGColorSpace.CMYK;
            }
        }
        throw new IIOException("Cannot determine source color space");
    }

    private ICC_Profile ensureDisplayProfile(ICC_Profile iCC_Profile) throws IOException {
        byte[] byArray;
        if (iCC_Profile != null && iCC_Profile.getProfileClass() != 1 && (byArray = iCC_Profile.getData())[64] == 0) {
            this.processWarningOccurred("ICC profile is Perceptual, ignoring, treating as Display class");
            JPEGImageReader.intToBigEndian(1835955314, byArray, 12);
            return ColorProfiles.createProfile((byte[])byArray);
        }
        return iCC_Profile;
    }

    static int intFromBigEndian(byte[] byArray, int n) {
        return (byArray[n] & 0xFF) << 24 | (byArray[n + 1] & 0xFF) << 16 | (byArray[n + 2] & 0xFF) << 8 | byArray[n + 3] & 0xFF;
    }

    static void intToBigEndian(int n, byte[] byArray, int n2) {
        byArray[n2] = (byte)(n >> 24);
        byArray[n2 + 1] = (byte)(n >> 16);
        byArray[n2 + 2] = (byte)(n >> 8);
        byArray[n2 + 3] = (byte)n;
    }

    public void setInput(Object object, boolean bl, boolean bl2) {
        super.setInput(object, bl, bl2);
        try {
            if (this.imageInput != null) {
                if (!(this.imageInput instanceof SubImageInputStream)) {
                    this.imageInput = new SubImageInputStream(this.imageInput, Long.MAX_VALUE);
                }
                this.streamOffsets.add(this.imageInput.getStreamPosition());
            }
            this.initDelegate(bl, bl2);
        }
        catch (IOException iOException) {
            throw new IllegalStateException(iOException.getMessage(), iOException);
        }
    }

    private void initDelegate(boolean bl, boolean bl2) throws IOException {
        this.delegate.setInput(this.imageInput != null ? new JPEGSegmentImageInputStream(this.imageInput, new JPEGSegmentWarningDelegate()) : null, bl, bl2);
    }

    private void initHeader() throws IOException {
        if (this.segments == null) {
            long l = DEBUG ? System.currentTimeMillis() : 0L;
            List<JPEGSegment> list = this.readSegments();
            ArrayList<Segment> arrayList = new ArrayList<Segment>(list.size());
            for (JPEGSegment jPEGSegment : list) {
                try {
                    DataInputStream dataInputStream = new DataInputStream(jPEGSegment.segmentData());
                    Throwable throwable = null;
                    try {
                        arrayList.add(Segment.read(jPEGSegment.marker(), jPEGSegment.identifier(), jPEGSegment.segmentLength(), dataInputStream));
                    }
                    catch (Throwable throwable2) {
                        throwable = throwable2;
                        throw throwable2;
                    }
                    finally {
                        if (dataInputStream == null) continue;
                        if (throwable != null) {
                            try {
                                dataInputStream.close();
                            }
                            catch (Throwable throwable3) {
                                throwable.addSuppressed(throwable3);
                            }
                            continue;
                        }
                        dataInputStream.close();
                    }
                }
                catch (IOException iOException) {
                    if (jPEGSegment.marker() >= 65504 && 65519 >= jPEGSegment.marker()) {
                        this.processWarningOccurred("Bogus APP" + (jPEGSegment.marker() & 0xF) + "/" + jPEGSegment.identifier() + " segment, ignoring");
                        continue;
                    }
                    throw iOException;
                }
            }
            this.segments = arrayList;
            if (DEBUG) {
                System.out.println("segments: " + arrayList);
                System.out.println("Read metadata in " + (System.currentTimeMillis() - l) + " ms");
            }
        }
    }

    private void initHeader(int n) throws IOException {
        this.assertInput();
        if (n < 0) {
            throw new IndexOutOfBoundsException("imageIndex < 0: " + n);
        }
        if (n == this.currentStreamIndex) {
            this.initHeader();
            return;
        }
        this.gotoImage(n);
        this.segments = null;
        this.thumbnails = null;
        this.initDelegate(this.seekForwardOnly, this.ignoreMetadata);
        this.initHeader();
    }

    private void gotoImage(int n) throws IOException {
        if (n < this.streamOffsets.size()) {
            this.imageInput.seek(this.streamOffsets.get(n));
        } else {
            long l = this.streamOffsets.get(this.streamOffsets.size() - 1);
            this.imageInput.seek(l);
            try {
                for (int i = this.streamOffsets.size() - 1; i < n; ++i) {
                    int n2;
                    long l2 = 0L;
                    if (DEBUG) {
                        l2 = System.currentTimeMillis();
                        System.out.printf("Start seeking for image index %d%n", i + 1);
                    }
                    JPEGSegmentUtil.readSegments((ImageInputStream)this.imageInput, Collections.emptyMap());
                    while ((n2 = this.imageInput.read()) != -1) {
                        if (n2 != 255 || (0xFF00 | this.imageInput.readUnsignedByte()) != 65497) continue;
                        while ((n2 = this.imageInput.read()) != -1) {
                            if (n2 != 255 || (0xFF00 | this.imageInput.readUnsignedByte()) != 65496) continue;
                            long l3 = this.imageInput.getStreamPosition() - 2L;
                            this.imageInput.seek(l3);
                            this.streamOffsets.add(l3);
                            break;
                        }
                        break;
                    }
                    if (!DEBUG) continue;
                    System.out.printf("Seek in %d ms%n", System.currentTimeMillis() - l2);
                }
            }
            catch (EOFException eOFException) {
                IndexOutOfBoundsException indexOutOfBoundsException = new IndexOutOfBoundsException("Image index " + n + " not found in stream");
                indexOutOfBoundsException.initCause(eOFException);
                throw indexOutOfBoundsException;
            }
            if (n >= this.streamOffsets.size()) {
                throw new IndexOutOfBoundsException("Image index " + n + " not found in stream");
            }
        }
        this.currentStreamIndex = n;
    }

    public int getNumImages(boolean bl) throws IOException {
        this.assertInput();
        if (bl) {
            if (this.seekForwardOnly) {
                throw new IllegalStateException("seekForwardOnly and allowSearch are both true");
            }
            int n = 0;
            int n2 = 0;
            while (true) {
                try {
                    this.gotoImage(n++);
                }
                catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    break;
                }
                try {
                    this.segments = null;
                    this.getSOF();
                    ++n2;
                }
                catch (IIOException iIOException) {}
            }
            this.imageInput.seek(this.streamOffsets.get(this.currentStreamIndex));
            return n2;
        }
        return -1;
    }

    private List<JPEGSegment> readSegments() throws IOException {
        this.imageInput.mark();
        try {
            this.imageInput.seek(this.streamOffsets.get(this.currentStreamIndex));
            List list = JPEGSegmentUtil.readSegments((ImageInputStream)this.imageInput, (Map)JPEGSegmentUtil.ALL_SEGMENTS);
            return list;
        }
        catch (IllegalArgumentException | IIOException exception) {
            if (DEBUG) {
                exception.printStackTrace();
            }
        }
        finally {
            this.imageInput.reset();
        }
        return Collections.emptyList();
    }

    List<Application> getAppSegments(int n, String string) throws IOException {
        this.initHeader();
        List<Application> list = Collections.emptyList();
        for (Segment segment : this.segments) {
            if (!(segment instanceof Application) || n != -1 && n != segment.marker || string != null && !string.equals(((Application)segment).identifier)) continue;
            if (list == Collections.EMPTY_LIST) {
                list = new ArrayList<Application>(this.segments.size());
            }
            list.add((Application)segment);
        }
        return list;
    }

    Frame getSOF() throws IOException {
        this.initHeader();
        for (Segment segment : this.segments) {
            if (!(segment instanceof Frame)) continue;
            return (Frame)segment;
        }
        throw new IIOException("No SOF segment in stream");
    }

    AdobeDCT getAdobeDCT() throws IOException {
        List<Application> list = this.getAppSegments(65518, "Adobe");
        return list.isEmpty() ? null : (AdobeDCT)list.get(0);
    }

    JFIF getJFIF() throws IOException {
        List<Application> list = this.getAppSegments(65504, "JFIF");
        return list.isEmpty() ? null : (JFIF)list.get(0);
    }

    JFXX getJFXX() throws IOException {
        List<Application> list = this.getAppSegments(65504, "JFXX");
        return list.isEmpty() ? null : (JFXX)list.get(0);
    }

    private EXIF getExif() throws IOException {
        List<Application> list = this.getAppSegments(65505, "Exif");
        return list.isEmpty() ? null : (EXIF)list.get(0);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private CompoundDirectory parseExif(EXIF eXIF) throws IOException {
        if (eXIF == null) return null;
        if (eXIF.data.length <= eXIF.identifier.length() + 2) {
            this.processWarningOccurred("Exif chunk has no data.");
            return null;
        }
        try (ImageInputStream imageInputStream = eXIF.exifData();){
            CompoundDirectory compoundDirectory = (CompoundDirectory)new TIFFReader().read(imageInputStream);
            return compoundDirectory;
        }
        catch (IIOException iIOException) {
            this.processWarningOccurred("Exif chunk is present, but can't be read: " + iIOException.getMessage());
            return null;
        }
    }

    ICC_Profile getEmbeddedICCProfile(boolean bl) throws IOException {
        List<Application> list = this.getAppSegments(65506, "ICC_PROFILE");
        if (list.size() == 1) {
            Application application = list.get(0);
            DataInputStream dataInputStream = new DataInputStream(application.data());
            int n = dataInputStream.readUnsignedByte();
            int n2 = dataInputStream.readUnsignedByte();
            if (n != 1 && n2 != 1) {
                this.processWarningOccurred(String.format("Unexpected number of 'ICC_PROFILE' chunks: %d of %d. Ignoring ICC profile.", n, n2));
                return null;
            }
            return this.readICCProfileSafe(dataInputStream, bl);
        }
        if (!list.isEmpty()) {
            DataInputStream dataInputStream = new DataInputStream(list.get(0).data());
            int n = dataInputStream.readUnsignedByte();
            int n3 = dataInputStream.readUnsignedByte();
            boolean bl2 = false;
            if (n3 != list.size()) {
                this.processWarningOccurred(String.format("Bad 'ICC_PROFILE' chunk count: %d. Ignoring ICC profile.", n3));
                bl2 = true;
                if (!bl) {
                    return null;
                }
            }
            if (!bl2 && n < 1) {
                this.processWarningOccurred(String.format("Invalid 'ICC_PROFILE' chunk index: %d. Ignoring ICC profile.", n));
                if (!bl) {
                    return null;
                }
            }
            int n4 = bl2 ? list.size() : n3;
            InputStream[] inputStreamArray = new InputStream[n4];
            inputStreamArray[bl2 ? 0 : n - 1] = dataInputStream;
            for (int i = 1; i < n4; ++i) {
                Application application = list.get(i);
                dataInputStream = new DataInputStream(application.data());
                n = dataInputStream.readUnsignedByte();
                if (dataInputStream.readUnsignedByte() != n3 && !bl2) {
                    throw new IIOException(String.format("Bad number of 'ICC_PROFILE' chunks: %d of %d.", n, n3));
                }
                int n5 = bl2 ? i : n - 1;
                inputStreamArray[n5] = dataInputStream;
            }
            return this.readICCProfileSafe(new SequenceInputStream(Collections.enumeration(Arrays.asList(inputStreamArray))), bl);
        }
        return null;
    }

    private ICC_Profile readICCProfileSafe(InputStream inputStream, boolean bl) {
        try {
            return bl ? ColorProfiles.readProfileRaw((InputStream)inputStream) : this.ensureDisplayProfile(ColorProfiles.readProfile((InputStream)inputStream));
        }
        catch (IOException | RuntimeException exception) {
            this.processWarningOccurred(String.format("Bad 'ICC_PROFILE' chunk(s): %s. Ignoring ICC profile.", exception.getMessage()));
            return null;
        }
    }

    public boolean canReadRaster() {
        return this.delegate.canReadRaster();
    }

    public Raster readRaster(int n, ImageReadParam imageReadParam) throws IOException {
        this.checkBounds(n);
        this.initHeader(n);
        if (this.isLossless()) {
            return new JPEGLosslessDecoderWrapper(this).readRaster(this.segments, this.imageInput);
        }
        try {
            return this.delegate.readRaster(0, imageReadParam);
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            throw new IIOException("Corrupt JPEG data: Bad segment length", indexOutOfBoundsException);
        }
    }

    public RenderedImage readAsRenderedImage(int n, ImageReadParam imageReadParam) throws IOException {
        return this.read(n, imageReadParam);
    }

    public void abort() {
        super.abort();
        this.delegate.abort();
    }

    public ImageReadParam getDefaultReadParam() {
        return this.delegate.getDefaultReadParam();
    }

    public boolean readerSupportsThumbnails() {
        return true;
    }

    private void readThumbnailMetadata(int n) throws IOException {
        this.checkBounds(n);
        this.initHeader(n);
        if (this.thumbnails == null) {
            Object object;
            this.thumbnails = new ArrayList<ThumbnailReader>();
            try {
                object = JFIFThumbnail.from(this.getJFIF());
                if (object != null) {
                    this.thumbnails.add((ThumbnailReader)object);
                }
            }
            catch (IOException iOException) {
                this.processWarningOccurred(iOException.getMessage());
            }
            try {
                object = JFXXThumbnail.from(this.getJFXX(), this.getThumbnailReader());
                if (object != null) {
                    this.thumbnails.add((ThumbnailReader)object);
                }
            }
            catch (IOException iOException) {
                this.processWarningOccurred(iOException.getMessage());
            }
            try {
                object = this.getExif();
                ThumbnailReader thumbnailReader = EXIFThumbnail.from((EXIF)object, this.parseExif((EXIF)object), this.getThumbnailReader());
                if (thumbnailReader != null) {
                    this.thumbnails.add(thumbnailReader);
                }
            }
            catch (IOException iOException) {
                this.processWarningOccurred(iOException.getMessage());
            }
        }
    }

    ImageReader getThumbnailReader() throws IOException {
        if (this.thumbnailReader == null) {
            this.thumbnailReader = this.delegate.getOriginatingProvider().createReaderInstance();
        }
        return this.thumbnailReader;
    }

    public int getNumThumbnails(int n) throws IOException {
        this.readThumbnailMetadata(n);
        return this.thumbnails.size();
    }

    private void checkThumbnailBounds(int n, int n2) throws IOException {
        Validate.isTrue((n2 >= 0 ? 1 : 0) != 0, (Object)n2, (String)"thumbnailIndex < 0; %d");
        Validate.isTrue((this.getNumThumbnails(n) > n2 ? 1 : 0) != 0, (Object)n2, (String)"thumbnailIndex >= numThumbnails; %d");
    }

    public int getThumbnailWidth(int n, int n2) throws IOException {
        this.checkThumbnailBounds(n, n2);
        return this.thumbnails.get(n2).getWidth();
    }

    public int getThumbnailHeight(int n, int n2) throws IOException {
        this.checkThumbnailBounds(n, n2);
        return this.thumbnails.get(n2).getHeight();
    }

    public BufferedImage readThumbnail(int n, int n2) throws IOException {
        this.checkThumbnailBounds(n, n2);
        this.processThumbnailStarted(n, n2);
        this.processThumbnailProgress(0.0f);
        BufferedImage bufferedImage = this.thumbnails.get(n2).read();
        this.processThumbnailProgress(100.0f);
        this.processThumbnailComplete();
        return bufferedImage;
    }

    public IIOMetadata getImageMetadata(int n) throws IOException {
        this.initHeader(n);
        return new JPEGImage10Metadata(this.segments, this.getSOF(), this.getJFIF(), this.getJFXX(), this.getEmbeddedICCProfile(true), this.getAdobeDCT(), this.parseExif(this.getExif()));
    }

    public IIOMetadata getStreamMetadata() throws IOException {
        return this.delegate.getStreamMetadata();
    }

    protected void processWarningOccurred(String string) {
        super.processWarningOccurred(string);
    }

    private static void invertCMYK(Raster raster) {
        byte[] byArray = ((DataBufferByte)raster.getDataBuffer()).getData();
        int n = byArray.length;
        for (int i = 0; i < n; ++i) {
            byArray[i] = (byte)(255 - byArray[i] & 0xFF);
        }
    }

    private static void convertYCbCr2RGB(Raster raster, int n) {
        int n2 = raster.getHeight();
        int n3 = raster.getWidth();
        byte[] byArray = ((DataBufferByte)raster.getDataBuffer()).getData();
        for (int i = 0; i < n2; ++i) {
            for (int j = 0; j < n3; ++j) {
                YCbCrConverter.convertJPEGYCbCr2RGB((byte[])byArray, (byte[])byArray, (int)((j + i * n3) * n));
            }
        }
    }

    private static void convertYCCK2CMYK(Raster raster) {
        int n = raster.getHeight();
        int n2 = raster.getWidth();
        byte[] byArray = ((DataBufferByte)raster.getDataBuffer()).getData();
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n2; ++j) {
                int n3 = (j + i * n2) * 4;
                YCbCrConverter.convertJPEGYCbCr2RGB((byte[])byArray, (byte[])byArray, (int)n3);
                byArray[n3 + 3] = (byte)(255 - byArray[n3 + 3] & 0xFF);
            }
        }
    }

    protected static void showIt(BufferedImage bufferedImage, String string) {
        ImageReaderBase.showIt((BufferedImage)bufferedImage, (String)string);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] stringArray) throws IOException {
        ImageIO.setUseCache(false);
        int n = 1;
        int n2 = 1;
        int n3 = 0;
        int n4 = 0;
        Rectangle rectangle = null;
        boolean bl = false;
        boolean bl2 = false;
        for (int i = 0; i < stringArray.length; ++i) {
            long l;
            Object object;
            ImageReader imageReader;
            ImageInputStream imageInputStream;
            String[] stringArray2;
            String string;
            block35: {
                Object object2;
                block34: {
                    string = stringArray[i];
                    if (string.charAt(0) == '-') {
                        if (string.equals("-s") || string.equals("--subsample") && stringArray.length > i + 1) {
                            stringArray2 = stringArray[++i].split(",");
                            try {
                                if (stringArray2.length >= 4) {
                                    n = Integer.parseInt(stringArray2[0]);
                                    n2 = Integer.parseInt(stringArray2[1]);
                                    n3 = Integer.parseInt(stringArray2[2]);
                                    n4 = Integer.parseInt(stringArray2[3]);
                                    continue;
                                }
                                n = Integer.parseInt(stringArray2[0]);
                                n2 = stringArray2.length > 1 ? Integer.parseInt(stringArray2[1]) : n;
                            }
                            catch (NumberFormatException numberFormatException) {
                                System.err.println("Bad sub sampling (x,y): '" + stringArray[i] + "'");
                            }
                            continue;
                        }
                        if (string.equals("-r") || string.equals("--roi") && stringArray.length > i + 1) {
                            stringArray2 = stringArray[++i].split(",");
                            try {
                                if (stringArray2.length >= 4) {
                                    rectangle = new Rectangle(Integer.parseInt(stringArray2[0]), Integer.parseInt(stringArray2[1]), Integer.parseInt(stringArray2[2]), Integer.parseInt(stringArray2[3]));
                                    continue;
                                }
                                rectangle = new Rectangle(Integer.parseInt(stringArray2[0]), Integer.parseInt(stringArray2[1]));
                            }
                            catch (IndexOutOfBoundsException | NumberFormatException runtimeException) {
                                System.err.println("Bad source region ([x,y,]w, h): '" + stringArray[i] + "'");
                            }
                            continue;
                        }
                        if (string.equals("-m") || string.equals("--metadata")) {
                            bl = true;
                            continue;
                        }
                        if (string.equals("-t") || string.equals("--thumbnails")) {
                            bl2 = true;
                            continue;
                        }
                        System.err.println("Unknown argument: '" + string + "'");
                        System.exit(-1);
                        continue;
                    }
                    stringArray2 = new File(string);
                    imageInputStream = ImageIO.createImageInputStream(stringArray2);
                    if (imageInputStream == null) {
                        System.err.println("Could not read file: " + stringArray2);
                        continue;
                    }
                    Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInputStream);
                    if (!iterator.hasNext()) {
                        System.err.println("No reader for: " + stringArray2);
                        continue;
                    }
                    imageReader = iterator.next();
                    System.err.println("Reading using: " + imageReader);
                    imageReader.addIIOReadWarningListener(new IIOReadWarningListener(){

                        @Override
                        public void warningOccurred(ImageReader imageReader, String string2) {
                            System.err.println("Warning: " + string + ": " + string2);
                        }
                    });
                    ProgressListenerBase progressListenerBase = new ProgressListenerBase(){
                        private static final int MAX_W = 78;
                        int lastProgress = 0;

                        public void imageStarted(ImageReader imageReader, int n) {
                            System.out.print("[");
                        }

                        public void imageProgress(ImageReader imageReader, float f) {
                            int n = (int)(f * 78.0f) / 100;
                            for (int i = this.lastProgress; i < n; ++i) {
                                System.out.print(".");
                            }
                            System.out.flush();
                            this.lastProgress = n;
                        }

                        public void imageComplete(ImageReader imageReader) {
                            for (int i = this.lastProgress; i < 78; ++i) {
                                System.out.print(".");
                            }
                            System.out.println("]");
                        }
                    };
                    imageReader.addIIOReadProgressListener((IIOReadProgressListener)progressListenerBase);
                    imageReader.setInput(imageInputStream);
                    if (imageReader.getNumImages(true) != 0) break block34;
                    object = imageReader.getStreamMetadata();
                    object2 = (IIOMetadataNode)((IIOMetadata)object).getAsTree(((IIOMetadata)object).getNativeMetadataFormatName());
                    new XMLSerializer((OutputStream)System.out, System.getProperty("file.encoding")).serialize((Node)object2, false);
                    imageInputStream.close();
                    continue;
                }
                object2 = imageReader.getDefaultReadParam();
                if (n > 1 || n2 > 1 || rectangle != null) {
                    ((IIOParam)object2).setSourceSubsampling(n, n2, n3, n4);
                    ((IIOParam)object2).setSourceRegion(rectangle);
                    object = null;
                } else {
                    object = null;
                }
                ((ImageReadParam)object2).setDestination((BufferedImage)object);
                l = DEBUG ? System.currentTimeMillis() : 0L;
                try {
                    object = imageReader.read(0, (ImageReadParam)object2);
                }
                catch (IOException iOException) {
                    iOException.printStackTrace();
                    if (object != null) break block35;
                    imageInputStream.close();
                    continue;
                }
            }
            try {
                if (DEBUG) {
                    System.err.println("Read time: " + (System.currentTimeMillis() - l) + " ms");
                    System.err.println("image: " + object);
                }
                JPEGImageReader.showIt((BufferedImage)object, String.format("Image: %s [%d x %d]", stringArray2.getName(), imageReader.getWidth(0), imageReader.getHeight(0)));
                if (bl) {
                    try {
                        IIOMetadata iIOMetadata = imageReader.getImageMetadata(0);
                        System.out.println("Metadata for File: " + stringArray2.getName());
                        if (iIOMetadata.getNativeMetadataFormatName() != null) {
                            System.out.println("Native:");
                            new XMLSerializer((OutputStream)System.out, System.getProperty("file.encoding")).serialize(iIOMetadata.getAsTree(iIOMetadata.getNativeMetadataFormatName()), false);
                        }
                        if (iIOMetadata.isStandardMetadataFormatSupported()) {
                            System.out.println("Standard:");
                            new XMLSerializer((OutputStream)System.out, System.getProperty("file.encoding")).serialize(iIOMetadata.getAsTree("javax_imageio_1.0"), false);
                        }
                        System.out.println();
                    }
                    catch (IIOException iIOException) {
                        System.err.println("Could not read thumbnails: " + string + ": " + iIOException.getMessage());
                        iIOException.printStackTrace();
                    }
                }
                if (!bl2) continue;
                try {
                    int n5 = imageReader.getNumThumbnails(0);
                    for (int j = 0; j < n5; ++j) {
                        BufferedImage bufferedImage = imageReader.readThumbnail(0, j);
                        JPEGImageReader.showIt(bufferedImage, String.format("Thumbnail: %s [%d x %d]", stringArray2.getName(), bufferedImage.getWidth(), bufferedImage.getHeight()));
                    }
                    continue;
                }
                catch (IIOException iIOException) {
                    System.err.println("Could not read thumbnails: " + string + ": " + iIOException.getMessage());
                    iIOException.printStackTrace();
                    continue;
                }
            }
            catch (Throwable throwable) {
                System.err.println(stringArray2);
                throwable.printStackTrace();
                continue;
            }
            catch (Throwable throwable) {
                throw throwable;
            }
            finally {
                imageInputStream.close();
            }
        }
    }

    private class JPEGSegmentWarningDelegate
    implements JPEGSegmentWarningListener {
        private JPEGSegmentWarningDelegate() {
        }

        @Override
        public void warningOccurred(String string) {
            JPEGImageReader.this.processWarningOccurred(string);
        }
    }

    private class ProgressDelegator
    extends ProgressListenerBase
    implements IIOReadUpdateListener,
    IIOReadWarningListener {
        private ProgressDelegator() {
        }

        public void imageComplete(ImageReader imageReader) {
            JPEGImageReader.this.processImageComplete();
        }

        public void imageProgress(ImageReader imageReader, float f) {
            JPEGImageReader.this.processImageProgress(f);
        }

        public void imageStarted(ImageReader imageReader, int n) {
            JPEGImageReader.this.processImageStarted(JPEGImageReader.this.currentStreamIndex);
        }

        public void readAborted(ImageReader imageReader) {
            JPEGImageReader.this.processReadAborted();
        }

        public void sequenceComplete(ImageReader imageReader) {
            JPEGImageReader.this.processSequenceComplete();
        }

        public void sequenceStarted(ImageReader imageReader, int n) {
            JPEGImageReader.this.processSequenceStarted(n);
        }

        public void thumbnailComplete(ImageReader imageReader) {
            JPEGImageReader.this.processThumbnailComplete();
        }

        public void thumbnailProgress(ImageReader imageReader, float f) {
            JPEGImageReader.this.processThumbnailProgress(f);
        }

        public void thumbnailStarted(ImageReader imageReader, int n, int n2) {
            JPEGImageReader.this.processThumbnailStarted(JPEGImageReader.this.currentStreamIndex, n2);
        }

        @Override
        public void passStarted(ImageReader imageReader, BufferedImage bufferedImage, int n, int n2, int n3, int n4, int n5, int n6, int n7, int[] nArray) {
            JPEGImageReader.this.processPassStarted(bufferedImage, n, n2, n3, n4, n5, n6, n7, nArray);
        }

        @Override
        public void imageUpdate(ImageReader imageReader, BufferedImage bufferedImage, int n, int n2, int n3, int n4, int n5, int n6, int[] nArray) {
            JPEGImageReader.this.processImageUpdate(bufferedImage, n, n2, n3, n4, n5, n6, nArray);
        }

        @Override
        public void passComplete(ImageReader imageReader, BufferedImage bufferedImage) {
            JPEGImageReader.this.processPassComplete(bufferedImage);
        }

        @Override
        public void thumbnailPassStarted(ImageReader imageReader, BufferedImage bufferedImage, int n, int n2, int n3, int n4, int n5, int n6, int n7, int[] nArray) {
            JPEGImageReader.this.processThumbnailPassStarted(bufferedImage, n, n2, n3, n4, n5, n6, n7, nArray);
        }

        @Override
        public void thumbnailUpdate(ImageReader imageReader, BufferedImage bufferedImage, int n, int n2, int n3, int n4, int n5, int n6, int[] nArray) {
            JPEGImageReader.this.processThumbnailUpdate(bufferedImage, n, n2, n3, n4, n5, n6, nArray);
        }

        @Override
        public void thumbnailPassComplete(ImageReader imageReader, BufferedImage bufferedImage) {
            JPEGImageReader.this.processThumbnailPassComplete(bufferedImage);
        }

        @Override
        public void warningOccurred(ImageReader imageReader, String string) {
            JPEGImageReader.this.processWarningOccurred(string);
        }
    }
}

