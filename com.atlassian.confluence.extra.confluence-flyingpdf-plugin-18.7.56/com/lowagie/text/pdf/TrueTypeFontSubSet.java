/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

class TrueTypeFontSubSet {
    static final String[] tableNamesSimple = new String[]{"cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "prep"};
    static final String[] tableNamesCmap = new String[]{"cmap", "cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "prep"};
    static final String[] tableNamesExtra = new String[]{"OS/2", "cmap", "cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "name, prep"};
    static final int[] entrySelectors = new int[]{0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4};
    static final int TABLE_CHECKSUM = 0;
    static final int TABLE_OFFSET = 1;
    static final int TABLE_LENGTH = 2;
    static final int HEAD_LOCA_FORMAT_OFFSET = 51;
    static final int ARG_1_AND_2_ARE_WORDS = 1;
    static final int WE_HAVE_A_SCALE = 8;
    static final int MORE_COMPONENTS = 32;
    static final int WE_HAVE_AN_X_AND_Y_SCALE = 64;
    static final int WE_HAVE_A_TWO_BY_TWO = 128;
    protected HashMap<String, int[]> tableDirectory;
    protected RandomAccessFileOrArray rf;
    protected String fileName;
    protected boolean includeCmap;
    protected boolean includeExtras;
    protected boolean locaShortTable;
    protected int[] locaTable;
    protected HashMap<Integer, int[]> glyphsUsed;
    protected ArrayList<Integer> glyphsInList;
    protected int tableGlyphOffset;
    protected int[] newLocaTable;
    protected byte[] newLocaTableOut;
    protected byte[] newGlyfTable;
    protected int glyfTableRealSize;
    protected int locaTableRealSize;
    protected byte[] outFont;
    protected int fontPtr;
    protected int directoryOffset;

    TrueTypeFontSubSet(String fileName, RandomAccessFileOrArray rf, HashMap<Integer, int[]> glyphsUsed, int directoryOffset, boolean includeCmap, boolean includeExtras) {
        this.fileName = fileName;
        this.rf = rf;
        this.glyphsUsed = glyphsUsed;
        this.includeCmap = includeCmap;
        this.includeExtras = includeExtras;
        this.directoryOffset = directoryOffset;
        this.glyphsInList = new ArrayList<Integer>(glyphsUsed.keySet());
    }

    byte[] process() throws IOException, DocumentException {
        try {
            this.rf.reOpen();
            this.createTableDirectory();
            this.readLoca();
            this.flatGlyphs();
            this.createNewGlyphTables();
            this.locaTobytes();
            this.assembleFont();
            byte[] byArray = this.outFont;
            return byArray;
        }
        finally {
            try {
                this.rf.close();
            }
            catch (Exception exception) {}
        }
    }

    protected void assembleFont() throws IOException {
        int[] tableLocation;
        int fullFontSize = 0;
        String[] tableNames = this.includeExtras ? tableNamesExtra : (this.includeCmap ? tableNamesCmap : tableNamesSimple);
        int tablesUsed = 2;
        int len = 0;
        for (String name : tableNames) {
            if (name.equals("glyf") || name.equals("loca") || (tableLocation = this.tableDirectory.get(name)) == null) continue;
            ++tablesUsed;
            fullFontSize += tableLocation[2] + 3 & 0xFFFFFFFC;
        }
        fullFontSize += this.newLocaTableOut.length;
        fullFontSize += this.newGlyfTable.length;
        int ref = 16 * tablesUsed + 12;
        this.outFont = new byte[fullFontSize += ref];
        this.fontPtr = 0;
        this.writeFontInt(65536);
        this.writeFontShort(tablesUsed);
        int selector = entrySelectors[tablesUsed];
        this.writeFontShort((1 << selector) * 16);
        this.writeFontShort(selector);
        this.writeFontShort((tablesUsed - (1 << selector)) * 16);
        for (String name : tableNames) {
            tableLocation = this.tableDirectory.get(name);
            if (tableLocation == null) continue;
            this.writeFontString(name);
            if (name.equals("glyf")) {
                this.writeFontInt(this.calculateChecksum(this.newGlyfTable));
                len = this.glyfTableRealSize;
            } else if (name.equals("loca")) {
                this.writeFontInt(this.calculateChecksum(this.newLocaTableOut));
                len = this.locaTableRealSize;
            } else {
                this.writeFontInt(tableLocation[0]);
                len = tableLocation[2];
            }
            this.writeFontInt(ref);
            this.writeFontInt(len);
            ref += len + 3 & 0xFFFFFFFC;
        }
        for (String name : tableNames) {
            tableLocation = this.tableDirectory.get(name);
            if (tableLocation == null) continue;
            if (name.equals("glyf")) {
                System.arraycopy(this.newGlyfTable, 0, this.outFont, this.fontPtr, this.newGlyfTable.length);
                this.fontPtr += this.newGlyfTable.length;
                this.newGlyfTable = null;
                continue;
            }
            if (name.equals("loca")) {
                System.arraycopy(this.newLocaTableOut, 0, this.outFont, this.fontPtr, this.newLocaTableOut.length);
                this.fontPtr += this.newLocaTableOut.length;
                this.newLocaTableOut = null;
                continue;
            }
            this.rf.seek(tableLocation[1]);
            this.rf.readFully(this.outFont, this.fontPtr, tableLocation[2]);
            this.fontPtr += tableLocation[2] + 3 & 0xFFFFFFFC;
        }
    }

    protected void createTableDirectory() throws IOException, DocumentException {
        this.tableDirectory = new HashMap();
        this.rf.seek(this.directoryOffset);
        int id = this.rf.readInt();
        if (id != 65536) {
            throw new DocumentException(MessageLocalization.getComposedMessage("1.is.not.a.true.type.file", this.fileName));
        }
        int num_tables = this.rf.readUnsignedShort();
        this.rf.skipBytes(6);
        for (int k = 0; k < num_tables; ++k) {
            String tag = this.readStandardString(4);
            int[] tableLocation = new int[]{this.rf.readInt(), this.rf.readInt(), this.rf.readInt()};
            this.tableDirectory.put(tag, tableLocation);
        }
    }

    protected void readLoca() throws IOException, DocumentException {
        int[] tableLocation = this.tableDirectory.get("head");
        if (tableLocation == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "head", this.fileName));
        }
        this.rf.seek(tableLocation[1] + 51);
        this.locaShortTable = this.rf.readUnsignedShort() == 0;
        tableLocation = this.tableDirectory.get("loca");
        if (tableLocation == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "loca", this.fileName));
        }
        this.rf.seek(tableLocation[1]);
        if (this.locaShortTable) {
            int entries = tableLocation[2] / 2;
            this.locaTable = new int[entries];
            for (int k = 0; k < entries; ++k) {
                this.locaTable[k] = this.rf.readUnsignedShort() * 2;
            }
        } else {
            int entries = tableLocation[2] / 4;
            this.locaTable = new int[entries];
            for (int k = 0; k < entries; ++k) {
                this.locaTable[k] = this.rf.readInt();
            }
        }
    }

    protected void createNewGlyphTables() throws IOException {
        this.newLocaTable = new int[this.locaTable.length];
        int[] activeGlyphs = new int[this.glyphsInList.size()];
        for (int k = 0; k < activeGlyphs.length; ++k) {
            activeGlyphs[k] = this.glyphsInList.get(k);
        }
        Arrays.sort(activeGlyphs);
        int glyfSize = 0;
        for (int glyph : activeGlyphs) {
            glyfSize += this.locaTable[glyph + 1] - this.locaTable[glyph];
        }
        this.glyfTableRealSize = glyfSize;
        glyfSize = glyfSize + 3 & 0xFFFFFFFC;
        this.newGlyfTable = new byte[glyfSize];
        int glyfPtr = 0;
        int listGlyf = 0;
        for (int k = 0; k < this.newLocaTable.length; ++k) {
            this.newLocaTable[k] = glyfPtr;
            if (listGlyf >= activeGlyphs.length || activeGlyphs[listGlyf] != k) continue;
            ++listGlyf;
            this.newLocaTable[k] = glyfPtr;
            int start = this.locaTable[k];
            int len = 0;
            if (this.locaTable.length > k + 1) {
                len = this.locaTable[k + 1] - start;
            }
            if (len <= 0) continue;
            this.rf.seek(this.tableGlyphOffset + start);
            this.rf.readFully(this.newGlyfTable, glyfPtr, len);
            glyfPtr += len;
        }
    }

    protected void locaTobytes() {
        this.locaTableRealSize = this.locaShortTable ? this.newLocaTable.length * 2 : this.newLocaTable.length * 4;
        this.newLocaTableOut = new byte[this.locaTableRealSize + 3 & 0xFFFFFFFC];
        this.outFont = this.newLocaTableOut;
        this.fontPtr = 0;
        for (int i : this.newLocaTable) {
            if (this.locaShortTable) {
                this.writeFontShort(i / 2);
                continue;
            }
            this.writeFontInt(i);
        }
    }

    protected void flatGlyphs() throws IOException, DocumentException {
        int[] tableLocation = this.tableDirectory.get("glyf");
        if (tableLocation == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("table.1.does.not.exist.in.2", "glyf", this.fileName));
        }
        Integer glyph0 = 0;
        if (!this.glyphsUsed.containsKey(glyph0)) {
            this.glyphsUsed.put(glyph0, null);
            this.glyphsInList.add(glyph0);
        }
        this.tableGlyphOffset = tableLocation[1];
        for (int k = 0; k < this.glyphsInList.size(); ++k) {
            int glyph = this.glyphsInList.get(k);
            this.checkGlyphComposite(glyph);
        }
    }

    protected void checkGlyphComposite(int glyph) throws IOException {
        int start = this.locaTable[glyph];
        if (start == this.locaTable[glyph + 1]) {
            return;
        }
        this.rf.seek(this.tableGlyphOffset + start);
        short numContours = this.rf.readShort();
        if (numContours >= 0) {
            return;
        }
        this.rf.skipBytes(8);
        while (true) {
            int flags = this.rf.readUnsignedShort();
            Integer cGlyph = this.rf.readUnsignedShort();
            if (!this.glyphsUsed.containsKey(cGlyph)) {
                this.glyphsUsed.put(cGlyph, null);
                this.glyphsInList.add(cGlyph);
            }
            if ((flags & 0x20) == 0) {
                return;
            }
            int skip = (flags & 1) != 0 ? 4 : 2;
            if ((flags & 8) != 0) {
                skip += 2;
            } else if ((flags & 0x40) != 0) {
                skip += 4;
            }
            if ((flags & 0x80) != 0) {
                skip += 8;
            }
            this.rf.skipBytes(skip);
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

    protected void writeFontShort(int n) {
        this.outFont[this.fontPtr++] = (byte)(n >> 8);
        this.outFont[this.fontPtr++] = (byte)n;
    }

    protected void writeFontInt(int n) {
        this.outFont[this.fontPtr++] = (byte)(n >> 24);
        this.outFont[this.fontPtr++] = (byte)(n >> 16);
        this.outFont[this.fontPtr++] = (byte)(n >> 8);
        this.outFont[this.fontPtr++] = (byte)n;
    }

    protected void writeFontString(String s) {
        byte[] b = PdfEncodings.convertToBytes(s, "Cp1252");
        System.arraycopy(b, 0, this.outFont, this.fontPtr, b.length);
        this.fontPtr += b.length;
    }

    protected int calculateChecksum(byte[] b) {
        int len = b.length / 4;
        int v0 = 0;
        int v1 = 0;
        int v2 = 0;
        int v3 = 0;
        int ptr = 0;
        for (int k = 0; k < len; ++k) {
            v3 += b[ptr++] & 0xFF;
            v2 += b[ptr++] & 0xFF;
            v1 += b[ptr++] & 0xFF;
            v0 += b[ptr++] & 0xFF;
        }
        return v0 + (v1 << 8) + (v2 << 16) + (v3 << 24);
    }
}

