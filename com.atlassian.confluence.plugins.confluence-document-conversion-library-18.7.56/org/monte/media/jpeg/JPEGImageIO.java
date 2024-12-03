/*
 * Decompiled with CFR 0.152.
 */
package org.monte.media.jpeg;

import com.atlassian.pdfview.colorspace.DefaultICCProfile;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.monte.media.jpeg.JFIFInputStream;

public class JPEGImageIO {
    private static final int SCALEBITS = 16;
    private static final int MAXJSAMPLE = 255;
    private static final int CENTERJSAMPLE = 128;
    private static final int ONE_HALF = 32768;
    private static final int[] Cr_r_tab = new int[256];
    private static final int[] Cb_b_tab = new int[256];
    private static final int[] Cr_g_tab = new int[256];
    private static final int[] Cb_g_tab = new int[256];

    private JPEGImageIO() {
    }

    public static BufferedImage read(InputStream in) throws IOException {
        return JPEGImageIO.read(in, true);
    }

    public static BufferedImage read(InputStream in, boolean inverseYCCKColors) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] b = new byte[512];
        int count = in.read(b);
        while (count != -1) {
            buf.write(b, 0, count);
            count = in.read(b);
        }
        byte[] byteArray = buf.toByteArray();
        int samplePrecision = 0;
        int numberOfLines = 0;
        int numberOfSamplesPerLine = 0;
        int numberOfComponentsInFrame = 0;
        int app14AdobeColorTransform = 0;
        ByteArrayOutputStream app2ICCProfile = new ByteArrayOutputStream();
        JFIFInputStream fifi = new JFIFInputStream(new ByteArrayInputStream(byteArray));
        JFIFInputStream.Segment seg = fifi.getNextSegment();
        while (seg != null) {
            DataInputStream dis;
            if (65472 <= seg.marker && seg.marker <= 65475 || 65477 <= seg.marker && seg.marker <= 65479 || 65481 <= seg.marker && seg.marker <= 65483 || 65485 <= seg.marker && seg.marker <= 65487) {
                dis = new DataInputStream(fifi);
                samplePrecision = dis.readUnsignedByte();
                numberOfLines = dis.readUnsignedShort();
                numberOfSamplesPerLine = dis.readUnsignedShort();
                numberOfComponentsInFrame = dis.readUnsignedByte();
                break;
            }
            if (seg.marker == 65506) {
                if (seg.length >= 26 && (dis = new DataInputStream(fifi)).readLong() == 5279137264856878918L && dis.readInt() == 1229735168) {
                    dis.skipBytes(2);
                    int count2 = dis.read(b);
                    while (count2 != -1) {
                        app2ICCProfile.write(b, 0, count2);
                        count2 = dis.read(b);
                    }
                }
            } else if (seg.marker == 65518 && seg.length == 12 && (long)(dis = new DataInputStream(fifi)).readInt() == 1097101154L && dis.readUnsignedShort() == 25856) {
                int version = dis.readUnsignedByte();
                int app14Flags0 = dis.readUnsignedShort();
                int app14Flags1 = dis.readUnsignedShort();
                app14AdobeColorTransform = dis.readUnsignedByte();
            }
            seg = fifi.getNextSegment();
        }
        BufferedImage img = null;
        if (numberOfComponentsInFrame != 4) {
            img = JPEGImageIO.readImageFromYUVorGray(new ByteArrayInputStream(byteArray));
        } else if (numberOfComponentsInFrame == 4) {
            ICC_Profile profile = null;
            if (app2ICCProfile.size() > 0) {
                try {
                    profile = ICC_Profile.getInstance(new ByteArrayInputStream(app2ICCProfile.toByteArray()));
                }
                catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
            if (profile == null) {
                profile = DefaultICCProfile.getDefaultIccProfile();
            }
            switch (app14AdobeColorTransform) {
                default: {
                    img = JPEGImageIO.readRGBImageFromCMYK(new ByteArrayInputStream(byteArray), profile);
                    break;
                }
                case 1: {
                    throw new IOException("YCbCr not supported");
                }
                case 2: {
                    img = inverseYCCKColors ? JPEGImageIO.readRGBImageFromInvertedYCCK(new ByteArrayInputStream(byteArray), profile) : JPEGImageIO.readRGBImageFromYCCK(new ByteArrayInputStream(byteArray), profile);
                }
            }
        }
        return img;
    }

    public static BufferedImage readImageFromYUVorGray(InputStream in) throws IOException {
        BufferedImage img = in instanceof ImageInputStream ? ImageIO.read((ImageInputStream)((Object)in)) : ImageIO.read(in);
        return img;
    }

    public static BufferedImage readRGBImageFromCMYK(InputStream in, ICC_Profile cmykProfile) throws IOException {
        ImageInputStream inputStream = null;
        ImageReader reader = ImageIO.getImageReadersByFormatName("JPEG").next();
        inputStream = in instanceof ImageInputStream ? (ImageInputStream)((Object)in) : ImageIO.createImageInputStream(in);
        reader.setInput(inputStream);
        Raster raster = reader.readRaster(0, null);
        BufferedImage image = JPEGImageIO.createRGBImageFromCMYK(raster, cmykProfile);
        return image;
    }

    public static BufferedImage readRGBImageFromYCCK(InputStream in, ICC_Profile cmykProfile) throws IOException {
        ImageInputStream inputStream = null;
        ImageReader reader = ImageIO.getImageReadersByFormatName("JPEG").next();
        inputStream = in instanceof ImageInputStream ? (ImageInputStream)((Object)in) : ImageIO.createImageInputStream(in);
        reader.setInput(inputStream);
        Raster raster = reader.readRaster(0, null);
        BufferedImage image = JPEGImageIO.createRGBImageFromYCCK(raster, cmykProfile);
        return image;
    }

    public static BufferedImage readRGBImageFromInvertedYCCK(InputStream in, ICC_Profile cmykProfile) throws IOException {
        ImageInputStream inputStream = null;
        ImageReader reader = ImageIO.getImageReadersByFormatName("JPEG").next();
        inputStream = in instanceof ImageInputStream ? (ImageInputStream)((Object)in) : ImageIO.createImageInputStream(in);
        reader.setInput(inputStream);
        Raster raster = reader.readRaster(0, null);
        raster = JPEGImageIO.convertInvertedYCCKToCMYK(raster);
        BufferedImage image = JPEGImageIO.createRGBImageFromCMYK(raster, cmykProfile);
        return image;
    }

    public static BufferedImage createRGBImageFromYCCK(Raster ycckRaster, ICC_Profile cmykProfile) {
        BufferedImage image;
        if (cmykProfile != null) {
            ycckRaster = JPEGImageIO.convertYCCKtoCMYK(ycckRaster);
            image = JPEGImageIO.createRGBImageFromCMYK(ycckRaster, cmykProfile);
        } else {
            int w = ycckRaster.getWidth();
            int h = ycckRaster.getHeight();
            int[] rgb = new int[w * h];
            int[] Y = ycckRaster.getSamples(0, 0, w, h, 0, (int[])null);
            int[] Cb = ycckRaster.getSamples(0, 0, w, h, 1, (int[])null);
            int[] Cr = ycckRaster.getSamples(0, 0, w, h, 2, (int[])null);
            int[] K = ycckRaster.getSamples(0, 0, w, h, 3, (int[])null);
            int imax = Y.length;
            for (int i = 0; i < imax; ++i) {
                float k = K[i];
                float y = Y[i];
                float cb = Cb[i];
                float cr = Cr[i];
                float vr = y + 1.402f * (cr - 128.0f) - k;
                float vg = y - 0.34414f * (cb - 128.0f) - 0.71414f * (cr - 128.0f) - k;
                float vb = y + 1.772f * (cb - 128.0f) - k;
                rgb[i] = (0xFF & (vr < 0.0f ? 0 : (vr > 255.0f ? 255 : (int)(vr + 0.5f)))) << 16 | (0xFF & (vg < 0.0f ? 0 : (vg > 255.0f ? 255 : (int)(vg + 0.5f)))) << 8 | 0xFF & (vb < 0.0f ? 0 : (vb > 255.0f ? 255 : (int)(vb + 0.5f)));
            }
            WritableRaster rgbRaster = Raster.createPackedRaster(new DataBufferInt(rgb, rgb.length), w, h, w, new int[]{0xFF0000, 65280, 255}, null);
            ColorSpace cs = ColorSpace.getInstance(1000);
            DirectColorModel cm = new DirectColorModel(cs, 24, 0xFF0000, 65280, 255, 0, false, 3);
            image = new BufferedImage(cm, rgbRaster, true, null);
        }
        return image;
    }

    public static BufferedImage createRGBImageFromInvertedYCCK(Raster ycckRaster, ICC_Profile cmykProfile) {
        BufferedImage image;
        if (cmykProfile != null) {
            ycckRaster = JPEGImageIO.convertInvertedYCCKToCMYK(ycckRaster);
            image = JPEGImageIO.createRGBImageFromCMYK(ycckRaster, cmykProfile);
        } else {
            int w = ycckRaster.getWidth();
            int h = ycckRaster.getHeight();
            int[] rgb = new int[w * h];
            int[] Y = ycckRaster.getSamples(0, 0, w, h, 0, (int[])null);
            int[] Cb = ycckRaster.getSamples(0, 0, w, h, 1, (int[])null);
            int[] Cr = ycckRaster.getSamples(0, 0, w, h, 2, (int[])null);
            int[] K = ycckRaster.getSamples(0, 0, w, h, 3, (int[])null);
            int imax = Y.length;
            for (int i = 0; i < imax; ++i) {
                float k = 255 - K[i];
                float y = 255 - Y[i];
                float cb = 255 - Cb[i];
                float cr = 255 - Cr[i];
                float vr = y + 1.402f * (cr - 128.0f) - k;
                float vg = y - 0.34414f * (cb - 128.0f) - 0.71414f * (cr - 128.0f) - k;
                float vb = y + 1.772f * (cb - 128.0f) - k;
                rgb[i] = (0xFF & (vr < 0.0f ? 0 : (vr > 255.0f ? 255 : (int)(vr + 0.5f)))) << 16 | (0xFF & (vg < 0.0f ? 0 : (vg > 255.0f ? 255 : (int)(vg + 0.5f)))) << 8 | 0xFF & (vb < 0.0f ? 0 : (vb > 255.0f ? 255 : (int)(vb + 0.5f)));
            }
            WritableRaster rgbRaster = Raster.createPackedRaster(new DataBufferInt(rgb, rgb.length), w, h, w, new int[]{0xFF0000, 65280, 255}, null);
            ColorSpace cs = ColorSpace.getInstance(1000);
            DirectColorModel cm = new DirectColorModel(cs, 24, 0xFF0000, 65280, 255, 0, false, 3);
            image = new BufferedImage(cm, rgbRaster, true, null);
        }
        return image;
    }

    public static BufferedImage createRGBImageFromCMYK(Raster cmykRaster, ICC_Profile cmykProfile) {
        BufferedImage image;
        int w = cmykRaster.getWidth();
        int h = cmykRaster.getHeight();
        if (cmykProfile != null) {
            ICC_ColorSpace cmykCS = new ICC_ColorSpace(cmykProfile);
            image = new BufferedImage(w, h, 1);
            WritableRaster rgbRaster = image.getRaster();
            ColorSpace rgbCS = image.getColorModel().getColorSpace();
            ColorConvertOp cmykToRgb = new ColorConvertOp(cmykCS, rgbCS, null);
            cmykToRgb.filter(cmykRaster, rgbRaster);
        } else {
            int[] rgb = new int[w * h];
            int[] C = cmykRaster.getSamples(0, 0, w, h, 0, (int[])null);
            int[] M = cmykRaster.getSamples(0, 0, w, h, 1, (int[])null);
            int[] Y = cmykRaster.getSamples(0, 0, w, h, 2, (int[])null);
            int[] K = cmykRaster.getSamples(0, 0, w, h, 3, (int[])null);
            int imax = C.length;
            for (int i = 0; i < imax; ++i) {
                int k = K[i];
                rgb[i] = 255 - Math.min(255, C[i] + k) << 16 | 255 - Math.min(255, M[i] + k) << 8 | 255 - Math.min(255, Y[i] + k);
            }
            WritableRaster rgbRaster = Raster.createPackedRaster(new DataBufferInt(rgb, rgb.length), w, h, w, new int[]{0xFF0000, 65280, 255}, null);
            ColorSpace cs = ColorSpace.getInstance(1000);
            DirectColorModel cm = new DirectColorModel(cs, 24, 0xFF0000, 65280, 255, 0, false, 3);
            image = new BufferedImage(cm, rgbRaster, true, null);
        }
        return image;
    }

    private static synchronized void buildYCCtoRGBtable() {
        if (Cr_r_tab[0] == 0) {
            int i = 0;
            int x = -128;
            while (i <= 255) {
                JPEGImageIO.Cr_r_tab[i] = (int)(91881.972 * (double)x + 32768.0) >> 16;
                JPEGImageIO.Cb_b_tab[i] = (int)(116130.292 * (double)x + 32768.0) >> 16;
                JPEGImageIO.Cr_g_tab[i] = -46802 * x;
                JPEGImageIO.Cb_g_tab[i] = -22554 * x + 32768;
                ++i;
                ++x;
            }
        }
    }

    private static Raster convertInvertedYCCKToCMYK(Raster ycckRaster) {
        JPEGImageIO.buildYCCtoRGBtable();
        int w = ycckRaster.getWidth();
        int h = ycckRaster.getHeight();
        int[] ycckY = ycckRaster.getSamples(0, 0, w, h, 0, (int[])null);
        int[] ycckCb = ycckRaster.getSamples(0, 0, w, h, 1, (int[])null);
        int[] ycckCr = ycckRaster.getSamples(0, 0, w, h, 2, (int[])null);
        int[] ycckK = ycckRaster.getSamples(0, 0, w, h, 3, (int[])null);
        int[] cmyk = new int[ycckY.length];
        for (int i = 0; i < ycckY.length; ++i) {
            int y = 255 - ycckY[i];
            int cb = 255 - ycckCb[i];
            int cr = 255 - ycckCr[i];
            int cmykC = 255 - (y + Cr_r_tab[cr]);
            int cmykM = 255 - (y + (Cb_g_tab[cb] + Cr_g_tab[cr] >> 16));
            int cmykY = 255 - (y + Cb_b_tab[cb]);
            cmyk[i] = (cmykC < 0 ? 0 : (cmykC > 255 ? 255 : cmykC)) << 24 | (cmykM < 0 ? 0 : (cmykM > 255 ? 255 : cmykM)) << 16 | (cmykY < 0 ? 0 : (cmykY > 255 ? 255 : cmykY)) << 8 | 255 - ycckK[i];
        }
        WritableRaster cmykRaster = Raster.createPackedRaster(new DataBufferInt(cmyk, cmyk.length), w, h, w, new int[]{-16777216, 0xFF0000, 65280, 255}, null);
        return cmykRaster;
    }

    private static Raster convertYCCKtoCMYK(Raster ycckRaster) {
        JPEGImageIO.buildYCCtoRGBtable();
        int w = ycckRaster.getWidth();
        int h = ycckRaster.getHeight();
        int[] ycckY = ycckRaster.getSamples(0, 0, w, h, 0, (int[])null);
        int[] ycckCb = ycckRaster.getSamples(0, 0, w, h, 1, (int[])null);
        int[] ycckCr = ycckRaster.getSamples(0, 0, w, h, 2, (int[])null);
        int[] ycckK = ycckRaster.getSamples(0, 0, w, h, 3, (int[])null);
        int[] cmyk = new int[ycckY.length];
        for (int i = 0; i < ycckY.length; ++i) {
            int y = ycckY[i];
            int cb = ycckCb[i];
            int cr = ycckCr[i];
            int cmykC = 255 - (y + Cr_r_tab[cr]);
            int cmykM = 255 - (y + (Cb_g_tab[cb] + Cr_g_tab[cr] >> 16));
            int cmykY = 255 - (y + Cb_b_tab[cb]);
            cmyk[i] = (cmykC < 0 ? 0 : (cmykC > 255 ? 255 : cmykC)) << 24 | (cmykM < 0 ? 0 : (cmykM > 255 ? 255 : cmykM)) << 16 | (cmykY < 0 ? 0 : (cmykY > 255 ? 255 : cmykY)) << 8 | ycckK[i];
        }
        return Raster.createPackedRaster(new DataBufferInt(cmyk, cmyk.length), w, h, w, new int[]{-16777216, 0xFF0000, 65280, 255}, null);
    }
}

