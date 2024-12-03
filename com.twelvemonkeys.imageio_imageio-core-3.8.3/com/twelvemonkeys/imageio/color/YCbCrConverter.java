/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.color;

import com.twelvemonkeys.imageio.color.ColorSpaces;

public final class YCbCrConverter {
    private static final int SCALEBITS = 16;
    private static final int MAXJSAMPLE = 255;
    private static final int CENTERJSAMPLE = 128;
    private static final int ONE_HALF = 32768;

    public static void convertYCbCr2RGB(byte[] byArray, byte[] byArray2, double[] dArray, double[] dArray2, int n) {
        double d;
        double d2;
        double d3;
        if (dArray2 == null) {
            d3 = byArray[n] & 0xFF;
            d2 = (byArray[n + 1] & 0xFF) - 128;
            d = (byArray[n + 2] & 0xFF) - 128;
        } else {
            d3 = ((double)(byArray[n] & 0xFF) - dArray2[0]) * 255.0 / (dArray2[1] - dArray2[0]);
            d2 = ((double)(byArray[n + 1] & 0xFF) - dArray2[2]) * 127.0 / (dArray2[3] - dArray2[2]);
            d = ((double)(byArray[n + 2] & 0xFF) - dArray2[4]) * 127.0 / (dArray2[5] - dArray2[4]);
        }
        double d4 = dArray[0];
        double d5 = dArray[1];
        double d6 = dArray[2];
        int n2 = (int)Math.round(d * (2.0 - 2.0 * d4) + d3);
        int n3 = (int)Math.round(d2 * (2.0 - 2.0 * d6) + d3);
        int n4 = (int)Math.round((d3 - d4 * (double)n2 - d6 * (double)n3) / d5);
        byArray2[n] = YCbCrConverter.clamp(n2);
        byArray2[n + 2] = YCbCrConverter.clamp(n3);
        byArray2[n + 1] = YCbCrConverter.clamp(n4);
    }

    public static void convertJPEGYCbCr2RGB(byte[] byArray, byte[] byArray2, int n) {
        int n2 = byArray[n] & 0xFF;
        int n3 = byArray[n + 1] & 0xFF;
        int n4 = byArray[n + 2] & 0xFF;
        byArray2[n] = YCbCrConverter.clamp(n2 + JPEG.Cr_R_LUT[n4]);
        byArray2[n + 1] = YCbCrConverter.clamp(n2 + (JPEG.Cb_G_LUT[n3] + JPEG.Cr_G_LUT[n4] >> 16));
        byArray2[n + 2] = YCbCrConverter.clamp(n2 + JPEG.Cb_B_LUT[n3]);
    }

    public static void convertRec601YCbCr2RGB(byte[] byArray, byte[] byArray2, int n) {
        int n2 = byArray[n] & 0xFF;
        int n3 = byArray[n + 1] & 0xFF;
        int n4 = byArray[n + 2] & 0xFF;
        byArray2[n] = YCbCrConverter.clamp(ITU_R_601.Y_LUT[n2] + ITU_R_601.Cr_R_LUT[n4]);
        byArray2[n + 1] = YCbCrConverter.clamp(ITU_R_601.Y_LUT[n2] + (ITU_R_601.Cr_G_LUT[n4] + ITU_R_601.Cb_G_LUT[n3] >> 16));
        byArray2[n + 2] = YCbCrConverter.clamp(ITU_R_601.Y_LUT[n2] + ITU_R_601.Cb_B_LUT[n3]);
    }

    private static byte clamp(int n) {
        return (byte)Math.max(0, Math.min(255, n));
    }

    private static final class ITU_R_601 {
        private static final int[] Cr_R_LUT = new int[256];
        private static final int[] Cb_B_LUT = new int[256];
        private static final int[] Cr_G_LUT = new int[256];
        private static final int[] Cb_G_LUT = new int[256];
        private static final int[] Y_LUT = new int[256];

        private ITU_R_601() {
        }

        private static void buildYCCtoRGBtable() {
            if (ColorSpaces.DEBUG) {
                System.err.println("Building ITU-R REC.601 YCbCr conversion table");
            }
            int n = 0;
            int n2 = -128;
            while (n <= 255) {
                ITU_R_601.Cr_R_LUT[n] = 104597 * n2 + 32768 >> 16;
                ITU_R_601.Cb_B_LUT[n] = 132201 * n2 + 32768 >> 16;
                ITU_R_601.Cr_G_LUT[n] = -53279 * n2;
                ITU_R_601.Cb_G_LUT[n] = -25674 * n2 + 32768;
                ITU_R_601.Y_LUT[n] = 76309 * (n - 16) + 32768 >> 16;
                ++n;
                ++n2;
            }
        }

        static {
            ITU_R_601.buildYCCtoRGBtable();
        }
    }

    private static final class JPEG {
        private static final int[] Cr_R_LUT = new int[256];
        private static final int[] Cb_B_LUT = new int[256];
        private static final int[] Cr_G_LUT = new int[256];
        private static final int[] Cb_G_LUT = new int[256];

        private JPEG() {
        }

        private static void buildYCCtoRGBtable() {
            if (ColorSpaces.DEBUG) {
                System.err.println("Building JPEG YCbCr conversion table");
            }
            int n = 0;
            int n2 = -128;
            while (n <= 255) {
                JPEG.Cr_R_LUT[n] = (int)(91881.972 * (double)n2 + 32768.0) >> 16;
                JPEG.Cb_B_LUT[n] = (int)(116130.292 * (double)n2 + 32768.0) >> 16;
                JPEG.Cr_G_LUT[n] = -46802 * n2;
                JPEG.Cb_G_LUT[n] = -22554 * n2 + 32768;
                ++n;
                ++n2;
            }
        }

        static {
            JPEG.buildYCCtoRGBtable();
        }
    }
}

