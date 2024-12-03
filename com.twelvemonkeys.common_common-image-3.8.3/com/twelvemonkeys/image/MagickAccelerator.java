/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.SystemUtil
 *  magick.MagickImage
 */
package com.twelvemonkeys.image;

import com.twelvemonkeys.image.ImageConversionException;
import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.image.Magick;
import com.twelvemonkeys.image.MagickUtil;
import com.twelvemonkeys.image.ResampleOp;
import com.twelvemonkeys.lang.SystemUtil;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import magick.MagickImage;

final class MagickAccelerator {
    private static final boolean DEBUG = Magick.DEBUG;
    private static final boolean USE_MAGICK = MagickAccelerator.useMagick();
    private static final int RESAMPLE_OP = 0;
    private static Class[] nativeOp = new Class[1];

    MagickAccelerator() {
    }

    private static boolean useMagick() {
        try {
            boolean bl;
            boolean bl2 = SystemUtil.isClassAvailable((String)"magick.MagickImage");
            if (DEBUG && !bl2) {
                System.err.print("ImageMagick bindings not available.");
            }
            boolean bl3 = bl = bl2 && !"FALSE".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.image.accel"));
            if (DEBUG) {
                System.err.println(bl ? "Will use ImageMagick bindings to accelerate image resampling operations." : "Will not use ImageMagick to accelerate image resampling operations.");
            }
            return bl;
        }
        catch (Throwable throwable) {
            System.err.println("Could not enable ImageMagick bindings: " + throwable);
            return false;
        }
    }

    private static int getNativeOpIndex(Class clazz) {
        for (int i = 0; i < nativeOp.length; ++i) {
            if (clazz != nativeOp[i]) continue;
            return i;
        }
        return -1;
    }

    public static BufferedImage filter(BufferedImageOp bufferedImageOp, BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        if (!USE_MAGICK) {
            return null;
        }
        BufferedImage bufferedImage3 = null;
        switch (MagickAccelerator.getNativeOpIndex(bufferedImageOp.getClass())) {
            case 0: {
                ResampleOp resampleOp = (ResampleOp)bufferedImageOp;
                bufferedImage3 = MagickAccelerator.resampleMagick(bufferedImage, resampleOp.width, resampleOp.height, resampleOp.filterType);
                if (bufferedImage2 == null) break;
                ImageUtil.drawOnto(bufferedImage2, bufferedImage3);
                bufferedImage3 = bufferedImage2;
                break;
            }
        }
        return bufferedImage3;
    }

    private static BufferedImage resampleMagick(BufferedImage bufferedImage, int n, int n2, int n3) {
        MagickImage magickImage = null;
        MagickImage magickImage2 = null;
        try {
            magickImage = MagickUtil.toMagick(bufferedImage);
            long l = 0L;
            if (DEBUG) {
                l = System.currentTimeMillis();
            }
            magickImage.setFilter(n3);
            magickImage2 = magickImage.zoomImage(n, n2);
            if (DEBUG) {
                long l2 = System.currentTimeMillis() - l;
                System.out.println("Filtered: " + l2 + " ms");
            }
            BufferedImage bufferedImage2 = MagickUtil.toBuffered(magickImage2);
            return bufferedImage2;
        }
        catch (Exception exception) {
            if (exception instanceof RuntimeException) {
                throw (RuntimeException)exception;
            }
            throw new ImageConversionException(exception.getMessage(), exception);
        }
        finally {
            if (magickImage != null) {
                magickImage.destroyImages();
            }
            if (magickImage2 != null) {
                magickImage2.destroyImages();
            }
        }
    }

    static {
        try {
            MagickAccelerator.nativeOp[0] = Class.forName("com.twelvemonkeys.image.ResampleOp");
        }
        catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Could not find class: " + classNotFoundException);
        }
    }
}

