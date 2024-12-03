/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.palette;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.palette.ColorSpaceSubset;
import org.apache.commons.imaging.palette.LongestAxisMedianCut;
import org.apache.commons.imaging.palette.MedianCutQuantizer;
import org.apache.commons.imaging.palette.Palette;
import org.apache.commons.imaging.palette.QuantizedPalette;
import org.apache.commons.imaging.palette.SimplePalette;

public class PaletteFactory {
    private static final Logger LOGGER = Logger.getLogger(PaletteFactory.class.getName());
    public static final int COMPONENTS = 3;

    public Palette makeExactRgbPaletteFancy(BufferedImage src) {
        int mask;
        byte[] rgbmap = new byte[0x200000];
        int width = src.getWidth();
        int height = src.getHeight();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int argb = src.getRGB(x, y);
                int rggbb = 0x1FFFFF & argb;
                int highred = 7 & argb >> 21;
                mask = 1 << highred;
                int n = rggbb;
                rgbmap[n] = (byte)(rgbmap[n] | mask);
            }
        }
        int count = 0;
        for (byte element : rgbmap) {
            int eight = 0xFF & element;
            count += Integer.bitCount(eight);
        }
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("Used colors: " + count);
        }
        int[] colormap = new int[count];
        int mapsize = 0;
        for (int i = 0; i < rgbmap.length; ++i) {
            int eight = 0xFF & rgbmap[i];
            mask = 128;
            for (int j = 0; j < 8; ++j) {
                int bit = eight & mask;
                mask >>>= 1;
                if (bit <= 0) continue;
                int rgb = i | 7 - j << 21;
                colormap[mapsize++] = rgb;
            }
        }
        Arrays.sort(colormap);
        return new SimplePalette(colormap);
    }

    private int pixelToQuantizationTableIndex(int argb, int precision) {
        int result = 0;
        int precisionMask = (1 << precision) - 1;
        for (int i = 0; i < 3; ++i) {
            int sample = argb & 0xFF;
            argb >>= 8;
            result = result << precision | (sample >>= 8 - precision) & precisionMask;
        }
        return result;
    }

    private int getFrequencyTotal(int[] table, int[] mins, int[] maxs, int precision) {
        int sum = 0;
        for (int blue = mins[2]; blue <= maxs[2]; ++blue) {
            int b = blue << 2 * precision;
            for (int green = mins[1]; green <= maxs[1]; ++green) {
                int g = green << 1 * precision;
                for (int red = mins[0]; red <= maxs[0]; ++red) {
                    int index = b | g | red;
                    sum += table[index];
                }
            }
        }
        return sum;
    }

    private DivisionCandidate finishDivision(ColorSpaceSubset subset, int component, int precision, int sum, int slice) {
        if (LOGGER.isLoggable(Level.FINEST)) {
            subset.dump("trying (" + component + "): ");
        }
        int total = subset.total;
        if (slice < subset.mins[component] || slice >= subset.maxs[component]) {
            return null;
        }
        if (sum < 1 || sum >= total) {
            return null;
        }
        int remainder = total - sum;
        if (remainder < 1 || remainder >= total) {
            return null;
        }
        int[] sliceMins = new int[subset.mins.length];
        System.arraycopy(subset.mins, 0, sliceMins, 0, subset.mins.length);
        int[] sliceMaxs = new int[subset.maxs.length];
        System.arraycopy(subset.maxs, 0, sliceMaxs, 0, subset.maxs.length);
        sliceMaxs[component] = slice;
        sliceMins[component] = slice + 1;
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("total: " + total);
            LOGGER.finest("first total: " + sum);
            LOGGER.finest("second total: " + (total - sum));
            LOGGER.finest("slice: " + slice);
        }
        ColorSpaceSubset first = new ColorSpaceSubset(sum, precision, subset.mins, sliceMaxs);
        ColorSpaceSubset second = new ColorSpaceSubset(total - sum, precision, sliceMins, subset.maxs);
        return new DivisionCandidate(first, second);
    }

    private List<DivisionCandidate> divideSubset2(int[] table, ColorSpaceSubset subset, int component, int precision) {
        if (LOGGER.isLoggable(Level.FINEST)) {
            subset.dump("trying (" + component + "): ");
        }
        int total = subset.total;
        int[] sliceMins = new int[subset.mins.length];
        System.arraycopy(subset.mins, 0, sliceMins, 0, subset.mins.length);
        int[] sliceMaxs = new int[subset.maxs.length];
        System.arraycopy(subset.maxs, 0, sliceMaxs, 0, subset.maxs.length);
        int sum1 = 0;
        int last = 0;
        int slice1 = subset.mins[component];
        while (slice1 != subset.maxs[component] + 1) {
            sliceMins[component] = slice1;
            sliceMaxs[component] = slice1++;
            last = this.getFrequencyTotal(table, sliceMins, sliceMaxs, precision);
            if ((sum1 += last) >= total / 2) break;
        }
        int sum2 = sum1 - last;
        int slice2 = slice1 - 1;
        DivisionCandidate dc1 = this.finishDivision(subset, component, precision, sum1, slice1);
        DivisionCandidate dc2 = this.finishDivision(subset, component, precision, sum2, slice2);
        ArrayList<DivisionCandidate> result = new ArrayList<DivisionCandidate>();
        if (dc1 != null) {
            result.add(dc1);
        }
        if (dc2 != null) {
            result.add(dc2);
        }
        return result;
    }

    private DivisionCandidate divideSubset2(int[] table, ColorSpaceSubset subset, int precision) {
        ArrayList<DivisionCandidate> dcs = new ArrayList<DivisionCandidate>();
        dcs.addAll(this.divideSubset2(table, subset, 0, precision));
        dcs.addAll(this.divideSubset2(table, subset, 1, precision));
        dcs.addAll(this.divideSubset2(table, subset, 2, precision));
        DivisionCandidate bestV = null;
        double bestScore = Double.MAX_VALUE;
        for (DivisionCandidate dc : dcs) {
            ColorSpaceSubset first = dc.dst_a;
            ColorSpaceSubset second = dc.dst_b;
            int area1 = first.total;
            int area2 = second.total;
            int diff = Math.abs(area1 - area2);
            double score = (double)diff / (double)Math.max(area1, area2);
            if (bestV == null) {
                bestV = dc;
                bestScore = score;
                continue;
            }
            if (!(score < bestScore)) continue;
            bestV = dc;
            bestScore = score;
        }
        return bestV;
    }

    private List<ColorSpaceSubset> divide(List<ColorSpaceSubset> v, int desiredCount, int[] table, int precision) {
        ArrayList<ColorSpaceSubset> ignore = new ArrayList<ColorSpaceSubset>();
        do {
            DivisionCandidate dc;
            int maxArea = -1;
            ColorSpaceSubset maxSubset = null;
            for (ColorSpaceSubset subset : v) {
                if (ignore.contains(subset)) continue;
                int area = subset.total;
                if (maxSubset == null) {
                    maxSubset = subset;
                    maxArea = area;
                    continue;
                }
                if (area <= maxArea) continue;
                maxSubset = subset;
                maxArea = area;
            }
            if (maxSubset == null) {
                return v;
            }
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("\tarea: " + maxArea);
            }
            if ((dc = this.divideSubset2(table, maxSubset, precision)) != null) {
                v.remove(maxSubset);
                v.add(dc.dst_a);
                v.add(dc.dst_b);
                continue;
            }
            ignore.add(maxSubset);
        } while (v.size() != desiredCount);
        return v;
    }

    public Palette makeQuantizedRgbPalette(BufferedImage src, int max) {
        int precision = 6;
        int tableScale = 18;
        int tableSize = 262144;
        int[] table = new int[262144];
        int width = src.getWidth();
        int height = src.getHeight();
        List<ColorSpaceSubset> subsets = new ArrayList<ColorSpaceSubset>();
        ColorSpaceSubset all = new ColorSpaceSubset(width * height, 6);
        subsets.add(all);
        if (LOGGER.isLoggable(Level.FINEST)) {
            int preTotal = this.getFrequencyTotal(table, all.mins, all.maxs, 6);
            LOGGER.finest("pre total: " + preTotal);
        }
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int index;
                int argb = src.getRGB(x, y);
                int n = index = this.pixelToQuantizationTableIndex(argb, 6);
                table[n] = table[n] + 1;
            }
        }
        if (LOGGER.isLoggable(Level.FINEST)) {
            int allTotal = this.getFrequencyTotal(table, all.mins, all.maxs, 6);
            LOGGER.finest("all total: " + allTotal);
            LOGGER.finest("width * height: " + width * height);
        }
        subsets = this.divide(subsets, max, table, 6);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("subsets: " + subsets.size());
            LOGGER.finest("width*height: " + width * height);
        }
        for (int i = 0; i < subsets.size(); ++i) {
            ColorSpaceSubset subset = subsets.get(i);
            subset.setAverageRGB(table);
            if (!LOGGER.isLoggable(Level.FINEST)) continue;
            subset.dump(i + ": ");
        }
        Collections.sort(subsets, ColorSpaceSubset.RGB_COMPARATOR);
        return new QuantizedPalette(subsets, 6);
    }

    public Palette makeQuantizedRgbaPalette(BufferedImage src, boolean transparent, int max) throws ImageWriteException {
        return new MedianCutQuantizer(!transparent).process(src, max, new LongestAxisMedianCut());
    }

    public SimplePalette makeExactRgbPaletteSimple(BufferedImage src, int max) {
        int rgb;
        HashSet<Integer> rgbs = new HashSet<Integer>();
        int width = src.getWidth();
        int height = src.getHeight();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int argb = src.getRGB(x, y);
                rgb = 0xFFFFFF & argb;
                if (!rgbs.add(rgb) || rgbs.size() <= max) continue;
                return null;
            }
        }
        int[] result = new int[rgbs.size()];
        int next = 0;
        Iterator iterator = rgbs.iterator();
        while (iterator.hasNext()) {
            rgb = (Integer)iterator.next();
            result[next++] = rgb;
        }
        Arrays.sort(result);
        return new SimplePalette(result);
    }

    public boolean isGrayscale(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        if (6 == src.getColorModel().getColorSpace().getType()) {
            return true;
        }
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int argb = src.getRGB(x, y);
                int red = 0xFF & argb >> 16;
                int green = 0xFF & argb >> 8;
                int blue = 0xFF & argb >> 0;
                if (red == green && red == blue) continue;
                return false;
            }
        }
        return true;
    }

    public boolean hasTransparency(BufferedImage src) {
        return this.hasTransparency(src, 255);
    }

    public boolean hasTransparency(BufferedImage src, int threshold) {
        int width = src.getWidth();
        int height = src.getHeight();
        if (!src.getColorModel().hasAlpha()) {
            return false;
        }
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int argb = src.getRGB(x, y);
                int alpha = 0xFF & argb >> 24;
                if (alpha >= threshold) continue;
                return true;
            }
        }
        return false;
    }

    public int countTrasparentColors(int[] rgbs) {
        int first = -1;
        for (int rgb : rgbs) {
            int alpha = 0xFF & rgb >> 24;
            if (alpha >= 255) continue;
            if (first < 0) {
                first = rgb;
                continue;
            }
            if (rgb == first) continue;
            return 2;
        }
        if (first < 0) {
            return 0;
        }
        return 1;
    }

    public int countTransparentColors(BufferedImage src) {
        ColorModel cm = src.getColorModel();
        if (!cm.hasAlpha()) {
            return 0;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        int first = -1;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int rgb = src.getRGB(x, y);
                int alpha = 0xFF & rgb >> 24;
                if (alpha >= 255) continue;
                if (first < 0) {
                    first = rgb;
                    continue;
                }
                if (rgb == first) continue;
                return 2;
            }
        }
        if (first < 0) {
            return 0;
        }
        return 1;
    }

    private static class DivisionCandidate {
        private final ColorSpaceSubset dst_a;
        private final ColorSpaceSubset dst_b;

        DivisionCandidate(ColorSpaceSubset dst_a, ColorSpaceSubset dst_b) {
            this.dst_a = dst_a;
            this.dst_b = dst_b;
        }
    }
}

