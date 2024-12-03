/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.decoder;

final class Dct {
    private static final float[] DCT_SCALING_FACTORS = new float[]{(float)(0.5 / Math.sqrt(2.0)), (float)(0.25 / Math.cos(0.19634954084936207)), (float)(0.25 / Math.cos(0.39269908169872414)), (float)(0.25 / Math.cos(0.5890486225480862)), (float)(0.25 / Math.cos(0.7853981633974483)), (float)(0.25 / Math.cos(0.9817477042468103)), (float)(0.25 / Math.cos(1.1780972450961724)), (float)(0.25 / Math.cos(1.3744467859455345))};
    private static final float[] IDCT_SCALING_FACTORS = new float[]{(float)(8.0 / Math.sqrt(2.0) * 0.0625), (float)(4.0 * Math.cos(0.19634954084936207) * 0.125), (float)(4.0 * Math.cos(0.39269908169872414) * 0.125), (float)(4.0 * Math.cos(0.5890486225480862) * 0.125), (float)(4.0 * Math.cos(0.7853981633974483) * 0.125), (float)(4.0 * Math.cos(0.9817477042468103) * 0.125), (float)(4.0 * Math.cos(1.1780972450961724) * 0.125), (float)(4.0 * Math.cos(1.3744467859455345) * 0.125)};
    private static final float A1 = (float)Math.cos(0.7853981633974483);
    private static final float A2 = (float)(Math.cos(0.39269908169872414) - Math.cos(1.1780972450961724));
    private static final float A3 = A1;
    private static final float A4 = (float)(Math.cos(0.39269908169872414) + Math.cos(1.1780972450961724));
    private static final float A5 = (float)Math.cos(1.1780972450961724);
    private static final float C2 = (float)(2.0 * Math.cos(0.39269908169872414));
    private static final float C4 = (float)(2.0 * Math.cos(0.7853981633974483));
    private static final float C6 = (float)(2.0 * Math.cos(1.1780972450961724));
    private static final float Q = C2 - C6;
    private static final float R = C2 + C6;

    private Dct() {
    }

    public static void scaleQuantizationVector(float[] vector) {
        for (int x = 0; x < 8; ++x) {
            int n = x;
            vector[n] = vector[n] * DCT_SCALING_FACTORS[x];
        }
    }

    public static void scaleDequantizationVector(float[] vector) {
        for (int x = 0; x < 8; ++x) {
            int n = x;
            vector[n] = vector[n] * IDCT_SCALING_FACTORS[x];
        }
    }

    public static void scaleQuantizationMatrix(float[] matrix) {
        for (int y = 0; y < 8; ++y) {
            for (int x = 0; x < 8; ++x) {
                int n = 8 * y + x;
                matrix[n] = matrix[n] * (DCT_SCALING_FACTORS[y] * DCT_SCALING_FACTORS[x]);
            }
        }
    }

    public static void scaleDequantizationMatrix(float[] matrix) {
        for (int y = 0; y < 8; ++y) {
            for (int x = 0; x < 8; ++x) {
                int n = 8 * y + x;
                matrix[n] = matrix[n] * (IDCT_SCALING_FACTORS[y] * IDCT_SCALING_FACTORS[x]);
            }
        }
    }

    public static void forwardDCT8(float[] vector) {
        float a00 = vector[0] + vector[7];
        float a10 = vector[1] + vector[6];
        float a20 = vector[2] + vector[5];
        float a30 = vector[3] + vector[4];
        float a40 = vector[3] - vector[4];
        float a50 = vector[2] - vector[5];
        float a60 = vector[1] - vector[6];
        float a70 = vector[0] - vector[7];
        float a01 = a00 + a30;
        float a11 = a10 + a20;
        float a21 = a10 - a20;
        float a31 = a00 - a30;
        float neg_a41 = a40 + a50;
        float a51 = a50 + a60;
        float a61 = a60 + a70;
        float a22 = a21 + a31;
        float a23 = a22 * A1;
        float mul5 = (a61 - neg_a41) * A5;
        float a43 = neg_a41 * A2 - mul5;
        float a53 = a51 * A3;
        float a63 = a61 * A4 - mul5;
        float a54 = a70 + a53;
        float a74 = a70 - a53;
        vector[0] = a01 + a11;
        vector[4] = a01 - a11;
        vector[2] = a31 + a23;
        vector[6] = a31 - a23;
        vector[5] = a74 + a43;
        vector[1] = a54 + a63;
        vector[7] = a54 - a63;
        vector[3] = a74 - a43;
    }

    public static void forwardDCT8x8(float[] matrix) {
        float a74;
        float a54;
        float a63;
        float a53;
        float a43;
        float mul5;
        float a23;
        float a22;
        float a61;
        float a51;
        float neg_a41;
        float a31;
        float a21;
        float a11;
        float a01;
        float a70;
        float a60;
        float a50;
        float a40;
        float a30;
        float a20;
        float a10;
        float a00;
        int i;
        for (i = 0; i < 8; ++i) {
            a00 = matrix[8 * i] + matrix[8 * i + 7];
            a10 = matrix[8 * i + 1] + matrix[8 * i + 6];
            a20 = matrix[8 * i + 2] + matrix[8 * i + 5];
            a30 = matrix[8 * i + 3] + matrix[8 * i + 4];
            a40 = matrix[8 * i + 3] - matrix[8 * i + 4];
            a50 = matrix[8 * i + 2] - matrix[8 * i + 5];
            a60 = matrix[8 * i + 1] - matrix[8 * i + 6];
            a70 = matrix[8 * i] - matrix[8 * i + 7];
            a01 = a00 + a30;
            a11 = a10 + a20;
            a21 = a10 - a20;
            a31 = a00 - a30;
            neg_a41 = a40 + a50;
            a51 = a50 + a60;
            a61 = a60 + a70;
            a22 = a21 + a31;
            a23 = a22 * A1;
            mul5 = (a61 - neg_a41) * A5;
            a43 = neg_a41 * A2 - mul5;
            a53 = a51 * A3;
            a63 = a61 * A4 - mul5;
            a54 = a70 + a53;
            a74 = a70 - a53;
            matrix[8 * i] = a01 + a11;
            matrix[8 * i + 4] = a01 - a11;
            matrix[8 * i + 2] = a31 + a23;
            matrix[8 * i + 6] = a31 - a23;
            matrix[8 * i + 5] = a74 + a43;
            matrix[8 * i + 1] = a54 + a63;
            matrix[8 * i + 7] = a54 - a63;
            matrix[8 * i + 3] = a74 - a43;
        }
        for (i = 0; i < 8; ++i) {
            a00 = matrix[i] + matrix[56 + i];
            a10 = matrix[8 + i] + matrix[48 + i];
            a20 = matrix[16 + i] + matrix[40 + i];
            a30 = matrix[24 + i] + matrix[32 + i];
            a40 = matrix[24 + i] - matrix[32 + i];
            a50 = matrix[16 + i] - matrix[40 + i];
            a60 = matrix[8 + i] - matrix[48 + i];
            a70 = matrix[i] - matrix[56 + i];
            a01 = a00 + a30;
            a11 = a10 + a20;
            a21 = a10 - a20;
            a31 = a00 - a30;
            neg_a41 = a40 + a50;
            a51 = a50 + a60;
            a61 = a60 + a70;
            a22 = a21 + a31;
            a23 = a22 * A1;
            mul5 = (a61 - neg_a41) * A5;
            a43 = neg_a41 * A2 - mul5;
            a53 = a51 * A3;
            a63 = a61 * A4 - mul5;
            a54 = a70 + a53;
            a74 = a70 - a53;
            matrix[i] = a01 + a11;
            matrix[32 + i] = a01 - a11;
            matrix[16 + i] = a31 + a23;
            matrix[48 + i] = a31 - a23;
            matrix[40 + i] = a74 + a43;
            matrix[8 + i] = a54 + a63;
            matrix[56 + i] = a54 - a63;
            matrix[24 + i] = a74 - a43;
        }
    }

    public static void inverseDCT8(float[] vector) {
        float a2 = vector[2] - vector[6];
        float a3 = vector[2] + vector[6];
        float a4 = vector[5] - vector[3];
        float tmp1 = vector[1] + vector[7];
        float tmp2 = vector[3] + vector[5];
        float a5 = tmp1 - tmp2;
        float a6 = vector[1] - vector[7];
        float a7 = tmp1 + tmp2;
        float tmp4 = C6 * (a4 + a6);
        float neg_b4 = Q * a4 + tmp4;
        float b6 = R * a6 - tmp4;
        float b2 = a2 * C4;
        float b5 = a5 * C4;
        float tmp3 = b6 - a7;
        float n0 = tmp3 - b5;
        float n1 = vector[0] - vector[4];
        float n2 = b2 - a3;
        float n3 = vector[0] + vector[4];
        float neg_n5 = neg_b4;
        float m3 = n1 + n2;
        float m4 = n3 + a3;
        float m5 = n1 - n2;
        float m6 = n3 - a3;
        float neg_m7 = neg_n5 + n0;
        vector[0] = m4 + a7;
        vector[1] = m3 + tmp3;
        vector[2] = m5 - n0;
        vector[3] = m6 + neg_m7;
        vector[4] = m6 - neg_m7;
        vector[5] = m5 + n0;
        vector[6] = m3 - tmp3;
        vector[7] = m4 - a7;
    }

    public static void inverseDCT8x8(float[] matrix) {
        float neg_m7;
        float m6;
        float m5;
        float m4;
        float m3;
        float neg_n5;
        float n3;
        float n2;
        float n1;
        float n0;
        float tmp3;
        float b5;
        float b2;
        float b6;
        float neg_b4;
        float tmp4;
        float a7;
        float a6;
        float a5;
        float tmp2;
        float tmp1;
        float a4;
        float a3;
        float a2;
        int i;
        for (i = 0; i < 8; ++i) {
            a2 = matrix[8 * i + 2] - matrix[8 * i + 6];
            a3 = matrix[8 * i + 2] + matrix[8 * i + 6];
            a4 = matrix[8 * i + 5] - matrix[8 * i + 3];
            tmp1 = matrix[8 * i + 1] + matrix[8 * i + 7];
            tmp2 = matrix[8 * i + 3] + matrix[8 * i + 5];
            a5 = tmp1 - tmp2;
            a6 = matrix[8 * i + 1] - matrix[8 * i + 7];
            a7 = tmp1 + tmp2;
            tmp4 = C6 * (a4 + a6);
            neg_b4 = Q * a4 + tmp4;
            b6 = R * a6 - tmp4;
            b2 = a2 * C4;
            b5 = a5 * C4;
            tmp3 = b6 - a7;
            n0 = tmp3 - b5;
            n1 = matrix[8 * i] - matrix[8 * i + 4];
            n2 = b2 - a3;
            n3 = matrix[8 * i] + matrix[8 * i + 4];
            neg_n5 = neg_b4;
            m3 = n1 + n2;
            m4 = n3 + a3;
            m5 = n1 - n2;
            m6 = n3 - a3;
            neg_m7 = neg_n5 + n0;
            matrix[8 * i] = m4 + a7;
            matrix[8 * i + 1] = m3 + tmp3;
            matrix[8 * i + 2] = m5 - n0;
            matrix[8 * i + 3] = m6 + neg_m7;
            matrix[8 * i + 4] = m6 - neg_m7;
            matrix[8 * i + 5] = m5 + n0;
            matrix[8 * i + 6] = m3 - tmp3;
            matrix[8 * i + 7] = m4 - a7;
        }
        for (i = 0; i < 8; ++i) {
            a2 = matrix[16 + i] - matrix[48 + i];
            a3 = matrix[16 + i] + matrix[48 + i];
            a4 = matrix[40 + i] - matrix[24 + i];
            tmp1 = matrix[8 + i] + matrix[56 + i];
            tmp2 = matrix[24 + i] + matrix[40 + i];
            a5 = tmp1 - tmp2;
            a6 = matrix[8 + i] - matrix[56 + i];
            a7 = tmp1 + tmp2;
            tmp4 = C6 * (a4 + a6);
            neg_b4 = Q * a4 + tmp4;
            b6 = R * a6 - tmp4;
            b2 = a2 * C4;
            b5 = a5 * C4;
            tmp3 = b6 - a7;
            n0 = tmp3 - b5;
            n1 = matrix[i] - matrix[32 + i];
            n2 = b2 - a3;
            n3 = matrix[i] + matrix[32 + i];
            neg_n5 = neg_b4;
            m3 = n1 + n2;
            m4 = n3 + a3;
            m5 = n1 - n2;
            m6 = n3 - a3;
            neg_m7 = neg_n5 + n0;
            matrix[i] = m4 + a7;
            matrix[8 + i] = m3 + tmp3;
            matrix[16 + i] = m5 - n0;
            matrix[24 + i] = m6 + neg_m7;
            matrix[32 + i] = m6 - neg_m7;
            matrix[40 + i] = m5 + n0;
            matrix[48 + i] = m3 - tmp3;
            matrix[56 + i] = m4 - a7;
        }
    }
}

