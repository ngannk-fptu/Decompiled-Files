/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.font.OutlineFont;
import com.sun.pdfview.font.PDFFontDescriptor;
import com.sun.pdfview.font.ttf.CMap;
import com.sun.pdfview.font.ttf.CMapFormat0;
import com.sun.pdfview.font.ttf.CMapFormat4;
import com.sun.pdfview.font.ttf.CmapTable;
import com.sun.pdfview.font.ttf.HeadTable;
import com.sun.pdfview.font.ttf.HmtxTable;
import com.sun.pdfview.font.ttf.NameTable;
import com.sun.pdfview.font.ttf.PostTable;
import com.sun.pdfview.font.ttf.TrueTypeFont;
import com.sun.pdfview.font.ttf.TrueTypeTable;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.OpenType;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class NativeFont
extends OutlineFont {
    protected static final char[] controlChars = new char[]{'\t', '\n', '\r'};
    protected static final short[] mapIDs = new short[]{3, 1, 0, 0, 0, 3, 1, 0};
    private Font f;
    private FontRenderContext basecontext = new FontRenderContext(new AffineTransform(), true, true);
    private CmapTable cmapTable;
    private PostTable postTable;
    private int unitsPerEm;
    private HmtxTable hmtxTable;

    public NativeFont(String baseFont, PDFObject fontObj, PDFFontDescriptor descriptor) throws IOException {
        super(baseFont, fontObj, descriptor);
        String fontName = descriptor.getFontName();
        PDFObject ttf = descriptor.getFontFile2();
        if (ttf != null) {
            byte[] fontdata = ttf.getStream();
            try {
                this.setFont(fontdata);
            }
            catch (FontFormatException ffe) {
                throw new PDFParseException("Font format exception: " + ffe);
            }
        } else {
            int style;
            int flags = descriptor.getFlags();
            int n = style = (flags & 0x40000) != 0 ? 1 : 0;
            if (fontName.indexOf("Bold") > 0) {
                style |= 1;
            }
            if (descriptor.getItalicAngle() != 0) {
                style |= 2;
            }
            if ((flags & 1) != 0) {
                this.setFont(new Font("Monospaced", style, 1));
            } else if ((flags & 2) != 0) {
                this.setFont(new Font("Serif", style, 1));
            } else {
                this.setFont(new Font("Sans-serif", style, 1));
            }
        }
    }

    @Override
    protected GeneralPath getOutline(String name, float width) {
        if (this.postTable != null && this.cmapTable != null) {
            CMap map;
            short glyphID = this.postTable.getGlyphNameIndex(name);
            if (glyphID == 0) {
                return null;
            }
            char mappedChar = '\u0000';
            for (int i = 0; i < mapIDs.length && ((map = this.cmapTable.getCMap(mapIDs[i], mapIDs[i + 1])) == null || (mappedChar = map.reverseMap(glyphID)) == '\u0000'); i += 2) {
            }
            return this.getOutline(mappedChar, width);
        }
        return null;
    }

    @Override
    protected GeneralPath getOutline(char src, float width) {
        if (!this.f.canDisplay(src) && this.f.canDisplay((char)(src + 61440))) {
            src = (char)(src + 61440);
        }
        for (int i = 0; i < controlChars.length; ++i) {
            if (controlChars[i] != src) continue;
            src = (char)(0xF000 | src);
            break;
        }
        char[] glyph = new char[]{src};
        GlyphVector gv = this.f.createGlyphVector(this.basecontext, glyph);
        GeneralPath gp = new GeneralPath(gv.getGlyphOutline(0));
        CMap map = this.cmapTable.getCMap(mapIDs[0], mapIDs[1]);
        char glyphID = map.map(src);
        float advance = (float)this.hmtxTable.getAdvance(glyphID) / (float)this.unitsPerEm;
        float widthfactor = width / advance;
        gp.transform(AffineTransform.getScaleInstance(widthfactor, -1.0));
        return gp;
    }

    protected void setFont(Font f) {
        this.f = f;
        if (f instanceof OpenType) {
            OpenType ot = (OpenType)((Object)f);
            byte[] cmapData = ot.getFontTable(1668112752);
            byte[] postData = ot.getFontTable(1886352244);
            TrueTypeFont ttf = new TrueTypeFont(65536);
            this.cmapTable = (CmapTable)TrueTypeTable.createTable(ttf, "cmap", ByteBuffer.wrap(cmapData));
            ttf.addTable("cmap", this.cmapTable);
            this.postTable = (PostTable)TrueTypeTable.createTable(ttf, "post", ByteBuffer.wrap(postData));
            ttf.addTable("post", this.postTable);
        }
    }

    protected void setFont(byte[] fontdata) throws FontFormatException, IOException {
        try {
            TrueTypeFont ttf = TrueTypeFont.parseFont(fontdata);
            this.cmapTable = (CmapTable)ttf.getTable("cmap");
            this.postTable = (PostTable)ttf.getTable("post");
            this.hmtxTable = (HmtxTable)ttf.getTable("hmtx");
            HeadTable headTable = (HeadTable)ttf.getTable("head");
            this.unitsPerEm = headTable.getUnitsPerEm();
            NameTable nameTable = null;
            try {
                nameTable = (NameTable)ttf.getTable("name");
            }
            catch (Exception ex) {
                System.out.println("Error reading name table for font " + this.getBaseFont() + ".  Repairing!");
            }
            boolean nameFixed = this.fixNameTable(ttf, nameTable);
            boolean cmapFixed = this.fixCMapTable(ttf, this.cmapTable);
            if (nameFixed || cmapFixed) {
                fontdata = ttf.writeFont();
            }
        }
        catch (Exception ex) {
            System.out.println("Error parsing font : " + this.getBaseFont());
            ex.printStackTrace();
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(fontdata);
        this.f = Font.createFont(0, bais);
        bais.close();
    }

    private boolean fixNameTable(TrueTypeFont ttf, NameTable name) {
        if (name == null) {
            name = (NameTable)TrueTypeTable.createTable(ttf, "name");
            ttf.addTable("name", name);
        }
        String fName = this.getBaseFont();
        String style = "Regular";
        if (fName.indexOf("Italic") > -1 || fName.indexOf("italic") > -1) {
            style = "Italic";
        } else if (fName.indexOf("Bold") > -1 || fName.indexOf("bold") > -1) {
            style = "Bold";
        }
        if (fName.indexOf(45) > -1) {
            fName = fName.substring(0, fName.indexOf(45));
        }
        short platID = 3;
        short encID = 1;
        short langID = 1033;
        short[] nameIDs = new short[]{0, 1, 2, 3, 4, 5, 6, 7};
        String[] defaultValues = new String[]{"No copyright", fName, style, fName + " " + style, fName + " " + style, "1.0 (Fake)", fName, "No Trademark"};
        boolean changed = false;
        for (int i = 0; i < nameIDs.length; ++i) {
            if (name.getRecord(platID, encID, langID, nameIDs[i]) != null) continue;
            name.addRecord(platID, encID, langID, nameIDs[i], defaultValues[i]);
            changed = true;
        }
        return changed;
    }

    private boolean fixCMapTable(TrueTypeFont ttf, CmapTable cmap) {
        int i;
        CMapFormat4 fourMap = null;
        CMapFormat0 zeroMap = null;
        for (i = 0; i < mapIDs.length; i += 2) {
            CMap map = this.cmapTable.getCMap(mapIDs[i], mapIDs[i + 1]);
            if (map == null) continue;
            if (fourMap == null && map instanceof CMapFormat4) {
                fourMap = (CMapFormat4)map;
                continue;
            }
            if (zeroMap != null || !(map instanceof CMapFormat0)) continue;
            zeroMap = (CMapFormat0)map;
        }
        if (zeroMap == null && fourMap == null) {
            fourMap = (CMapFormat4)CMap.createMap((short)4, (short)0);
            fourMap.addSegment((short)this.getFirstChar(), (short)this.getLastChar(), (short)0);
        }
        if (zeroMap != null) {
            fourMap = (CMapFormat4)CMap.createMap((short)4, (short)0);
            fourMap.addSegment((short)0, (short)1, (short)0);
            for (i = this.getFirstChar(); i <= this.getLastChar(); ++i) {
                short value = (short)(zeroMap.map((byte)i) & 0xFF);
                if (value == 0) continue;
                fourMap.addSegment((short)i, (short)i, (short)(value - i));
            }
        }
        for (i = 0; i < controlChars.length; ++i) {
            short idx = (short)(0xF000 | controlChars[i]);
            short value = (short)fourMap.map(controlChars[i]);
            fourMap.addSegment(idx, idx, (short)(value - idx));
        }
        cmap = (CmapTable)TrueTypeTable.createTable(ttf, "cmap");
        cmap.addCMap((short)3, (short)1, fourMap);
        ttf.addTable("cmap", cmap);
        this.cmapTable = cmap;
        return true;
    }
}

