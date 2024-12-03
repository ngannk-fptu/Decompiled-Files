/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.batik.ext.awt.image.GraphicsUtil;

public class IndexImage {
    static byte[][] computeRGB(int nCubes, Cube[] cubes) {
        byte[] r = new byte[nCubes];
        byte[] g = new byte[nCubes];
        byte[] b = new byte[nCubes];
        byte[] rgb = new byte[3];
        for (int i = 0; i < nCubes; ++i) {
            rgb = cubes[i].averageColorRGB(rgb);
            r[i] = rgb[0];
            g[i] = rgb[1];
            b[i] = rgb[2];
        }
        byte[][] result = new byte[][]{r, g, b};
        return result;
    }

    static void logRGB(byte[] r, byte[] g, byte[] b) {
        StringBuffer buff = new StringBuffer(100);
        int nColors = r.length;
        for (int i = 0; i < nColors; ++i) {
            String rgbStr = "(" + (r[i] + 128) + ',' + (g[i] + 128) + ',' + (b[i] + 128) + "),";
            buff.append(rgbStr);
        }
        System.out.println("RGB:" + nColors + buff);
    }

    static List[] createColorList(BufferedImage bi) {
        int w = bi.getWidth();
        int h = bi.getHeight();
        List[] colors = new ArrayList[4096];
        for (int i_w = 0; i_w < w; ++i_w) {
            block1: for (int i_h = 0; i_h < h; ++i_h) {
                int rgb = bi.getRGB(i_w, i_h) & 0xFFFFFF;
                int idx = (rgb & 0xF00000) >>> 12 | (rgb & 0xF000) >>> 8 | (rgb & 0xF0) >>> 4;
                ArrayList<Counter> v = colors[idx];
                if (v == null) {
                    v = new ArrayList<Counter>();
                    v.add(new Counter(rgb));
                    colors[idx] = v;
                    continue;
                }
                Iterator i = v.iterator();
                while (i.hasNext()) {
                    if (!((Counter)i.next()).add(rgb)) continue;
                    continue block1;
                }
                v.add(new Counter(rgb));
            }
        }
        return colors;
    }

    static Counter[][] convertColorList(List[] colors) {
        Counter[] EMPTY_COUNTER = new Counter[]{};
        Counter[][] colorTbl = new Counter[4096][];
        for (int i = 0; i < colors.length; ++i) {
            List cl = colors[i];
            if (cl == null) {
                colorTbl[i] = EMPTY_COUNTER;
                continue;
            }
            int nSlots = cl.size();
            colorTbl[i] = cl.toArray(new Counter[nSlots]);
            colors[i] = null;
        }
        return colorTbl;
    }

    public static BufferedImage getIndexedImage(BufferedImage bi, int nColors) {
        int bits;
        int w = bi.getWidth();
        int h = bi.getHeight();
        List[] colors = IndexImage.createColorList(bi);
        Counter[][] colorTbl = IndexImage.convertColorList(colors);
        colors = null;
        int nCubes = 1;
        int fCube = 0;
        Cube[] cubes = new Cube[nColors];
        cubes[0] = new Cube(colorTbl, w * h);
        while (nCubes < nColors) {
            int i;
            while (cubes[fCube].isDone() && ++fCube != nCubes) {
            }
            if (fCube == nCubes) break;
            Cube c = cubes[fCube];
            Cube nc = c.split();
            if (nc == null) continue;
            if (nc.count > c.count) {
                Cube tmp = c;
                c = nc;
                nc = tmp;
            }
            int j = fCube;
            int cnt = c.count;
            for (i = fCube + 1; i < nCubes && cubes[i].count >= cnt; ++i) {
                cubes[j++] = cubes[i];
            }
            cubes[j++] = c;
            cnt = nc.count;
            while (j < nCubes && cubes[j].count >= cnt) {
                ++j;
            }
            for (i = nCubes; i > j; --i) {
                cubes[i] = cubes[i - 1];
            }
            cubes[j++] = nc;
            ++nCubes;
        }
        byte[][] rgbTbl = IndexImage.computeRGB(nCubes, cubes);
        IndexColorModel icm = new IndexColorModel(8, nCubes, rgbTbl[0], rgbTbl[1], rgbTbl[2]);
        BufferedImage indexed = new BufferedImage(w, h, 13, icm);
        Graphics2D g2d = indexed.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.drawImage((Image)bi, 0, 0, null);
        g2d.dispose();
        for (bits = 1; bits <= 8 && 1 << bits < nCubes; ++bits) {
        }
        if (bits > 4) {
            return indexed;
        }
        if (bits == 3) {
            bits = 4;
        }
        IndexColorModel cm = new IndexColorModel(bits, nCubes, rgbTbl[0], rgbTbl[1], rgbTbl[2]);
        MultiPixelPackedSampleModel sm = new MultiPixelPackedSampleModel(0, w, h, bits);
        WritableRaster ras = Raster.createWritableRaster(sm, new Point(0, 0));
        bi = indexed;
        indexed = new BufferedImage(cm, ras, bi.isAlphaPremultiplied(), null);
        GraphicsUtil.copyData(bi, indexed);
        return indexed;
    }

    private static class Cube {
        static final byte[] RGB_BLACK = new byte[]{0, 0, 0};
        int[] min = new int[]{0, 0, 0};
        int[] max = new int[]{255, 255, 255};
        boolean done = false;
        final Counter[][] colors;
        int count = 0;
        static final int RED = 0;
        static final int GRN = 1;
        static final int BLU = 2;

        Cube(Counter[][] colors, int count) {
            this.colors = colors;
            this.count = count;
        }

        public boolean isDone() {
            return this.done;
        }

        private boolean contains(int[] val) {
            int vRed = val[0];
            int vGrn = val[1];
            int vBlu = val[2];
            return this.min[0] <= vRed && vRed <= this.max[0] && this.min[1] <= vGrn && vGrn <= this.max[1] && this.min[2] <= vBlu && vBlu <= this.max[2];
        }

        Cube split() {
            int c1;
            int c0;
            int splitChannel;
            int dr = this.max[0] - this.min[0] + 1;
            int dg = this.max[1] - this.min[1] + 1;
            int db = this.max[2] - this.min[2] + 1;
            if (dr >= dg) {
                if (dr >= db) {
                    splitChannel = 0;
                    c0 = 1;
                    c1 = 2;
                } else {
                    splitChannel = 2;
                    c0 = 0;
                    c1 = 1;
                }
            } else if (dg >= db) {
                splitChannel = 1;
                c0 = 0;
                c1 = 2;
            } else {
                splitChannel = 2;
                c0 = 1;
                c1 = 0;
            }
            Cube ret = this.splitChannel(splitChannel, c0, c1);
            if (ret != null) {
                return ret;
            }
            ret = this.splitChannel(c0, splitChannel, c1);
            if (ret != null) {
                return ret;
            }
            ret = this.splitChannel(c1, splitChannel, c0);
            if (ret != null) {
                return ret;
            }
            this.done = true;
            return null;
        }

        private void normalize(int splitChannel, int[] counts) {
            boolean flagChangedHi;
            int i;
            if (this.count == 0) {
                return;
            }
            int iMin = this.min[splitChannel];
            int iMax = this.max[splitChannel];
            int loBound = -1;
            int hiBound = -1;
            for (i = iMin; i <= iMax; ++i) {
                if (counts[i] == 0) continue;
                loBound = i;
                break;
            }
            for (i = iMax; i >= iMin; --i) {
                if (counts[i] == 0) continue;
                hiBound = i;
                break;
            }
            boolean flagChangedLo = loBound != -1 && iMin != loBound;
            boolean bl = flagChangedHi = hiBound != -1 && iMax != hiBound;
            if (flagChangedLo) {
                this.min[splitChannel] = loBound;
            }
            if (flagChangedHi) {
                this.max[splitChannel] = hiBound;
            }
        }

        Cube splitChannel(int splitChannel, int c0, int c1) {
            if (this.min[splitChannel] == this.max[splitChannel]) {
                return null;
            }
            if (this.count == 0) {
                return null;
            }
            int half = this.count / 2;
            int[] counts = this.computeCounts(splitChannel, c0, c1);
            int tcount = 0;
            int lastAdd = -1;
            int splitLo = this.min[splitChannel];
            int splitHi = this.max[splitChannel];
            for (int i = this.min[splitChannel]; i <= this.max[splitChannel]; ++i) {
                int c = counts[i];
                if (c == 0) {
                    if (tcount != 0 || i >= this.max[splitChannel]) continue;
                    this.min[splitChannel] = i + 1;
                    continue;
                }
                if (tcount + c < half) {
                    lastAdd = i;
                    tcount += c;
                    continue;
                }
                if (half - tcount <= tcount + c - half) {
                    if (lastAdd == -1) {
                        if (c == this.count) {
                            this.max[splitChannel] = i;
                            return null;
                        }
                        splitLo = i;
                        splitHi = i + 1;
                        tcount += c;
                        break;
                    }
                    splitLo = lastAdd;
                    splitHi = i;
                    break;
                }
                if (i == this.max[splitChannel]) {
                    if (c == this.count) {
                        return null;
                    }
                    splitLo = lastAdd;
                    splitHi = i;
                    break;
                }
                tcount += c;
                splitLo = i;
                splitHi = i + 1;
                break;
            }
            Cube ret = new Cube(this.colors, tcount);
            this.count -= tcount;
            ret.min[splitChannel] = this.min[splitChannel];
            ret.max[splitChannel] = splitLo;
            this.min[splitChannel] = splitHi;
            ret.min[c0] = this.min[c0];
            ret.max[c0] = this.max[c0];
            ret.min[c1] = this.min[c1];
            ret.max[c1] = this.max[c1];
            this.normalize(splitChannel, counts);
            ret.normalize(splitChannel, counts);
            return ret;
        }

        private int[] computeCounts(int splitChannel, int c0, int c1) {
            int splitSh4 = (2 - splitChannel) * 4;
            int c0Sh4 = (2 - c0) * 4;
            int c1Sh4 = (2 - c1) * 4;
            int half = this.count / 2;
            int[] counts = new int[256];
            int tcount = 0;
            int minR = this.min[0];
            int minG = this.min[1];
            int minB = this.min[2];
            int maxR = this.max[0];
            int maxG = this.max[1];
            int maxB = this.max[2];
            int[] minIdx = new int[]{minR >> 4, minG >> 4, minB >> 4};
            int[] maxIdx = new int[]{maxR >> 4, maxG >> 4, maxB >> 4};
            int[] vals = new int[]{0, 0, 0};
            for (int i = minIdx[splitChannel]; i <= maxIdx[splitChannel]; ++i) {
                int idx1 = i << splitSh4;
                for (int j = minIdx[c0]; j <= maxIdx[c0]; ++j) {
                    int idx2 = idx1 | j << c0Sh4;
                    for (int k = minIdx[c1]; k <= maxIdx[c1]; ++k) {
                        Counter[] v;
                        int idx = idx2 | k << c1Sh4;
                        for (Counter c : v = this.colors[idx]) {
                            if (!this.contains(vals = c.getRgb(vals))) continue;
                            int n = vals[splitChannel];
                            counts[n] = counts[n] + c.count;
                            tcount += c.count;
                        }
                    }
                }
            }
            return counts;
        }

        public String toString() {
            return "Cube: [" + this.min[0] + '-' + this.max[0] + "] [" + this.min[1] + '-' + this.max[1] + "] [" + this.min[2] + '-' + this.max[2] + "] n:" + this.count;
        }

        public int averageColor() {
            if (this.count == 0) {
                return 0;
            }
            byte[] rgb = this.averageColorRGB(null);
            return rgb[0] << 16 & 0xFF0000 | rgb[1] << 8 & 0xFF00 | rgb[2] & 0xFF;
        }

        public byte[] averageColorRGB(byte[] rgb) {
            if (this.count == 0) {
                return RGB_BLACK;
            }
            float red = 0.0f;
            float grn = 0.0f;
            float blu = 0.0f;
            int minR = this.min[0];
            int minG = this.min[1];
            int minB = this.min[2];
            int maxR = this.max[0];
            int maxG = this.max[1];
            int maxB = this.max[2];
            int[] minIdx = new int[]{minR >> 4, minG >> 4, minB >> 4};
            int[] maxIdx = new int[]{maxR >> 4, maxG >> 4, maxB >> 4};
            int[] vals = new int[3];
            for (int i = minIdx[0]; i <= maxIdx[0]; ++i) {
                int idx1 = i << 8;
                for (int j = minIdx[1]; j <= maxIdx[1]; ++j) {
                    int idx2 = idx1 | j << 4;
                    for (int k = minIdx[2]; k <= maxIdx[2]; ++k) {
                        Counter[] v;
                        int idx = idx2 | k;
                        for (Counter c : v = this.colors[idx]) {
                            if (!this.contains(vals = c.getRgb(vals))) continue;
                            float weight = (float)c.count / (float)this.count;
                            red += (float)vals[0] * weight;
                            grn += (float)vals[1] * weight;
                            blu += (float)vals[2] * weight;
                        }
                    }
                }
            }
            byte[] result = rgb == null ? new byte[3] : rgb;
            result[0] = (byte)(red + 0.5f);
            result[1] = (byte)(grn + 0.5f);
            result[2] = (byte)(blu + 0.5f);
            return result;
        }
    }

    private static class Counter {
        final int val;
        int count = 1;

        Counter(int val) {
            this.val = val;
        }

        boolean add(int val) {
            if (this.val != val) {
                return false;
            }
            ++this.count;
            return true;
        }

        int[] getRgb(int[] rgb) {
            rgb[0] = (this.val & 0xFF0000) >> 16;
            rgb[1] = (this.val & 0xFF00) >> 8;
            rgb[2] = this.val & 0xFF;
            return rgb;
        }
    }
}

