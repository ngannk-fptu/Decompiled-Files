/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hwpf.model.Ffn;
import org.apache.poi.hwpf.model.io.HWPFFileSystem;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class FontTable {
    private static final Logger LOG = LogManager.getLogger(FontTable.class);
    private short _stringCount;
    private short _extraDataSz;
    private int lcbSttbfffn;
    private int fcSttbfffn;
    private Ffn[] _fontNames;

    public FontTable(byte[] buf, int offset, int lcbSttbfffn) {
        this.lcbSttbfffn = lcbSttbfffn;
        this.fcSttbfffn = offset;
        this._stringCount = LittleEndian.getShort(buf, offset);
        this._extraDataSz = LittleEndian.getShort(buf, offset += 2);
        offset += 2;
        this._fontNames = new Ffn[this._stringCount];
        for (int i = 0; i < this._stringCount; ++i) {
            this._fontNames[i] = new Ffn(buf, offset);
            offset += this._fontNames[i].getSize();
        }
    }

    public short getStringCount() {
        return this._stringCount;
    }

    public short getExtraDataSz() {
        return this._extraDataSz;
    }

    public Ffn[] getFontNames() {
        return this._fontNames;
    }

    public int getSize() {
        return this.lcbSttbfffn;
    }

    public String getMainFont(int chpFtc) {
        if (chpFtc >= this._stringCount) {
            LOG.atInfo().log("Mismatch in chpFtc with stringCount");
            return null;
        }
        return this._fontNames[chpFtc].getMainFontName();
    }

    public String getAltFont(int chpFtc) {
        if (chpFtc >= this._stringCount) {
            LOG.atInfo().log("Mismatch in chpFtc with stringCount");
            return null;
        }
        return this._fontNames[chpFtc].getAltFontName();
    }

    public void setStringCount(short stringCount) {
        this._stringCount = stringCount;
    }

    @Deprecated
    public void writeTo(HWPFFileSystem sys) throws IOException {
        ByteArrayOutputStream tableStream = sys.getStream("1Table");
        this.writeTo(tableStream);
    }

    public void writeTo(ByteArrayOutputStream tableStream) throws IOException {
        byte[] buf = new byte[2];
        LittleEndian.putShort(buf, 0, this._stringCount);
        tableStream.write(buf);
        LittleEndian.putShort(buf, 0, this._extraDataSz);
        tableStream.write(buf);
        for (Ffn fontName : this._fontNames) {
            tableStream.write(fontName.toByteArray());
        }
    }

    public boolean equals(Object other) {
        if (!(other instanceof FontTable)) {
            return false;
        }
        FontTable o = (FontTable)other;
        if (o._stringCount != this._stringCount || o._extraDataSz != this._extraDataSz || o._fontNames.length != this._fontNames.length) {
            return false;
        }
        for (int i = 0; i < o._fontNames.length; ++i) {
            if (o._fontNames[i].equals(this._fontNames[i])) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }
}

