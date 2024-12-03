/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.model.FontTable;
import org.apache.poi.hwpf.model.OldCHPBinTable;
import org.apache.poi.hwpf.model.OldComplexFileTable;
import org.apache.poi.hwpf.model.OldFfn;
import org.apache.poi.hwpf.model.OldFontTable;
import org.apache.poi.hwpf.model.OldPAPBinTable;
import org.apache.poi.hwpf.model.OldSectionTable;
import org.apache.poi.hwpf.model.OldTextPieceTable;
import org.apache.poi.hwpf.model.PieceDescriptor;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.hwpf.model.TextPieceTable;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.util.DoubleByteUtil;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.util.StringUtil;

public class HWPFOldDocument
extends HWPFDocumentCore {
    private static final Logger LOG = LogManager.getLogger(HWPFOldDocument.class);
    private static final int DEFAULT_MAX_RECORD_LENGTH = 10000000;
    private static int MAX_RECORD_LENGTH = 10000000;
    private static final Charset DEFAULT_CHARSET = StringUtil.WIN_1252;
    private OldTextPieceTable tpt;
    private StringBuilder _text;
    private final OldFontTable fontTable;
    private final Charset guessedCharset;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public HWPFOldDocument(POIFSFileSystem fs) throws IOException {
        this(fs.getRoot());
    }

    public HWPFOldDocument(DirectoryNode directory) throws IOException {
        super(directory);
        int sedTableOffset = LittleEndian.getInt(this._mainStream, 136);
        int sedTableSize = LittleEndian.getInt(this._mainStream, 140);
        int chpTableOffset = LittleEndian.getInt(this._mainStream, 184);
        int chpTableSize = LittleEndian.getInt(this._mainStream, 188);
        int papTableOffset = LittleEndian.getInt(this._mainStream, 192);
        int papTableSize = LittleEndian.getInt(this._mainStream, 196);
        int fontTableOffset = LittleEndian.getInt(this._mainStream, 208);
        int fontTableSize = LittleEndian.getInt(this._mainStream, 212);
        this.fontTable = new OldFontTable(this._mainStream, fontTableOffset, fontTableSize);
        this.guessedCharset = this.guessCodePage(this.fontTable);
        int complexTableOffset = LittleEndian.getInt(this._mainStream, 352);
        OldComplexFileTable cft = null;
        if (this._fib.getFibBase().isFComplex()) {
            cft = new OldComplexFileTable(this._mainStream, this._mainStream, complexTableOffset, this._fib.getFibBase().getFcMin(), this.guessedCharset);
            this.tpt = (OldTextPieceTable)cft.getTextPieceTable();
        } else {
            TextPiece tp = null;
            try {
                tp = this.buildTextPiece(this.guessedCharset);
            }
            catch (IllegalStateException e) {
                tp = this.buildTextPiece(StringUtil.WIN_1252);
                LOG.atWarn().log("Error with {}. Backing off to Windows-1252", (Object)this.guessedCharset);
            }
            this.tpt.add(tp);
        }
        this._text = this.tpt.getText();
        this._cbt = new OldCHPBinTable(this._mainStream, chpTableOffset, chpTableSize, this._fib.getFibBase().getFcMin(), this.tpt);
        this._pbt = new OldPAPBinTable(this._mainStream, papTableOffset, papTableSize, this._fib.getFibBase().getFcMin(), this.tpt);
        this._st = new OldSectionTable(this._mainStream, sedTableOffset, sedTableSize, this._fib.getFibBase().getFcMin(), this.tpt);
        boolean preserveBinTables = false;
        try {
            preserveBinTables = Boolean.parseBoolean(System.getProperty("org.apache.poi.hwpf.preserveBinTables"));
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (!preserveBinTables) {
            this._cbt.rebuild(cft);
            this._pbt.rebuild(this._text, cft);
        }
    }

    private TextPiece buildTextPiece(Charset guessedCharset) throws IllegalStateException {
        PieceDescriptor pd = new PieceDescriptor(new byte[]{0, 0, 0, 0, 0, 127, 0, 0}, 0, guessedCharset);
        pd.setFilePosition(this._fib.getFibBase().getFcMin());
        this.tpt = new OldTextPieceTable();
        byte[] textData = IOUtils.safelyClone(this._mainStream, this._fib.getFibBase().getFcMin(), this._fib.getFibBase().getFcMac() - this._fib.getFibBase().getFcMin(), MAX_RECORD_LENGTH);
        int numChars = textData.length;
        if (DoubleByteUtil.DOUBLE_BYTE_CHARSETS.contains(guessedCharset)) {
            numChars /= 2;
        }
        return new TextPiece(0, numChars, textData, pd);
    }

    private Charset guessCodePage(OldFontTable fontTable) {
        for (OldFfn oldFfn : fontTable.getFontNames()) {
            FontCharset wmfCharset = FontCharset.valueOf(oldFfn.getChs() & 0xFF);
            if (wmfCharset == null || wmfCharset == FontCharset.ANSI || wmfCharset == FontCharset.DEFAULT || wmfCharset == FontCharset.SYMBOL) continue;
            return wmfCharset.getCharset();
        }
        LOG.atWarn().log("Couldn't find a defined charset; backing off to cp1252");
        return DEFAULT_CHARSET;
    }

    @Override
    public Range getOverallRange() {
        return new Range(0, this._fib.getFibBase().getFcMac() - this._fib.getFibBase().getFcMin(), this);
    }

    @Override
    @NotImplemented
    public FontTable getFontTable() {
        throw new UnsupportedOperationException("Use getOldFontTable instead.");
    }

    public OldFontTable getOldFontTable() {
        return this.fontTable;
    }

    @Override
    public Range getRange() {
        return this.getOverallRange();
    }

    @Override
    public TextPieceTable getTextTable() {
        return this.tpt;
    }

    @Override
    public StringBuilder getText() {
        return this._text;
    }

    @Override
    public void write() throws IOException {
        throw new IllegalStateException("Writing is not available for the older file formats");
    }

    @Override
    public void write(File out) throws IOException {
        throw new IllegalStateException("Writing is not available for the older file formats");
    }

    @Override
    public void write(OutputStream out) throws IOException {
        throw new IllegalStateException("Writing is not available for the older file formats");
    }

    public Charset getGuessedCharset() {
        return this.guessedCharset;
    }
}

