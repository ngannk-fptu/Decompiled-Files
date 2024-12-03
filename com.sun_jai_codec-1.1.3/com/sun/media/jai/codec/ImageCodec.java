/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.media.jai.util.SimpleCMYKColorSpace
 */
package com.sun.media.jai.codec;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JaiI18N;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.BMPCodec;
import com.sun.media.jai.codecimpl.FPXCodec;
import com.sun.media.jai.codecimpl.GIFCodec;
import com.sun.media.jai.codecimpl.ImagingListenerProxy;
import com.sun.media.jai.codecimpl.JPEGCodec;
import com.sun.media.jai.codecimpl.PNGCodec;
import com.sun.media.jai.codecimpl.PNMCodec;
import com.sun.media.jai.codecimpl.TIFFCodec;
import com.sun.media.jai.codecimpl.WBMPCodec;
import com.sun.media.jai.codecimpl.util.FloatDoubleColorModel;
import com.sun.media.jai.util.SimpleCMYKColorSpace;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public abstract class ImageCodec {
    private static Hashtable codecs = new Hashtable();
    private static final byte[][] grayIndexCmaps;
    private static final int[] GrayBits8;
    private static final ComponentColorModel colorModelGray8;
    private static final int[] GrayAlphaBits8;
    private static final ComponentColorModel colorModelGrayAlpha8;
    private static final int[] GrayBits16;
    private static final ComponentColorModel colorModelGray16;
    private static final int[] GrayAlphaBits16;
    private static final ComponentColorModel colorModelGrayAlpha16;
    private static final int[] GrayBits32;
    private static final ComponentColorModel colorModelGray32;
    private static final int[] GrayAlphaBits32;
    private static final ComponentColorModel colorModelGrayAlpha32;
    private static final int[] RGBBits8;
    private static final ComponentColorModel colorModelRGB8;
    private static final int[] RGBABits8;
    private static final ComponentColorModel colorModelRGBA8;
    private static final int[] RGBBits16;
    private static final ComponentColorModel colorModelRGB16;
    private static final int[] RGBABits16;
    private static final ComponentColorModel colorModelRGBA16;
    private static final int[] RGBBits32;
    private static final ComponentColorModel colorModelRGB32;
    private static final int[] RGBABits32;
    private static final ComponentColorModel colorModelRGBA32;
    static /* synthetic */ Class class$com$sun$media$jai$codec$ImageCodec;

    protected ImageCodec() {
    }

    public static ImageCodec getCodec(String name) {
        return (ImageCodec)codecs.get(name.toLowerCase());
    }

    public static void registerCodec(ImageCodec codec) {
        codecs.put(codec.getFormatName().toLowerCase(), codec);
    }

    public static void unregisterCodec(String name) {
        codecs.remove(name.toLowerCase());
    }

    public static Enumeration getCodecs() {
        return codecs.elements();
    }

    public static ImageEncoder createImageEncoder(String name, OutputStream dst, ImageEncodeParam param) {
        ImageCodec codec = ImageCodec.getCodec(name);
        if (codec == null) {
            return null;
        }
        return codec.createImageEncoder(dst, param);
    }

    public static ImageDecoder createImageDecoder(String name, InputStream src, ImageDecodeParam param) {
        ImageCodec codec = ImageCodec.getCodec(name);
        if (codec == null) {
            return null;
        }
        return codec.createImageDecoder(src, param);
    }

    public static ImageDecoder createImageDecoder(String name, File src, ImageDecodeParam param) throws IOException {
        ImageCodec codec = ImageCodec.getCodec(name);
        if (codec == null) {
            return null;
        }
        return codec.createImageDecoder(src, param);
    }

    public static ImageDecoder createImageDecoder(String name, SeekableStream src, ImageDecodeParam param) {
        ImageCodec codec = ImageCodec.getCodec(name);
        if (codec == null) {
            return null;
        }
        return codec.createImageDecoder(src, param);
    }

    private static String[] vectorToStrings(Vector nameVec) {
        int count = nameVec.size();
        String[] names = new String[count];
        for (int i = 0; i < count; ++i) {
            names[i] = (String)nameVec.elementAt(i);
        }
        return names;
    }

    public static String[] getDecoderNames(SeekableStream src) {
        if (!src.canSeekBackwards() && !src.markSupported()) {
            throw new IllegalArgumentException(JaiI18N.getString("ImageCodec2"));
        }
        Enumeration enumeration = codecs.elements();
        Vector<String> nameVec = new Vector<String>();
        Object opName = null;
        while (enumeration.hasMoreElements()) {
            ImageCodec codec = (ImageCodec)enumeration.nextElement();
            int bytesNeeded = codec.getNumHeaderBytes();
            if (bytesNeeded == 0 && !src.canSeekBackwards()) continue;
            try {
                if (bytesNeeded > 0) {
                    src.mark(bytesNeeded);
                    byte[] header = new byte[bytesNeeded];
                    src.readFully(header);
                    src.reset();
                    if (!codec.isFormatRecognized(header)) continue;
                    nameVec.add(codec.getFormatName());
                    continue;
                }
                long pointer = src.getFilePointer();
                src.seek(0L);
                if (codec.isFormatRecognized(src)) {
                    nameVec.add(codec.getFormatName());
                }
                src.seek(pointer);
            }
            catch (IOException e) {
                ImagingListenerProxy.errorOccurred(JaiI18N.getString("ImageCodec3"), e, class$com$sun$media$jai$codec$ImageCodec == null ? ImageCodec.class$("com.sun.media.jai.codec.ImageCodec") : class$com$sun$media$jai$codec$ImageCodec, false);
            }
        }
        return ImageCodec.vectorToStrings(nameVec);
    }

    public static String[] getEncoderNames(RenderedImage im, ImageEncodeParam param) {
        Enumeration enumeration = codecs.elements();
        Vector<String> nameVec = new Vector<String>();
        Object opName = null;
        while (enumeration.hasMoreElements()) {
            ImageCodec codec = (ImageCodec)enumeration.nextElement();
            if (!codec.canEncodeImage(im, param)) continue;
            nameVec.add(codec.getFormatName());
        }
        return ImageCodec.vectorToStrings(nameVec);
    }

    public abstract String getFormatName();

    public int getNumHeaderBytes() {
        return 0;
    }

    public boolean isFormatRecognized(byte[] header) {
        throw new RuntimeException(JaiI18N.getString("ImageCodec0"));
    }

    public boolean isFormatRecognized(SeekableStream src) throws IOException {
        throw new RuntimeException(JaiI18N.getString("ImageCodec1"));
    }

    protected abstract Class getEncodeParamClass();

    protected abstract Class getDecodeParamClass();

    protected abstract ImageEncoder createImageEncoder(OutputStream var1, ImageEncodeParam var2);

    public abstract boolean canEncodeImage(RenderedImage var1, ImageEncodeParam var2);

    protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param) {
        SeekableStream stream = SeekableStream.wrapInputStream(src, true);
        return this.createImageDecoder(stream, param);
    }

    protected ImageDecoder createImageDecoder(File src, ImageDecodeParam param) throws IOException {
        return this.createImageDecoder(new FileSeekableStream(src), param);
    }

    protected abstract ImageDecoder createImageDecoder(SeekableStream var1, ImageDecodeParam var2);

    public static ColorModel createGrayIndexColorModel(SampleModel sm, boolean blackIsZero) {
        byte[] cmap;
        int sampleSize;
        block7: {
            block6: {
                if (sm.getNumBands() != 1) {
                    throw new IllegalArgumentException();
                }
                sampleSize = sm.getSampleSize(0);
                cmap = null;
                if (sampleSize >= 8) break block6;
                cmap = grayIndexCmaps[sampleSize];
                if (blackIsZero) break block7;
                int length = cmap.length;
                byte[] newCmap = new byte[length];
                for (int i = 0; i < length; ++i) {
                    newCmap[i] = cmap[length - i - 1];
                }
                cmap = newCmap;
                break block7;
            }
            cmap = new byte[256];
            if (blackIsZero) {
                for (int i = 0; i < 256; ++i) {
                    cmap[i] = (byte)i;
                }
            } else {
                for (int i = 0; i < 256; ++i) {
                    cmap[i] = (byte)(255 - i);
                }
            }
        }
        return new IndexColorModel(sampleSize, cmap.length, cmap, cmap, cmap);
    }

    public static ColorModel createComponentColorModel(SampleModel sm) {
        int type = sm.getDataType();
        int bands = sm.getNumBands();
        ComponentColorModel cm = null;
        if (type == 0) {
            switch (bands) {
                case 1: {
                    cm = colorModelGray8;
                    break;
                }
                case 2: {
                    cm = colorModelGrayAlpha8;
                    break;
                }
                case 3: {
                    cm = colorModelRGB8;
                    break;
                }
                case 4: {
                    cm = colorModelRGBA8;
                }
            }
        } else if (type == 1) {
            switch (bands) {
                case 1: {
                    cm = colorModelGray16;
                    break;
                }
                case 2: {
                    cm = colorModelGrayAlpha16;
                    break;
                }
                case 3: {
                    cm = colorModelRGB16;
                    break;
                }
                case 4: {
                    cm = colorModelRGBA16;
                }
            }
        } else if (type == 3) {
            switch (bands) {
                case 1: {
                    cm = colorModelGray32;
                    break;
                }
                case 2: {
                    cm = colorModelGrayAlpha32;
                    break;
                }
                case 3: {
                    cm = colorModelRGB32;
                    break;
                }
                case 4: {
                    cm = colorModelRGBA32;
                }
            }
        } else if (type == 4 && bands >= 1 && bands <= 4) {
            ColorSpace cs = bands <= 2 ? ColorSpace.getInstance(1003) : ColorSpace.getInstance(1000);
            boolean hasAlpha = bands % 2 == 0;
            cm = new FloatDoubleColorModel(cs, hasAlpha, false, hasAlpha ? 3 : 1, 4);
        }
        return cm;
    }

    public static ColorModel createComponentColorModel(SampleModel sm, ColorSpace cp) {
        int transparency;
        boolean hasAlpha;
        if (cp == null) {
            return ImageCodec.createComponentColorModel(sm);
        }
        int type = sm.getDataType();
        int bands = sm.getNumBands();
        ComponentColorModel cm = null;
        int[] bits = null;
        int transferType = -1;
        boolean bl = hasAlpha = bands % 2 == 0;
        if (cp instanceof SimpleCMYKColorSpace) {
            hasAlpha = false;
        }
        int n = transparency = hasAlpha ? 3 : 1;
        if (type == 0) {
            transferType = 0;
            switch (bands) {
                case 1: {
                    bits = GrayBits8;
                    break;
                }
                case 2: {
                    bits = GrayAlphaBits8;
                    break;
                }
                case 3: {
                    bits = RGBBits8;
                    break;
                }
                case 4: {
                    bits = RGBABits8;
                }
            }
        } else if (type == 1) {
            transferType = 1;
            switch (bands) {
                case 1: {
                    bits = GrayBits16;
                    break;
                }
                case 2: {
                    bits = GrayAlphaBits16;
                    break;
                }
                case 3: {
                    bits = RGBBits16;
                    break;
                }
                case 4: {
                    bits = RGBABits16;
                }
            }
        } else if (type == 3) {
            transferType = 3;
            switch (bands) {
                case 1: {
                    bits = GrayBits32;
                    break;
                }
                case 2: {
                    bits = GrayAlphaBits32;
                    break;
                }
                case 3: {
                    bits = RGBBits32;
                    break;
                }
                case 4: {
                    bits = RGBABits32;
                }
            }
        }
        cm = type == 4 && bands >= 1 && bands <= 4 ? new FloatDoubleColorModel(cp, hasAlpha, false, transparency, 4) : new ComponentColorModel(cp, bits, hasAlpha, false, transparency, transferType);
        return cm;
    }

    public static boolean isIndicesForGrayscale(byte[] r, byte[] g, byte[] b) {
        if (r.length != g.length || r.length != b.length) {
            return false;
        }
        int size = r.length;
        if (size != 256) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            byte temp = (byte)i;
            if (r[i] == temp && g[i] == temp && b[i] == temp) continue;
            return false;
        }
        return true;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        ImageCodec.registerCodec(new BMPCodec());
        ImageCodec.registerCodec(new GIFCodec());
        ImageCodec.registerCodec(new FPXCodec());
        ImageCodec.registerCodec(new JPEGCodec());
        ImageCodec.registerCodec(new PNGCodec());
        ImageCodec.registerCodec(new PNMCodec());
        ImageCodec.registerCodec(new TIFFCodec());
        ImageCodec.registerCodec(new WBMPCodec());
        grayIndexCmaps = new byte[][]{null, {0, -1}, {0, 85, -86, -1}, null, {0, 17, 34, 51, 68, 85, 102, 119, -120, -103, -86, -69, -52, -35, -18, -1}};
        GrayBits8 = new int[]{8};
        colorModelGray8 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayBits8, false, false, 1, 0);
        GrayAlphaBits8 = new int[]{8, 8};
        colorModelGrayAlpha8 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayAlphaBits8, true, false, 3, 0);
        GrayBits16 = new int[]{16};
        colorModelGray16 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayBits16, false, false, 1, 1);
        GrayAlphaBits16 = new int[]{16, 16};
        colorModelGrayAlpha16 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayAlphaBits16, true, false, 3, 1);
        GrayBits32 = new int[]{32};
        colorModelGray32 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayBits32, false, false, 1, 3);
        GrayAlphaBits32 = new int[]{32, 32};
        colorModelGrayAlpha32 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayAlphaBits32, true, false, 3, 3);
        RGBBits8 = new int[]{8, 8, 8};
        colorModelRGB8 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBBits8, false, false, 1, 0);
        RGBABits8 = new int[]{8, 8, 8, 8};
        colorModelRGBA8 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBABits8, true, false, 3, 0);
        RGBBits16 = new int[]{16, 16, 16};
        colorModelRGB16 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBBits16, false, false, 1, 1);
        RGBABits16 = new int[]{16, 16, 16, 16};
        colorModelRGBA16 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBABits16, true, false, 3, 1);
        RGBBits32 = new int[]{32, 32, 32};
        colorModelRGB32 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBBits32, false, false, 1, 3);
        RGBABits32 = new int[]{32, 32, 32, 32};
        colorModelRGBA32 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBABits32, true, false, 3, 3);
    }
}

