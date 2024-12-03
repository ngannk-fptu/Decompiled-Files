/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.GlyphList;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.TrueTypeFontSubSet;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class TrueTypeFont
extends BaseFont {
    static final String[] codePages = new String[]{"1252 Latin 1", "1250 Latin 2: Eastern Europe", "1251 Cyrillic", "1253 Greek", "1254 Turkish", "1255 Hebrew", "1256 Arabic", "1257 Windows Baltic", "1258 Vietnamese", null, null, null, null, null, null, null, "874 Thai", "932 JIS/Japan", "936 Chinese: Simplified chars--PRC and Singapore", "949 Korean Wansung", "950 Chinese: Traditional chars--Taiwan and Hong Kong", "1361 Korean Johab", null, null, null, null, null, null, null, "Macintosh Character Set (US Roman)", "OEM Character Set", "Symbol Character Set", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "869 IBM Greek", "866 MS-DOS Russian", "865 MS-DOS Nordic", "864 Arabic", "863 MS-DOS Canadian French", "862 Hebrew", "861 MS-DOS Icelandic", "860 MS-DOS Portuguese", "857 IBM Turkish", "855 IBM Cyrillic; primarily Russian", "852 Latin 2", "775 MS-DOS Baltic", "737 Greek; former 437 G", "708 Arabic; ASMO 708", "850 WE/Latin 1", "437 US"};
    protected boolean justNames = false;
    protected HashMap<String, int[]> tables;
    protected RandomAccessFileOrArray rf;
    protected String fileName;
    protected boolean cff = false;
    protected int cffOffset;
    protected int cffLength;
    protected int directoryOffset;
    protected String ttcIndex;
    protected String style = "";
    protected FontHeader head = new FontHeader();
    protected HorizontalHeader hhea = new HorizontalHeader();
    protected WindowsMetrics os_2 = new WindowsMetrics();
    protected int[] GlyphWidths;
    protected int[][] bboxes;
    protected HashMap<Integer, int[]> cmap10;
    protected HashMap<Integer, int[]> cmap31;
    protected HashMap<Integer, int[]> cmapExt;
    protected IntHashtable kerning = new IntHashtable();
    protected String fontName;
    protected String[][] fullName;
    protected String[][] allNameEntries;
    protected String[][] familyName;
    protected double italicAngle;
    protected boolean isFixedPitch = false;
    protected int underlinePosition;
    protected int underlineThickness;

    protected TrueTypeFont() {
    }

    TrueTypeFont(String ttFile, String enc, boolean emb, byte[] ttfAfm, boolean justNames, boolean forceRead) throws DocumentException, IOException {
        this.justNames = justNames;
        String nameBase = TrueTypeFont.getBaseName(ttFile);
        String ttcName = TrueTypeFont.getTTCName(nameBase);
        if (nameBase.length() < ttFile.length()) {
            this.style = ttFile.substring(nameBase.length());
        }
        this.encoding = enc;
        this.embedded = emb;
        this.fileName = ttcName;
        this.fontType = 1;
        this.ttcIndex = "";
        if (ttcName.length() < nameBase.length()) {
            this.ttcIndex = nameBase.substring(ttcName.length() + 1);
        }
        if (this.fileName.toLowerCase().endsWith(".ttf") || this.fileName.toLowerCase().endsWith(".otf") || this.fileName.toLowerCase().endsWith(".ttc")) {
            this.process(ttfAfm, forceRead);
            if (!justNames && this.embedded && this.os_2.fsType == 2) {
                throw new DocumentException(MessageLocalization.getComposedMessage("1.cannot.be.embedded.due.to.licensing.restrictions", this.fileName + this.style));
            }
        } else {
            throw new DocumentException(MessageLocalization.getComposedMessage("1.is.not.a.ttf.otf.or.ttc.font.file", this.fileName + this.style));
        }
        if (!this.encoding.startsWith("#")) {
            PdfEncodings.convertToBytes(" ", enc);
        }
        this.createEncoding();
    }

    protected static String getTTCName(String name) {
        int idx = name.toLowerCase().indexOf(".ttc,");
        if (idx < 0) {
            return name;
        }
        return name.substring(0, idx + 4);
    }

    void fillTables() throws DocumentException, IOException {
        int[] table_location = this.tables.get("head");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "head", this.fileName + this.style));
        }
        this.rf.seek(table_location[0] + 16);
        this.head.flags = this.rf.readUnsignedShort();
        this.head.unitsPerEm = this.rf.readUnsignedShort();
        this.rf.skipBytes(16);
        this.head.xMin = this.rf.readShort();
        this.head.yMin = this.rf.readShort();
        this.head.xMax = this.rf.readShort();
        this.head.yMax = this.rf.readShort();
        this.head.macStyle = this.rf.readUnsignedShort();
        table_location = this.tables.get("hhea");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "hhea", this.fileName + this.style));
        }
        this.rf.seek(table_location[0] + 4);
        this.hhea.Ascender = this.rf.readShort();
        this.hhea.Descender = this.rf.readShort();
        this.hhea.LineGap = this.rf.readShort();
        this.hhea.advanceWidthMax = this.rf.readUnsignedShort();
        this.hhea.minLeftSideBearing = this.rf.readShort();
        this.hhea.minRightSideBearing = this.rf.readShort();
        this.hhea.xMaxExtent = this.rf.readShort();
        this.hhea.caretSlopeRise = this.rf.readShort();
        this.hhea.caretSlopeRun = this.rf.readShort();
        this.rf.skipBytes(12);
        this.hhea.numberOfHMetrics = this.rf.readUnsignedShort();
        table_location = this.tables.get("OS/2");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "OS/2", this.fileName + this.style));
        }
        this.rf.seek(table_location[0]);
        int version = this.rf.readUnsignedShort();
        this.os_2.xAvgCharWidth = this.rf.readShort();
        this.os_2.usWeightClass = this.rf.readUnsignedShort();
        this.os_2.usWidthClass = this.rf.readUnsignedShort();
        this.os_2.fsType = this.rf.readShort();
        this.os_2.ySubscriptXSize = this.rf.readShort();
        this.os_2.ySubscriptYSize = this.rf.readShort();
        this.os_2.ySubscriptXOffset = this.rf.readShort();
        this.os_2.ySubscriptYOffset = this.rf.readShort();
        this.os_2.ySuperscriptXSize = this.rf.readShort();
        this.os_2.ySuperscriptYSize = this.rf.readShort();
        this.os_2.ySuperscriptXOffset = this.rf.readShort();
        this.os_2.ySuperscriptYOffset = this.rf.readShort();
        this.os_2.yStrikeoutSize = this.rf.readShort();
        this.os_2.yStrikeoutPosition = this.rf.readShort();
        this.os_2.sFamilyClass = this.rf.readShort();
        this.rf.readFully(this.os_2.panose);
        this.rf.skipBytes(16);
        this.rf.readFully(this.os_2.achVendID);
        this.os_2.fsSelection = this.rf.readUnsignedShort();
        this.os_2.usFirstCharIndex = this.rf.readUnsignedShort();
        this.os_2.usLastCharIndex = this.rf.readUnsignedShort();
        this.os_2.sTypoAscender = this.rf.readShort();
        this.os_2.sTypoDescender = this.rf.readShort();
        if (this.os_2.sTypoDescender > 0) {
            this.os_2.sTypoDescender = -this.os_2.sTypoDescender;
        }
        this.os_2.sTypoLineGap = this.rf.readShort();
        this.os_2.usWinAscent = this.rf.readUnsignedShort();
        this.os_2.usWinDescent = this.rf.readUnsignedShort();
        this.os_2.ulCodePageRange1 = 0;
        this.os_2.ulCodePageRange2 = 0;
        if (version > 0) {
            this.os_2.ulCodePageRange1 = this.rf.readInt();
            this.os_2.ulCodePageRange2 = this.rf.readInt();
        }
        if (version > 1) {
            this.rf.skipBytes(2);
            this.os_2.sCapHeight = this.rf.readShort();
        } else {
            this.os_2.sCapHeight = (int)(0.7 * (double)this.head.unitsPerEm);
        }
        table_location = this.tables.get("post");
        if (table_location == null) {
            this.italicAngle = -Math.atan2(this.hhea.caretSlopeRun, this.hhea.caretSlopeRise) * 180.0 / Math.PI;
            return;
        }
        this.rf.seek(table_location[0] + 4);
        short mantissa = this.rf.readShort();
        int fraction = this.rf.readUnsignedShort();
        this.italicAngle = (double)mantissa + (double)fraction / 16384.0;
        this.underlinePosition = this.rf.readShort();
        this.underlineThickness = this.rf.readShort();
        this.isFixedPitch = this.rf.readInt() != 0;
    }

    String getBaseFont() throws DocumentException, IOException {
        int[] table_location = this.tables.get("name");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "name", this.fileName + this.style));
        }
        this.rf.seek(table_location[0] + 2);
        int numRecords = this.rf.readUnsignedShort();
        int startOfStorage = this.rf.readUnsignedShort();
        for (int k = 0; k < numRecords; ++k) {
            int platformID = this.rf.readUnsignedShort();
            int platformEncodingID = this.rf.readUnsignedShort();
            int languageID = this.rf.readUnsignedShort();
            int nameID = this.rf.readUnsignedShort();
            int length = this.rf.readUnsignedShort();
            int offset = this.rf.readUnsignedShort();
            if (nameID != 6) continue;
            this.rf.seek(table_location[0] + startOfStorage + offset);
            if (platformID == 0 || platformID == 3) {
                return this.readUnicodeString(length);
            }
            return this.readStandardString(length);
        }
        File file = new File(this.fileName);
        return file.getName().replace(' ', '-');
    }

    String[][] getNames(int id) throws DocumentException, IOException {
        int[] table_location = this.tables.get("name");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "name", this.fileName + this.style));
        }
        this.rf.seek(table_location[0] + 2);
        int numRecords = this.rf.readUnsignedShort();
        int startOfStorage = this.rf.readUnsignedShort();
        ArrayList<String[]> names = new ArrayList<String[]>();
        for (int k = 0; k < numRecords; ++k) {
            int platformID = this.rf.readUnsignedShort();
            int platformEncodingID = this.rf.readUnsignedShort();
            int languageID = this.rf.readUnsignedShort();
            int nameID = this.rf.readUnsignedShort();
            int length = this.rf.readUnsignedShort();
            int offset = this.rf.readUnsignedShort();
            if (nameID != id) continue;
            int pos = this.rf.getFilePointer();
            this.rf.seek(table_location[0] + startOfStorage + offset);
            String name = platformID == 0 || platformID == 3 || platformID == 2 && platformEncodingID == 1 ? this.readUnicodeString(length) : this.readStandardString(length);
            names.add(new String[]{String.valueOf(platformID), String.valueOf(platformEncodingID), String.valueOf(languageID), name});
            this.rf.seek(pos);
        }
        String[][] thisName = new String[names.size()][];
        for (int k = 0; k < names.size(); ++k) {
            thisName[k] = (String[])names.get(k);
        }
        return thisName;
    }

    String[][] getAllNames() throws DocumentException, IOException {
        int[] table_location = this.tables.get("name");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "name", this.fileName + this.style));
        }
        this.rf.seek(table_location[0] + 2);
        int numRecords = this.rf.readUnsignedShort();
        int startOfStorage = this.rf.readUnsignedShort();
        ArrayList<String[]> names = new ArrayList<String[]>();
        for (int k = 0; k < numRecords; ++k) {
            int platformID = this.rf.readUnsignedShort();
            int platformEncodingID = this.rf.readUnsignedShort();
            int languageID = this.rf.readUnsignedShort();
            int nameID = this.rf.readUnsignedShort();
            int length = this.rf.readUnsignedShort();
            int offset = this.rf.readUnsignedShort();
            int pos = this.rf.getFilePointer();
            this.rf.seek(table_location[0] + startOfStorage + offset);
            String name = platformID == 0 || platformID == 3 || platformID == 2 && platformEncodingID == 1 ? this.readUnicodeString(length) : this.readStandardString(length);
            names.add(new String[]{String.valueOf(nameID), String.valueOf(platformID), String.valueOf(platformEncodingID), String.valueOf(languageID), name});
            this.rf.seek(pos);
        }
        String[][] thisName = new String[names.size()][];
        for (int k = 0; k < names.size(); ++k) {
            thisName[k] = (String[])names.get(k);
        }
        return thisName;
    }

    void checkCff() {
        int[] table_location = this.tables.get("CFF ");
        if (table_location != null) {
            this.cff = true;
            this.cffOffset = table_location[0];
            this.cffLength = table_location[1];
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void process(byte[] ttfAfm, boolean preload) throws DocumentException, IOException {
        this.tables = new HashMap();
        try {
            this.rf = ttfAfm == null ? new RandomAccessFileOrArray(this.fileName, preload, Document.plainRandomAccess) : new RandomAccessFileOrArray(ttfAfm);
            if (this.ttcIndex.length() > 0) {
                int dirIdx = Integer.parseInt(this.ttcIndex);
                if (dirIdx < 0) {
                    throw new DocumentException(MessageLocalization.getComposedMessage("the.font.index.for.1.must.be.positive", this.fileName));
                }
                String mainTag = this.readStandardString(4);
                if (!mainTag.equals("ttcf")) {
                    throw new DocumentException(MessageLocalization.getComposedMessage("1.is.not.a.valid.ttc.file", this.fileName));
                }
                this.rf.skipBytes(4);
                int dirCount = this.rf.readInt();
                if (dirIdx >= dirCount) {
                    throw new DocumentException(MessageLocalization.getComposedMessage("the.font.index.for.1.must.be.between.0.and.2.it.was.3", this.fileName, String.valueOf(dirCount - 1), String.valueOf(dirIdx)));
                }
                this.rf.skipBytes(dirIdx * 4);
                this.directoryOffset = this.rf.readInt();
            }
            this.rf.seek(this.directoryOffset);
            int ttId = this.rf.readInt();
            if (ttId != 65536 && ttId != 0x4F54544F) {
                throw new DocumentException(MessageLocalization.getComposedMessage("1.is.not.a.valid.ttf.or.otf.file", this.fileName));
            }
            int num_tables = this.rf.readUnsignedShort();
            this.rf.skipBytes(6);
            for (int k = 0; k < num_tables; ++k) {
                String tag = this.readStandardString(4);
                this.rf.skipBytes(4);
                int[] table_location = new int[]{this.rf.readInt(), this.rf.readInt()};
                this.tables.put(tag, table_location);
            }
            this.checkCff();
            this.fontName = this.getBaseFont();
            this.fullName = this.getNames(4);
            this.familyName = this.getNames(1);
            this.allNameEntries = this.getAllNames();
            if (!this.justNames) {
                this.fillTables();
                this.readGlyphWidths();
                this.readCMaps();
                this.readKerning();
                this.readBbox();
            }
        }
        finally {
            if (this.rf != null) {
                this.rf.close();
                if (!this.embedded) {
                    this.rf = null;
                }
            }
        }
    }

    protected String readStandardString(int length) throws IOException {
        byte[] buf = new byte[length];
        this.rf.readFully(buf);
        try {
            return new String(buf, "Cp1252");
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    protected String readUnicodeString(int length) throws IOException {
        StringBuilder buf = new StringBuilder();
        length /= 2;
        for (int k = 0; k < length; ++k) {
            buf.append(this.rf.readChar());
        }
        return buf.toString();
    }

    protected void readGlyphWidths() throws DocumentException, IOException {
        int[] table_location = this.tables.get("hmtx");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "hmtx", this.fileName + this.style));
        }
        this.rf.seek(table_location[0]);
        this.GlyphWidths = new int[this.hhea.numberOfHMetrics];
        for (int k = 0; k < this.hhea.numberOfHMetrics; ++k) {
            this.GlyphWidths[k] = this.rf.readUnsignedShort() * 1000 / this.head.unitsPerEm;
            this.rf.readUnsignedShort();
        }
    }

    protected int getGlyphWidth(int glyph) {
        if (glyph >= this.GlyphWidths.length) {
            glyph = this.GlyphWidths.length - 1;
        }
        return this.GlyphWidths[glyph];
    }

    private void readBbox() throws DocumentException, IOException {
        int k;
        int[] locaTable;
        int entries;
        int[] tableLocation = this.tables.get("head");
        if (tableLocation == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "head", this.fileName + this.style));
        }
        this.rf.seek(tableLocation[0] + 51);
        boolean locaShortTable = this.rf.readUnsignedShort() == 0;
        tableLocation = this.tables.get("loca");
        if (tableLocation == null) {
            return;
        }
        this.rf.seek(tableLocation[0]);
        if (locaShortTable) {
            entries = tableLocation[1] / 2;
            locaTable = new int[entries];
            for (k = 0; k < entries; ++k) {
                locaTable[k] = this.rf.readUnsignedShort() * 2;
            }
        } else {
            entries = tableLocation[1] / 4;
            locaTable = new int[entries];
            for (k = 0; k < entries; ++k) {
                locaTable[k] = this.rf.readInt();
            }
        }
        if ((tableLocation = this.tables.get("glyf")) == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "glyf", this.fileName + this.style));
        }
        int tableGlyphOffset = tableLocation[0];
        this.bboxes = new int[locaTable.length - 1][];
        for (int glyph = 0; glyph < locaTable.length - 1; ++glyph) {
            int start = locaTable[glyph];
            if (start == locaTable[glyph + 1]) continue;
            this.rf.seek(tableGlyphOffset + start + 2);
            this.bboxes[glyph] = new int[]{this.rf.readShort() * 1000 / this.head.unitsPerEm, this.rf.readShort() * 1000 / this.head.unitsPerEm, this.rf.readShort() * 1000 / this.head.unitsPerEm, this.rf.readShort() * 1000 / this.head.unitsPerEm};
        }
    }

    void readCMaps() throws DocumentException, IOException {
        int format;
        int[] table_location = this.tables.get("cmap");
        if (table_location == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "cmap", this.fileName + this.style));
        }
        this.rf.seek(table_location[0]);
        this.rf.skipBytes(2);
        int num_tables = this.rf.readUnsignedShort();
        this.fontSpecific = false;
        int map10 = 0;
        int map31 = 0;
        int map30 = 0;
        int mapExt = 0;
        for (int k = 0; k < num_tables; ++k) {
            int platId = this.rf.readUnsignedShort();
            int platSpecId = this.rf.readUnsignedShort();
            int offset = this.rf.readInt();
            if (platId == 3 && platSpecId == 0) {
                this.fontSpecific = true;
                map30 = offset;
            } else if (platId == 3 && platSpecId == 1) {
                map31 = offset;
            } else if (platId == 3 && platSpecId == 10) {
                mapExt = offset;
            }
            if (platId != 1 || platSpecId != 0) continue;
            map10 = offset;
        }
        if (map10 > 0) {
            this.rf.seek(table_location[0] + map10);
            format = this.rf.readUnsignedShort();
            switch (format) {
                case 0: {
                    this.cmap10 = this.readFormat0();
                    break;
                }
                case 4: {
                    this.cmap10 = this.readFormat4();
                    break;
                }
                case 6: {
                    this.cmap10 = this.readFormat6();
                }
            }
        }
        if (map31 > 0) {
            this.rf.seek(table_location[0] + map31);
            format = this.rf.readUnsignedShort();
            if (format == 4) {
                this.cmap31 = this.readFormat4();
            }
        }
        if (map30 > 0) {
            this.rf.seek(table_location[0] + map30);
            format = this.rf.readUnsignedShort();
            if (format == 4) {
                this.cmap10 = this.readFormat4();
            }
        }
        if (mapExt > 0) {
            this.rf.seek(table_location[0] + mapExt);
            format = this.rf.readUnsignedShort();
            switch (format) {
                case 0: {
                    this.cmapExt = this.readFormat0();
                    break;
                }
                case 4: {
                    this.cmapExt = this.readFormat4();
                    break;
                }
                case 6: {
                    this.cmapExt = this.readFormat6();
                    break;
                }
                case 12: {
                    this.cmapExt = this.readFormat12();
                }
            }
        }
    }

    HashMap<Integer, int[]> readFormat12() throws IOException {
        HashMap<Integer, int[]> h = new HashMap<Integer, int[]>();
        this.rf.skipBytes(2);
        this.rf.readInt();
        this.rf.skipBytes(4);
        int nGroups = this.rf.readInt();
        for (int k = 0; k < nGroups; ++k) {
            int startCharCode = this.rf.readInt();
            int endCharCode = this.rf.readInt();
            int startGlyphID = this.rf.readInt();
            for (int i = startCharCode; i <= endCharCode; ++i) {
                int[] r;
                r = new int[]{startGlyphID++, this.getGlyphWidth(r[0])};
                h.put(i, r);
            }
        }
        return h;
    }

    HashMap<Integer, int[]> readFormat0() throws IOException {
        HashMap<Integer, int[]> h = new HashMap<Integer, int[]>();
        this.rf.skipBytes(4);
        for (int k = 0; k < 256; ++k) {
            int[] r;
            r = new int[]{this.rf.readUnsignedByte(), this.getGlyphWidth(r[0])};
            h.put(k, r);
        }
        return h;
    }

    HashMap<Integer, int[]> readFormat4() throws IOException {
        int k;
        HashMap<Integer, int[]> h = new HashMap<Integer, int[]>();
        int table_lenght = this.rf.readUnsignedShort();
        this.rf.skipBytes(2);
        int segCount = this.rf.readUnsignedShort() / 2;
        this.rf.skipBytes(6);
        int[] endCount = new int[segCount];
        for (int k2 = 0; k2 < segCount; ++k2) {
            endCount[k2] = this.rf.readUnsignedShort();
        }
        this.rf.skipBytes(2);
        int[] startCount = new int[segCount];
        for (int k3 = 0; k3 < segCount; ++k3) {
            startCount[k3] = this.rf.readUnsignedShort();
        }
        int[] idDelta = new int[segCount];
        for (int k4 = 0; k4 < segCount; ++k4) {
            idDelta[k4] = this.rf.readUnsignedShort();
        }
        int[] idRO = new int[segCount];
        for (int k5 = 0; k5 < segCount; ++k5) {
            idRO[k5] = this.rf.readUnsignedShort();
        }
        int[] glyphId = new int[table_lenght / 2 - 8 - segCount * 4];
        for (k = 0; k < glyphId.length; ++k) {
            glyphId[k] = this.rf.readUnsignedShort();
        }
        for (k = 0; k < segCount; ++k) {
            for (int j = startCount[k]; j <= endCount[k] && j != 65535; ++j) {
                int[] r;
                int glyph;
                if (idRO[k] == 0) {
                    glyph = j + idDelta[k] & 0xFFFF;
                } else {
                    int idx = k + idRO[k] / 2 - segCount + j - startCount[k];
                    if (idx >= glyphId.length) continue;
                    glyph = glyphId[idx] + idDelta[k] & 0xFFFF;
                }
                r = new int[]{glyph, this.getGlyphWidth(r[0])};
                h.put(this.fontSpecific ? ((j & 0xFF00) == 61440 ? j & 0xFF : j) : j, r);
            }
        }
        return h;
    }

    HashMap<Integer, int[]> readFormat6() throws IOException {
        HashMap<Integer, int[]> h = new HashMap<Integer, int[]>();
        this.rf.skipBytes(4);
        int start_code = this.rf.readUnsignedShort();
        int code_count = this.rf.readUnsignedShort();
        for (int k = 0; k < code_count; ++k) {
            int[] r;
            r = new int[]{this.rf.readUnsignedShort(), this.getGlyphWidth(r[0])};
            h.put(k + start_code, r);
        }
        return h;
    }

    void readKerning() throws IOException {
        int[] table_location = this.tables.get("kern");
        if (table_location == null) {
            return;
        }
        this.rf.seek(table_location[0] + 2);
        int nTables = this.rf.readUnsignedShort();
        int checkpoint = table_location[0] + 4;
        int length = 0;
        for (int k = 0; k < nTables; ++k) {
            this.rf.seek(checkpoint += length);
            this.rf.skipBytes(2);
            length = this.rf.readUnsignedShort();
            int coverage = this.rf.readUnsignedShort();
            if ((coverage & 0xFFF7) != 1) continue;
            int nPairs = this.rf.readUnsignedShort();
            this.rf.skipBytes(6);
            for (int j = 0; j < nPairs; ++j) {
                int pair = this.rf.readInt();
                int value = this.rf.readShort() * 1000 / this.head.unitsPerEm;
                this.kerning.put(pair, value);
            }
        }
    }

    @Override
    public int getKerning(int char1, int char2) {
        int[] metrics = this.getMetricsTT(char1);
        if (metrics == null) {
            return 0;
        }
        int c1 = metrics[0];
        metrics = this.getMetricsTT(char2);
        if (metrics == null) {
            return 0;
        }
        int c2 = metrics[0];
        return this.kerning.get((c1 << 16) + c2);
    }

    @Override
    int getRawWidth(int c, String name) {
        int[] metric = this.getMetricsTT(c);
        if (metric == null) {
            return 0;
        }
        return metric[1];
    }

    protected PdfDictionary getFontDescriptor(PdfIndirectReference fontStream, String subsetPrefix, PdfIndirectReference cidset) {
        PdfDictionary dic = new PdfDictionary(PdfName.FONTDESCRIPTOR);
        dic.put(PdfName.ASCENT, new PdfNumber(this.os_2.sTypoAscender * 1000 / this.head.unitsPerEm));
        dic.put(PdfName.CAPHEIGHT, new PdfNumber(this.os_2.sCapHeight * 1000 / this.head.unitsPerEm));
        dic.put(PdfName.DESCENT, new PdfNumber(this.os_2.sTypoDescender * 1000 / this.head.unitsPerEm));
        dic.put(PdfName.FONTBBOX, new PdfRectangle(this.head.xMin * 1000 / this.head.unitsPerEm, this.head.yMin * 1000 / this.head.unitsPerEm, this.head.xMax * 1000 / this.head.unitsPerEm, this.head.yMax * 1000 / this.head.unitsPerEm));
        if (cidset != null) {
            dic.put(PdfName.CIDSET, cidset);
        }
        if (this.cff) {
            if (this.encoding.startsWith("Identity-")) {
                dic.put(PdfName.FONTNAME, new PdfName(subsetPrefix + this.fontName + "-" + this.encoding));
            } else {
                dic.put(PdfName.FONTNAME, new PdfName(subsetPrefix + this.fontName + this.style));
            }
        } else {
            dic.put(PdfName.FONTNAME, new PdfName(subsetPrefix + this.fontName + this.style));
        }
        dic.put(PdfName.ITALICANGLE, new PdfNumber(this.italicAngle));
        dic.put(PdfName.STEMV, new PdfNumber(80));
        if (fontStream != null) {
            if (this.cff) {
                dic.put(PdfName.FONTFILE3, fontStream);
            } else {
                dic.put(PdfName.FONTFILE2, fontStream);
            }
        }
        int flags = 0;
        if (this.isFixedPitch) {
            flags |= 1;
        }
        flags |= this.fontSpecific ? 4 : 32;
        if ((this.head.macStyle & 2) != 0) {
            flags |= 0x40;
        }
        if ((this.head.macStyle & 1) != 0) {
            flags |= 0x40000;
        }
        dic.put(PdfName.FLAGS, new PdfNumber(flags));
        return dic;
    }

    protected PdfDictionary getFontBaseType(PdfIndirectReference fontDescriptor, String subsetPrefix, int firstChar, int lastChar, byte[] shortTag) {
        PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        if (this.cff) {
            dic.put(PdfName.SUBTYPE, PdfName.TYPE1);
            dic.put(PdfName.BASEFONT, new PdfName(this.fontName + this.style));
        } else {
            dic.put(PdfName.SUBTYPE, PdfName.TRUETYPE);
            dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName + this.style));
        }
        dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName + this.style));
        if (!this.fontSpecific) {
            for (int k = firstChar; k <= lastChar; ++k) {
                if (this.differences[k].equals(".notdef")) continue;
                firstChar = k;
                break;
            }
            if (this.encoding.equals("Cp1252") || this.encoding.equals("MacRoman")) {
                dic.put(PdfName.ENCODING, this.encoding.equals("Cp1252") ? PdfName.WIN_ANSI_ENCODING : PdfName.MAC_ROMAN_ENCODING);
            } else {
                PdfDictionary enc = new PdfDictionary(PdfName.ENCODING);
                PdfArray dif = new PdfArray();
                boolean gap = true;
                for (int k = firstChar; k <= lastChar; ++k) {
                    if (shortTag[k] != 0) {
                        if (gap) {
                            dif.add(new PdfNumber(k));
                            gap = false;
                        }
                        dif.add(new PdfName(this.differences[k]));
                        continue;
                    }
                    gap = true;
                }
                enc.put(PdfName.DIFFERENCES, dif);
                dic.put(PdfName.ENCODING, enc);
            }
        }
        dic.put(PdfName.FIRSTCHAR, new PdfNumber(firstChar));
        dic.put(PdfName.LASTCHAR, new PdfNumber(lastChar));
        PdfArray wd = new PdfArray();
        for (int k = firstChar; k <= lastChar; ++k) {
            if (shortTag[k] == 0) {
                wd.add(new PdfNumber(0));
                continue;
            }
            wd.add(new PdfNumber(this.widths[k]));
        }
        dic.put(PdfName.WIDTHS, wd);
        if (fontDescriptor != null) {
            dic.put(PdfName.FONTDESCRIPTOR, fontDescriptor);
        }
        return dic;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected byte[] getFullFont() throws IOException {
        RandomAccessFileOrArray rf2 = null;
        try {
            rf2 = new RandomAccessFileOrArray(this.rf);
            rf2.reOpen();
            byte[] b = new byte[rf2.length()];
            rf2.readFully(b);
            byte[] byArray = b;
            return byArray;
        }
        finally {
            try {
                if (rf2 != null) {
                    rf2.close();
                }
            }
            catch (Exception exception) {}
        }
    }

    protected static int[] compactRanges(ArrayList ranges) {
        int[] r;
        ArrayList<int[]> simp = new ArrayList<int[]>();
        for (Object range : ranges) {
            r = (int[])range;
            for (int j = 0; j < r.length; j += 2) {
                if (r.length <= j + 1) continue;
                simp.add(new int[]{Math.max(0, Math.min(r[j], r[j + 1])), Math.min(65535, Math.max(r[j], r[j + 1]))});
            }
        }
        for (int k1 = 0; k1 < simp.size() - 1; ++k1) {
            for (int k2 = k1 + 1; k2 < simp.size(); ++k2) {
                int[] r2;
                int[] r1 = (int[])simp.get(k1);
                if ((r1[0] < (r2 = (int[])simp.get(k2))[0] || r1[0] > r2[1]) && (r1[1] < r2[0] || r1[0] > r2[1])) continue;
                r1[0] = Math.min(r1[0], r2[0]);
                r1[1] = Math.max(r1[1], r2[1]);
                simp.remove(k2);
                --k2;
            }
        }
        int[] s = new int[simp.size() * 2];
        for (int k = 0; k < simp.size(); ++k) {
            r = (int[])simp.get(k);
            s[k * 2] = r[0];
            s[k * 2 + 1] = r[1];
        }
        return s;
    }

    protected void addRangeUni(Map<Integer, int[]> longTag, boolean includeMetrics, boolean subsetp) {
        if (!(subsetp || this.subsetRanges == null && this.directoryOffset <= 0)) {
            int[] rg;
            int[] nArray;
            if (this.subsetRanges == null && this.directoryOffset > 0) {
                int[] nArray2 = new int[2];
                nArray2[0] = 0;
                nArray = nArray2;
                nArray2[1] = 65535;
            } else {
                nArray = rg = TrueTypeFont.compactRanges(this.subsetRanges);
            }
            HashMap<Integer, int[]> usemap = !this.fontSpecific && this.cmap31 != null ? this.cmap31 : (this.fontSpecific && this.cmap10 != null ? this.cmap10 : (this.cmap31 != null ? this.cmap31 : this.cmap10));
            for (Map.Entry e : usemap.entrySet()) {
                int[] nArray3;
                int[] v = (int[])e.getValue();
                Integer gi = v[0];
                if (longTag.containsKey(gi)) continue;
                int c = (Integer)e.getKey();
                boolean skip = true;
                for (int k = 0; k < rg.length; k += 2) {
                    if (c < rg[k] || rg.length <= k + 1 || c > rg[k + 1]) continue;
                    skip = false;
                    break;
                }
                if (skip) continue;
                if (includeMetrics) {
                    int[] nArray4 = new int[3];
                    nArray4[0] = v[0];
                    nArray4[1] = v[1];
                    nArray3 = nArray4;
                    nArray4[2] = c;
                } else {
                    nArray3 = null;
                }
                longTag.put(gi, nArray3);
            }
        }
    }

    @Override
    void writeFont(PdfWriter writer, PdfIndirectReference ref, Object[] params) throws DocumentException, IOException {
        boolean subsetp;
        int firstChar = (Integer)params[0];
        int lastChar = (Integer)params[1];
        byte[] shortTag = (byte[])params[2];
        boolean bl = subsetp = (Boolean)params[3] != false && this.subset;
        if (!subsetp) {
            firstChar = 0;
            lastChar = shortTag.length - 1;
            for (int k = 0; k < shortTag.length; ++k) {
                shortTag[k] = 1;
            }
        }
        PdfIndirectReference ind_font = null;
        PdfDictionary pobj = null;
        PdfIndirectObject obj = null;
        String subsetPrefix = "";
        if (this.embedded) {
            if (this.cff) {
                pobj = new BaseFont.StreamFont(this.readCffFont(), "Type1C", this.compressionLevel);
                obj = writer.addToBody(pobj);
                ind_font = obj.getIndirectReference();
            } else {
                if (subsetp) {
                    subsetPrefix = TrueTypeFont.createSubsetPrefix();
                }
                HashMap<Integer, int[]> glyphs = new HashMap<Integer, int[]>();
                for (int k = firstChar; k <= lastChar; ++k) {
                    if (shortTag[k] == 0) continue;
                    int[] metrics = null;
                    if (this.specialMap != null) {
                        int[] cd = GlyphList.nameToUnicode(this.differences[k]);
                        if (cd != null) {
                            metrics = this.getMetricsTT(cd[0]);
                        }
                    } else {
                        metrics = this.fontSpecific ? this.getMetricsTT(k) : this.getMetricsTT(this.unicodeDifferences[k]);
                    }
                    if (metrics == null) continue;
                    glyphs.put(metrics[0], null);
                }
                this.addRangeUni(glyphs, false, subsetp);
                byte[] b = null;
                if (subsetp || this.directoryOffset != 0 || this.subsetRanges != null) {
                    TrueTypeFontSubSet sb = new TrueTypeFontSubSet(this.fileName, new RandomAccessFileOrArray(this.rf), glyphs, this.directoryOffset, true, !subsetp);
                    b = sb.process();
                } else {
                    b = this.getFullFont();
                }
                int[] lengths = new int[]{b.length};
                pobj = new BaseFont.StreamFont(b, lengths, this.compressionLevel);
                obj = writer.addToBody(pobj);
                ind_font = obj.getIndirectReference();
            }
        }
        if ((pobj = this.getFontDescriptor(ind_font, subsetPrefix, null)) != null) {
            obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        }
        pobj = this.getFontBaseType(ind_font, subsetPrefix, firstChar, lastChar, shortTag);
        writer.addToBody((PdfObject)pobj, ref);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected byte[] readCffFont() throws IOException {
        RandomAccessFileOrArray rf2 = new RandomAccessFileOrArray(this.rf);
        byte[] b = new byte[this.cffLength];
        try {
            rf2.reOpen();
            rf2.seek(this.cffOffset);
            rf2.readFully(b);
        }
        finally {
            try {
                rf2.close();
            }
            catch (Exception exception) {}
        }
        return b;
    }

    @Override
    public PdfStream getFullFontStream() throws IOException, DocumentException {
        if (this.cff) {
            return new BaseFont.StreamFont(this.readCffFont(), "Type1C", this.compressionLevel);
        }
        byte[] b = this.getFullFont();
        int[] lengths = new int[]{b.length};
        return new BaseFont.StreamFont(b, lengths, this.compressionLevel);
    }

    @Override
    public float getFontDescriptor(int key, float fontSize) {
        switch (key) {
            case 1: {
                return (float)this.os_2.sTypoAscender * fontSize / (float)this.head.unitsPerEm;
            }
            case 2: {
                return (float)this.os_2.sCapHeight * fontSize / (float)this.head.unitsPerEm;
            }
            case 3: {
                return (float)this.os_2.sTypoDescender * fontSize / (float)this.head.unitsPerEm;
            }
            case 4: {
                return (float)this.italicAngle;
            }
            case 5: {
                return fontSize * (float)this.head.xMin / (float)this.head.unitsPerEm;
            }
            case 6: {
                return fontSize * (float)this.head.yMin / (float)this.head.unitsPerEm;
            }
            case 7: {
                return fontSize * (float)this.head.xMax / (float)this.head.unitsPerEm;
            }
            case 8: {
                return fontSize * (float)this.head.yMax / (float)this.head.unitsPerEm;
            }
            case 9: {
                return fontSize * (float)this.hhea.Ascender / (float)this.head.unitsPerEm;
            }
            case 10: {
                return fontSize * (float)this.hhea.Descender / (float)this.head.unitsPerEm;
            }
            case 11: {
                return fontSize * (float)this.hhea.LineGap / (float)this.head.unitsPerEm;
            }
            case 12: {
                return fontSize * (float)this.hhea.advanceWidthMax / (float)this.head.unitsPerEm;
            }
            case 13: {
                return (float)(this.underlinePosition - this.underlineThickness / 2) * fontSize / (float)this.head.unitsPerEm;
            }
            case 14: {
                return (float)this.underlineThickness * fontSize / (float)this.head.unitsPerEm;
            }
            case 15: {
                return (float)this.os_2.yStrikeoutPosition * fontSize / (float)this.head.unitsPerEm;
            }
            case 16: {
                return (float)this.os_2.yStrikeoutSize * fontSize / (float)this.head.unitsPerEm;
            }
            case 17: {
                return (float)this.os_2.ySubscriptYSize * fontSize / (float)this.head.unitsPerEm;
            }
            case 18: {
                return (float)(-this.os_2.ySubscriptYOffset) * fontSize / (float)this.head.unitsPerEm;
            }
            case 19: {
                return (float)this.os_2.ySuperscriptYSize * fontSize / (float)this.head.unitsPerEm;
            }
            case 20: {
                return (float)this.os_2.ySuperscriptYOffset * fontSize / (float)this.head.unitsPerEm;
            }
        }
        return 0.0f;
    }

    public int[] getMetricsTT(int c) {
        if (this.cmapExt != null) {
            return this.cmapExt.get(c);
        }
        if (!this.fontSpecific && this.cmap31 != null) {
            return this.cmap31.get(c);
        }
        if (this.fontSpecific && this.cmap10 != null) {
            return this.cmap10.get(c);
        }
        if (this.cmap31 != null) {
            return this.cmap31.get(c);
        }
        if (this.cmap10 != null) {
            return this.cmap10.get(c);
        }
        return null;
    }

    @Override
    public String getPostscriptFontName() {
        return this.fontName;
    }

    @Override
    public String[] getCodePagesSupported() {
        long cp = ((long)this.os_2.ulCodePageRange2 << 32) + ((long)this.os_2.ulCodePageRange1 & 0xFFFFFFFFL);
        int count = 0;
        long bit = 1L;
        for (int k = 0; k < 64; ++k) {
            if ((cp & bit) != 0L && codePages[k] != null) {
                ++count;
            }
            bit <<= 1;
        }
        String[] ret = new String[count];
        count = 0;
        bit = 1L;
        for (int k = 0; k < 64; ++k) {
            if ((cp & bit) != 0L && codePages[k] != null) {
                ret[count++] = codePages[k];
            }
            bit <<= 1;
        }
        return ret;
    }

    @Override
    public String[][] getFullFontName() {
        return this.fullName;
    }

    @Override
    public String[][] getAllNameEntries() {
        return this.allNameEntries;
    }

    @Override
    public String[][] getFamilyFontName() {
        return this.familyName;
    }

    @Override
    public boolean hasKernPairs() {
        return this.kerning.size() > 0;
    }

    @Override
    public void setPostscriptFontName(String name) {
        this.fontName = name;
    }

    @Override
    public boolean setKerning(int char1, int char2, int kern) {
        int[] metrics = this.getMetricsTT(char1);
        if (metrics == null) {
            return false;
        }
        int c1 = metrics[0];
        metrics = this.getMetricsTT(char2);
        if (metrics == null) {
            return false;
        }
        int c2 = metrics[0];
        this.kerning.put((c1 << 16) + c2, kern);
        return true;
    }

    @Override
    protected int[] getRawCharBBox(int c, String name) {
        HashMap<Integer, int[]> map = name == null || this.cmap31 == null ? this.cmap10 : this.cmap31;
        if (map == null) {
            return null;
        }
        int[] metric = (int[])map.get(c);
        if (metric == null || this.bboxes == null) {
            return null;
        }
        return this.bboxes[metric[0]];
    }

    protected static class WindowsMetrics {
        short xAvgCharWidth;
        int usWeightClass;
        int usWidthClass;
        short fsType;
        short ySubscriptXSize;
        short ySubscriptYSize;
        short ySubscriptXOffset;
        short ySubscriptYOffset;
        short ySuperscriptXSize;
        short ySuperscriptYSize;
        short ySuperscriptXOffset;
        short ySuperscriptYOffset;
        short yStrikeoutSize;
        short yStrikeoutPosition;
        short sFamilyClass;
        byte[] panose = new byte[10];
        byte[] achVendID = new byte[4];
        int fsSelection;
        int usFirstCharIndex;
        int usLastCharIndex;
        short sTypoAscender;
        short sTypoDescender;
        short sTypoLineGap;
        int usWinAscent;
        int usWinDescent;
        int ulCodePageRange1;
        int ulCodePageRange2;
        int sCapHeight;

        protected WindowsMetrics() {
        }
    }

    protected static class HorizontalHeader {
        short Ascender;
        short Descender;
        short LineGap;
        int advanceWidthMax;
        short minLeftSideBearing;
        short minRightSideBearing;
        short xMaxExtent;
        short caretSlopeRise;
        short caretSlopeRun;
        int numberOfHMetrics;

        protected HorizontalHeader() {
        }
    }

    protected static class FontHeader {
        int flags;
        int unitsPerEm;
        short xMin;
        short yMin;
        short xMax;
        short yMax;
        int macStyle;

        protected FontHeader() {
        }
    }
}

