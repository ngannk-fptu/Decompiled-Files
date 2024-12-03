/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.TTFDataStream;

public class KerningSubtable {
    private static final Log LOG = LogFactory.getLog(KerningSubtable.class);
    private static final int COVERAGE_HORIZONTAL = 1;
    private static final int COVERAGE_MINIMUMS = 2;
    private static final int COVERAGE_CROSS_STREAM = 4;
    private static final int COVERAGE_FORMAT = 65280;
    private static final int COVERAGE_HORIZONTAL_SHIFT = 0;
    private static final int COVERAGE_MINIMUMS_SHIFT = 1;
    private static final int COVERAGE_CROSS_STREAM_SHIFT = 2;
    private static final int COVERAGE_FORMAT_SHIFT = 8;
    private boolean horizontal;
    private boolean minimums;
    private boolean crossStream;
    private PairData pairs;

    KerningSubtable() {
    }

    void read(TTFDataStream data, int version) throws IOException {
        if (version == 0) {
            this.readSubtable0(data);
        } else if (version == 1) {
            this.readSubtable1(data);
        } else {
            throw new IllegalStateException();
        }
    }

    public boolean isHorizontalKerning() {
        return this.isHorizontalKerning(false);
    }

    public boolean isHorizontalKerning(boolean cross) {
        if (!this.horizontal) {
            return false;
        }
        if (this.minimums) {
            return false;
        }
        if (cross) {
            return this.crossStream;
        }
        return !this.crossStream;
    }

    public int[] getKerning(int[] glyphs) {
        int[] kerning = null;
        if (this.pairs != null) {
            int ng = glyphs.length;
            kerning = new int[ng];
            for (int i = 0; i < ng; ++i) {
                int l = glyphs[i];
                int r = -1;
                for (int k = i + 1; k < ng; ++k) {
                    int g = glyphs[k];
                    if (g < 0) continue;
                    r = g;
                    break;
                }
                kerning[i] = this.getKerning(l, r);
            }
        } else {
            LOG.warn((Object)"No kerning subtable data available due to an unsupported kerning subtable version");
        }
        return kerning;
    }

    public int getKerning(int l, int r) {
        if (this.pairs == null) {
            LOG.warn((Object)"No kerning subtable data available due to an unsupported kerning subtable version");
            return 0;
        }
        return this.pairs.getKerning(l, r);
    }

    private void readSubtable0(TTFDataStream data) throws IOException {
        int format;
        int version = data.readUnsignedShort();
        if (version != 0) {
            LOG.info((Object)("Unsupported kerning sub-table version: " + version));
            return;
        }
        int length = data.readUnsignedShort();
        if (length < 6) {
            LOG.warn((Object)("Kerning sub-table too short, got " + length + " bytes, expect 6 or more."));
            return;
        }
        int coverage = data.readUnsignedShort();
        if (KerningSubtable.isBitsSet(coverage, 1, 0)) {
            this.horizontal = true;
        }
        if (KerningSubtable.isBitsSet(coverage, 2, 1)) {
            this.minimums = true;
        }
        if (KerningSubtable.isBitsSet(coverage, 4, 2)) {
            this.crossStream = true;
        }
        if ((format = KerningSubtable.getBits(coverage, 65280, 8)) == 0) {
            this.readSubtable0Format0(data);
        } else if (format == 2) {
            this.readSubtable0Format2(data);
        } else {
            LOG.debug((Object)("Skipped kerning subtable due to an unsupported kerning subtable version: " + format));
        }
    }

    private void readSubtable0Format0(TTFDataStream data) throws IOException {
        this.pairs = new PairData0Format0();
        this.pairs.read(data);
    }

    private void readSubtable0Format2(TTFDataStream data) {
        LOG.info((Object)"Kerning subtable format 2 not yet supported.");
    }

    private void readSubtable1(TTFDataStream data) {
        LOG.info((Object)"Kerning subtable format 1 not yet supported.");
    }

    private static boolean isBitsSet(int bits, int mask, int shift) {
        return KerningSubtable.getBits(bits, mask, shift) != 0;
    }

    private static int getBits(int bits, int mask, int shift) {
        return (bits & mask) >> shift;
    }

    private static class PairData0Format0
    implements Comparator<int[]>,
    PairData {
        private int searchRange;
        private int[][] pairs;

        private PairData0Format0() {
        }

        @Override
        public void read(TTFDataStream data) throws IOException {
            int numPairs = data.readUnsignedShort();
            this.searchRange = data.readUnsignedShort() / 6;
            int entrySelector = data.readUnsignedShort();
            int rangeShift = data.readUnsignedShort();
            this.pairs = new int[numPairs][3];
            for (int i = 0; i < numPairs; ++i) {
                int left = data.readUnsignedShort();
                int right = data.readUnsignedShort();
                short value = data.readSignedShort();
                this.pairs[i][0] = left;
                this.pairs[i][1] = right;
                this.pairs[i][2] = value;
            }
        }

        @Override
        public int getKerning(int l, int r) {
            int[] key = new int[]{l, r, 0};
            int index = Arrays.binarySearch(this.pairs, key, this);
            if (index >= 0) {
                return this.pairs[index][2];
            }
            return 0;
        }

        @Override
        public int compare(int[] p1, int[] p2) {
            assert (p1 != null);
            assert (p1.length >= 2);
            assert (p2 != null);
            assert (p2.length >= 2);
            int l1 = p1[0];
            int l2 = p2[0];
            if (l1 < l2) {
                return -1;
            }
            if (l1 > l2) {
                return 1;
            }
            int r1 = p1[1];
            int r2 = p2[1];
            if (r1 < r2) {
                return -1;
            }
            if (r1 > r2) {
                return 1;
            }
            return 0;
        }
    }

    private static interface PairData {
        public void read(TTFDataStream var1) throws IOException;

        public int getKerning(int var1, int var2);
    }
}

