/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.BytePropertyNode;
import org.apache.poi.hwpf.model.ParagraphHeight;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.sprm.ParagraphSprmUncompressor;
import org.apache.poi.hwpf.sprm.SprmBuffer;
import org.apache.poi.hwpf.sprm.SprmOperation;
import org.apache.poi.hwpf.usermodel.ParagraphProperties;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class PAPX
extends BytePropertyNode<PAPX> {
    private ParagraphHeight _phe;

    public PAPX(PAPX other) {
        super(other);
        this._phe = other._phe == null ? null : other._phe.copy();
    }

    public PAPX(int charStart, int charEnd, byte[] papx, ParagraphHeight phe, byte[] dataStream) {
        super(charStart, charEnd, new SprmBuffer(papx, 2));
        this._phe = phe;
        SprmBuffer buf = this.findHuge(new SprmBuffer(papx, 2), dataStream);
        if (buf != null) {
            this._buf = buf;
        }
    }

    public PAPX(int charStart, int charEnd, SprmBuffer buf) {
        super(charStart, charEnd, buf);
        this._phe = new ParagraphHeight();
    }

    private SprmBuffer findHuge(SprmBuffer buf, byte[] datastream) {
        short grpprlSize;
        int hugeGrpprlOffset;
        SprmOperation sprm;
        byte[] grpprl = buf.toByteArray();
        if (grpprl.length == 8 && datastream != null && ((sprm = new SprmOperation(grpprl, 2)).getOperation() == 69 || sprm.getOperation() == 70) && sprm.getSizeCode() == 3 && (hugeGrpprlOffset = sprm.getOperand()) + 1 < datastream.length && hugeGrpprlOffset + (grpprlSize = LittleEndian.getShort(datastream, hugeGrpprlOffset)) < datastream.length) {
            byte[] hugeGrpprl = new byte[grpprlSize + 2];
            hugeGrpprl[0] = grpprl[0];
            hugeGrpprl[1] = grpprl[1];
            System.arraycopy(datastream, hugeGrpprlOffset + 2, hugeGrpprl, 2, grpprlSize);
            return new SprmBuffer(hugeGrpprl, 2);
        }
        return null;
    }

    public ParagraphHeight getParagraphHeight() {
        return this._phe;
    }

    public byte[] getGrpprl() {
        if (this._buf == null) {
            return new byte[0];
        }
        return ((SprmBuffer)this._buf).toByteArray();
    }

    public short getIstd() {
        if (this._buf == null) {
            return 0;
        }
        byte[] buf = this.getGrpprl();
        if (buf.length == 0) {
            return 0;
        }
        if (buf.length == 1) {
            return LittleEndian.getUByte(buf, 0);
        }
        return LittleEndian.getShort(buf);
    }

    public SprmBuffer getSprmBuf() {
        return (SprmBuffer)this._buf;
    }

    @Deprecated
    @Internal
    public ParagraphProperties getParagraphProperties(StyleSheet ss) {
        if (ss == null) {
            return new ParagraphProperties();
        }
        short istd = this.getIstd();
        ParagraphProperties baseStyle = ss.getParagraphStyle(istd);
        return ParagraphSprmUncompressor.uncompressPAP(baseStyle, this.getGrpprl(), 2);
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            return this._phe.equals(((PAPX)o)._phe);
        }
        return false;
    }

    @Override
    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    public String toString() {
        return "PAPX from " + this.getStart() + " to " + this.getEnd() + " (in bytes " + this.getStartBytes() + " to " + this.getEndBytes() + ")";
    }

    @Override
    public PAPX copy() {
        return new PAPX(this);
    }
}

