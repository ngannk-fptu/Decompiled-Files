/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.image.ImageUtil
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.imageio.util.IIOInputStreamAdapter;
import com.twelvemonkeys.imageio.util.IIOOutputStreamAdapter;
import com.twelvemonkeys.lang.Validate;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.TreeSet;
import javax.imageio.IIOParam;
import javax.imageio.ImageIO;
import javax.imageio.spi.IIOServiceProvider;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

public final class IIOUtil {
    private IIOUtil() {
    }

    public static InputStream createStreamAdapter(ImageInputStream imageInputStream) {
        return new BufferedInputStream(new IIOInputStreamAdapter(imageInputStream));
    }

    public static InputStream createStreamAdapter(ImageInputStream imageInputStream, long l) {
        return new BufferedInputStream(new IIOInputStreamAdapter(imageInputStream, l));
    }

    public static OutputStream createStreamAdapter(ImageOutputStream imageOutputStream) {
        return new BufferedOutputStream(new IIOOutputStreamAdapter(imageOutputStream));
    }

    public static Image fakeSubsampling(Image image, IIOParam iIOParam) {
        if (image == null) {
            return null;
        }
        if (iIOParam != null) {
            int n = iIOParam.getSourceXSubsampling();
            int n2 = iIOParam.getSourceYSubsampling();
            if (n > 1 || n2 > 1) {
                int n3 = (ImageUtil.getWidth((Image)image) + n - 1) / n;
                int n4 = (ImageUtil.getHeight((Image)image) + n2 - 1) / n2;
                return image.getScaledInstance(n3, n4, 2);
            }
        }
        return image;
    }

    public static Rectangle getSourceRegion(IIOParam iIOParam, int n, int n2) {
        Rectangle rectangle = new Rectangle(n, n2);
        if (iIOParam != null) {
            Rectangle rectangle2 = iIOParam.getSourceRegion();
            if (rectangle2 != null) {
                rectangle = rectangle.intersection(rectangle2);
            }
            int n3 = iIOParam.getSubsamplingXOffset();
            int n4 = iIOParam.getSubsamplingYOffset();
            rectangle.x += n3;
            rectangle.y += n4;
            rectangle.width -= n3;
            rectangle.height -= n4;
        }
        return rectangle;
    }

    public static BufferedImage fakeAOI(BufferedImage bufferedImage, Rectangle rectangle) {
        if (bufferedImage == null) {
            return null;
        }
        if (rectangle != null && (rectangle.x != 0 || rectangle.y != 0 || rectangle.width != bufferedImage.getWidth() || rectangle.height != bufferedImage.getHeight())) {
            return bufferedImage.getSubimage(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
        return bufferedImage;
    }

    public static <T> void deregisterProvider(ServiceRegistry serviceRegistry, IIOServiceProvider iIOServiceProvider, Class<T> clazz) {
        serviceRegistry.deregisterServiceProvider(clazz.cast(iIOServiceProvider), clazz);
    }

    public static <T> T lookupProviderByName(ServiceRegistry serviceRegistry, String string, Class<T> clazz) {
        Iterator<T> iterator = serviceRegistry.getServiceProviders(clazz, true);
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (!t.getClass().getName().equals(string)) continue;
            return t;
        }
        return null;
    }

    public static String[] getNormalizedReaderFormatNames() {
        return IIOUtil.normalizeNames(ImageIO.getReaderFormatNames());
    }

    public static String[] getNormalizedWriterFormatNames() {
        return IIOUtil.normalizeNames(ImageIO.getWriterFormatNames());
    }

    private static String[] normalizeNames(String[] stringArray) {
        TreeSet<String> treeSet = new TreeSet<String>();
        for (String string : stringArray) {
            treeSet.add(string.toUpperCase());
        }
        return treeSet.toArray(new String[0]);
    }

    public static void subsampleRow(byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4, int n5, int n6) {
        if (n6 == 1) {
            return;
        }
        Validate.isTrue((n6 > 1 ? 1 : 0) != 0, (String)"samplePeriod must be > 1");
        Validate.isTrue((n5 > 0 && n5 <= 8 && (n5 == 1 || n5 % 2 == 0) ? 1 : 0) != 0, (String)"bitsPerSample must be > 0 and <= 8 and a power of 2");
        Validate.isTrue((n4 > 0 ? 1 : 0) != 0, (String)"samplesPerPixel must be > 0");
        Validate.isTrue((n4 * n5 <= 8 || n4 * n5 % 8 == 0 ? 1 : 0) != 0, (String)"samplesPerPixel * bitsPerSample must be < 8 or a multiple of 8 ");
        if (n5 * n4 % 8 == 0) {
            int n7 = n5 * n4 / 8;
            for (int i = 0; i < n2 * n7; i += n6 * n7) {
                System.arraycopy(byArray, n + i, byArray2, n3 + i / n6, n7);
            }
        } else {
            int n8 = n5 * n4;
            int n9 = (1 << n8) - 1;
            for (int i = 0; i < n2; i += n6) {
                int n10 = (n3 + i / n6) * n8 / 8;
                int n11 = (n + i) * n8 / 8;
                int n12 = 8 - n8 - i * n8 % 8;
                int n13 = n9 << n12;
                int n14 = 8 - n8 - i * n8 / n6 % 8;
                int n15 = ~(n9 << n14);
                int n16 = (byArray[n11] & n13) >> n12;
                byArray2[n10] = (byte)(byArray2[n10] & n15 | n16 << n14);
            }
        }
    }

    public static void subsampleRow(short[] sArray, int n, int n2, short[] sArray2, int n3, int n4, int n5, int n6) {
        if (n6 == 1) {
            return;
        }
        Validate.isTrue((n6 > 1 ? 1 : 0) != 0, (String)"samplePeriod must be > 1");
        Validate.isTrue((n5 > 0 && n5 <= 16 && (n5 == 1 || n5 % 2 == 0) ? 1 : 0) != 0, (String)"bitsPerSample must be > 0 and <= 16 and a power of 2");
        Validate.isTrue((n4 > 0 ? 1 : 0) != 0, (String)"samplesPerPixel must be > 0");
        Validate.isTrue((n4 * n5 <= 16 || n4 * n5 % 16 == 0 ? 1 : 0) != 0, (String)"samplesPerPixel * bitsPerSample must be < 16 or a multiple of 16 ");
        int n7 = n5 * n4 / 16;
        for (int i = 0; i < n2 * n7; i += n6 * n7) {
            System.arraycopy(sArray, n + i, sArray2, n3 + i / n6, n7);
        }
    }

    public static void subsampleRow(int[] nArray, int n, int n2, int[] nArray2, int n3, int n4, int n5, int n6) {
        if (n6 == 1) {
            return;
        }
        Validate.isTrue((n6 > 1 ? 1 : 0) != 0, (String)"samplePeriod must be > 1");
        Validate.isTrue((n5 > 0 && n5 <= 32 && (n5 == 1 || n5 % 2 == 0) ? 1 : 0) != 0, (String)"bitsPerSample must be > 0 and <= 32 and a power of 2");
        Validate.isTrue((n4 > 0 ? 1 : 0) != 0, (String)"samplesPerPixel must be > 0");
        Validate.isTrue((n4 * n5 <= 32 || n4 * n5 % 32 == 0 ? 1 : 0) != 0, (String)"samplesPerPixel * bitsPerSample must be < 32 or a multiple of 32 ");
        int n7 = n5 * n4 / 32;
        for (int i = 0; i < n2 * n7; i += n6 * n7) {
            System.arraycopy(nArray, n + i, nArray2, n3 + i / n6, n7);
        }
    }

    public static void subsampleRow(float[] fArray, int n, int n2, float[] fArray2, int n3, int n4, int n5, int n6) {
        Validate.isTrue((n6 > 1 ? 1 : 0) != 0, (String)"samplePeriod must be > 1");
        Validate.isTrue((n5 > 0 && n5 <= 32 && (n5 == 1 || n5 % 2 == 0) ? 1 : 0) != 0, (String)"bitsPerSample must be > 0 and <= 32 and a power of 2");
        Validate.isTrue((n4 > 0 ? 1 : 0) != 0, (String)"samplesPerPixel must be > 0");
        Validate.isTrue((n4 * n5 <= 32 || n4 * n5 % 32 == 0 ? 1 : 0) != 0, (String)"samplesPerPixel * bitsPerSample must be < 32 or a multiple of 32 ");
        int n7 = n5 * n4 / 32;
        for (int i = 0; i < n2 * n7; i += n6 * n7) {
            System.arraycopy(fArray, n + i, fArray2, n3 + i / n6, n7);
        }
    }
}

