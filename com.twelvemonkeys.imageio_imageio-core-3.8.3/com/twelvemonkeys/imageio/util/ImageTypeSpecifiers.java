/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.imageio.color.DiscreteAlphaIndexColorModel;
import com.twelvemonkeys.imageio.util.IndexedImageTypeSpecifier;
import com.twelvemonkeys.imageio.util.Int16ImageTypeSpecifier;
import com.twelvemonkeys.imageio.util.UInt32ImageTypeSpecifier;
import com.twelvemonkeys.lang.Validate;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.SampleModel;
import javax.imageio.ImageTypeSpecifier;

public final class ImageTypeSpecifiers {
    private ImageTypeSpecifiers() {
    }

    public static ImageTypeSpecifier createFromBufferedImageType(int n) {
        switch (n) {
            case 8: {
                return ImageTypeSpecifiers.createPacked(ColorSpace.getInstance(1000), 63488, 2016, 31, 0, 1, false);
            }
            case 9: {
                return ImageTypeSpecifiers.createPacked(ColorSpace.getInstance(1000), 31744, 992, 31, 0, 1, false);
            }
        }
        return ImageTypeSpecifier.createFromBufferedImageType(n);
    }

    public static ImageTypeSpecifier createPacked(ColorSpace colorSpace, int n, int n2, int n3, int n4, int n5, boolean bl) {
        if (n5 == 0 || n5 == 1) {
            Validate.notNull((Object)colorSpace, (String)"colorSpace");
            Validate.isTrue((colorSpace.getType() == 5 ? 1 : 0) != 0, (Object)colorSpace, (String)"ColorSpace must be TYPE_RGB");
            Validate.isTrue((n != 0 || n2 != 0 || n3 != 0 || n4 != 0 ? 1 : 0) != 0, (String)"No mask has at least 1 bit set");
            int n6 = n5 == 0 ? 8 : 16;
            DirectColorModel directColorModel = new DirectColorModel(colorSpace, n6, n, n2, n3, n4, bl, n5);
            return new ImageTypeSpecifier(directColorModel, ((ColorModel)directColorModel).createCompatibleSampleModel(1, 1));
        }
        return ImageTypeSpecifier.createPacked(colorSpace, n, n2, n3, n4, n5, bl);
    }

    public static ImageTypeSpecifier createInterleaved(ColorSpace colorSpace, int[] nArray, int n, boolean bl, boolean bl2) {
        if (n == 3) {
            return UInt32ImageTypeSpecifier.createInterleaved(colorSpace, nArray, bl, bl2);
        }
        return ImageTypeSpecifier.createInterleaved(colorSpace, nArray, n, bl, bl2);
    }

    public static ImageTypeSpecifier createBanded(ColorSpace colorSpace, int[] nArray, int[] nArray2, int n, boolean bl, boolean bl2) {
        if (n == 3) {
            return UInt32ImageTypeSpecifier.createBanded(colorSpace, nArray, nArray2, bl, bl2);
        }
        return ImageTypeSpecifier.createBanded(colorSpace, nArray, nArray2, n, bl, bl2);
    }

    public static ImageTypeSpecifier createGrayscale(int n, int n2) {
        if (n == 16 && n2 == 2) {
            return new Int16ImageTypeSpecifier(ColorSpace.getInstance(1003), new int[]{0}, false, false);
        }
        if (n == 32 && n2 == 3) {
            return UInt32ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(1003), new int[]{0}, false, false);
        }
        if (n2 == 4 || n2 == 5) {
            return ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(1003), new int[]{0}, n2, false, false);
        }
        return ImageTypeSpecifier.createGrayscale(n, n2, false);
    }

    public static ImageTypeSpecifier createGrayscale(int n, int n2, boolean bl) {
        if (n == 16 && n2 == 2) {
            return new Int16ImageTypeSpecifier(ColorSpace.getInstance(1003), new int[]{0, 1}, true, bl);
        }
        if (n == 32 && n2 == 3) {
            return UInt32ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(1003), new int[]{0, 1}, true, bl);
        }
        if (n2 == 4 || n2 == 5) {
            return ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(1003), new int[]{0, 1}, n2, true, bl);
        }
        return ImageTypeSpecifier.createGrayscale(n, n2, false, bl);
    }

    public static ImageTypeSpecifier createPackedGrayscale(ColorSpace colorSpace, int n, int n2) {
        IndexColorModel indexColorModel;
        Object object;
        Validate.notNull((Object)colorSpace, (String)"colorSpace");
        Validate.isTrue((colorSpace.getType() == 6 ? 1 : 0) != 0, (Object)colorSpace, (String)"ColorSpace must be TYPE_GRAY");
        Validate.isTrue((n == 1 || n == 2 || n == 4 ? 1 : 0) != 0, (Object)n, (String)"bits must be 1, 2, or 4: %s");
        Validate.isTrue((n2 == 0 ? 1 : 0) != 0, (Object)n2, (String)"dataType must be TYPE_BYTE: %s");
        int n3 = 1 << n;
        if (ColorSpace.getInstance(1003).equals(colorSpace)) {
            object = new byte[n3];
            for (int i = 0; i < n3; ++i) {
                object[i] = (byte)(i * 255 / (n3 - 1));
            }
            indexColorModel = new IndexColorModel(n, n3, (byte[])object, (byte[])object, (byte[])object);
        } else {
            object = new byte[n3];
            byte[] byArray = new byte[n3];
            byte[] byArray2 = new byte[n3];
            for (int i = 0; i < n3; ++i) {
                float[] fArray = new float[]{(float)i / (float)(n3 - 1)};
                float[] fArray2 = colorSpace.toRGB(fArray);
                object[i] = (byte)Math.round(fArray2[0] * 255.0f);
                byArray[i] = (byte)Math.round(fArray2[1] * 255.0f);
                byArray2[i] = (byte)Math.round(fArray2[2] * 255.0f);
            }
            indexColorModel = new IndexColorModel(n, n3, (byte[])object, byArray, byArray2);
        }
        object = new MultiPixelPackedSampleModel(n2, 1, 1, n);
        return new ImageTypeSpecifier(indexColorModel, (SampleModel)object);
    }

    public static ImageTypeSpecifier createIndexed(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, int n, int n2) {
        return ImageTypeSpecifier.createIndexed(byArray, byArray2, byArray3, byArray4, n, n2);
    }

    public static ImageTypeSpecifier createIndexed(int[] nArray, boolean bl, int n, int n2, int n3) {
        return ImageTypeSpecifiers.createFromIndexColorModel(new IndexColorModel(n2, nArray.length, nArray, 0, bl, n, n3));
    }

    public static ImageTypeSpecifier createFromIndexColorModel(IndexColorModel indexColorModel) {
        return new IndexedImageTypeSpecifier(indexColorModel);
    }

    public static ImageTypeSpecifier createDiscreteAlphaIndexedFromIndexColorModel(IndexColorModel indexColorModel) {
        DiscreteAlphaIndexColorModel discreteAlphaIndexColorModel = new DiscreteAlphaIndexColorModel(indexColorModel);
        return new ImageTypeSpecifier(discreteAlphaIndexColorModel, ((ColorModel)discreteAlphaIndexColorModel).createCompatibleSampleModel(1, 1));
    }

    public static ImageTypeSpecifier createDiscreteExtraSamplesIndexedFromIndexColorModel(IndexColorModel indexColorModel, int n, boolean bl) {
        DiscreteAlphaIndexColorModel discreteAlphaIndexColorModel = new DiscreteAlphaIndexColorModel(indexColorModel, n, bl);
        return new ImageTypeSpecifier(discreteAlphaIndexColorModel, ((ColorModel)discreteAlphaIndexColorModel).createCompatibleSampleModel(1, 1));
    }
}

