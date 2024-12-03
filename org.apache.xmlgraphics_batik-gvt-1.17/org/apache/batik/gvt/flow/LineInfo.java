/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.flow;

import java.awt.geom.Point2D;
import org.apache.batik.gvt.flow.BlockInfo;
import org.apache.batik.gvt.flow.FlowRegions;
import org.apache.batik.gvt.flow.GlyphGroupInfo;
import org.apache.batik.gvt.flow.WordInfo;
import org.apache.batik.gvt.font.GVTGlyphVector;

public class LineInfo {
    FlowRegions fr;
    double lineHeight = -1.0;
    double ascent = -1.0;
    double descent = -1.0;
    double hLeading = -1.0;
    double baseline;
    int numGlyphs;
    int words = 0;
    int size = 0;
    GlyphGroupInfo[] ggis = null;
    int newSize = 0;
    GlyphGroupInfo[] newGGIS = null;
    int numRanges;
    double[] ranges;
    double[] rangeAdv;
    BlockInfo bi = null;
    boolean paraStart;
    boolean paraEnd;
    protected static final int FULL_WORD = 0;
    protected static final int FULL_ADV = 1;
    static final float MAX_COMPRESS = 0.1f;
    static final float COMRESS_SCALE = 3.0f;

    public LineInfo(FlowRegions fr, BlockInfo bi, boolean paraStart) {
        this.fr = fr;
        this.bi = bi;
        this.lineHeight = bi.getLineHeight();
        this.ascent = bi.getAscent();
        this.descent = bi.getDescent();
        this.hLeading = (this.lineHeight - (this.ascent + this.descent)) / 2.0;
        this.baseline = (float)(fr.getCurrentY() + this.hLeading + this.ascent);
        this.paraStart = paraStart;
        this.paraEnd = false;
        if (this.lineHeight > 0.0) {
            fr.newLineHeight(this.lineHeight);
            this.updateRangeInfo();
        }
    }

    public void setParaEnd(boolean paraEnd) {
        this.paraEnd = paraEnd;
    }

    public boolean addWord(WordInfo wi) {
        double nlh = wi.getLineHeight();
        if (nlh <= this.lineHeight) {
            return this.insertWord(wi);
        }
        this.fr.newLineHeight(nlh);
        if (!this.updateRangeInfo()) {
            if (this.lineHeight > 0.0) {
                this.fr.newLineHeight(this.lineHeight);
            }
            return false;
        }
        if (!this.insertWord(wi)) {
            if (this.lineHeight > 0.0) {
                this.setLineHeight(this.lineHeight);
            }
            return false;
        }
        this.lineHeight = nlh;
        if ((double)wi.getAscent() > this.ascent) {
            this.ascent = wi.getAscent();
        }
        if ((double)wi.getDescent() > this.descent) {
            this.descent = wi.getDescent();
        }
        this.hLeading = (nlh - (this.ascent + this.descent)) / 2.0;
        this.baseline = (float)(this.fr.getCurrentY() + this.hLeading + this.ascent);
        return true;
    }

    public boolean insertWord(WordInfo wi) {
        this.mergeGlyphGroups(wi);
        if (!this.assignGlyphGroupRanges(this.newSize, this.newGGIS)) {
            return false;
        }
        this.swapGlyphGroupInfo();
        return true;
    }

    public boolean assignGlyphGroupRanges(int ggSz, GlyphGroupInfo[] ggis) {
        int i = 0;
        for (int r = 0; r < this.numRanges; ++r) {
            GlyphGroupInfo ggi;
            double range = this.ranges[2 * r + 1] - this.ranges[2 * r];
            float adv = 0.0f;
            float rangeAdvance = 0.0f;
            while (i < ggSz) {
                ggi = ggis[i];
                ggi.setRange(r);
                adv = ggi.getAdvance();
                double delta = range - (double)(rangeAdvance + adv);
                if (delta < 0.0) break;
                ++i;
                rangeAdvance += adv;
            }
            if (i == ggSz) {
                --i;
                rangeAdvance -= adv;
            }
            ggi = ggis[i];
            float ladv = ggi.getLastAdvance();
            while ((double)(rangeAdvance + ladv) > range) {
                ladv = 0.0f;
                if (--i < 0 || r != (ggi = ggis[i]).getRange()) break;
                rangeAdvance -= ggi.getAdvance();
                ladv = ggi.getLastAdvance();
            }
            this.rangeAdv[r] = rangeAdvance + ladv;
            if (++i != ggSz) continue;
            return true;
        }
        return false;
    }

    public boolean setLineHeight(double lh) {
        this.fr.newLineHeight(lh);
        if (this.updateRangeInfo()) {
            this.lineHeight = lh;
            return true;
        }
        if (this.lineHeight > 0.0) {
            this.fr.newLineHeight(this.lineHeight);
        }
        return false;
    }

    public double getCurrentY() {
        return this.fr.getCurrentY();
    }

    public boolean gotoY(double y) {
        if (this.fr.gotoY(y)) {
            return true;
        }
        if (this.lineHeight > 0.0) {
            this.updateRangeInfo();
        }
        this.baseline = (float)(this.fr.getCurrentY() + this.hLeading + this.ascent);
        return false;
    }

    protected boolean updateRangeInfo() {
        this.fr.resetRange();
        int nr = this.fr.getNumRangeOnLine();
        if (nr == 0) {
            return false;
        }
        this.numRanges = nr;
        if (this.ranges == null) {
            this.rangeAdv = new double[this.numRanges];
            this.ranges = new double[2 * this.numRanges];
        } else if (this.numRanges > this.rangeAdv.length) {
            int sz = 2 * this.rangeAdv.length;
            if (sz < this.numRanges) {
                sz = this.numRanges;
            }
            this.rangeAdv = new double[sz];
            this.ranges = new double[2 * sz];
        }
        for (int r = 0; r < this.numRanges; ++r) {
            double[] rangeBounds = this.fr.nextRange();
            double r0 = rangeBounds[0];
            if (r == 0) {
                double delta = this.bi.getLeftMargin();
                if (this.paraStart) {
                    double indent = this.bi.getIndent();
                    delta = delta < -indent ? 0.0 : (delta += indent);
                }
                r0 += delta;
            }
            double r1 = rangeBounds[1];
            if (r == this.numRanges - 1) {
                r1 -= (double)this.bi.getRightMargin();
            }
            this.ranges[2 * r] = r0;
            this.ranges[2 * r + 1] = r1;
        }
        return true;
    }

    protected void swapGlyphGroupInfo() {
        GlyphGroupInfo[] tmp = this.ggis;
        this.ggis = this.newGGIS;
        this.newGGIS = tmp;
        this.size = this.newSize;
        this.newSize = 0;
    }

    protected void mergeGlyphGroups(WordInfo wi) {
        int numGG = wi.getNumGlyphGroups();
        this.newSize = 0;
        if (this.ggis == null) {
            this.newSize = numGG;
            this.newGGIS = new GlyphGroupInfo[numGG];
            for (int i = 0; i < numGG; ++i) {
                this.newGGIS[i] = wi.getGlyphGroup(i);
            }
        } else {
            int s = 0;
            int i = 0;
            GlyphGroupInfo nggi = wi.getGlyphGroup(i);
            int nStart = nggi.getStart();
            GlyphGroupInfo oggi = this.ggis[this.size - 1];
            int oStart = oggi.getStart();
            this.newGGIS = LineInfo.assureSize(this.newGGIS, this.size + numGG);
            if (nStart < oStart) {
                oggi = this.ggis[s];
                oStart = oggi.getStart();
                while (s < this.size && i < numGG) {
                    if (nStart < oStart) {
                        this.newGGIS[this.newSize++] = nggi;
                        if (++i >= numGG) continue;
                        nggi = wi.getGlyphGroup(i);
                        nStart = nggi.getStart();
                        continue;
                    }
                    this.newGGIS[this.newSize++] = oggi;
                    if (++s >= this.size) continue;
                    oggi = this.ggis[s];
                    oStart = oggi.getStart();
                }
            }
            while (s < this.size) {
                this.newGGIS[this.newSize++] = this.ggis[s++];
            }
            while (i < numGG) {
                this.newGGIS[this.newSize++] = wi.getGlyphGroup(i++);
            }
        }
    }

    public void layout() {
        int r;
        if (this.size == 0) {
            return;
        }
        this.assignGlyphGroupRanges(this.size, this.ggis);
        GVTGlyphVector gv = this.ggis[0].getGlyphVector();
        boolean justType = false;
        double ggAdv = 0.0;
        double gAdv = 0.0;
        int[] rangeGG = new int[this.numRanges];
        int[] rangeG = new int[this.numRanges];
        GlyphGroupInfo[] rangeLastGGI = new GlyphGroupInfo[this.numRanges];
        GlyphGroupInfo ggi = this.ggis[0];
        int n = r = ggi.getRange();
        rangeGG[n] = rangeGG[n] + 1;
        int n2 = r;
        rangeG[n2] = rangeG[n2] + ggi.getGlyphCount();
        for (int i = 1; i < this.size; ++i) {
            ggi = this.ggis[i];
            r = ggi.getRange();
            if (rangeLastGGI[r] == null || !rangeLastGGI[r].getHideLast()) {
                int n3 = r;
                rangeGG[n3] = rangeGG[n3] + 1;
            }
            rangeLastGGI[r] = ggi;
            int n4 = r;
            rangeG[n4] = rangeG[n4] + ggi.getGlyphCount();
            GlyphGroupInfo pggi = this.ggis[i - 1];
            int pr = pggi.getRange();
            if (r == pr) continue;
            int n5 = pr;
            rangeG[n5] = rangeG[n5] + (pggi.getLastGlyphCount() - pggi.getGlyphCount());
        }
        int n6 = r;
        rangeG[n6] = rangeG[n6] + (ggi.getLastGlyphCount() - ggi.getGlyphCount());
        int currRange = -1;
        double locX = 0.0;
        double range = 0.0;
        double rAdv = 0.0;
        r = -1;
        ggi = null;
        for (int i = 0; i < this.size; ++i) {
            block20: {
                GlyphGroupInfo pggi;
                block19: {
                    pggi = ggi;
                    int prevRange = currRange;
                    ggi = this.ggis[i];
                    currRange = ggi.getRange();
                    if (currRange == prevRange) break block19;
                    locX = this.ranges[2 * currRange];
                    range = this.ranges[2 * currRange + 1] - locX;
                    rAdv = this.rangeAdv[currRange];
                    int textAlign = this.bi.getTextAlignment();
                    if (this.paraEnd && textAlign == 3) {
                        textAlign = 0;
                    }
                    switch (textAlign) {
                        default: {
                            int numSp;
                            double delta = range - rAdv;
                            if (!justType) {
                                numSp = rangeGG[currRange] - 1;
                                if (numSp >= 1) {
                                    ggAdv = delta / (double)numSp;
                                    break;
                                }
                            } else {
                                numSp = rangeG[currRange] - 1;
                                if (numSp >= 1) {
                                    gAdv = delta / (double)numSp;
                                    break;
                                }
                            }
                            break block20;
                        }
                        case 0: {
                            break;
                        }
                        case 1: {
                            locX += (range - rAdv) / 2.0;
                            break;
                        }
                        case 2: {
                            locX += range - rAdv;
                        }
                    }
                    break block20;
                }
                if (pggi != null && pggi.getHideLast()) {
                    gv.setGlyphVisible(pggi.getEnd(), false);
                }
            }
            int start = ggi.getStart();
            int end = ggi.getEnd();
            boolean[] hide = ggi.getHide();
            Point2D p2d = gv.getGlyphPosition(start);
            double deltaX = p2d.getX();
            double advAdj = 0.0;
            for (int g = start; g <= end; ++g) {
                Point2D np2d = gv.getGlyphPosition(g + 1);
                if (hide[g - start]) {
                    gv.setGlyphVisible(g, false);
                    advAdj += np2d.getX() - p2d.getX();
                } else {
                    gv.setGlyphVisible(g, true);
                }
                p2d.setLocation(p2d.getX() - deltaX - advAdj + locX, p2d.getY() + this.baseline);
                gv.setGlyphPosition(g, p2d);
                p2d = np2d;
                advAdj -= gAdv;
            }
            if (ggi.getHideLast()) {
                locX += (double)ggi.getAdvance() - advAdj;
                continue;
            }
            locX += (double)ggi.getAdvance() - advAdj + ggAdv;
        }
    }

    public static GlyphGroupInfo[] assureSize(GlyphGroupInfo[] ggis, int sz) {
        if (ggis == null) {
            if (sz < 10) {
                sz = 10;
            }
            return new GlyphGroupInfo[sz];
        }
        if (sz <= ggis.length) {
            return ggis;
        }
        int nsz = ggis.length * 2;
        if (nsz < sz) {
            nsz = sz;
        }
        return new GlyphGroupInfo[nsz];
    }
}

