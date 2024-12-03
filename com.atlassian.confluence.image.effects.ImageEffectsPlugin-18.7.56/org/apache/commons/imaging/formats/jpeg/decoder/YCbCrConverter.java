/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.decoder;

final class YCbCrConverter {
    private static final int[] REDS;
    private static final int[] BLUES;
    private static final int[] GREENS1;
    private static final int[] GREENS2;

    private YCbCrConverter() {
    }

    private static int fastRound(float x) {
        return (int)(x + 0.5f);
    }

    public static int convertYCbCrToRGB(int Y, int Cb, int Cr) {
        int r = REDS[Cr << 8 | Y];
        int g1 = GREENS1[Cb << 8 | Cr];
        int g = GREENS2[g1 << 8 | Y];
        int b = BLUES[Cb << 8 | Y];
        return r | g | b;
    }

    static {
        int Cr;
        int Y;
        REDS = new int[65536];
        BLUES = new int[65536];
        GREENS1 = new int[65536];
        GREENS2 = new int[131072];
        for (Y = 0; Y < 256; ++Y) {
            for (Cr = 0; Cr < 256; ++Cr) {
                int r = Y + YCbCrConverter.fastRound(1.402f * (float)(Cr - 128));
                if (r < 0) {
                    r = 0;
                }
                if (r > 255) {
                    r = 255;
                }
                YCbCrConverter.REDS[Cr << 8 | Y] = r << 16;
            }
        }
        for (Y = 0; Y < 256; ++Y) {
            for (int Cb = 0; Cb < 256; ++Cb) {
                int b = Y + YCbCrConverter.fastRound(1.772f * (float)(Cb - 128));
                if (b < 0) {
                    b = 0;
                }
                if (b > 255) {
                    b = 255;
                }
                YCbCrConverter.BLUES[Cb << 8 | Y] = b;
            }
        }
        for (int Cb = 0; Cb < 256; ++Cb) {
            for (Cr = 0; Cr < 256; ++Cr) {
                int value = YCbCrConverter.fastRound(0.34414f * (float)(Cb - 128) + 0.71414f * (float)(Cr - 128));
                YCbCrConverter.GREENS1[Cb << 8 | Cr] = value + 135;
            }
        }
        for (Y = 0; Y < 256; ++Y) {
            for (int value = 0; value < 270; ++value) {
                int green = Y - (value - 135);
                if (green < 0) {
                    green = 0;
                } else if (green > 255) {
                    green = 255;
                }
                YCbCrConverter.GREENS2[value << 8 | Y] = green << 8;
            }
        }
    }
}

