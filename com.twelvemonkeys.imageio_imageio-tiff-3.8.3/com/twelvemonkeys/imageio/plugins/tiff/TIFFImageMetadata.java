/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.AbstractMetadata
 *  com.twelvemonkeys.imageio.metadata.Directory
 *  com.twelvemonkeys.imageio.metadata.Entry
 *  com.twelvemonkeys.imageio.metadata.tiff.IFD
 *  com.twelvemonkeys.imageio.metadata.tiff.Rational
 *  com.twelvemonkeys.imageio.metadata.tiff.TIFF
 *  com.twelvemonkeys.imageio.metadata.tiff.TIFFEntry
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.imageio.AbstractMetadata;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.tiff.IFD;
import com.twelvemonkeys.imageio.metadata.tiff.Rational;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFEntry;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageMetadataFormat;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReader;
import com.twelvemonkeys.lang.Validate;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class TIFFImageMetadata
extends AbstractMetadata {
    static final int RATIONAL_SCALE_FACTOR = 100000;
    private final Directory original;
    private Directory ifd;

    public TIFFImageMetadata() {
        this((Directory)new IFD(Collections.emptyList()));
    }

    public TIFFImageMetadata(Directory directory) {
        super(true, "com_sun_media_imageio_plugins_tiff_image_1.0", TIFFImageMetadataFormat.class.getName(), null, null);
        this.ifd = (Directory)Validate.notNull((Object)directory, (String)"IFD");
        this.original = directory;
    }

    public TIFFImageMetadata(Collection<? extends Entry> collection) {
        this((Directory)new IFD(collection));
    }

    protected IIOMetadataNode getNativeTree() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode(this.nativeMetadataFormatName);
        iIOMetadataNode.appendChild(this.asTree(this.ifd));
        return iIOMetadataNode;
    }

    private IIOMetadataNode asTree(Directory directory) {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("TIFFIFD");
        for (Entry entry : directory) {
            String string;
            IIOMetadataNode iIOMetadataNode2;
            Object object = entry.getValue();
            if (object instanceof Directory) {
                iIOMetadataNode2 = this.asTree((Directory)object);
                iIOMetadataNode2.setAttribute("parentTagNumber", String.valueOf(entry.getIdentifier()));
                string = entry.getFieldName();
                if (string != null) {
                    iIOMetadataNode2.setAttribute("parentTagName", string);
                }
            } else {
                Object object2;
                iIOMetadataNode2 = new IIOMetadataNode("TIFFField");
                iIOMetadataNode2.setAttribute("number", String.valueOf(entry.getIdentifier()));
                string = entry.getFieldName();
                if (string != null) {
                    iIOMetadataNode2.setAttribute("name", string);
                }
                int n = entry.valueCount();
                if (TIFF.TYPE_NAMES[7].equals(entry.getTypeName())) {
                    object2 = new IIOMetadataNode("TIFFUndefined");
                    iIOMetadataNode2.appendChild((Node)object2);
                    if (!(n != 1 || object != null && object.getClass().isArray())) {
                        ((IIOMetadataNode)object2).setAttribute("value", String.valueOf(object));
                    } else {
                        ((IIOMetadataNode)object2).setAttribute("value", Arrays.toString((byte[])object).replaceAll("\\[?\\]?", ""));
                    }
                } else {
                    object2 = this.getMetadataArrayType(entry);
                    IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode((String)object2);
                    iIOMetadataNode2.appendChild(iIOMetadataNode3);
                    boolean bl = !this.isSignedType(entry);
                    String string2 = this.getMetadataType(entry);
                    if (!(n != 1 || object != null && object.getClass().isArray())) {
                        IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode(string2);
                        iIOMetadataNode3.appendChild(iIOMetadataNode4);
                        this.setTIFFNativeValue(object, bl, iIOMetadataNode4);
                    } else {
                        for (int i = 0; i < n; ++i) {
                            Object object3 = Array.get(object, i);
                            IIOMetadataNode iIOMetadataNode5 = new IIOMetadataNode(string2);
                            iIOMetadataNode3.appendChild(iIOMetadataNode5);
                            this.setTIFFNativeValue(object3, bl, iIOMetadataNode5);
                        }
                    }
                }
            }
            iIOMetadataNode.appendChild(iIOMetadataNode2);
        }
        return iIOMetadataNode;
    }

    private void setTIFFNativeValue(Object object, boolean bl, IIOMetadataNode iIOMetadataNode) {
        if (bl && object instanceof Byte) {
            iIOMetadataNode.setAttribute("value", String.valueOf((Byte)object & 0xFF));
        } else if (bl && object instanceof Short) {
            iIOMetadataNode.setAttribute("value", String.valueOf((Short)object & 0xFFFF));
        } else if (bl && object instanceof Integer) {
            iIOMetadataNode.setAttribute("value", String.valueOf((long)((Integer)object).intValue() & 0xFFFFFFFFL));
        } else if (object instanceof Rational) {
            String string = String.valueOf(object);
            iIOMetadataNode.setAttribute("value", string.indexOf(47) < 0 && !"NaN".equals(string) ? string + "/1" : string);
        } else {
            iIOMetadataNode.setAttribute("value", String.valueOf(object));
        }
    }

    private boolean isSignedType(Entry entry) {
        String string = entry.getTypeName();
        if ("SBYTE".equals(string)) {
            return true;
        }
        if ("SSHORT".equals(string)) {
            return true;
        }
        if ("SLONG".equals(string)) {
            return true;
        }
        if ("SRATIONAL".equals(string)) {
            return true;
        }
        if ("FLOAT".equals(string)) {
            return true;
        }
        if ("DOUBLE".equals(string)) {
            return true;
        }
        return "SLONG8".equals(string);
    }

    private String getMetadataArrayType(Entry entry) {
        String string = entry.getTypeName();
        if ("BYTE".equals(string)) {
            return "TIFFBytes";
        }
        if ("ASCII".equals(string)) {
            return "TIFFAsciis";
        }
        if ("SHORT".equals(string)) {
            return "TIFFShorts";
        }
        if ("LONG".equals(string)) {
            return "TIFFLongs";
        }
        if ("RATIONAL".equals(string)) {
            return "TIFFRationals";
        }
        if ("SBYTE".equals(string)) {
            return "TIFFSBytes";
        }
        if ("SSHORT".equals(string)) {
            return "TIFFSShorts";
        }
        if ("SLONG".equals(string)) {
            return "TIFFSLongs";
        }
        if ("SRATIONAL".equals(string)) {
            return "TIFFSRationals";
        }
        if ("FLOAT".equals(string)) {
            return "TIFFFloats";
        }
        if ("DOUBLE".equals(string)) {
            return "TIFFDoubles";
        }
        if ("LONG8".equals(string)) {
            return "TIFFLong8s";
        }
        if ("SLONG8".equals(string)) {
            return "TIFFSLong8s";
        }
        throw new IllegalArgumentException(string);
    }

    private String getMetadataType(Entry entry) {
        String string = entry.getTypeName();
        if ("BYTE".equals(string)) {
            return "TIFFByte";
        }
        if ("ASCII".equals(string)) {
            return "TIFFAscii";
        }
        if ("SHORT".equals(string)) {
            return "TIFFShort";
        }
        if ("LONG".equals(string)) {
            return "TIFFLong";
        }
        if ("RATIONAL".equals(string)) {
            return "TIFFRational";
        }
        if ("SBYTE".equals(string)) {
            return "TIFFSByte";
        }
        if ("SSHORT".equals(string)) {
            return "TIFFSShort";
        }
        if ("SLONG".equals(string)) {
            return "TIFFSLong";
        }
        if ("SRATIONAL".equals(string)) {
            return "TIFFSRational";
        }
        if ("FLOAT".equals(string)) {
            return "TIFFFloat";
        }
        if ("DOUBLE".equals(string)) {
            return "TIFFDouble";
        }
        if ("LONG8".equals(string)) {
            return "TIFFLong8";
        }
        if ("SLONG8".equals(string)) {
            return "TIFFSLong8";
        }
        throw new IllegalArgumentException(string);
    }

    private IIOMetadataNode addChildNode(IIOMetadataNode iIOMetadataNode, String string, Object object) {
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode(string);
        if (object != null) {
            iIOMetadataNode2.setUserObject(object);
            iIOMetadataNode2.setNodeValue(object.toString());
        }
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        return iIOMetadataNode2;
    }

    protected IIOMetadataNode getStandardChromaNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Chroma");
        int n = this.getPhotometricInterpretationWithFallback();
        int n2 = this.getSamplesPerPixelWithFallback();
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        switch (n) {
            case 0: 
            case 1: 
            case 4: {
                iIOMetadataNode2.setAttribute("name", "GRAY");
                break;
            }
            case 2: 
            case 3: {
                iIOMetadataNode2.setAttribute("name", "RGB");
                break;
            }
            case 6: {
                iIOMetadataNode2.setAttribute("name", "YCbCr");
                break;
            }
            case 8: 
            case 9: 
            case 10: {
                iIOMetadataNode2.setAttribute("name", "Lab");
                break;
            }
            case 5: {
                if (n2 == 3) {
                    iIOMetadataNode2.setAttribute("name", "CMY");
                    break;
                }
                iIOMetadataNode2.setAttribute("name", "CMYK");
                break;
            }
            case 32844: 
            case 32845: {
                iIOMetadataNode2.setAttribute("name", "Luv");
                break;
            }
            case 32803: 
            case 34892: {
                iIOMetadataNode2.setAttribute("name", "3CLR");
                break;
            }
            default: {
                iIOMetadataNode2.setAttribute("name", Integer.toHexString(n2) + "CLR");
            }
        }
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("NumChannels");
        iIOMetadataNode.appendChild(iIOMetadataNode3);
        if (n == 3) {
            iIOMetadataNode3.setAttribute("value", "3");
        } else {
            iIOMetadataNode3.setAttribute("value", Integer.toString(n2));
        }
        IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("BlackIsZero");
        iIOMetadataNode.appendChild(iIOMetadataNode4);
        switch (n) {
            case 0: {
                iIOMetadataNode4.setAttribute("value", "FALSE");
                break;
            }
        }
        Entry entry = this.ifd.getEntryById((Object)320);
        if (entry != null) {
            int[] nArray = (int[])entry.getValue();
            IIOMetadataNode iIOMetadataNode5 = new IIOMetadataNode("Palette");
            iIOMetadataNode.appendChild(iIOMetadataNode5);
            int n3 = nArray.length / 3;
            for (int i = 0; i < n3; ++i) {
                IIOMetadataNode iIOMetadataNode6 = new IIOMetadataNode("PaletteEntry");
                iIOMetadataNode6.setAttribute("index", Integer.toString(i));
                iIOMetadataNode6.setAttribute("red", Integer.toString(nArray[i] >> 8 & 0xFF));
                iIOMetadataNode6.setAttribute("green", Integer.toString(nArray[i + n3] >> 8 & 0xFF));
                iIOMetadataNode6.setAttribute("blue", Integer.toString(nArray[i + n3 * 2] >> 8 & 0xFF));
                iIOMetadataNode5.appendChild(iIOMetadataNode6);
            }
        }
        return iIOMetadataNode;
    }

    private int getPhotometricInterpretationWithFallback() {
        Entry entry = this.ifd.getEntryById((Object)262);
        return entry != null ? TIFFImageMetadata.getValueAsInt(entry) : TIFFImageReader.guessPhotometricInterpretation(this.getCompression(), this.getSamplesPerPixelWithFallback(), this.ifd.getEntryById((Object)338), this.ifd.getEntryById((Object)320));
    }

    private int getSamplesPerPixelWithFallback() {
        Entry entry = this.ifd.getEntryById((Object)277);
        Entry entry2 = this.ifd.getEntryById((Object)258);
        return entry != null ? TIFFImageMetadata.getValueAsInt(entry) : (entry2 != null ? entry2.valueCount() : 1);
    }

    private int getCompression() {
        Entry entry = this.ifd.getEntryById((Object)259);
        return entry == null ? 1 : TIFFImageMetadata.getValueAsInt(entry);
    }

    protected IIOMetadataNode getStandardCompressionNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Compression");
        IIOMetadataNode iIOMetadataNode2 = this.addChildNode(iIOMetadataNode, "CompressionTypeName", null);
        int n = this.getCompression();
        switch (n) {
            case 1: {
                iIOMetadataNode2.setAttribute("value", "None");
                break;
            }
            case 2: {
                iIOMetadataNode2.setAttribute("value", "CCITT RLE");
                break;
            }
            case 3: {
                iIOMetadataNode2.setAttribute("value", "CCITT T4");
                break;
            }
            case 4: {
                iIOMetadataNode2.setAttribute("value", "CCITT T6");
                break;
            }
            case 5: {
                iIOMetadataNode2.setAttribute("value", "LZW");
                break;
            }
            case 6: {
                iIOMetadataNode2.setAttribute("value", "Old JPEG");
                break;
            }
            case 7: {
                iIOMetadataNode2.setAttribute("value", "JPEG");
                break;
            }
            case 8: {
                iIOMetadataNode2.setAttribute("value", "ZLib");
                break;
            }
            case 32946: {
                iIOMetadataNode2.setAttribute("value", "Deflate");
                break;
            }
            case 32773: {
                iIOMetadataNode2.setAttribute("value", "PackBits");
                break;
            }
            case 32771: {
                iIOMetadataNode2.setAttribute("value", "CCITT RLEW");
                break;
            }
            case 32947: {
                iIOMetadataNode2.setAttribute("value", "DCS");
                break;
            }
            case 32898: {
                iIOMetadataNode2.setAttribute("value", "IT8BL");
                break;
            }
            case 32895: {
                iIOMetadataNode2.setAttribute("value", "IT8CTPAD");
                break;
            }
            case 32896: {
                iIOMetadataNode2.setAttribute("value", "IT8LW");
                break;
            }
            case 32897: {
                iIOMetadataNode2.setAttribute("value", "IT8MP");
                break;
            }
            case 34661: {
                iIOMetadataNode2.setAttribute("value", "JBIG");
                break;
            }
            case 34712: {
                iIOMetadataNode2.setAttribute("value", "JPEG 2000");
                break;
            }
            case 32766: {
                iIOMetadataNode2.setAttribute("value", "NEXT");
                break;
            }
            case 32908: {
                iIOMetadataNode2.setAttribute("value", "Pixar Film");
                break;
            }
            case 32909: {
                iIOMetadataNode2.setAttribute("value", "Pixar Log");
                break;
            }
            case 34676: {
                iIOMetadataNode2.setAttribute("value", "SGI Log");
                break;
            }
            case 34677: {
                iIOMetadataNode2.setAttribute("value", "SGI Log24");
                break;
            }
            case 32809: {
                iIOMetadataNode2.setAttribute("value", "ThunderScan");
                break;
            }
            default: {
                iIOMetadataNode2.setAttribute("value", "Unknown " + n);
            }
        }
        if (n != 1) {
            IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("Lossless");
            iIOMetadataNode.appendChild(iIOMetadataNode3);
            switch (n) {
                case 6: 
                case 7: 
                case 34661: 
                case 34712: {
                    iIOMetadataNode3.setAttribute("value", "FALSE");
                    break;
                }
            }
        }
        return iIOMetadataNode;
    }

    protected IIOMetadataNode getStandardDataNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Data");
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("PlanarConfiguration");
        Entry entry = this.ifd.getEntryById((Object)284);
        int n = entry == null ? 1 : TIFFImageMetadata.getValueAsInt(entry);
        switch (n) {
            case 1: {
                iIOMetadataNode2.setAttribute("value", "PixelInterleaved");
                break;
            }
            case 2: {
                iIOMetadataNode2.setAttribute("value", "PlaneInterleaved");
                break;
            }
            default: {
                iIOMetadataNode2.setAttribute("value", "Unknown " + n);
            }
        }
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        Entry entry2 = this.ifd.getEntryById((Object)262);
        int n2 = entry2 == null ? 0 : TIFFImageMetadata.getValueAsInt(entry2);
        Entry entry3 = this.ifd.getEntryById((Object)339);
        int n3 = entry3 == null ? 1 : TIFFImageMetadata.getValueAsInt(entry3);
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("SampleFormat");
        iIOMetadataNode.appendChild(iIOMetadataNode3);
        switch (n3) {
            case 1: {
                if (n2 == 3) {
                    iIOMetadataNode3.setAttribute("value", "Index");
                    break;
                }
                iIOMetadataNode3.setAttribute("value", "UnsignedIntegral");
                break;
            }
            case 2: {
                iIOMetadataNode3.setAttribute("value", "SignedIntegral");
                break;
            }
            case 3: {
                iIOMetadataNode3.setAttribute("value", "Real");
                break;
            }
            default: {
                iIOMetadataNode3.setAttribute("value", "Unknown " + n3);
            }
        }
        Entry entry4 = this.ifd.getEntryById((Object)258);
        String string = entry4 == null ? "1" : entry4.getValueAsString().replaceAll("\\[?\\]?,?", "");
        IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("BitsPerSample");
        iIOMetadataNode.appendChild(iIOMetadataNode4);
        iIOMetadataNode4.setAttribute("value", string);
        int n4 = this.getSamplesPerPixelWithFallback();
        Entry entry5 = this.ifd.getEntryById((Object)266);
        int n5 = entry5 != null ? TIFFImageMetadata.getValueAsInt(entry5) : 1;
        IIOMetadataNode iIOMetadataNode5 = new IIOMetadataNode("SampleMSB");
        iIOMetadataNode.appendChild(iIOMetadataNode5);
        if (n5 == 1) {
            iIOMetadataNode5.setAttribute("value", this.createListValue(n4, "0"));
        } else if ("1".equals(string)) {
            iIOMetadataNode5.setAttribute("value", this.createListValue(n4, "7"));
        } else {
            iIOMetadataNode5.setAttribute("value", this.createListValue(n4, "7"));
        }
        return iIOMetadataNode;
    }

    private static int getValueAsInt(Entry entry) {
        Object object = entry.getValue();
        if (object instanceof Number) {
            return ((Number)object).intValue();
        }
        if (object instanceof short[]) {
            return ((short[])object)[0];
        }
        if (object instanceof int[]) {
            return ((int[])object)[0];
        }
        throw new IllegalArgumentException("Unsupported type: " + entry);
    }

    private String createListValue(int n, String ... stringArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(' ');
            }
            stringBuilder.append(stringArray[i % stringArray.length]);
        }
        return stringBuilder.toString();
    }

    protected IIOMetadataNode getStandardDimensionNode() {
        Entry entry;
        int n;
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Dimension");
        Entry entry2 = this.ifd.getEntryById((Object)282);
        Entry entry3 = this.ifd.getEntryById((Object)283);
        double d = 1.0 / (entry2 == null ? 72.0 : ((Number)entry2.getValue()).doubleValue());
        double d2 = 1.0 / (entry2 == null ? 72.0 : ((Number)entry3.getValue()).doubleValue());
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("PixelAspectRatio");
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        iIOMetadataNode2.setAttribute("value", String.valueOf(d / d2));
        Entry entry4 = this.ifd.getEntryById((Object)274);
        if (entry4 != null) {
            int n2 = TIFFImageMetadata.getValueAsInt(entry4);
            String string = null;
            switch (n2) {
                case 1: {
                    string = "Normal";
                    break;
                }
                case 2: {
                    string = "FlipH";
                    break;
                }
                case 3: {
                    string = "Rotate180";
                    break;
                }
                case 4: {
                    string = "FlipV";
                    break;
                }
                case 5: {
                    string = "FlipHRotate90";
                    break;
                }
                case 6: {
                    string = "Rotate270";
                    break;
                }
                case 7: {
                    string = "FlipVRotate90";
                    break;
                }
                case 8: {
                    string = "Rotate90";
                }
            }
            if (string != null) {
                IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("ImageOrientation");
                iIOMetadataNode.appendChild(iIOMetadataNode3);
                iIOMetadataNode3.setAttribute("value", string);
            }
        }
        int n3 = n = (entry = this.ifd.getEntryById((Object)296)) == null ? 2 : TIFFImageMetadata.getValueAsInt(entry);
        if (n == 3 || n == 2) {
            double d3 = n == 3 ? 10.0 : 25.4;
            IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("HorizontalPixelSize");
            iIOMetadataNode.appendChild(iIOMetadataNode4);
            iIOMetadataNode4.setAttribute("value", String.valueOf(d * d3));
            IIOMetadataNode iIOMetadataNode5 = new IIOMetadataNode("VerticalPixelSize");
            iIOMetadataNode.appendChild(iIOMetadataNode5);
            iIOMetadataNode5.setAttribute("value", String.valueOf(d2 * d3));
            Entry entry5 = this.ifd.getEntryById((Object)286);
            Entry entry6 = this.ifd.getEntryById((Object)287);
            if (entry5 != null && entry6 != null) {
                double d4 = ((Number)entry5.getValue()).doubleValue();
                double d5 = ((Number)entry6.getValue()).doubleValue();
                IIOMetadataNode iIOMetadataNode6 = new IIOMetadataNode("HorizontalPosition");
                iIOMetadataNode.appendChild(iIOMetadataNode6);
                iIOMetadataNode6.setAttribute("value", String.valueOf(d4 * d3));
                IIOMetadataNode iIOMetadataNode7 = new IIOMetadataNode("VerticalPosition");
                iIOMetadataNode.appendChild(iIOMetadataNode7);
                iIOMetadataNode7.setAttribute("value", String.valueOf(d5 * d3));
            }
        }
        return iIOMetadataNode;
    }

    protected IIOMetadataNode getStandardTransparencyNode() {
        Entry entry = this.ifd.getEntryById((Object)338);
        if (entry != null) {
            int n;
            int n2 = n = entry.getValue() instanceof Number ? TIFFImageMetadata.getValueAsInt(entry) : ((Number)Array.get(entry.getValue(), 0)).intValue();
            if (n == 1 || n == 2) {
                IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Transparency");
                IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("Alpha");
                iIOMetadataNode.appendChild(iIOMetadataNode2);
                iIOMetadataNode2.setAttribute("value", n == 1 ? "premultiplied" : "nonpremultiplied");
                return iIOMetadataNode;
            }
        }
        return null;
    }

    protected IIOMetadataNode getStandardDocumentNode() {
        IIOMetadataNode iIOMetadataNode;
        Object object;
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("Document");
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("FormatVersion");
        iIOMetadataNode2.appendChild(iIOMetadataNode3);
        iIOMetadataNode3.setAttribute("value", "6.0");
        Entry entry = this.ifd.getEntryById((Object)254);
        if (entry != null) {
            object = null;
            int n = TIFFImageMetadata.getValueAsInt(entry);
            if ((n & 4) != 0) {
                object = "TransparencyMask";
            } else if ((n & 1) != 0) {
                object = "ReducedResolution";
            } else if ((n & 2) != 0) {
                object = "SinglePage";
            }
            if (object != null) {
                iIOMetadataNode = new IIOMetadataNode("SubImageInterpretation");
                iIOMetadataNode2.appendChild(iIOMetadataNode);
                iIOMetadataNode.setAttribute("value", (String)object);
            }
        }
        if ((object = this.ifd.getEntryById((Object)306)) != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
            try {
                iIOMetadataNode = new IIOMetadataNode("ImageCreationTime");
                iIOMetadataNode2.appendChild(iIOMetadataNode);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(simpleDateFormat.parse(object.getValueAsString()));
                iIOMetadataNode.setAttribute("year", String.valueOf(calendar.get(1)));
                iIOMetadataNode.setAttribute("month", String.valueOf(calendar.get(2) + 1));
                iIOMetadataNode.setAttribute("day", String.valueOf(calendar.get(5)));
                iIOMetadataNode.setAttribute("hour", String.valueOf(calendar.get(11)));
                iIOMetadataNode.setAttribute("minute", String.valueOf(calendar.get(12)));
                iIOMetadataNode.setAttribute("second", String.valueOf(calendar.get(13)));
            }
            catch (ParseException parseException) {
                // empty catch block
            }
        }
        return iIOMetadataNode2;
    }

    protected IIOMetadataNode getStandardTextNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Text");
        this.addTextEntryIfPresent(iIOMetadataNode, 269);
        this.addTextEntryIfPresent(iIOMetadataNode, 270);
        this.addTextEntryIfPresent(iIOMetadataNode, 271);
        this.addTextEntryIfPresent(iIOMetadataNode, 272);
        this.addTextEntryIfPresent(iIOMetadataNode, 285);
        this.addTextEntryIfPresent(iIOMetadataNode, 305);
        this.addTextEntryIfPresent(iIOMetadataNode, 315);
        this.addTextEntryIfPresent(iIOMetadataNode, 316);
        this.addTextEntryIfPresent(iIOMetadataNode, 333);
        this.addTextEntryIfPresent(iIOMetadataNode, 33432);
        return iIOMetadataNode.hasChildNodes() ? iIOMetadataNode : null;
    }

    private void addTextEntryIfPresent(IIOMetadataNode iIOMetadataNode, int n) {
        Entry entry = this.ifd.getEntryById((Object)n);
        if (entry != null) {
            IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("TextEntry");
            iIOMetadataNode.appendChild(iIOMetadataNode2);
            iIOMetadataNode2.setAttribute("keyword", entry.getFieldName());
            iIOMetadataNode2.setAttribute("value", entry.getValueAsString());
        }
    }

    protected IIOMetadataNode getStandardTileNode() {
        return super.getStandardTileNode();
    }

    public boolean isReadOnly() {
        return false;
    }

    public void setFromTree(String string, Node node) throws IIOInvalidTreeException {
        super.setFromTree(string, node);
        LinkedHashMap<Integer, Entry> linkedHashMap = new LinkedHashMap<Integer, Entry>();
        this.mergeEntries(string, node, linkedHashMap);
        this.ifd = new IFD(linkedHashMap.values());
    }

    public void mergeTree(String string, Node node) throws IIOInvalidTreeException {
        super.mergeTree(string, node);
        LinkedHashMap<Integer, Entry> linkedHashMap = new LinkedHashMap<Integer, Entry>(this.ifd.size() + 10);
        for (Entry entry : this.ifd) {
            linkedHashMap.put((Integer)entry.getIdentifier(), entry);
        }
        this.mergeEntries(string, node, linkedHashMap);
        this.ifd = new IFD(linkedHashMap.values());
    }

    private void mergeEntries(String string, Node node, Map<Integer, Entry> map) throws IIOInvalidTreeException {
        if (this.getNativeMetadataFormatName().equals(string)) {
            this.mergeNativeTree(node, map);
        } else if ("javax_imageio_1.0".equals(string)) {
            this.mergeStandardTree(node, map);
        } else {
            throw new AssertionError();
        }
    }

    private void mergeStandardTree(Node node, Map<Integer, Entry> map) throws IIOInvalidTreeException {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node2 = nodeList.item(i);
            if ("Dimension".equals(node2.getNodeName())) {
                this.mergeFromStandardDimensionNode(node2, map);
                continue;
            }
            if ("Document".equals(node2.getNodeName())) {
                this.mergeFromStandardDocumentNode(node2, map);
                continue;
            }
            if (!"Text".equals(node2.getNodeName())) continue;
            this.mergeFromStandardTextNode(node2, map);
        }
    }

    private void mergeFromStandardDimensionNode(Node node, Map<Integer, Entry> map) {
        int n;
        NodeList nodeList = node.getChildNodes();
        Float f = null;
        Float f2 = null;
        Float f3 = null;
        Integer n2 = null;
        for (n = 0; n < nodeList.getLength(); ++n) {
            Node node2 = nodeList.item(n);
            String string = node2.getNodeName();
            if ("PixelAspectRatio".equals(string)) {
                f = Float.valueOf(Float.parseFloat(this.getAttribute(node2, "value")));
                continue;
            }
            if ("HorizontalPixelSize".equals(string)) {
                f2 = Float.valueOf(Float.parseFloat(this.getAttribute(node2, "value")));
                continue;
            }
            if ("VerticalPixelSize".equals(string)) {
                f3 = Float.valueOf(Float.parseFloat(this.getAttribute(node2, "value")));
                continue;
            }
            if (!"ImageOrientation".equals(string)) continue;
            n2 = this.toTIFFOrientation(this.getAttribute(node2, "value"));
        }
        if (f2 == null && f3 != null) {
            f2 = Float.valueOf(f3.floatValue() * (f != null ? f.floatValue() : 1.0f));
        } else if (f3 == null && f2 != null) {
            f3 = Float.valueOf(f2.floatValue() / (f != null ? f.floatValue() : 1.0f));
        }
        if (f2 != null && f3 != null) {
            Entry entry = map.get(296);
            int n3 = entry != null && entry.getValue() != null && ((Number)entry.getValue()).intValue() == 2 ? 2 : 3;
            float f4 = n3 == 3 ? 10.0f : 25.4f;
            int n4 = Math.round(f2.floatValue() * f4 * 100000.0f);
            int n5 = Math.round(f3.floatValue() * f4 * 100000.0f);
            map.put(282, (Entry)new TIFFEntry(282, (Object)new Rational((long)n4, 100000L)));
            map.put(283, (Entry)new TIFFEntry(283, (Object)new Rational((long)n5, 100000L)));
            map.put(296, (Entry)new TIFFEntry(296, 3, (Object)n3));
        } else if (f != null) {
            if (f.floatValue() >= 1.0f) {
                n = Math.round(f.floatValue() * 100000.0f);
                map.put(282, (Entry)new TIFFEntry(282, (Object)new Rational((long)n, 100000L)));
                map.put(283, (Entry)new TIFFEntry(283, (Object)new Rational(1L)));
            } else {
                n = Math.round(100000.0f / f.floatValue());
                map.put(282, (Entry)new TIFFEntry(282, (Object)new Rational(1L)));
                map.put(283, (Entry)new TIFFEntry(283, (Object)new Rational((long)n, 100000L)));
            }
            map.put(296, (Entry)new TIFFEntry(296, 3, (Object)1));
        }
        if (n2 != null) {
            map.put(274, (Entry)new TIFFEntry(274, 3, (Object)n2.shortValue()));
        }
    }

    private void mergeFromStandardDocumentNode(Node node, Map<Integer, Entry> map) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node2 = nodeList.item(i);
            String string = node2.getNodeName();
            if (!"SubimageInterpretation".equals(string) && !"ImageCreationTime".equals(string)) continue;
        }
    }

    private void mergeFromStandardTextNode(Node node, Map<Integer, Entry> map) throws IIOInvalidTreeException {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            TIFFEntry tIFFEntry;
            Node node2 = nodeList.item(i);
            if (!"TextEntry".equals(node2.getNodeName())) {
                throw new IIOInvalidTreeException("Text node should only contain TextEntry nodes", node);
            }
            String string = this.getAttribute(node2, "keyword");
            String string2 = this.getAttribute(node2, "value");
            if (string2 == null || string2.isEmpty() || string == null) continue;
            if ("documentname".equals(string = string.toLowerCase())) {
                tIFFEntry = new TIFFEntry(269, 2, (Object)string2);
            } else if ("imagedescription".equals(string)) {
                tIFFEntry = new TIFFEntry(270, 2, (Object)string2);
            } else if ("make".equals(string)) {
                tIFFEntry = new TIFFEntry(271, 2, (Object)string2);
            } else if ("model".equals(string)) {
                tIFFEntry = new TIFFEntry(272, 2, (Object)string2);
            } else if ("pagename".equals(string)) {
                tIFFEntry = new TIFFEntry(285, 2, (Object)string2);
            } else if ("software".equals(string)) {
                tIFFEntry = new TIFFEntry(305, 2, (Object)string2);
            } else if ("artist".equals(string)) {
                tIFFEntry = new TIFFEntry(315, 2, (Object)string2);
            } else if ("hostcomputer".equals(string)) {
                tIFFEntry = new TIFFEntry(316, 2, (Object)string2);
            } else if ("inknames".equals(string)) {
                tIFFEntry = new TIFFEntry(333, 2, (Object)string2);
            } else {
                if (!"copyright".equals(string)) continue;
                tIFFEntry = new TIFFEntry(33432, 2, (Object)string2);
            }
            map.put((Integer)tIFFEntry.getIdentifier(), (Entry)tIFFEntry);
        }
    }

    private void mergeNativeTree(Node node, Map<Integer, Entry> map) throws IIOInvalidTreeException {
        Directory directory = this.toIFD(node.getFirstChild());
        for (Entry entry : directory) {
            map.put((Integer)entry.getIdentifier(), entry);
        }
    }

    private Directory toIFD(Node node) throws IIOInvalidTreeException {
        if (node == null || !node.getNodeName().equals("TIFFIFD")) {
            throw new IIOInvalidTreeException("Expected \"TIFFIFD\" node", node);
        }
        NodeList nodeList = node.getChildNodes();
        int n = nodeList.getLength();
        ArrayList<Entry> arrayList = new ArrayList<Entry>(n);
        for (int i = 0; i < n; ++i) {
            arrayList.add(this.toEntry(nodeList.item(i)));
        }
        return new IFD(arrayList);
    }

    private Entry toEntry(Node node) throws IIOInvalidTreeException {
        String string = node.getNodeName();
        if (string.equals("TIFFIFD")) {
            int n = Integer.parseInt(this.getAttribute(node, "parentTagNumber"));
            Directory directory = this.toIFD(node);
            return new TIFFEntry(n, 13, (Object)directory);
        }
        if (string.equals("TIFFField")) {
            int n = Integer.parseInt(this.getAttribute(node, "number"));
            short s = this.getTIFFType(node);
            Object object = this.getValue(node, s);
            return object != null ? new TIFFEntry(n, s, object) : null;
        }
        throw new IIOInvalidTreeException("Expected \"TIFFIFD\" or \"TIFFField\" node: " + string, node);
    }

    private Integer toTIFFOrientation(String string) {
        if (string == null) {
            return null;
        }
        switch (string.toLowerCase()) {
            case "normal": {
                return 1;
            }
            case "fliph": {
                return 2;
            }
            case "rotate180": {
                return 3;
            }
            case "flipv": {
                return 4;
            }
            case "fliphrotate90": {
                return 5;
            }
            case "rotate270": {
                return 6;
            }
            case "flipvrotate90": {
                return 7;
            }
            case "rotate90": {
                return 8;
            }
        }
        return null;
    }

    private short getTIFFType(Node node) throws IIOInvalidTreeException {
        Node node2 = node.getFirstChild();
        if (node2 == null) {
            throw new IIOInvalidTreeException("Missing value wrapper node", node);
        }
        String string = node2.getNodeName();
        if (!string.startsWith("TIFF")) {
            throw new IIOInvalidTreeException("Unexpected value wrapper node, expected type", node2);
        }
        String string2 = string.substring(4);
        if (string2.equals("Undefined")) {
            return 7;
        }
        string2 = string2.substring(0, string2.length() - 1).toUpperCase();
        for (int i = 1; i < TIFF.TYPE_NAMES.length; ++i) {
            if (!string2.equals(TIFF.TYPE_NAMES[i])) continue;
            return (short)i;
        }
        throw new IIOInvalidTreeException("Unknown TIFF type: " + string2, node2);
    }

    private Object getValue(Node node, short s) throws IIOInvalidTreeException {
        Node node2 = node.getFirstChild();
        if (node2 != null) {
            String string = node2.getNodeName();
            if (s == 7) {
                String string2 = this.getAttribute(node2, "value");
                String[] stringArray = string2.split(",\\s?");
                byte[] byArray = new byte[stringArray.length];
                for (int i = 0; i < stringArray.length; ++i) {
                    byArray[i] = Byte.parseByte(stringArray[i]);
                }
                return byArray;
            }
            NodeList nodeList = node2.getChildNodes();
            int n = nodeList.getLength();
            Object object = this.createArrayForType(s, n);
            block10: for (int i = 0; i < n; ++i) {
                Node node3 = nodeList.item(i);
                if (!string.startsWith(node3.getNodeName())) {
                    throw new IIOInvalidTreeException("Value node does not match container node", node2);
                }
                String string3 = this.getAttribute(node3, "value");
                switch (s) {
                    case 1: 
                    case 6: {
                        ((byte[])object)[i] = (byte)Short.parseShort(string3);
                        continue block10;
                    }
                    case 2: {
                        ((String[])object)[i] = string3;
                        continue block10;
                    }
                    case 3: 
                    case 8: {
                        ((short[])object)[i] = (short)Integer.parseInt(string3);
                        continue block10;
                    }
                    case 4: 
                    case 9: {
                        ((int[])object)[i] = (int)Long.parseLong(string3);
                        continue block10;
                    }
                    case 5: 
                    case 10: {
                        String[] stringArray = string3.split("/");
                        ((Rational[])object)[i] = stringArray.length > 1 ? new Rational(Long.parseLong(stringArray[0]), Long.parseLong(stringArray[1])) : new Rational(Long.parseLong(stringArray[0]));
                        continue block10;
                    }
                    case 11: {
                        ((float[])object)[i] = Float.parseFloat(string3);
                        continue block10;
                    }
                    case 12: {
                        ((double[])object)[i] = Double.parseDouble(string3);
                        continue block10;
                    }
                    default: {
                        throw new AssertionError((Object)("Unsupported TIFF type: " + s));
                    }
                }
            }
            if (n == 0) {
                return null;
            }
            if (n == 1) {
                return Array.get(object, 0);
            }
            return object;
        }
        throw new IIOInvalidTreeException("Empty TIFField node", node);
    }

    private Object createArrayForType(short s, int n) {
        switch (s) {
            case 2: {
                return new String[n];
            }
            case 1: 
            case 6: 
            case 7: {
                return new byte[n];
            }
            case 3: 
            case 8: {
                return new short[n];
            }
            case 4: 
            case 9: {
                return new int[n];
            }
            case 13: {
                return new long[n];
            }
            case 5: 
            case 10: {
                return new Rational[n];
            }
            case 11: {
                return new float[n];
            }
            case 12: {
                return new double[n];
            }
        }
        throw new AssertionError((Object)("Unsupported TIFF type: " + s));
    }

    private String getAttribute(Node node, String string) {
        return node instanceof Element ? ((Element)node).getAttribute(string) : null;
    }

    public void reset() {
        super.reset();
        this.ifd = this.original;
    }

    Directory getIFD() {
        return this.ifd;
    }

    public Entry getTIFFField(int n) {
        return this.ifd.getEntryById((Object)n);
    }
}

