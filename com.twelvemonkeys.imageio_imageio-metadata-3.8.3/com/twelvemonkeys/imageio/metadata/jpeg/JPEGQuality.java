/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.metadata.jpeg;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegment;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegmentUtil;
import com.twelvemonkeys.lang.Validate;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.stream.ImageInputStream;

public final class JPEGQuality {
    static final int NUM_QUANT_TABLES = 4;
    static final int DCT_SIZE_2 = 64;

    public static float getJPEGQuality(List<JPEGSegment> list) throws IOException {
        int n = JPEGQuality.getJPEGQuality(JPEGQuality.getQuantizationTables(list));
        return n >= 0 ? (float)n / 100.0f : (float)n;
    }

    public static float getJPEGQuality(ImageInputStream imageInputStream) throws IOException {
        return JPEGQuality.getJPEGQuality(JPEGSegmentUtil.readSegments(imageInputStream, 65499, null));
    }

    private static int getJPEGQuality(int[][] nArray) throws IOException {
        int n;
        int[] nArray2;
        int[] nArray3;
        int n2 = 0;
        for (int i = 0; i < 4; ++i) {
            if (nArray[i] == null) continue;
            for (int j = 0; j < 64; ++j) {
                n2 += nArray[i][j];
            }
        }
        if (nArray[0] != null && nArray[1] != null) {
            nArray3 = new int[]{1020, 1015, 932, 848, 780, 735, 702, 679, 660, 645, 632, 623, 613, 607, 600, 594, 589, 585, 581, 571, 555, 542, 529, 514, 494, 474, 457, 439, 424, 410, 397, 386, 373, 364, 351, 341, 334, 324, 317, 309, 299, 294, 287, 279, 274, 267, 262, 257, 251, 247, 243, 237, 232, 227, 222, 217, 213, 207, 202, 198, 192, 188, 183, 177, 173, 168, 163, 157, 153, 148, 143, 139, 132, 128, 125, 119, 115, 108, 104, 99, 94, 90, 84, 79, 74, 70, 64, 59, 55, 49, 45, 40, 34, 30, 25, 20, 15, 11, 6, 4, 0};
            nArray2 = new int[]{32640, 32635, 32266, 31495, 30665, 29804, 29146, 28599, 28104, 27670, 27225, 26725, 26210, 25716, 25240, 24789, 24373, 23946, 23572, 22846, 21801, 20842, 19949, 19121, 18386, 17651, 16998, 16349, 15800, 15247, 14783, 14321, 13859, 13535, 13081, 12702, 12423, 12056, 11779, 11513, 11135, 10955, 10676, 10392, 10208, 9928, 9747, 9564, 9369, 9193, 9017, 8822, 8639, 8458, 8270, 8084, 7896, 7710, 7527, 7347, 7156, 6977, 6788, 6607, 6422, 6236, 6054, 5867, 5684, 5495, 5305, 5128, 4945, 4751, 4638, 4442, 4248, 4065, 3888, 3698, 3509, 3326, 3139, 2957, 2775, 2586, 2405, 2216, 2037, 1846, 1666, 1483, 1297, 1109, 927, 735, 554, 375, 201, 128, 0};
            n = nArray[0][2] + nArray[0][53] + nArray[1][0] + nArray[1][63];
        } else if (nArray[0] != null) {
            nArray3 = new int[]{510, 505, 422, 380, 355, 338, 326, 318, 311, 305, 300, 297, 293, 291, 288, 286, 284, 283, 281, 280, 279, 278, 277, 273, 262, 251, 243, 233, 225, 218, 211, 205, 198, 193, 186, 181, 177, 172, 168, 164, 158, 156, 152, 148, 145, 142, 139, 136, 133, 131, 129, 126, 123, 120, 118, 115, 113, 110, 107, 105, 102, 100, 97, 94, 92, 89, 87, 83, 81, 79, 76, 74, 70, 68, 66, 63, 61, 57, 55, 52, 50, 48, 44, 42, 39, 37, 34, 31, 29, 26, 24, 21, 18, 16, 13, 11, 8, 6, 3, 2, 0};
            nArray2 = new int[]{16320, 16315, 15946, 15277, 14655, 14073, 13623, 13230, 12859, 12560, 12240, 11861, 11456, 11081, 10714, 10360, 10027, 9679, 9368, 9056, 8680, 8331, 7995, 7668, 7376, 7084, 6823, 6562, 6345, 6125, 5939, 5756, 5571, 5421, 5240, 5086, 4976, 4829, 4719, 4616, 4463, 4393, 4280, 4166, 4092, 3980, 3909, 3835, 3755, 3688, 3621, 3541, 3467, 3396, 3323, 3247, 3170, 3096, 3021, 2952, 2874, 2804, 2727, 2657, 2583, 2509, 2437, 2362, 2290, 2211, 2136, 2068, 1996, 1915, 1858, 1773, 1692, 1620, 1552, 1477, 1398, 1326, 1251, 1179, 1109, 1031, 961, 884, 814, 736, 667, 592, 518, 441, 369, 292, 221, 151, 86, 64, 0};
            n = nArray[0][2] + nArray[0][53];
        } else {
            return -1;
        }
        for (int i = 0; i < 100; ++i) {
            if (n < nArray3[i] && n2 < nArray2[i]) continue;
            if ((n > nArray3[i] || n2 > nArray2[i]) && i < 50) break;
            return i + 1;
        }
        return -1;
    }

    public static JPEGQTable[] getQTables(List<JPEGSegment> list) throws IOException {
        int[][] nArray = JPEGQuality.getQuantizationTables(list);
        ArrayList<JPEGQTable> arrayList = new ArrayList<JPEGQTable>();
        for (int[] nArray2 : nArray) {
            if (nArray2 == null) continue;
            arrayList.add(new JPEGQTable(nArray2));
        }
        return arrayList.toArray(new JPEGQTable[arrayList.size()]);
    }

    private static int[][] getQuantizationTables(List<JPEGSegment> list) throws IOException {
        Validate.notNull(list, (String)"segments");
        int[][] nArrayArray = new int[4][];
        for (JPEGSegment jPEGSegment : list) {
            if (jPEGSegment.marker() != 65499) continue;
            DataInputStream dataInputStream = new DataInputStream(jPEGSegment.data());
            int n = 0;
            block5: while (n < jPEGSegment.length()) {
                int n2 = dataInputStream.read();
                ++n;
                int n3 = n2 & 0xF;
                int n4 = n2 >> 4;
                if (n3 >= 4) {
                    throw new IIOException("Bad DQT table index: " + n3);
                }
                if (nArrayArray[n3] != null) {
                    throw new IIOException("Duplicate DQT table index: " + n3);
                }
                if (n4 < 0 || n4 > 1) {
                    throw new IIOException("Bad DQT bit info: " + n4);
                }
                byte[] byArray = new byte[64 * (n4 + 1)];
                dataInputStream.readFully(byArray);
                n += byArray.length;
                nArrayArray[n3] = new int[64];
                switch (n4) {
                    case 0: {
                        int n5;
                        int n6 = byArray.length;
                        for (n5 = 0; n5 < n6; ++n5) {
                            nArrayArray[n3][n5] = (short)(byArray[n5] & 0xFF);
                        }
                        continue block5;
                    }
                    case 1: {
                        int n5;
                        int n6 = byArray.length;
                        for (n5 = 0; n5 < n6; n5 += 2) {
                            nArrayArray[n3][n5 / 2] = (short)((byArray[n5] & 0xFF) << 8 | byArray[n5 + 1] & 0xFF);
                        }
                        break;
                    }
                }
            }
        }
        return nArrayArray;
    }

    public static void main(String[] stringArray) throws IOException {
        for (String string : stringArray) {
            float f = JPEGQuality.getJPEGQuality(ImageIO.createImageInputStream(new File(string)));
            System.err.println(string + " quality: " + f + "/" + (int)(f * 100.0f));
        }
    }
}

