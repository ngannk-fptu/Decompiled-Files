/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.ImageReaderBase
 *  com.twelvemonkeys.imageio.color.CIELabColorConverter
 *  com.twelvemonkeys.imageio.color.CIELabColorConverter$Illuminant
 *  com.twelvemonkeys.imageio.color.ColorProfiles
 *  com.twelvemonkeys.imageio.color.ColorSpaces
 *  com.twelvemonkeys.imageio.color.YCbCrConverter
 *  com.twelvemonkeys.imageio.metadata.CompoundDirectory
 *  com.twelvemonkeys.imageio.metadata.Directory
 *  com.twelvemonkeys.imageio.metadata.Entry
 *  com.twelvemonkeys.imageio.metadata.iptc.IPTCReader
 *  com.twelvemonkeys.imageio.metadata.psd.PSDReader
 *  com.twelvemonkeys.imageio.metadata.tiff.Half
 *  com.twelvemonkeys.imageio.metadata.tiff.Rational
 *  com.twelvemonkeys.imageio.metadata.tiff.TIFFReader
 *  com.twelvemonkeys.imageio.metadata.tiff.TIFFReader$HexDump
 *  com.twelvemonkeys.imageio.metadata.xmp.XMPReader
 *  com.twelvemonkeys.imageio.stream.ByteArrayImageInputStream
 *  com.twelvemonkeys.imageio.stream.DirectImageInputStream
 *  com.twelvemonkeys.imageio.stream.SubImageInputStream
 *  com.twelvemonkeys.imageio.util.IIOUtil
 *  com.twelvemonkeys.imageio.util.ImageTypeSpecifiers
 *  com.twelvemonkeys.imageio.util.ProgressListenerBase
 *  com.twelvemonkeys.io.FastByteArrayOutputStream
 *  com.twelvemonkeys.io.FileUtil
 *  com.twelvemonkeys.io.enc.Decoder
 *  com.twelvemonkeys.io.enc.DecoderStream
 *  com.twelvemonkeys.io.enc.PackBitsDecoder
 *  com.twelvemonkeys.lang.StringUtil
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.imageio.ImageReaderBase;
import com.twelvemonkeys.imageio.color.CIELabColorConverter;
import com.twelvemonkeys.imageio.color.ColorProfiles;
import com.twelvemonkeys.imageio.color.ColorSpaces;
import com.twelvemonkeys.imageio.color.YCbCrConverter;
import com.twelvemonkeys.imageio.metadata.CompoundDirectory;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.iptc.IPTCReader;
import com.twelvemonkeys.imageio.metadata.psd.PSDReader;
import com.twelvemonkeys.imageio.metadata.tiff.Half;
import com.twelvemonkeys.imageio.metadata.tiff.Rational;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFReader;
import com.twelvemonkeys.imageio.metadata.xmp.XMPReader;
import com.twelvemonkeys.imageio.plugins.tiff.BitPaddingStream;
import com.twelvemonkeys.imageio.plugins.tiff.CCITTFaxDecoderStream;
import com.twelvemonkeys.imageio.plugins.tiff.ExtraSamplesColorModel;
import com.twelvemonkeys.imageio.plugins.tiff.HorizontalDeDifferencingStream;
import com.twelvemonkeys.imageio.plugins.tiff.LZWDecoder;
import com.twelvemonkeys.imageio.plugins.tiff.ReverseInputStream;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageMetadata;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFStreamMetadata;
import com.twelvemonkeys.imageio.plugins.tiff.YCbCr16UpsamplerStream;
import com.twelvemonkeys.imageio.plugins.tiff.YCbCrPlanarUpsamplerStream;
import com.twelvemonkeys.imageio.plugins.tiff.YCbCrUpsamplerStream;
import com.twelvemonkeys.imageio.stream.ByteArrayImageInputStream;
import com.twelvemonkeys.imageio.stream.DirectImageInputStream;
import com.twelvemonkeys.imageio.stream.SubImageInputStream;
import com.twelvemonkeys.imageio.util.IIOUtil;
import com.twelvemonkeys.imageio.util.ImageTypeSpecifiers;
import com.twelvemonkeys.imageio.util.ProgressListenerBase;
import com.twelvemonkeys.io.FastByteArrayOutputStream;
import com.twelvemonkeys.io.FileUtil;
import com.twelvemonkeys.io.enc.Decoder;
import com.twelvemonkeys.io.enc.DecoderStream;
import com.twelvemonkeys.io.enc.PackBitsDecoder;
import com.twelvemonkeys.lang.StringUtil;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.NodeList;

public final class TIFFImageReader
extends ImageReaderBase {
    static final boolean DEBUG = "true".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.imageio.plugins.tiff.debug"));
    static final double[] CCIR_601_1_COEFFICIENTS = new double[]{0.299, 0.587, 0.114};
    static final double[] REFERENCE_BLACK_WHITE_YCC_DEFAULT = new double[]{0.0, 255.0, 128.0, 255.0, 128.0, 255.0};
    private CompoundDirectory IFDs;
    private Directory currentIFD;
    private int overrideCCITTCompression = -1;
    private static final Set<String> BYTE_TO_UNDEFINED_NODES = new HashSet<String>(Arrays.asList("700", "34377", "37724"));

    TIFFImageReader(ImageReaderSpi imageReaderSpi) {
        super(imageReaderSpi);
    }

    protected void resetMembers() {
        this.IFDs = null;
        this.currentIFD = null;
        this.overrideCCITTCompression = -1;
    }

    private void readMetadata() throws IOException {
        if (this.imageInput == null) {
            throw new IllegalStateException("input not set");
        }
        if (this.IFDs == null) {
            this.IFDs = (CompoundDirectory)new TIFFReader().read(this.imageInput);
            if (DEBUG) {
                Entry entry;
                Object object;
                Object object2;
                System.err.println("Byte order: " + this.imageInput.getByteOrder());
                System.err.println("Number of images: " + this.IFDs.directoryCount());
                for (int i = 0; i < this.IFDs.directoryCount(); ++i) {
                    System.err.printf("IFD %d: %s\n", i, this.IFDs.getDirectory(i));
                }
                Entry entry2 = this.IFDs.getEntryById((Object)700);
                if (entry2 != null) {
                    object2 = (byte[])entry2.getValue();
                    int n = ((byte[])object2).length;
                    for (int i = n - 1; i > 0 && object2[i] == 0; --i) {
                        --n;
                    }
                    object = new XMPReader().read((ImageInputStream)new ByteArrayImageInputStream(object2, 0, n));
                    System.err.println("-----------------------------------------------------------------------------");
                    System.err.println("xmp: " + object);
                }
                if ((object2 = (Object)this.IFDs.getEntryById((Object)33723)) != null) {
                    Object object3 = object2.getValue();
                    if (object3 instanceof short[]) {
                        System.err.println("short[]: " + object3);
                    }
                    if (object3 instanceof long[]) {
                        System.err.println("long[]: " + object3);
                        object = (long[])object3;
                        object3 = new byte[((Entry)object).length * 8];
                        ByteBuffer.wrap((byte[])object3).asLongBuffer().put((long[])object);
                    }
                    if (object3 instanceof float[]) {
                        System.err.println("float[]: " + object3);
                    }
                    if (object3 instanceof double[]) {
                        System.err.println("double[]: " + object3);
                    }
                    object = new IPTCReader().read((ImageInputStream)new ByteArrayImageInputStream((byte[])object3));
                    System.err.println("-----------------------------------------------------------------------------");
                    System.err.println("iptc: " + object);
                }
                if ((entry = this.IFDs.getEntryById((Object)34377)) != null) {
                    object = new PSDReader().read((ImageInputStream)new ByteArrayImageInputStream((byte[])entry.getValue()));
                    System.err.println("-----------------------------------------------------------------------------");
                    System.err.println("psd: " + object);
                }
                if ((object = this.IFDs.getEntryById((Object)37724)) != null) {
                    byte[] byArray = (byte[])object.getValue();
                    String string = "Adobe Photoshop Document Data Block";
                    if (Arrays.equals(string.getBytes(StandardCharsets.US_ASCII), Arrays.copyOf(byArray, string.length()))) {
                        System.err.println("foo: " + string);
                        int n = string.length() + 1;
                        ByteArrayImageInputStream byteArrayImageInputStream = new ByteArrayImageInputStream(byArray, n, byArray.length - n);
                        while (byteArrayImageInputStream.getStreamPosition() < (long)(byArray.length - n)) {
                            int n2 = byteArrayImageInputStream.readInt();
                            if (n2 != 943868237) {
                                System.err.println("Not a PSD resource: " + n2);
                                break;
                            }
                            int n3 = byteArrayImageInputStream.readInt();
                            System.err.println("resourceKey: " + TIFFImageReader.intToStr(n3));
                            long l = byteArrayImageInputStream.readUnsignedInt();
                            System.err.println("resourceLength: " + l);
                            long l2 = (4L - l % 4L) % 4L;
                            long l3 = l + l2;
                            long l4 = byteArrayImageInputStream.getStreamPosition();
                            if (n3 == 1281456498) {
                                int n4 = byteArrayImageInputStream.readShort();
                                System.err.println("layer count: " + n4);
                                for (int i = 0; i < n4; ++i) {
                                    int n5;
                                    int n6;
                                    int n7 = byteArrayImageInputStream.readInt();
                                    int n8 = byteArrayImageInputStream.readInt();
                                    int n9 = byteArrayImageInputStream.readInt();
                                    int n10 = byteArrayImageInputStream.readInt();
                                    System.err.printf("%d, %d, %d, %d\n", n7, n8, n9, n10);
                                    int n11 = byteArrayImageInputStream.readShort();
                                    System.err.println("channels: " + n11);
                                    for (n6 = 0; n6 < n11; ++n6) {
                                        n5 = byteArrayImageInputStream.readShort();
                                        System.err.println("channelId: " + n5);
                                        long l5 = byteArrayImageInputStream.readUnsignedInt();
                                        System.err.println("channelLength: " + l5);
                                    }
                                    System.err.println("8BIM: " + TIFFImageReader.intToStr(byteArrayImageInputStream.readInt()));
                                    n6 = byteArrayImageInputStream.readInt();
                                    System.err.println("blend mode key: " + TIFFImageReader.intToStr(n6));
                                    n5 = byteArrayImageInputStream.readUnsignedByte();
                                    System.err.println("opacity: " + n5);
                                    int n12 = byteArrayImageInputStream.readUnsignedByte();
                                    System.err.println("clipping: " + n12);
                                    byte by = byteArrayImageInputStream.readByte();
                                    System.err.printf("flags: 0x%02x\n", by);
                                    byteArrayImageInputStream.readByte();
                                    long l6 = byteArrayImageInputStream.readUnsignedInt();
                                    long l7 = byteArrayImageInputStream.getStreamPosition();
                                    System.err.println("length: " + l6);
                                    long l8 = byteArrayImageInputStream.readUnsignedInt();
                                    byteArrayImageInputStream.skipBytes(l8);
                                    long l9 = byteArrayImageInputStream.readUnsignedInt();
                                    byteArrayImageInputStream.skipBytes(l9);
                                    String string2 = TIFFImageReader.readPascalString((DataInput)byteArrayImageInputStream);
                                    System.err.println("layerName: " + string2);
                                    int n13 = (string2.length() + 1) % 4;
                                    System.err.println("mod: " + n13);
                                    if (n13 != 0) {
                                        byteArrayImageInputStream.skipBytes(4 - n13);
                                    }
                                    System.err.println("input.getStreamPosition(): " + byteArrayImageInputStream.getStreamPosition());
                                    System.err.println(TIFFReader.HexDump.dump((long)0L, (byte[])byArray, (int)((int)((long)n + byteArrayImageInputStream.getStreamPosition())), (int)64));
                                    byteArrayImageInputStream.seek(l7 + l6);
                                }
                                System.err.println(TIFFReader.HexDump.dump((long)0L, (byte[])byArray, (int)((int)((long)n + byteArrayImageInputStream.getStreamPosition())), (int)64));
                            }
                            byteArrayImageInputStream.seek(l4 + l3);
                            System.out.println("input.getStreamPosition(): " + byteArrayImageInputStream.getStreamPosition());
                        }
                    }
                }
            }
        }
    }

    static String readPascalString(DataInput dataInput) throws IOException {
        int n = dataInput.readUnsignedByte();
        if (n == 0) {
            return "";
        }
        byte[] byArray = new byte[n];
        dataInput.readFully(byArray);
        return StringUtil.decode((byte[])byArray, (int)0, (int)byArray.length, (String)"ASCII");
    }

    static String intToStr(int n) {
        return new String(new byte[]{(byte)((n & 0xFF000000) >>> 24), (byte)((n & 0xFF0000) >> 16), (byte)((n & 0xFF00) >> 8), (byte)(n & 0xFF)});
    }

    private void readIFD(int n) throws IOException {
        this.readMetadata();
        this.checkBounds(n);
        this.currentIFD = this.IFDs.getDirectory(n);
        this.overrideCCITTCompression = -1;
    }

    public int getNumImages(boolean bl) throws IOException {
        this.readMetadata();
        return this.IFDs.directoryCount();
    }

    private Number getValueAsNumberWithDefault(int n, String string, Number number) throws IIOException {
        Entry entry = this.currentIFD.getEntryById((Object)n);
        if (entry == null) {
            if (number != null) {
                return number;
            }
            throw new IIOException("Missing TIFF tag: " + (string != null ? string : Integer.valueOf(n)));
        }
        return (Number)entry.getValue();
    }

    private long getValueAsLongWithDefault(int n, String string, Long l) throws IIOException {
        return this.getValueAsNumberWithDefault(n, string, l).longValue();
    }

    private long getValueAsLongWithDefault(int n, Long l) throws IIOException {
        return this.getValueAsLongWithDefault(n, null, l);
    }

    private int getValueAsIntWithDefault(int n, String string, Integer n2) throws IIOException {
        return this.getValueAsNumberWithDefault(n, string, n2).intValue();
    }

    private int getValueAsIntWithDefault(int n, Integer n2) throws IIOException {
        return this.getValueAsIntWithDefault(n, null, n2);
    }

    private int getValueAsInt(int n, String string) throws IIOException {
        return this.getValueAsIntWithDefault(n, string, null);
    }

    public int getWidth(int n) throws IOException {
        this.readIFD(n);
        return this.getValueAsInt(256, "ImageWidth");
    }

    public int getHeight(int n) throws IOException {
        this.readIFD(n);
        return this.getValueAsInt(257, "ImageHeight");
    }

    public ImageTypeSpecifier getRawImageType(int n) throws IOException {
        this.readIFD(n);
        int n2 = this.getSampleFormat();
        int n3 = this.getValueAsIntWithDefault(284, 1);
        int n4 = this.getPhotometricInterpretationWithFallback();
        int n5 = this.getValueAsIntWithDefault(277, 1);
        int n6 = this.getBitsPerSample();
        int n7 = this.getDataType(n2, n6);
        int n8 = this.getOpaqueSamplesPerPixel(n4);
        long[] lArray = this.getValueAsLongArray(338, "ExtraSamples", false);
        if (lArray == null && n5 > n8) {
            lArray = new long[n5 - n8];
            lArray[0] = 2L;
        }
        boolean bl = lArray != null && (lArray[0] == 1L || lArray[0] == 2L);
        boolean bl2 = bl && lArray[0] == 1L;
        int n9 = n8 + (bl ? 1 : 0);
        ICC_Profile iCC_Profile = this.getICCProfile();
        switch (n4) {
            case 0: {
                if (n9 == 1 && n6 == 1) {
                    if (iCC_Profile != null) {
                        this.processWarningOccurred("Ignoring embedded ICC color profile for Bi-level/Gray TIFF");
                    }
                    byte[] byArray = new byte[]{-1, 0};
                    return ImageTypeSpecifier.createIndexed(byArray, byArray, byArray, null, n6, n7);
                }
            }
            case 1: {
                switch (n9) {
                    case 1: {
                        ColorSpace colorSpace;
                        if (iCC_Profile != null && iCC_Profile.getColorSpaceType() != 6) {
                            this.processWarningOccurred(String.format("Embedded ICC color profile (type %s), is incompatible with image data (GRAY/type 6). Ignoring profile.", iCC_Profile.getColorSpaceType()));
                            iCC_Profile = null;
                        }
                        ColorSpace colorSpace2 = colorSpace = iCC_Profile == null ? ColorSpace.getInstance(1003) : ColorSpaces.createColorSpace((ICC_Profile)iCC_Profile);
                        if (colorSpace == ColorSpace.getInstance(1003) && (n6 == 1 || n6 == 2 || n6 == 4 || n6 == 8 || n6 == 16 || n6 == 32)) {
                            return ImageTypeSpecifiers.createGrayscale((int)n6, (int)n7);
                        }
                        if (n6 == 1 || n6 == 2 || n6 == 4) {
                            return ImageTypeSpecifiers.createPackedGrayscale((ColorSpace)colorSpace, (int)n6, (int)n7);
                        }
                        if (n6 == 8 || n6 == 16 || n6 == 32) {
                            return this.createImageTypeSpecifier(1, colorSpace, n7, n9, n5, false, false);
                        }
                        if (n6 % 2 == 0) {
                            ComponentColorModel componentColorModel = new ComponentColorModel(colorSpace, new int[]{n6}, false, false, 1, n7);
                            return new ImageTypeSpecifier(componentColorModel, ((ColorModel)componentColorModel).createCompatibleSampleModel(1, 1));
                        }
                        throw new IIOException(String.format("Unsupported BitsPerSample for Bi-level/Gray TIFF (expected 1, 2, 4, 8, 16 or 32): %d", n6));
                    }
                    case 2: {
                        ColorSpace colorSpace;
                        if (iCC_Profile != null && iCC_Profile.getColorSpaceType() != 6) {
                            this.processWarningOccurred(String.format("Embedded ICC color profile (type %s), is incompatible with image data (GRAY/type 6). Ignoring profile.", iCC_Profile.getColorSpaceType()));
                            iCC_Profile = null;
                        }
                        ColorSpace colorSpace3 = colorSpace = iCC_Profile == null ? ColorSpace.getInstance(1003) : ColorSpaces.createColorSpace((ICC_Profile)iCC_Profile);
                        if (colorSpace == ColorSpace.getInstance(1003) && (n6 == 8 || n6 == 16 || n6 == 32)) {
                            switch (n3) {
                                case 1: {
                                    return ImageTypeSpecifiers.createGrayscale((int)n6, (int)n7, (boolean)bl2);
                                }
                                case 2: {
                                    return ImageTypeSpecifiers.createBanded((ColorSpace)colorSpace, (int[])new int[]{0, 1}, (int[])new int[]{0, 0}, (int)n7, (boolean)true, (boolean)bl2);
                                }
                            }
                        } else if (n6 == 8 || n6 == 16 || n6 == 32) {
                            return this.createImageTypeSpecifier(n3, colorSpace, n7, n9, n5, true, bl2);
                        }
                        throw new IIOException(String.format("Unsupported BitsPerSample for Gray + Alpha TIFF (expected 8, 16 or 32): %d", n6));
                    }
                }
                throw new IIOException(String.format("Unsupported SamplesPerPixel/BitsPerSample combination for Bi-level/Gray TIFF (expected 1/1, 1/2, 1/4, 1/8, 1/16 or 1/32, or 2/8, 2/16 or 2/32): %d/%d", n5, n6));
            }
            case 2: 
            case 6: {
                if (iCC_Profile != null && iCC_Profile.getColorSpaceType() != 5) {
                    this.processWarningOccurred(String.format("Embedded ICC color profile (type %s), is incompatible with image data (RGB/type 5). Ignoring profile.", iCC_Profile.getColorSpaceType()));
                    iCC_Profile = null;
                }
                ColorSpace colorSpace = iCC_Profile == null ? ColorSpace.getInstance(1000) : ColorSpaces.createColorSpace((ICC_Profile)iCC_Profile);
                switch (n9) {
                    case 3: {
                        if (n6 == 8 || n6 == 16 || n6 == 32) {
                            return this.createImageTypeSpecifier(n3, colorSpace, n7, n9, n5, false, false);
                        }
                        if (n6 == 2 && n3 == 1) {
                            return ImageTypeSpecifiers.createPacked((ColorSpace)colorSpace, (int)48, (int)12, (int)3, (int)0, (int)0, (boolean)false);
                        }
                        if (n6 == 4 && n3 == 1) {
                            return ImageTypeSpecifiers.createPacked((ColorSpace)colorSpace, (int)3840, (int)240, (int)15, (int)0, (int)1, (boolean)false);
                        }
                        if (n6 > 8 && n6 % 2 == 0) {
                            ComponentColorModel componentColorModel = new ComponentColorModel(colorSpace, new int[]{n6, n6, n6}, false, false, 1, n7);
                            SampleModel sampleModel = n3 == 1 ? ((ColorModel)componentColorModel).createCompatibleSampleModel(1, 1) : new BandedSampleModel(n7, 1, 1, 3, new int[]{0, 1, 2}, new int[]{0, 0, 0});
                            return new ImageTypeSpecifier(componentColorModel, sampleModel);
                        }
                    }
                    case 4: {
                        if (n6 == 8 || n6 == 16 || n6 == 32) {
                            return this.createImageTypeSpecifier(n3, colorSpace, n7, n9, n5, true, bl2);
                        }
                        if (n6 == 2 && n3 == 1) {
                            return ImageTypeSpecifiers.createPacked((ColorSpace)colorSpace, (int)192, (int)48, (int)12, (int)3, (int)0, (boolean)bl2);
                        }
                        if (n6 != 4 || n3 != 1) break;
                        return ImageTypeSpecifiers.createPacked((ColorSpace)colorSpace, (int)61440, (int)3840, (int)240, (int)15, (int)1, (boolean)bl2);
                    }
                }
                throw new IIOException(String.format("Unsupported SamplesPerPixel/BitsPerSample combination for RGB TIFF (expected 3 or 4/a multiple of 2): %d/%d", n5, n6));
            }
            case 3: {
                if (n5 != 1 && (n5 != 2 || lArray == null || lArray.length != 1)) {
                    throw new IIOException("Bad SamplesPerPixel value for Palette TIFF (expected 1): " + n5);
                }
                if (n6 <= 0 || n6 > 16) {
                    throw new IIOException("Bad BitsPerSample value for Palette TIFF (expected <= 16): " + n6);
                }
                Entry entry = this.currentIFD.getEntryById((Object)320);
                if (entry == null) {
                    throw new IIOException("Missing ColorMap for Palette TIFF");
                }
                IndexColorModel indexColorModel = this.createIndexColorModel(n6, n7, (int[])entry.getValue());
                if (lArray != null) {
                    return ImageTypeSpecifiers.createDiscreteExtraSamplesIndexedFromIndexColorModel((IndexColorModel)indexColorModel, (int)lArray.length, (boolean)bl);
                }
                return ImageTypeSpecifiers.createFromIndexColorModel((IndexColorModel)indexColorModel);
            }
            case 5: {
                int n10 = this.getValueAsIntWithDefault(332, 1);
                int n11 = this.getValueAsIntWithDefault(334, 4);
                if (n10 != 1 && (iCC_Profile == null || iCC_Profile.getNumComponents() != n11)) {
                    throw new IIOException(String.format("Embedded ICC color profile for Photometric Separated is missing or is incompatible with image data: %s != NumberOfInks (%s).", iCC_Profile != null ? Integer.valueOf(iCC_Profile.getNumComponents()) : "null", n11));
                }
                if (iCC_Profile != null && n10 == 1 && iCC_Profile.getColorSpaceType() != 9) {
                    this.processWarningOccurred(String.format("Embedded ICC color profile (type %s), is incompatible with image data (CMYK/type 9). Ignoring profile.", iCC_Profile.getColorSpaceType()));
                    iCC_Profile = null;
                }
                ColorSpace colorSpace = iCC_Profile == null ? ColorSpaces.getColorSpace((int)5001) : ColorSpaces.createColorSpace((ICC_Profile)iCC_Profile);
                switch (n9) {
                    case 4: 
                    case 5: {
                        if (n6 != 8 && n6 != 16) break;
                        return this.createImageTypeSpecifier(n3, colorSpace, n7, n9, n5, n9 == 5, bl2);
                    }
                }
                throw new IIOException(String.format("Unsupported SamplesPerPixel/BitsPerSample combination for Separated TIFF (expected 4/8, 4/16, 5/8 or 5/16): %d/%s", n5, n6));
            }
            case 8: 
            case 9: 
            case 10: {
                ColorSpace colorSpace = ColorSpace.getInstance(1000);
                switch (n3) {
                    case 1: {
                        return this.createImageTypeSpecifier(1, colorSpace, n7, 3, n5, false, false);
                    }
                }
                throw new IIOException(String.format("Unsupported PlanarConfiguration for Lab color TIFF (expected 1): %d", n3));
            }
            case 4: 
            case 32803: 
            case 32844: 
            case 32845: 
            case 34892: {
                throw new IIOException("Unsupported TIFF PhotometricInterpretation value: " + n4);
            }
        }
        throw new IIOException("Unknown TIFF PhotometricInterpretation value: " + n4);
    }

    private ImageTypeSpecifier createImageTypeSpecifier(int n, ColorSpace colorSpace, int n2, int n3, int n4, boolean bl, boolean bl2) throws IIOException {
        switch (n) {
            case 1: {
                if (n4 > n3) {
                    return new ImageTypeSpecifier(new ExtraSamplesColorModel(colorSpace, bl, bl2, n2, n4 - n3), new PixelInterleavedSampleModel(n2, 1, 1, n4, n4, TIFFImageReader.createOffsets(n4)));
                }
                return ImageTypeSpecifiers.createInterleaved((ColorSpace)colorSpace, (int[])TIFFImageReader.createOffsets(n3), (int)n2, (boolean)bl, (boolean)bl2);
            }
            case 2: {
                return ImageTypeSpecifiers.createBanded((ColorSpace)colorSpace, (int[])TIFFImageReader.createOffsets(n3), (int[])new int[n3], (int)n2, (boolean)bl, (boolean)bl2);
            }
        }
        throw new IIOException(String.format("Unsupported PlanarConfiguration (expected 1 or 2): %d", n));
    }

    private static int[] createOffsets(int n) {
        int[] nArray = new int[n];
        for (int i = 0; i < n; ++i) {
            nArray[i] = i;
        }
        return nArray;
    }

    private int getPhotometricInterpretationWithFallback() throws IIOException {
        int n = this.getValueAsIntWithDefault(262, "PhotometricInterpretation", -1);
        if (n == -1) {
            int n2 = this.getValueAsIntWithDefault(259, 1);
            int n3 = this.getValueAsIntWithDefault(277, 1);
            Entry entry = this.currentIFD.getEntryById((Object)338);
            Entry entry2 = this.currentIFD.getEntryById((Object)320);
            n = TIFFImageReader.guessPhotometricInterpretation(n2, n3, entry, entry2);
            this.processWarningOccurred("Missing PhotometricInterpretation, determining fallback: " + n);
        }
        return n;
    }

    static int guessPhotometricInterpretation(int n, int n2, Entry entry, Entry entry2) {
        int n3;
        int n4 = n3 = entry == null ? 0 : entry.valueCount();
        if (n == 2 || n == 3 || n == 4) {
            return 0;
        }
        if (entry2 != null) {
            return 3;
        }
        if (n2 - n3 == 3) {
            if (n == 7 || n == 6) {
                return 6;
            }
            return 2;
        }
        if (n2 - n3 == 4) {
            return 5;
        }
        return 1;
    }

    private int getOpaqueSamplesPerPixel(int n) throws IIOException {
        switch (n) {
            case 0: 
            case 1: 
            case 3: 
            case 4: {
                return 1;
            }
            case 2: 
            case 6: 
            case 8: 
            case 9: 
            case 10: {
                return 3;
            }
            case 5: {
                return this.getValueAsIntWithDefault(334, 4);
            }
            case 32803: 
            case 32844: 
            case 32845: 
            case 34892: {
                throw new IIOException("Unsupported TIFF PhotometricInterpretation value: " + n);
            }
        }
        throw new IIOException("Unknown TIFF PhotometricInterpretation value: " + n);
    }

    private int getDataType(int n, int n2) throws IIOException {
        switch (n) {
            case 1: 
            case 4: {
                return n2 <= 8 ? 0 : (n2 <= 16 ? 1 : 3);
            }
            case 2: {
                switch (n2) {
                    case 8: {
                        return 0;
                    }
                    case 16: {
                        return 2;
                    }
                    case 32: {
                        return 3;
                    }
                }
                throw new IIOException("Unsupported BitsPerSample for SampleFormat 2/Signed Integer (expected 8/16/32): " + n2);
            }
            case 3: {
                if (n2 == 16 || n2 == 32) {
                    return 4;
                }
                throw new IIOException("Unsupported BitsPerSample for SampleFormat 3/Floating Point (expected 16/32): " + n2);
            }
        }
        throw new IIOException("Unknown TIFF SampleFormat (expected 1, 2, 3 or 4): " + n);
    }

    private IndexColorModel createIndexColorModel(int n, int n2, int[] nArray) {
        int n3;
        int[] nArray2 = new int[nArray.length / 3];
        boolean bl = true;
        for (n3 = 0; n3 < nArray2.length; ++n3) {
            nArray2[n3] = nArray[n3] / 256 << 16 | nArray[n3 + nArray2.length] / 256 << 8 | nArray[n3 + 2 * nArray2.length] / 256;
            if (!bl || nArray2[n3] == 0) continue;
            bl = false;
        }
        if (bl) {
            this.processWarningOccurred("8 bit ColorMap detected.");
            for (n3 = 0; n3 < nArray2.length; ++n3) {
                nArray2[n3] = nArray[n3] << 16 | nArray[n3 + nArray2.length] << 8 | nArray[n3 + 2 * nArray2.length];
            }
        }
        return new IndexColorModel(n, nArray2.length, nArray2, 0, false, -1, n2);
    }

    private int getSampleFormat() throws IIOException {
        long[] lArray = this.getValueAsLongArray(339, "SampleFormat", false);
        if (lArray != null) {
            long l = lArray[0];
            for (int i = 1; i < lArray.length; ++i) {
                if (lArray[i] == l) continue;
                throw new IIOException("Variable TIFF SampleFormat not supported: " + Arrays.toString(lArray));
            }
            return (int)l;
        }
        return 1;
    }

    private int getBitsPerSample() throws IIOException {
        long[] lArray = this.getValueAsLongArray(258, "BitsPerSample", false);
        if (lArray == null || lArray.length == 0) {
            return 1;
        }
        int n = (int)lArray[0];
        if (lArray.length != 3 || lArray[0] != 5L || lArray[1] != 6L || lArray[2] != 5L) {
            for (int i = 1; i < lArray.length; ++i) {
                if (lArray[i] == (long)n) continue;
                throw new IIOException("Variable BitsPerSample not supported: " + Arrays.toString(lArray));
            }
        }
        return n;
    }

    public Iterator<ImageTypeSpecifier> getImageTypes(int n) throws IOException {
        this.readIFD(n);
        ImageTypeSpecifier imageTypeSpecifier = this.getRawImageType(n);
        LinkedHashSet<ImageTypeSpecifier> linkedHashSet = new LinkedHashSet<ImageTypeSpecifier>(5);
        if (imageTypeSpecifier.getColorModel().getColorSpace().getType() == 5) {
            if (imageTypeSpecifier.getNumBands() == 3 && imageTypeSpecifier.getBitsPerBand(0) == 8) {
                linkedHashSet.add(ImageTypeSpecifier.createFromBufferedImageType(5));
            } else if (imageTypeSpecifier.getNumBands() == 4 && imageTypeSpecifier.getBitsPerBand(0) == 8) {
                linkedHashSet.add(ImageTypeSpecifier.createFromBufferedImageType(6));
                linkedHashSet.add(ImageTypeSpecifier.createFromBufferedImageType(7));
            }
        }
        linkedHashSet.add(imageTypeSpecifier);
        return linkedHashSet.iterator();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public BufferedImage read(int n, ImageReadParam imageReadParam) throws IOException {
        BufferedImage bufferedImage;
        block130: {
            int n2;
            int n3;
            byte[] byArray;
            long l;
            JPEGImageReadParam jPEGImageReadParam;
            ImageReader imageReader;
            long[] lArray;
            this.readIFD(n);
            int n4 = this.getWidth(n);
            int n5 = this.getHeight(n);
            bufferedImage = TIFFImageReader.getDestination((ImageReadParam)imageReadParam, this.getImageTypes(n), (int)n4, (int)n5);
            ImageTypeSpecifier imageTypeSpecifier = this.getRawImageType(n);
            TIFFImageReader.checkReadParamBandSettings((ImageReadParam)imageReadParam, (int)imageTypeSpecifier.getNumBands(), (int)bufferedImage.getSampleModel().getNumBands());
            Rectangle rectangle = new Rectangle();
            Rectangle rectangle2 = new Rectangle();
            TIFFImageReader.computeRegions((ImageReadParam)imageReadParam, (int)n4, (int)n5, (BufferedImage)bufferedImage, (Rectangle)rectangle, (Rectangle)rectangle2);
            int n6 = imageReadParam != null ? imageReadParam.getSourceXSubsampling() : 1;
            int n7 = imageReadParam != null ? imageReadParam.getSourceYSubsampling() : 1;
            WritableRaster writableRaster = this.clipToRect(bufferedImage.getRaster(), rectangle2, imageReadParam != null ? imageReadParam.getDestinationBands() : null);
            int n8 = this.getPhotometricInterpretationWithFallback();
            int n9 = this.getValueAsIntWithDefault(259, 1);
            int n10 = this.getValueAsIntWithDefault(317, 1);
            int n11 = this.getValueAsIntWithDefault(284, 1);
            int n12 = n11 == 2 ? 1 : imageTypeSpecifier.getNumBands();
            int n13 = n4;
            long l2 = this.getValueAsLongWithDefault(278, (Long)Integer.MAX_VALUE);
            int n14 = l2 < (long)n5 ? (int)l2 : n5;
            long[] lArray2 = this.getValueAsLongArray(324, "TileOffsets", false);
            if (lArray2 != null) {
                lArray = this.getValueAsLongArray(325, "TileByteCounts", false);
                if (lArray == null) {
                    this.processWarningOccurred("Missing TileByteCounts for tiled TIFF with compression: " + n9);
                } else if (lArray.length == 0 || this.containsZero(lArray)) {
                    lArray = null;
                    this.processWarningOccurred("Ignoring all-zero TileByteCounts for tiled TIFF with compression: " + n9);
                }
                n13 = this.getValueAsInt(322, "TileWidth");
                n14 = this.getValueAsInt(323, "TileHeight");
            } else {
                lArray2 = this.getValueAsLongArray(273, "StripOffsets", true);
                lArray = this.getValueAsLongArray(279, "StripByteCounts", false);
                if (lArray == null) {
                    this.processWarningOccurred("Missing StripByteCounts for TIFF with compression: " + n9);
                } else if (lArray.length == 0 || this.containsZero(lArray)) {
                    lArray = null;
                    this.processWarningOccurred("Ignoring all-zero StripByteCounts for TIFF with compression: " + n9);
                }
                n13 = this.getValueAsIntWithDefault(322, "TileWidth", n13);
                n14 = this.getValueAsIntWithDefault(323, "TileHeight", n14);
            }
            int n15 = (n4 + n13 - 1) / n13;
            int n16 = (n5 + n14 - 1) / n14;
            WritableRaster writableRaster2 = imageTypeSpecifier.createBufferedImage(n13, 1).getRaster();
            Rectangle rectangle3 = new Rectangle(rectangle);
            int n17 = 0;
            Boolean bl = null;
            switch (n9) {
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 8: 
                case 32773: 
                case 32946: 
                case 50013: {
                    int[] nArray = null;
                    int n18 = 1;
                    if (n8 == 6) {
                        Entry entry;
                        if (writableRaster2.getNumBands() != 3) {
                            throw new IIOException("TIFF PhotometricInterpretation YCbCr requires SamplesPerPixel == 3: " + writableRaster2.getNumBands());
                        }
                        if (writableRaster2.getTransferType() != 0 && writableRaster2.getTransferType() != 1) {
                            throw new IIOException("TIFF PhotometricInterpretation YCbCr requires BitsPerSample == [8,8,8] or [16,16,16]");
                        }
                        n18 = this.getValueAsIntWithDefault(531, 1);
                        if (n18 != 1 && n18 != 2) {
                            this.processWarningOccurred("Uknown TIFF YCbCrPositioning value, expected 1 or 2: " + n18);
                        }
                        if ((entry = this.currentIFD.getEntryById((Object)530)) != null) {
                            try {
                                nArray = (int[])entry.getValue();
                            }
                            catch (ClassCastException classCastException) {
                                throw new IIOException("Unknown TIFF YCbCrSubSampling value type: " + entry.getTypeName(), classCastException);
                            }
                            if (nArray.length != 2 || nArray[0] != 1 && nArray[0] != 2 && nArray[0] != 4 || nArray[1] != 1 && nArray[1] != 2 && nArray[1] != 4) {
                                throw new IIOException("Bad TIFF YCbCrSubSampling value: " + Arrays.toString(nArray));
                            }
                            if (nArray[0] < nArray[1]) {
                                this.processWarningOccurred("TIFF PhotometricInterpretation YCbCr with bad subsampling, expected subHoriz >= subVert: " + Arrays.toString(nArray));
                            }
                        } else {
                            nArray = new int[]{2, 2};
                        }
                    }
                    this.processImageStarted(n);
                    int n19 = n11 == 2 ? imageTypeSpecifier.getNumBands() : 1;
                    int n20 = this.getValueAsIntWithDefault(266, 1);
                    int n21 = this.getBitsPerSample();
                    boolean bl2 = n21 > 16 && n21 % 16 != 0 || n21 > 8 && n21 % 8 != 0 || n12 == 1 && n21 == 6 || n12 == 3 && (n21 == 2 || n21 == 4);
                    boolean bl3 = n9 != 1 || n20 != 1 || n8 == 6 || bl2;
                    for (int i = 0; i < n16; ++i) {
                        int n22;
                        int n23 = 0;
                        int n24 = Math.min(n14, n5 - n17);
                        for (int j = 0; j < n15; n23 += n22, ++j) {
                            n22 = Math.min(n13, n4 - n23);
                            for (int k = 0; k < n19; ++k) {
                                InputStream inputStream;
                                SubImageInputStream subImageInputStream;
                                int n25 = k * n16 * n15 + i * n15 + j;
                                rectangle3.width = Math.min(n22, rectangle.width);
                                Raster raster = this.clipRowToRect(writableRaster2, rectangle3, imageReadParam != null ? imageReadParam.getSourceBands() : null, imageReadParam != null ? imageReadParam.getSourceXSubsampling() : 1);
                                this.imageInput.seek(lArray2[n25]);
                                if (!bl3) {
                                    long l3 = (long)n14 * (((long)n13 * (long)n21 * (long)n12 + 7L) / 8L);
                                    if (lArray != null && lArray[n25] < l3) {
                                        this.processWarningOccurred("strip/tileByteCount < required ( " + l3 + "):" + lArray[n25]);
                                    }
                                    subImageInputStream = new SubImageInputStream(this.imageInput, l3);
                                } else {
                                    inputStream = lArray != null ? IIOUtil.createStreamAdapter((ImageInputStream)this.imageInput, (long)lArray[n25]) : IIOUtil.createStreamAdapter((ImageInputStream)this.imageInput);
                                    inputStream = this.createFillOrderStream(n20, inputStream);
                                    int n26 = n11 == 2 && k > 0 && nArray != null ? (n13 + nArray[0] - 1) / nArray[0] : n13;
                                    inputStream = this.createDecompressorStream(n9, n26, n12, inputStream);
                                    inputStream = this.createUnpredictorStream(n10, n26, n12, n21, inputStream, this.imageInput.getByteOrder());
                                    inputStream = this.createYCbCrUpsamplerStream(n8, n11, k, writableRaster2.getTransferType(), nArray, n18, n22, inputStream, this.imageInput.getByteOrder());
                                    if (bl2) {
                                        inputStream = n21 < 8 ? new BitPaddingStream(inputStream, 1, n12 * n21, n22, this.imageInput.getByteOrder()) : new BitPaddingStream(inputStream, n12, n21, n22, this.imageInput.getByteOrder());
                                    }
                                    subImageInputStream = new DirectImageInputStream(inputStream);
                                }
                                inputStream = subImageInputStream;
                                Throwable throwable = null;
                                try {
                                    if (imageTypeSpecifier.getColorModel() instanceof DirectColorModel && imageTypeSpecifier.getColorModel().getTransferType() == 1) {
                                        inputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
                                    } else {
                                        inputStream.setByteOrder(this.imageInput.getByteOrder());
                                    }
                                    this.readStripTileData(raster, rectangle, n6, n7, k, n12, n8, writableRaster, n23, n17, n22, n24, (ImageInputStream)subImageInputStream);
                                    continue;
                                }
                                catch (Throwable throwable2) {
                                    throwable = throwable2;
                                    throw throwable2;
                                }
                                finally {
                                    if (inputStream != null) {
                                        if (throwable != null) {
                                            try {
                                                inputStream.close();
                                            }
                                            catch (Throwable throwable3) {
                                                throwable.addSuppressed(throwable3);
                                            }
                                        } else {
                                            inputStream.close();
                                        }
                                    }
                                }
                            }
                            if (n11 == 2) {
                                this.normalizeColorPlanar(n8, writableRaster);
                            }
                            if (this.abortRequested()) break;
                        }
                        this.processImageProgress(100.0f * (float)(n17 += n24) / (float)n5);
                        if (!this.abortRequested()) continue;
                        this.processReadAborted();
                        break block130;
                    }
                    break block130;
                }
                case 7: {
                    int n27;
                    byte[] byArray2;
                    ImageReader imageReader2 = this.createJPEGDelegate();
                    imageReader2.addIIOReadWarningListener(new IIOReadWarningListener(){

                        @Override
                        public void warningOccurred(ImageReader imageReader, String string) {
                            TIFFImageReader.this.processWarningOccurred(string);
                        }
                    });
                    JPEGImageReadParam jPEGImageReadParam2 = (JPEGImageReadParam)imageReader2.getDefaultReadParam();
                    Entry entry = this.currentIFD.getEntryById((Object)347);
                    byte[] byArray3 = byArray2 = entry != null ? (byte[])entry.getValue() : null;
                    if (byArray2 != null) {
                        imageReader2.setInput(new ByteArrayImageInputStream(byArray2));
                        imageReader2.getStreamMetadata();
                    } else if (n16 * n15 > 1) {
                        this.processWarningOccurred("Missing JPEGTables for tiled/striped TIFF with compression: 7 (JPEG)");
                    }
                    this.processImageStarted(n);
                    for (int i = 0; i < n16; n17 += n27, ++i) {
                        int n28;
                        int n29 = 0;
                        n27 = Math.min(n14, n5 - n17);
                        for (int j = 0; j < n15; n29 += n28, ++j) {
                            int n30 = i * n15 + j;
                            n28 = Math.min(n13, n4 - n29);
                            Rectangle rectangle4 = new Rectangle(n29, n17, n28, n27);
                            Rectangle rectangle5 = rectangle4.intersection(rectangle);
                            if (!rectangle5.isEmpty()) {
                                this.imageInput.seek(lArray2[n30]);
                                int n31 = lArray != null ? (int)lArray[n30] : Short.MAX_VALUE;
                                try (SubImageInputStream subImageInputStream = new SubImageInputStream(this.imageInput, (long)n31);){
                                    imageReader2.setInput(subImageInputStream);
                                    jPEGImageReadParam2.setSourceRegion(new Rectangle(rectangle5.x - n29, rectangle5.y - n17, rectangle5.width, rectangle5.height));
                                    jPEGImageReadParam2.setSourceSubsampling(n6, n7, 0, 0);
                                    Point point = new Point((rectangle5.x - rectangle.x) / n6, (rectangle5.y - rectangle.y) / n7);
                                    if (bl == null) {
                                        bl = this.needsCSConversion(n9, n8, this.readJPEGMetadataSafe(imageReader2));
                                    }
                                    if (!bl.booleanValue()) {
                                        jPEGImageReadParam2.setDestinationOffset(point);
                                        jPEGImageReadParam2.setDestination(bufferedImage);
                                        imageReader2.read(0, jPEGImageReadParam2);
                                    } else {
                                        Raster raster = imageReader2.readRaster(0, jPEGImageReadParam2);
                                        switch (raster.getTransferType()) {
                                            case 0: {
                                                this.normalizeColor(n8, n12, ((DataBufferByte)raster.getDataBuffer()).getData());
                                                break;
                                            }
                                            case 1: {
                                                this.normalizeColor(n8, n12, ((DataBufferUShort)raster.getDataBuffer()).getData());
                                                break;
                                            }
                                            default: {
                                                throw new IllegalStateException("Unsupported transfer type: " + raster.getTransferType());
                                            }
                                        }
                                        bufferedImage.getRaster().setDataElements(point.x, point.y, raster);
                                    }
                                }
                            }
                            if (this.abortRequested()) break;
                        }
                        this.processImageProgress(100.0f * (float)n17 / (float)n5);
                        if (!this.abortRequested()) continue;
                        this.processReadAborted();
                        break block130;
                    }
                    break block130;
                }
                case 6: {
                    int n32;
                    int n33;
                    int n34;
                    int n35;
                    int n36 = this.getValueAsIntWithDefault(512, 1);
                    switch (n36) {
                        case 1: 
                        case 14: {
                            break;
                        }
                        default: {
                            throw new IIOException("Unknown TIFF JPEGProcessingMode value: " + n36);
                        }
                    }
                    imageReader = this.createJPEGDelegate();
                    jPEGImageReadParam = (JPEGImageReadParam)imageReader.getDefaultReadParam();
                    int n37 = this.getValueAsIntWithDefault(513, -1);
                    int n38 = this.getValueAsIntWithDefault(514, -1);
                    if (n37 > 0) {
                        if (this.currentIFD.getEntryById((Object)519) != null || this.currentIFD.getEntryById((Object)520) != null || this.currentIFD.getEntryById((Object)521) != null) {
                            this.processWarningOccurred("Old-style JPEG compressed TIFF with JPEGInterchangeFormat encountered. Ignoring JPEG tables.");
                        } else {
                            this.processWarningOccurred("Old-style JPEG compressed TIFF with JPEGInterchangeFormat encountered.");
                        }
                        this.imageInput.seek(n37);
                        l = n37;
                        short s = (short)(this.imageInput.readByte() << 8 | this.imageInput.readByte());
                        if (s != -40) {
                            if (lArray2 != null && lArray2.length == 1) {
                                this.imageInput.seek(lArray2[0]);
                                s = (short)(this.imageInput.readByte() << 8 | this.imageInput.readByte());
                                if (s == -40) {
                                    l = lArray2[0];
                                }
                            }
                            if (l != (long)n37) {
                                this.processWarningOccurred("Incorrect JPEGInterchangeFormat tag, using StripOffsets/TileOffsets instead.");
                            } else {
                                this.processWarningOccurred("Incorrect JPEGInterchangeFormat tag encountered (not a valid SOI marker).");
                            }
                        }
                        if (lArray2 == null || lArray2.length == 1 && l == lArray2[0]) {
                            byArray = new byte[]{};
                        } else {
                            this.imageInput.seek(lArray2[0]);
                            if ((short)(this.imageInput.readByte() << 8 | this.imageInput.readByte()) == -38) {
                                this.processWarningOccurred("Incorrect StripOffsets/TileOffsets, points to SOS marker, ignoring offsets/byte counts.");
                                n3 = 2 + (this.imageInput.readUnsignedByte() << 8 | this.imageInput.readUnsignedByte());
                                lArray2[0] = lArray2[0] + (long)n3;
                                lArray[0] = lArray[0] - (long)n3;
                            }
                            this.imageInput.seek(l);
                            byArray = new byte[Math.max(0, (int)(lArray2[0] - l))];
                            this.imageInput.readFully(byArray);
                        }
                        if (lArray != null && lArray.length == 1 && lArray[0] < (long)n38) {
                            this.processWarningOccurred("Incorrect StripByteCounts/TileByteCounts for single tile, using JPEGInterchangeFormatLength instead.");
                            lArray[0] = n38;
                        }
                        this.processImageStarted(n);
                        break;
                    }
                    this.processWarningOccurred("Old-style JPEG compressed TIFF without JPEGInterchangeFormat encountered. Attempting to re-create JFIF stream.");
                    long[] lArray3 = this.getValueAsLongArray(519, "JPEGQTables", true);
                    byte[][] byArray4 = new byte[lArray3.length][64];
                    for (int i = 0; i < byArray4.length; ++i) {
                        this.imageInput.seek(lArray3[i]);
                        this.imageInput.readFully(byArray4[i]);
                    }
                    long[] lArray4 = this.getValueAsLongArray(520, "JPEGDCTables", true);
                    byte[][] byArrayArray = new byte[lArray4.length][];
                    for (int i = 0; i < byArrayArray.length; ++i) {
                        this.imageInput.seek(lArray4[i]);
                        byte[] byArray5 = new byte[16];
                        this.imageInput.readFully(byArray5);
                        n35 = 0;
                        for (int j = 0; j < 16; n35 += byArray5[j] & 0xFF, ++j) {
                        }
                        byArrayArray[i] = new byte[16 + n35];
                        System.arraycopy(byArray5, 0, byArrayArray[i], 0, 16);
                        this.imageInput.readFully(byArrayArray[i], 16, n35);
                    }
                    long[] lArray5 = this.getValueAsLongArray(521, "JPEGACTables", true);
                    byte[][] byArrayArray2 = new byte[lArray5.length][];
                    for (n35 = 0; n35 < byArrayArray2.length; ++n35) {
                        this.imageInput.seek(lArray5[n35]);
                        byte[] byArray6 = new byte[16];
                        this.imageInput.readFully(byArray6);
                        n34 = 0;
                        for (n33 = 0; n33 < 16; n34 += byArray6[n33] & 0xFF, ++n33) {
                        }
                        byArrayArray2[n35] = new byte[16 + n34];
                        System.arraycopy(byArray6, 0, byArrayArray2[n35], 0, 16);
                        this.imageInput.readFully(byArrayArray2[n35], 16, n34);
                    }
                    long[] lArray6 = this.getValueAsLongArray(530, "YCbCrSubSampling", false);
                    int n39 = lArray6 != null ? (int)((lArray6[0] & 0xFL) << 4 | lArray6[1] & 0xFL) : 34;
                    this.processImageStarted(n);
                    for (n34 = 0; n34 < n16; n17 += n32, ++n34) {
                        int n40;
                        n33 = 0;
                        n32 = Math.min(n14, n5 - n17);
                        for (int i = 0; i < n15; n33 += n40, ++i) {
                            n40 = Math.min(n13, n4 - n33);
                            int n41 = n34 * n15 + i;
                            if (new Rectangle(n33, n17, n40, n32).intersects(rectangle)) {
                                int n42 = lArray != null ? (int)lArray[n41] : Short.MAX_VALUE;
                                this.imageInput.seek(lArray2[n41]);
                                if (i == 0 && n34 == 0) {
                                    if ((short)(this.imageInput.readByte() << 8 | this.imageInput.readByte()) == -38) {
                                        this.imageInput.seek(lArray2[n41] + 14L);
                                        n42 -= 14;
                                    } else {
                                        this.imageInput.seek(lArray2[n41]);
                                    }
                                }
                                try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(new SequenceInputStream(Collections.enumeration(Arrays.asList(TIFFImageReader.createJFIFStream(writableRaster.getNumBands(), n13, n14, byArray4, byArrayArray, byArrayArray2, n39), IIOUtil.createStreamAdapter((ImageInputStream)this.imageInput, (long)n42), new ByteArrayInputStream(new byte[]{-1, -39})))));){
                                    imageReader.setInput(imageInputStream);
                                    jPEGImageReadParam.setSourceRegion(new Rectangle(0, 0, n40, n32));
                                    jPEGImageReadParam.setSourceSubsampling(n6, n7, 0, 0);
                                    Point point = new Point(n33 - rectangle.x, n17 - rectangle.y);
                                    if (bl == null) {
                                        bl = this.needsCSConversion(n9, n8, this.readJPEGMetadataSafe(imageReader));
                                    }
                                    if (!bl.booleanValue()) {
                                        jPEGImageReadParam.setDestinationOffset(point);
                                        jPEGImageReadParam.setDestination(bufferedImage);
                                        imageReader.read(0, jPEGImageReadParam);
                                    } else {
                                        Raster raster = imageReader.readRaster(0, jPEGImageReadParam);
                                        this.normalizeColor(n8, n12, ((DataBufferByte)raster.getDataBuffer()).getData());
                                        bufferedImage.getRaster().setDataElements(point.x, point.y, raster);
                                    }
                                }
                            }
                            if (this.abortRequested()) break;
                        }
                        this.processImageProgress(100.0f * (float)n17 / (float)n5);
                        if (!this.abortRequested()) continue;
                        this.processReadAborted();
                        break block130;
                    }
                    break block130;
                }
                case 32766: 
                case 32771: 
                case 32809: 
                case 32895: 
                case 32896: 
                case 32897: 
                case 32898: 
                case 32908: 
                case 32909: 
                case 32947: 
                case 34661: 
                case 34676: 
                case 34677: 
                case 34712: {
                    throw new IIOException("Unsupported TIFF Compression value: " + n9);
                }
                default: {
                    throw new IIOException("Unknown TIFF Compression value: " + n9);
                }
            }
            for (n3 = 0; n3 < n16; n17 += n2, ++n3) {
                int n43;
                int n44 = 0;
                n2 = Math.min(n14, n5 - n17);
                for (int i = 0; i < n15; n44 += n43, ++i) {
                    n43 = Math.min(n13, n4 - n44);
                    int n45 = n3 * n15 + i;
                    if (new Rectangle(n44, n17, n43, n2).intersects(rectangle)) {
                        int n46 = lArray != null ? (int)lArray[n45] : Integer.MAX_VALUE;
                        this.imageInput.seek(lArray2 != null ? lArray2[n45] : l);
                        try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(new SequenceInputStream(Collections.enumeration(Arrays.asList(new ByteArrayInputStream(byArray), IIOUtil.createStreamAdapter((ImageInputStream)this.imageInput, (long)n46), new ByteArrayInputStream(new byte[]{-1, -39})))));){
                            imageReader.setInput(imageInputStream);
                            jPEGImageReadParam.setSourceRegion(new Rectangle(0, 0, n43, n2));
                            jPEGImageReadParam.setSourceSubsampling(n6, n7, 0, 0);
                            Point point = new Point(n44 - rectangle.x, n17 - rectangle.y);
                            if (bl == null) {
                                bl = this.needsCSConversion(n9, n8, this.readJPEGMetadataSafe(imageReader));
                            }
                            if (!bl.booleanValue()) {
                                jPEGImageReadParam.setDestinationOffset(point);
                                jPEGImageReadParam.setDestination(bufferedImage);
                                imageReader.read(0, jPEGImageReadParam);
                            } else {
                                Raster raster = imageReader.readRaster(0, jPEGImageReadParam);
                                this.normalizeColor(n8, n12, ((DataBufferByte)raster.getDataBuffer()).getData());
                                bufferedImage.getRaster().setDataElements(point.x, point.y, raster);
                            }
                        }
                    }
                    if (this.abortRequested()) break;
                }
                this.processImageProgress(100.0f * (float)n17 / (float)n5);
                if (!this.abortRequested()) continue;
                this.processReadAborted();
                break;
            }
        }
        this.processImageComplete();
        return bufferedImage;
    }

    private InputStream createYCbCrUpsamplerStream(int n, int n2, int n3, int n4, int[] nArray, int n5, int n6, InputStream inputStream, ByteOrder byteOrder) {
        if (n == 6) {
            if (n2 == 2 && n4 == 0) {
                return n3 > 0 && (nArray[0] != 1 || nArray[1] != 1) ? new YCbCrPlanarUpsamplerStream(inputStream, nArray, n5, n6) : inputStream;
            }
            if (n4 == 0) {
                return new YCbCrUpsamplerStream(inputStream, nArray, n5, n6);
            }
            if (n4 == 1) {
                return new YCbCr16UpsamplerStream(inputStream, nArray, n5, n6, byteOrder);
            }
            throw new AssertionError();
        }
        return inputStream;
    }

    private boolean containsZero(long[] lArray) {
        for (long l : lArray) {
            if (l > 0L) continue;
            return true;
        }
        return false;
    }

    private IIOMetadata readJPEGMetadataSafe(ImageReader imageReader) throws IOException {
        try {
            return imageReader.getImageMetadata(0);
        }
        catch (IIOException iIOException) {
            this.processWarningOccurred(String.format("Could not read metadata for JPEG compressed TIFF (%s). Colors may look incorrect", iIOException.getMessage()));
            return null;
        }
    }

    private boolean needsCSConversion(int n, int n2, IIOMetadata iIOMetadata) {
        if (iIOMetadata == null) {
            return false;
        }
        int n3 = this.getJPEGSourceCS(iIOMetadata);
        if (n3 == 3 && n2 == 6 || n3 == 5 && n2 == 2 || n3 == 6 && n2 == 1) {
            return false;
        }
        if ((n3 == 9 || n3 == 14) && n2 == 5) {
            return true;
        }
        if (n == 7) {
            return true;
        }
        this.processWarningOccurred(String.format("Determined color space from JPEG stream: '%s' does not match PhotometricInterpretation: %d. Colors may look incorrect", n3, n2));
        return n3 != 3;
    }

    private int getJPEGSourceCS(IIOMetadata iIOMetadata) {
        if (iIOMetadata == null) {
            return -1;
        }
        IIOMetadataNode iIOMetadataNode = (IIOMetadataNode)iIOMetadata.getAsTree("javax_imageio_jpeg_image_1.0");
        IIOMetadataNode iIOMetadataNode2 = this.getNode(iIOMetadataNode, "sof");
        IIOMetadataNode iIOMetadataNode3 = this.getNode(iIOMetadataNode, "app0JFIF");
        IIOMetadataNode iIOMetadataNode4 = this.getNode(iIOMetadataNode, "app14Adobe");
        if (iIOMetadataNode2 != null) {
            int n = Integer.parseInt(iIOMetadataNode2.getAttribute("numFrameComponents"));
            switch (n) {
                case 1: 
                case 2: {
                    return 6;
                }
                case 3: {
                    if (iIOMetadataNode3 != null) {
                        return 3;
                    }
                    if (iIOMetadataNode4 != null) {
                        int n2 = Integer.parseInt(iIOMetadataNode4.getAttribute("transform"));
                        switch (n2) {
                            case 0: {
                                return 5;
                            }
                            case 1: {
                                return 3;
                            }
                        }
                        return 3;
                    }
                    NodeList nodeList = iIOMetadataNode2.getElementsByTagName("componentSpec");
                    int n3 = Integer.parseInt(((IIOMetadataNode)nodeList.item(0)).getAttribute("componentId"));
                    int n4 = Integer.parseInt(((IIOMetadataNode)nodeList.item(1)).getAttribute("componentId"));
                    int n5 = Integer.parseInt(((IIOMetadataNode)nodeList.item(2)).getAttribute("componentId"));
                    if (n3 == 1 && n4 == 2 && n5 == 3) {
                        return 3;
                    }
                    if (n3 == 82 && n4 == 71 && n5 == 66) {
                        return 5;
                    }
                    if (n3 == 89 && n4 == 67 && n5 == 99) {
                        return 13;
                    }
                    return 3;
                }
                case 4: {
                    if (iIOMetadataNode4 != null) {
                        int n6 = Integer.parseInt(iIOMetadataNode4.getAttribute("transform"));
                        switch (n6) {
                            case 0: {
                                return 9;
                            }
                            case 2: {
                                return 14;
                            }
                        }
                        return 14;
                    }
                    NodeList nodeList = iIOMetadataNode2.getElementsByTagName("componentSpec");
                    int n7 = Integer.parseInt(((IIOMetadataNode)nodeList.item(0)).getAttribute("componentId"));
                    int n8 = Integer.parseInt(((IIOMetadataNode)nodeList.item(1)).getAttribute("componentId"));
                    int n9 = Integer.parseInt(((IIOMetadataNode)nodeList.item(2)).getAttribute("componentId"));
                    int n10 = Integer.parseInt(((IIOMetadataNode)nodeList.item(3)).getAttribute("componentId"));
                    if (n7 == 1 && n8 == 2 && n9 == 3 && n10 == 4) {
                        return 3;
                    }
                    if (n7 == 82 && n8 == 71 && n9 == 66 && n10 == 65) {
                        return 5;
                    }
                    if (n7 == 89 && n8 == 67 && n9 == 99 && n10 == 65) {
                        return 13;
                    }
                    return 9;
                }
            }
            return -1;
        }
        return -1;
    }

    private IIOMetadataNode getNode(IIOMetadataNode iIOMetadataNode, String string) {
        NodeList nodeList = iIOMetadataNode.getElementsByTagName(string);
        return nodeList.getLength() >= 1 ? (IIOMetadataNode)nodeList.item(0) : null;
    }

    private ImageReader createJPEGDelegate() throws IOException {
        Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("JPEG");
        if (!iterator.hasNext()) {
            throw new IIOException("Could not instantiate JPEGImageReader");
        }
        return iterator.next();
    }

    private static InputStream createJFIFStream(int n, int n2, int n3, byte[][] byArray, byte[][] byArray2, byte[][] byArray3, int n4) throws IOException {
        byte[] byArray4;
        int n5;
        FastByteArrayOutputStream fastByteArrayOutputStream = new FastByteArrayOutputStream(2 + 5 * byArray.length + byArray.length * byArray[0].length + 5 * byArray2.length + byArray2.length * byArray2[0].length + 5 * byArray3.length + byArray3.length * byArray3[0].length + 2 + 2 + 6 + 3 * n + 8 + 2 * n);
        DataOutputStream dataOutputStream = new DataOutputStream((OutputStream)fastByteArrayOutputStream);
        dataOutputStream.writeShort(65496);
        for (n5 = 0; n5 < byArray.length; ++n5) {
            byArray4 = byArray[n5];
            dataOutputStream.writeShort(65499);
            dataOutputStream.writeShort(3 + byArray4.length);
            dataOutputStream.writeByte(n5);
            dataOutputStream.write(byArray4);
        }
        for (n5 = 0; n5 < byArray2.length; ++n5) {
            byArray4 = byArray2[n5];
            dataOutputStream.writeShort(65476);
            dataOutputStream.writeShort(3 + byArray4.length);
            dataOutputStream.writeByte(n5 & 0xF);
            dataOutputStream.write(byArray4);
        }
        for (n5 = 0; n5 < byArray3.length; ++n5) {
            byArray4 = byArray3[n5];
            dataOutputStream.writeShort(65476);
            dataOutputStream.writeShort(3 + byArray4.length);
            dataOutputStream.writeByte(16 + (n5 & 0xF));
            dataOutputStream.write(byArray4);
        }
        dataOutputStream.writeShort(65472);
        dataOutputStream.writeShort(8 + 3 * n);
        dataOutputStream.writeByte(8);
        dataOutputStream.writeShort(n3);
        dataOutputStream.writeShort(n2);
        dataOutputStream.writeByte(n);
        for (n5 = 0; n5 < n; ++n5) {
            dataOutputStream.writeByte(n5);
            dataOutputStream.writeByte(n5 == 0 ? n4 : 17);
            dataOutputStream.writeByte(n5);
        }
        dataOutputStream.writeShort(65498);
        dataOutputStream.writeShort(6 + 2 * n);
        dataOutputStream.writeByte(n);
        for (n5 = 0; n5 < n; ++n5) {
            dataOutputStream.writeByte(n5);
            dataOutputStream.writeByte(n5 == 0 ? n5 : 16 + (n5 & 0xF));
        }
        dataOutputStream.writeByte(0);
        dataOutputStream.writeByte(0);
        dataOutputStream.writeByte(0);
        return fastByteArrayOutputStream.createInputStream();
    }

    private Raster clipRowToRect(Raster raster, Rectangle rectangle, int[] nArray, int n) {
        if (rectangle.contains(raster.getMinX(), 0, raster.getWidth(), 1) && n == 1 && nArray == null) {
            return raster;
        }
        return raster.createChild((rectangle.x + n - 1) / n, 0, (rectangle.width + n - 1) / n, 1, 0, 0, nArray);
    }

    private WritableRaster clipToRect(WritableRaster writableRaster, Rectangle rectangle, int[] nArray) {
        if (rectangle.contains(writableRaster.getMinX(), writableRaster.getMinY(), writableRaster.getWidth(), writableRaster.getHeight()) && nArray == null) {
            return writableRaster;
        }
        return writableRaster.createWritableChild(rectangle.x, rectangle.y, rectangle.width, rectangle.height, 0, 0, nArray);
    }

    private void readStripTileData(Raster raster, Rectangle rectangle, int n, int n2, int n3, int n4, int n5, WritableRaster writableRaster, int n6, int n7, int n8, int n9, ImageInputStream imageInputStream) throws IOException {
        DataBuffer dataBuffer = raster.getDataBuffer();
        boolean bl = dataBuffer.getNumBanks() > 1;
        int n10 = this.getBitsPerSample();
        WritableRaster writableRaster2 = bl ? writableRaster.createWritableChild(writableRaster.getMinX(), writableRaster.getMinY(), writableRaster.getWidth(), writableRaster.getHeight(), 0, 0, new int[]{n3}) : writableRaster;
        Raster raster2 = bl ? raster.createChild(raster.getMinX(), 0, raster.getWidth(), 1, 0, 0, new int[]{n3}) : raster;
        switch (raster.getTransferType()) {
            case 0: {
                int n11 = bl ? ((BandedSampleModel)raster.getSampleModel()).getBankIndices()[n3] : n3;
                byte[] byArray = ((DataBufferByte)dataBuffer).getData(n11);
                for (int i = n7; i < n7 + n9 && i < rectangle.y + rectangle.height; ++i) {
                    imageInputStream.readFully(byArray);
                    if (i % n2 != 0 || i < rectangle.y) continue;
                    if (!bl) {
                        this.normalizeColor(n5, n4, byArray);
                    }
                    IIOUtil.subsampleRow((byte[])byArray, (int)(rectangle.x * n4), (int)n8, (byte[])byArray, (int)(rectangle.x * n4 / n), (int)n4, (int)n10, (int)n);
                    writableRaster2.setDataElements(n6 / n, (i - rectangle.y) / n2, raster2);
                }
                break;
            }
            case 1: 
            case 2: {
                short[] sArray = dataBuffer.getDataType() == 1 ? ((DataBufferUShort)dataBuffer).getData(n3) : ((DataBufferShort)dataBuffer).getData(n3);
                for (int i = n7; i < n7 + n9 && i < rectangle.y + rectangle.height; ++i) {
                    imageInputStream.readFully(sArray, 0, sArray.length);
                    if (i < rectangle.y) continue;
                    this.normalizeColor(n5, n4, sArray);
                    IIOUtil.subsampleRow((short[])sArray, (int)(rectangle.x * n4), (int)n8, (short[])sArray, (int)(rectangle.x * n4 / n), (int)n4, (int)n10, (int)n);
                    writableRaster2.setDataElements(n6 / n, (i - rectangle.y) / n2, raster2);
                }
                break;
            }
            case 3: {
                int[] nArray = ((DataBufferInt)dataBuffer).getData(n3);
                for (int i = n7; i < n7 + n9 && i < rectangle.y + rectangle.height; ++i) {
                    imageInputStream.readFully(nArray, 0, nArray.length);
                    if (i < rectangle.y) continue;
                    this.normalizeColor(n5, n4, nArray);
                    IIOUtil.subsampleRow((int[])nArray, (int)(rectangle.x * n4), (int)n8, (int[])nArray, (int)(rectangle.x * n4 / n), (int)n4, (int)n10, (int)n);
                    writableRaster2.setDataElements(n6 / n, (i - rectangle.y) / n2, raster2);
                }
                break;
            }
            case 4: {
                boolean bl2 = this.getBitsPerSample() == 16;
                float[] fArray = ((DataBufferFloat)raster.getDataBuffer()).getData(n3);
                short[] sArray = bl2 ? new short[fArray.length] : null;
                for (int i = n7; i < n7 + n9 && i < rectangle.y + rectangle.height; ++i) {
                    if (bl2) {
                        imageInputStream.readFully(sArray, 0, sArray.length);
                        this.toFloat(sArray, fArray);
                    } else {
                        imageInputStream.readFully(fArray, 0, fArray.length);
                    }
                    if (i < rectangle.y) continue;
                    this.normalizeColor(n5, n4, fArray);
                    if (n != 1) {
                        for (int j = rectangle.x / n * n4; j < (rectangle.x + rectangle.width) / n * n4; j += n4) {
                            System.arraycopy(fArray, j * n, fArray, j, n4);
                        }
                    }
                    writableRaster2.setDataElements(n6, i - rectangle.y, raster2);
                }
                break;
            }
            default: {
                throw new AssertionError((Object)("Unsupported data type: " + raster.getTransferType()));
            }
        }
    }

    private void toFloat(short[] sArray, float[] fArray) {
        for (int i = 0; i < fArray.length; ++i) {
            fArray[i] = Half.shortBitsToFloat((short)sArray[i]);
        }
    }

    private void clamp(float[] fArray) {
        for (int i = 0; i < fArray.length; ++i) {
            if (fArray[i] > 1.0f) {
                fArray[i] = 1.0f;
                continue;
            }
            if (!(fArray[i] < 0.0f)) continue;
            fArray[i] = 0.0f;
        }
    }

    private void normalizeColorPlanar(int n, WritableRaster writableRaster) throws IIOException {
        if (writableRaster.getTransferType() != 0) {
            return;
        }
        byte[] byArray = null;
        switch (n) {
            case 6: {
                double[] dArray = this.getValueAsDoubleArray(529, "YCbCrCoefficients", false, 3);
                double[] dArray2 = this.getValueAsDoubleArray(532, "ReferenceBlackWhite", false, 6);
                if ((dArray == null || Arrays.equals(dArray, CCIR_601_1_COEFFICIENTS)) && (dArray2 == null || Arrays.equals(dArray2, REFERENCE_BLACK_WHITE_YCC_DEFAULT))) {
                    for (int i = 0; i < writableRaster.getHeight(); ++i) {
                        for (int j = 0; j < writableRaster.getWidth(); ++j) {
                            byArray = (byte[])writableRaster.getDataElements(j, i, byArray);
                            YCbCrConverter.convertJPEGYCbCr2RGB((byte[])byArray, (byte[])byArray, (int)0);
                            writableRaster.setDataElements(j, i, byArray);
                        }
                    }
                } else {
                    if (dArray == null) {
                        dArray = CCIR_601_1_COEFFICIENTS;
                    }
                    if (dArray2 != null && Arrays.equals(dArray2, REFERENCE_BLACK_WHITE_YCC_DEFAULT)) {
                        dArray2 = null;
                    }
                    for (int i = 0; i < writableRaster.getHeight(); ++i) {
                        for (int j = 0; j < writableRaster.getWidth(); ++j) {
                            byArray = (byte[])writableRaster.getDataElements(j, i, byArray);
                            YCbCrConverter.convertYCbCr2RGB((byte[])byArray, (byte[])byArray, (double[])dArray, (double[])dArray2, (int)0);
                            writableRaster.setDataElements(j, i, byArray);
                        }
                    }
                }
                break;
            }
            case 8: 
            case 9: 
            case 10: {
                CIELabColorConverter cIELabColorConverter = new CIELabColorConverter(n == 8 ? CIELabColorConverter.Illuminant.D65 : CIELabColorConverter.Illuminant.D50);
                float[] fArray = new float[3];
                for (int i = 0; i < writableRaster.getHeight(); ++i) {
                    for (int j = 0; j < writableRaster.getWidth(); ++j) {
                        float f;
                        float f2;
                        byArray = (byte[])writableRaster.getDataElements(j, i, byArray);
                        float f3 = (float)(byArray[0] & 0xFF) * 100.0f / 255.0f;
                        if (n == 8) {
                            f2 = byArray[1];
                            f = byArray[2];
                        } else {
                            f2 = (byArray[1] & 0xFF) - 128;
                            f = (byArray[2] & 0xFF) - 128;
                        }
                        cIELabColorConverter.toRGB(f3, f2, f, fArray);
                        byArray[0] = (byte)fArray[0];
                        byArray[1] = (byte)fArray[1];
                        byArray[2] = (byte)fArray[2];
                        writableRaster.setDataElements(j, i, byArray);
                    }
                }
                break;
            }
        }
    }

    private void normalizeColor(int n, int n2, byte[] byArray) throws IOException {
        switch (n) {
            case 0: {
                if (this.getBitsPerSample() <= 1 && this.getValueAsIntWithDefault(277, 1) <= 1) break;
                int n3 = 0;
                while (n3 < byArray.length) {
                    int n4 = n3++;
                    byArray[n4] = ~byArray[n4];
                }
                break;
            }
            case 8: 
            case 9: 
            case 10: {
                CIELabColorConverter cIELabColorConverter = new CIELabColorConverter(n == 8 ? CIELabColorConverter.Illuminant.D65 : CIELabColorConverter.Illuminant.D50);
                float[] fArray = new float[3];
                for (int i = 0; i < byArray.length; i += n2) {
                    float f;
                    float f2;
                    float f3 = (float)(byArray[i] & 0xFF) * 100.0f / 255.0f;
                    if (n == 8) {
                        f2 = byArray[i + 1];
                        f = byArray[i + 2];
                    } else {
                        f2 = (byArray[i + 1] & 0xFF) - 128;
                        f = (byArray[i + 2] & 0xFF) - 128;
                    }
                    cIELabColorConverter.toRGB(f3, f2, f, fArray);
                    byArray[i] = (byte)fArray[0];
                    byArray[i + 1] = (byte)fArray[1];
                    byArray[i + 2] = (byte)fArray[2];
                }
                break;
            }
            case 6: {
                double[] dArray = this.getValueAsDoubleArray(529, "YCbCrCoefficients", false, 3);
                double[] dArray2 = this.getValueAsDoubleArray(532, "ReferenceBlackWhite", false, 6);
                if ((dArray == null || Arrays.equals(dArray, CCIR_601_1_COEFFICIENTS)) && (dArray2 == null || Arrays.equals(dArray2, REFERENCE_BLACK_WHITE_YCC_DEFAULT))) {
                    for (int i = 0; i < byArray.length; i += 3) {
                        YCbCrConverter.convertJPEGYCbCr2RGB((byte[])byArray, (byte[])byArray, (int)i);
                    }
                } else {
                    if (dArray == null) {
                        dArray = CCIR_601_1_COEFFICIENTS;
                    }
                    if (dArray2 != null && Arrays.equals(dArray2, REFERENCE_BLACK_WHITE_YCC_DEFAULT)) {
                        dArray2 = null;
                    }
                    for (int i = 0; i < byArray.length; i += 3) {
                        YCbCrConverter.convertYCbCr2RGB((byte[])byArray, (byte[])byArray, (double[])dArray, (double[])dArray2, (int)i);
                    }
                }
                break;
            }
        }
    }

    private void normalizeColor(int n, int n2, short[] sArray) throws IIOException {
        switch (n) {
            case 0: {
                int n3 = 0;
                while (n3 < sArray.length) {
                    int n4 = n3++;
                    sArray[n4] = ~sArray[n4];
                }
                break;
            }
            case 8: 
            case 9: 
            case 10: {
                CIELabColorConverter cIELabColorConverter = new CIELabColorConverter(n == 10 ? CIELabColorConverter.Illuminant.D65 : CIELabColorConverter.Illuminant.D50);
                float[] fArray = new float[3];
                float f = n == 8 ? 65535.0f : 65280.0f;
                for (int i = 0; i < sArray.length; i += n2) {
                    float f2;
                    float f3;
                    float f4 = (float)(sArray[i] & 0xFFFF) * 100.0f / f;
                    if (n == 8) {
                        f3 = (float)sArray[i + 1] / 256.0f;
                        f2 = (float)sArray[i + 2] / 256.0f;
                    } else {
                        f3 = (float)((sArray[i + 1] & 0xFFFF) - 32768) / 256.0f;
                        f2 = (float)((sArray[i + 2] & 0xFFFF) - 32768) / 256.0f;
                    }
                    cIELabColorConverter.toRGB(f4, f3, f2, fArray);
                    sArray[i] = (short)(fArray[0] * 257.0f);
                    sArray[i + 1] = (short)(fArray[1] * 257.0f);
                    sArray[i + 2] = (short)(fArray[2] * 257.0f);
                }
                break;
            }
            case 6: {
                double[] dArray = this.getValueAsDoubleArray(529, "YCbCrCoefficients", false, 3);
                double[] dArray2 = this.getValueAsDoubleArray(532, "ReferenceBlackWhite", false, 6);
                if (dArray == null) {
                    dArray = CCIR_601_1_COEFFICIENTS;
                }
                if (dArray2 != null && Arrays.equals(dArray2, REFERENCE_BLACK_WHITE_YCC_DEFAULT)) {
                    dArray2 = null;
                }
                for (int i = 0; i < sArray.length; i += 3) {
                    this.convertYCbCr2RGB(sArray, sArray, dArray, dArray2, i);
                }
                break;
            }
        }
    }

    private void normalizeColor(int n, int n2, int[] nArray) {
        switch (n) {
            case 0: {
                int n3 = 0;
                while (n3 < nArray.length) {
                    int n4 = n3++;
                    nArray[n4] = ~nArray[n4];
                }
                break;
            }
        }
    }

    private void normalizeColor(int n, int n2, float[] fArray) {
        this.clamp(fArray);
        switch (n) {
            case 0: {
                for (int i = 0; i < fArray.length; ++i) {
                    fArray[i] = 1.0f - fArray[i];
                }
                break;
            }
        }
    }

    private void convertYCbCr2RGB(short[] sArray, short[] sArray2, double[] dArray, double[] dArray2, int n) {
        double d;
        double d2;
        double d3;
        if (dArray2 == null) {
            d3 = sArray[n] & 0xFFFF;
            d2 = (sArray[n + 1] & 0xFFFF) - 32768;
            d = (sArray[n + 2] & 0xFFFF) - 32768;
        } else {
            d3 = ((double)(sArray[n] & 0xFFFF) - dArray2[0]) * 65535.0 / (dArray2[1] - dArray2[0]);
            d2 = ((double)(sArray[n + 1] & 0xFFFF) - dArray2[2]) * 32767.0 / (dArray2[3] - dArray2[2]);
            d = ((double)(sArray[n + 2] & 0xFFFF) - dArray2[4]) * 32767.0 / (dArray2[5] - dArray2[4]);
        }
        double d4 = dArray[0];
        double d5 = dArray[1];
        double d6 = dArray[2];
        int n2 = (int)Math.round(d * (2.0 - 2.0 * d4) + d3);
        int n3 = (int)Math.round(d2 * (2.0 - 2.0 * d6) + d3);
        int n4 = (int)Math.round((d3 - d4 * (double)n2 - d6 * (double)n3) / d5);
        short s = this.clampShort(n2);
        short s2 = this.clampShort(n4);
        short s3 = this.clampShort(n3);
        sArray2[n] = s;
        sArray2[n + 1] = s2;
        sArray2[n + 2] = s3;
    }

    private short clampShort(int n) {
        return (short)Math.max(0, Math.min(65535, n));
    }

    private InputStream createDecompressorStream(int n, int n2, int n3, InputStream inputStream) throws IOException {
        switch (n) {
            case 1: {
                return inputStream;
            }
            case 32773: {
                return new DecoderStream(inputStream, (Decoder)new PackBitsDecoder(), 256);
            }
            case 5: {
                return new DecoderStream(inputStream, LZWDecoder.create(LZWDecoder.isOldBitReversedStream(inputStream)), Math.max(n2 * n3, 4096));
            }
            case 8: 
            case 32946: 
            case 50013: {
                return new InflaterInputStream(inputStream, new Inflater(), 1024);
            }
            case 2: 
            case 3: 
            case 4: {
                if (this.overrideCCITTCompression == -1) {
                    this.overrideCCITTCompression = this.findCCITTType(n, inputStream);
                }
                return new CCITTFaxDecoderStream(inputStream, n2, this.overrideCCITTCompression, this.getCCITTOptions(n), n == 2);
            }
        }
        throw new IllegalArgumentException("Unsupported TIFF compression: " + n);
    }

    private int findCCITTType(int n, InputStream inputStream) throws IOException {
        int n2 = CCITTFaxDecoderStream.findCompressionType(n, inputStream);
        if (n2 != n) {
            this.processWarningOccurred(String.format("Detected compression type %d, does not match encoded compression type: %d", n2, n));
        }
        return n2;
    }

    private InputStream createFillOrderStream(int n, InputStream inputStream) {
        switch (n) {
            case 1: {
                return inputStream;
            }
            case 2: {
                return new ReverseInputStream(inputStream);
            }
        }
        throw new IllegalArgumentException("Unsupported TIFF FillOrder: " + n);
    }

    private long getCCITTOptions(int n) throws IIOException {
        switch (n) {
            case 2: {
                return 0L;
            }
            case 3: {
                return this.getValueAsLongWithDefault(292, 0L);
            }
            case 4: {
                return this.getValueAsLongWithDefault(293, 0L);
            }
        }
        throw new IllegalArgumentException("No CCITT options for compression: " + n);
    }

    private InputStream createUnpredictorStream(int n, int n2, int n3, int n4, InputStream inputStream, ByteOrder byteOrder) throws IOException {
        switch (n) {
            case 1: {
                return inputStream;
            }
            case 2: {
                return new HorizontalDeDifferencingStream(inputStream, n2, n3, n4, byteOrder);
            }
            case 3: {
                throw new IIOException("Unsupported TIFF Predictor value: " + n);
            }
        }
        throw new IIOException("Unknown TIFF Predictor value: " + n);
    }

    private long[] getValueAsLongArray(int n, String string, boolean bl) throws IIOException {
        long[] lArray;
        Entry entry = this.currentIFD.getEntryById((Object)n);
        if (entry == null) {
            if (bl) {
                throw new IIOException("Missing TIFF tag " + string);
            }
            return null;
        }
        if (entry.valueCount() == 1) {
            lArray = new long[]{((Number)entry.getValue()).longValue()};
        } else if (entry.getValue() instanceof short[]) {
            short[] sArray = (short[])entry.getValue();
            lArray = new long[sArray.length];
            int n2 = lArray.length;
            for (int i = 0; i < n2; ++i) {
                lArray[i] = sArray[i];
            }
        } else if (entry.getValue() instanceof int[]) {
            int[] nArray = (int[])entry.getValue();
            lArray = new long[nArray.length];
            int n3 = lArray.length;
            for (int i = 0; i < n3; ++i) {
                lArray[i] = nArray[i];
            }
        } else if (entry.getValue() instanceof long[]) {
            lArray = (long[])entry.getValue();
        } else {
            throw new IIOException(String.format("Unsupported %s type: %s (%s)", string, entry.getTypeName(), entry.getValue().getClass()));
        }
        return lArray;
    }

    private double[] getValueAsDoubleArray(int n, String string, boolean bl, int n2) throws IIOException {
        double[] dArray;
        Entry entry = this.currentIFD.getEntryById((Object)n);
        if (entry == null) {
            if (bl) {
                throw new IIOException("Missing TIFF tag " + string);
            }
            return null;
        }
        if (n2 > 0 && entry.valueCount() != n2) {
            if (bl) {
                throw new IIOException(String.format("Unexpected value count for %s: %d (expected %d values)", string, entry.valueCount(), n2));
            }
            return null;
        }
        if (entry.valueCount() == 1) {
            dArray = new double[]{((Number)entry.getValue()).doubleValue()};
        } else if (entry.getValue() instanceof float[]) {
            float[] fArray = (float[])entry.getValue();
            dArray = new double[fArray.length];
            int n3 = dArray.length;
            for (int i = 0; i < n3; ++i) {
                dArray[i] = fArray[i];
            }
        } else if (entry.getValue() instanceof double[]) {
            dArray = (double[])entry.getValue();
        } else if (entry.getValue() instanceof Rational[]) {
            Rational[] rationalArray = (Rational[])entry.getValue();
            dArray = new double[rationalArray.length];
            int n4 = dArray.length;
            for (int i = 0; i < n4; ++i) {
                dArray[i] = rationalArray[i].doubleValue();
            }
        } else {
            throw new IIOException(String.format("Unsupported %s type: %s (%s)", string, entry.getTypeName(), entry.getValue().getClass()));
        }
        return dArray;
    }

    private ICC_Profile getICCProfile() {
        Entry entry = this.currentIFD.getEntryById((Object)34675);
        if (entry != null) {
            try {
                return ColorProfiles.createProfile((byte[])((byte[])entry.getValue()));
            }
            catch (CMMException | IllegalArgumentException runtimeException) {
                this.processWarningOccurred("Ignoring broken/incompatible ICC profile: " + runtimeException.getMessage());
            }
        }
        return null;
    }

    public boolean canReadRaster() {
        return true;
    }

    public Raster readRaster(int n, ImageReadParam imageReadParam) throws IOException {
        return this.read(n, imageReadParam).getData();
    }

    public boolean isImageTiled(int n) throws IOException {
        this.readIFD(n);
        return this.currentIFD.getEntryById((Object)322) != null && this.currentIFD.getEntryById((Object)323) != null;
    }

    public int getTileWidth(int n) throws IOException {
        this.readIFD(n);
        int n2 = this.getValueAsIntWithDefault(322, -1);
        if (n2 > 0) {
            return n2;
        }
        return this.getWidth(n);
    }

    public int getTileHeight(int n) throws IOException {
        this.readIFD(n);
        int n2 = this.getValueAsIntWithDefault(323, -1);
        if (n2 > 0) {
            return n2;
        }
        return this.getHeight(n);
    }

    private Rectangle computeTileRegion(int n, int n2, int n3) throws IOException {
        int n4 = this.getTileWidth(n);
        int n5 = this.getTileHeight(n);
        int n6 = this.getWidth(n);
        int n7 = this.getHeight(n);
        int n8 = n2 * n4;
        int n9 = n3 * n5;
        int n10 = Math.min(n4, n6 - n8);
        int n11 = Math.min(n5, n7 - n9);
        if (n2 < 0 || n3 < 0 || n10 < 0 || n11 < 0) {
            throw new IllegalArgumentException("Tile [" + n2 + "," + n3 + "] out of bounds");
        }
        return new Rectangle(n8, n9, n10, n11);
    }

    public BufferedImage readTile(int n, int n2, int n3) throws IOException {
        ImageReadParam imageReadParam = this.getDefaultReadParam();
        imageReadParam.setSourceRegion(this.computeTileRegion(n, n2, n3));
        return this.read(n, imageReadParam);
    }

    public Raster readTileRaster(int n, int n2, int n3) throws IOException {
        ImageReadParam imageReadParam = this.getDefaultReadParam();
        imageReadParam.setSourceRegion(this.computeTileRegion(n, n2, n3));
        return this.readRaster(n, imageReadParam);
    }

    public IIOMetadata getImageMetadata(int n) throws IOException {
        this.readIFD(n);
        return new TIFFImageMetadata(this.currentIFD);
    }

    public IIOMetadata getStreamMetadata() throws IOException {
        this.readMetadata();
        return new TIFFStreamMetadata(this.imageInput.getByteOrder());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] stringArray) throws IOException {
        ImageIO.setUseCache(false);
        for (final String string : stringArray) {
            Object object;
            File file = new File(string);
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(file);
            if (imageInputStream == null) {
                System.err.println("Could not read file: " + file);
                continue;
            }
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInputStream);
            if (!iterator.hasNext()) {
                object = FileUtil.getExtension((String)file.getName());
                iterator = ImageIO.getImageReadersBySuffix((String)object);
                if (!iterator.hasNext()) {
                    System.err.println("No reader for: " + file);
                    System.err.println("Supported formats: " + Arrays.toString(IIOUtil.getNormalizedReaderFormatNames()));
                    continue;
                }
                System.err.println("Could not determine file format, falling back to file extension: ." + (String)object);
            }
            object = iterator.next();
            System.out.printf("Reading %s format (%s)%n", ((ImageReader)object).getFormatName(), object);
            ((ImageReader)object).addIIOReadWarningListener(new IIOReadWarningListener(){

                @Override
                public void warningOccurred(ImageReader imageReader, String string2) {
                    System.err.println("Warning: " + string + ": " + string2);
                }
            });
            ((ImageReader)object).addIIOReadProgressListener((IIOReadProgressListener)new ProgressListenerBase(){
                private static final int MAX_W = 78;
                int lastProgress;

                public void imageStarted(ImageReader imageReader, int n) {
                    this.lastProgress = 0;
                    System.out.print("[");
                }

                public void imageProgress(ImageReader imageReader, float f) {
                    int n = (int)(f * 78.0f) / 100;
                    if (n > this.lastProgress) {
                        for (int i = this.lastProgress; i < n; ++i) {
                            System.out.print(".");
                        }
                        System.out.flush();
                        this.lastProgress = n;
                    }
                }

                public void imageComplete(ImageReader imageReader) {
                    for (int i = this.lastProgress; i < 78; ++i) {
                        System.out.print(".");
                    }
                    System.out.println("]");
                }
            });
            ((ImageReader)object).setInput(imageInputStream);
            try {
                ImageReadParam imageReadParam = ((ImageReader)object).getDefaultReadParam();
                if (imageReadParam.getClass().getName().equals("com.twelvemonkeys.imageio.plugins.svg.SVGReadParam")) {
                    Method method = imageReadParam.getClass().getMethod("setBaseURI", String.class);
                    String string2 = file.getAbsoluteFile().toURI().toString();
                    method.invoke((Object)imageReadParam, string2);
                }
                int n = ((ImageReader)object).getNumImages(true);
                for (int i = 0; i < n; ++i) {
                    try {
                        long l = System.currentTimeMillis();
                        if (imageReadParam.canSetSourceRenderSize()) {
                            int n2 = 512;
                            float f = ((ImageReader)object).getAspectRatio(i);
                            imageReadParam.setSourceRenderSize(f > 1.0f ? new Dimension(n2, (int)Math.ceil((float)n2 / f)) : new Dimension((int)Math.ceil((float)n2 * f), n2));
                        }
                        BufferedImage bufferedImage = ((ImageReader)object).read(i, imageReadParam);
                        System.err.println("Read time: " + (System.currentTimeMillis() - l) + " ms");
                        System.err.println("image: " + bufferedImage);
                        if (bufferedImage != null && bufferedImage.getType() == 0) {
                            l = System.currentTimeMillis();
                            bufferedImage = new ColorConvertOp(null).filter(bufferedImage, new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 2));
                            System.err.println("Conversion time: " + (System.currentTimeMillis() - l) + " ms");
                        }
                        TIFFImageReader.showIt(bufferedImage, String.format("Image: %s [%d x %d]", file.getName(), ((ImageReader)object).getWidth(i), ((ImageReader)object).getHeight(i)));
                        try {
                            int n3 = ((ImageReader)object).getNumThumbnails(i);
                            for (int j = 0; j < n3; ++j) {
                                BufferedImage bufferedImage2 = ((ImageReader)object).readThumbnail(i, j);
                                TIFFImageReader.showIt(bufferedImage2, String.format("Thumbnail: %s [%d x %d]", file.getName(), bufferedImage2.getWidth(), bufferedImage2.getHeight()));
                            }
                            continue;
                        }
                        catch (IIOException iIOException) {
                            System.err.println("Could not read thumbnails: " + iIOException.getMessage());
                            iIOException.printStackTrace();
                            continue;
                        }
                    }
                    catch (Throwable throwable) {
                        System.err.println(file + " image " + i + " can't be read:");
                        throwable.printStackTrace();
                    }
                }
            }
            catch (Throwable throwable) {
                System.err.println(file + " can't be read:");
                throwable.printStackTrace();
            }
            finally {
                imageInputStream.close();
            }
        }
    }

    private static void replaceBytesWithUndefined(IIOMetadataNode iIOMetadataNode) {
        NodeList nodeList = iIOMetadataNode.getElementsByTagName("TIFFBytes");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            IIOMetadataNode iIOMetadataNode2 = (IIOMetadataNode)nodeList.item(i);
            IIOMetadataNode iIOMetadataNode3 = (IIOMetadataNode)iIOMetadataNode2.getParentNode();
            NodeList nodeList2 = iIOMetadataNode2.getChildNodes();
            if (!BYTE_TO_UNDEFINED_NODES.contains(iIOMetadataNode3.getAttribute("number")) || nodeList2.getLength() <= 16) continue;
            IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("TIFFUndefined");
            StringBuilder stringBuilder = new StringBuilder();
            for (IIOMetadataNode iIOMetadataNode5 = (IIOMetadataNode)iIOMetadataNode2.getFirstChild(); iIOMetadataNode5 != null; iIOMetadataNode5 = (IIOMetadataNode)iIOMetadataNode5.getNextSibling()) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                String string = iIOMetadataNode5.getAttribute("value");
                stringBuilder.append(string);
            }
            iIOMetadataNode4.setAttribute("value", stringBuilder.toString());
            iIOMetadataNode3.replaceChild(iIOMetadataNode4, iIOMetadataNode2);
        }
    }

    protected static void showIt(BufferedImage bufferedImage, String string) {
        ImageReaderBase.showIt((BufferedImage)bufferedImage, (String)string);
    }
}

