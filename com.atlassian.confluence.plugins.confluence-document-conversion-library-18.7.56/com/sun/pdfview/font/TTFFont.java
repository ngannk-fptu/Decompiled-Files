/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.font.OutlineFont;
import com.sun.pdfview.font.PDFFontDescriptor;
import com.sun.pdfview.font.ttf.AdobeGlyphList;
import com.sun.pdfview.font.ttf.CMap;
import com.sun.pdfview.font.ttf.CmapTable;
import com.sun.pdfview.font.ttf.Glyf;
import com.sun.pdfview.font.ttf.GlyfCompound;
import com.sun.pdfview.font.ttf.GlyfSimple;
import com.sun.pdfview.font.ttf.GlyfTable;
import com.sun.pdfview.font.ttf.HeadTable;
import com.sun.pdfview.font.ttf.HmtxTable;
import com.sun.pdfview.font.ttf.PostTable;
import com.sun.pdfview.font.ttf.TrueTypeFont;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.IOException;

public class TTFFont
extends OutlineFont {
    private TrueTypeFont font;
    private float unitsPerEm;

    public TTFFont(String baseFont, PDFObject fontObj, PDFFontDescriptor descriptor) throws IOException {
        super(baseFont, fontObj, descriptor);
        String fontName = descriptor.getFontName();
        PDFObject ttfObj = descriptor.getFontFile2();
        if (ttfObj != null) {
            this.font = TrueTypeFont.parseFont(ttfObj.getStreamBuffer());
            HeadTable head = (HeadTable)this.font.getTable("head");
            this.unitsPerEm = head.getUnitsPerEm();
        } else {
            this.font = null;
        }
    }

    @Override
    protected synchronized GeneralPath getOutline(char src, float width) {
        CmapTable cmap = (CmapTable)this.font.getTable("cmap");
        if (cmap == null) {
            return this.getOutline((int)src, width);
        }
        CMap[] maps = cmap.getCMaps();
        for (int i = 0; i < maps.length; ++i) {
            char idx = maps[i].map(src);
            if (idx == '\u0000') continue;
            return this.getOutline((int)idx, width);
        }
        return this.getOutline(0, width);
    }

    protected synchronized GeneralPath getOutlineFromCMaps(char val, float width) {
        char idx;
        CmapTable cmap = (CmapTable)this.font.getTable("cmap");
        if (cmap == null) {
            return null;
        }
        CMap map = cmap.getCMap((short)3, (short)1);
        if (map == null) {
            map = cmap.getCMap((short)1, (short)0);
        }
        if ((idx = map.map(val)) != '\u0000') {
            return this.getOutline((int)idx, width);
        }
        return null;
    }

    @Override
    protected synchronized GeneralPath getOutline(String name, float width) {
        PostTable post = (PostTable)this.font.getTable("post");
        if (post != null) {
            short idx = post.getGlyphNameIndex(name);
            if (idx != 0) {
                return this.getOutline(idx, width);
            }
            return null;
        }
        Integer res = AdobeGlyphList.getGlyphNameIndex(name);
        if (res != null) {
            int idx = res;
            return this.getOutlineFromCMaps((char)idx, width);
        }
        return null;
    }

    protected synchronized GeneralPath getOutline(int glyphId, float width) {
        GlyfTable glyf = (GlyfTable)this.font.getTable("glyf");
        Glyf g = glyf.getGlyph(glyphId);
        GeneralPath gp = null;
        gp = g instanceof GlyfSimple ? this.renderSimpleGlyph((GlyfSimple)g) : (g instanceof GlyfCompound ? this.renderCompoundGlyph(glyf, (GlyfCompound)g) : new GeneralPath());
        HmtxTable hmtx = (HmtxTable)this.font.getTable("hmtx");
        float advance = (float)hmtx.getAdvance(glyphId) / this.unitsPerEm;
        float widthfactor = width / advance;
        AffineTransform at = AffineTransform.getScaleInstance(1.0f / this.unitsPerEm, 1.0f / this.unitsPerEm);
        if (advance != 0.0f) {
            at.concatenate(AffineTransform.getScaleInstance(widthfactor, 1.0));
        }
        gp.transform(at);
        return gp;
    }

    protected GeneralPath renderSimpleGlyph(GlyfSimple g) {
        int curContour = 0;
        RenderState rs = new RenderState();
        rs.gp = new GeneralPath();
        for (int i = 0; i < g.getNumPoints(); ++i) {
            PointRec rec = new PointRec(g, i);
            if (rec.onCurve) {
                this.addOnCurvePoint(rec, rs);
            } else {
                this.addOffCurvePoint(rec, rs);
            }
            if (i != g.getContourEndPoint(curContour)) continue;
            ++curContour;
            if (rs.firstOff != null) {
                this.addOffCurvePoint(rs.firstOff, rs);
            }
            if (rs.firstOn != null) {
                this.addOnCurvePoint(rs.firstOn, rs);
            }
            rs.firstOn = null;
            rs.firstOff = null;
            rs.prevOff = null;
        }
        return rs.gp;
    }

    protected GeneralPath renderCompoundGlyph(GlyfTable glyf, GlyfCompound g) {
        GeneralPath gp = new GeneralPath();
        for (int i = 0; i < g.getNumComponents(); ++i) {
            Glyf gl = glyf.getGlyph(g.getGlyphIndex(i));
            GeneralPath path = null;
            if (gl instanceof GlyfSimple) {
                path = this.renderSimpleGlyph((GlyfSimple)gl);
            } else if (gl instanceof GlyfCompound) {
                path = this.renderCompoundGlyph(glyf, (GlyfCompound)gl);
            } else {
                throw new RuntimeException("Unsupported glyph type " + gl.getClass().getCanonicalName());
            }
            double[] matrix = g.getTransform(i);
            path.transform(new AffineTransform(matrix));
            gp.append(path, false);
        }
        return gp;
    }

    private void addOnCurvePoint(PointRec rec, RenderState rs) {
        if (rs.firstOn == null) {
            rs.firstOn = rec;
            rs.gp.moveTo(rec.x, rec.y);
        } else if (rs.prevOff != null) {
            rs.gp.quadTo(rs.prevOff.x, rs.prevOff.y, rec.x, rec.y);
            rs.prevOff = null;
        } else {
            rs.gp.lineTo(rec.x, rec.y);
        }
    }

    private void addOffCurvePoint(PointRec rec, RenderState rs) {
        if (rs.prevOff != null) {
            PointRec oc = new PointRec((rec.x + rs.prevOff.x) / 2, (rec.y + rs.prevOff.y) / 2, true);
            this.addOnCurvePoint(oc, rs);
        } else if (rs.firstOn == null) {
            rs.firstOff = rec;
        }
        rs.prevOff = rec;
    }

    class PointRec {
        int x;
        int y;
        boolean onCurve;

        public PointRec(int x, int y, boolean onCurve) {
            this.x = x;
            this.y = y;
            this.onCurve = onCurve;
        }

        public PointRec(GlyfSimple g, int idx) {
            this.x = g.getXCoord(idx);
            this.y = g.getYCoord(idx);
            this.onCurve = g.onCurve(idx);
        }
    }

    class RenderState {
        GeneralPath gp;
        PointRec firstOn;
        PointRec firstOff;
        PointRec prevOff;

        RenderState() {
        }
    }
}

