/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class ParagraphHeight
implements Duplicatable {
    private static final BitField fSpare = BitFieldFactory.getInstance(1);
    private static final BitField fUnk = BitFieldFactory.getInstance(2);
    private static final BitField fDiffLines = BitFieldFactory.getInstance(4);
    private static final BitField clMac = BitFieldFactory.getInstance(65280);
    private short infoField;
    private short reserved;
    private int dxaCol;
    private int dymLineOrHeight;

    public ParagraphHeight() {
    }

    public ParagraphHeight(ParagraphHeight other) {
        this.infoField = other.infoField;
        this.reserved = other.reserved;
        this.dxaCol = other.dxaCol;
        this.dymLineOrHeight = other.dymLineOrHeight;
    }

    public ParagraphHeight(byte[] buf, int offset) {
        this.infoField = LittleEndian.getShort(buf, offset);
        this.reserved = LittleEndian.getShort(buf, offset += 2);
        this.dxaCol = LittleEndian.getInt(buf, offset += 2);
        this.dymLineOrHeight = LittleEndian.getInt(buf, offset += 4);
    }

    public void write(OutputStream out) throws IOException {
        out.write(this.toByteArray());
    }

    protected byte[] toByteArray() {
        byte[] buf = new byte[12];
        int offset = 0;
        LittleEndian.putShort(buf, offset, this.infoField);
        LittleEndian.putShort(buf, offset += 2, this.reserved);
        LittleEndian.putInt(buf, offset += 2, this.dxaCol);
        LittleEndian.putInt(buf, offset += 4, this.dymLineOrHeight);
        return buf;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ParagraphHeight)) {
            return false;
        }
        ParagraphHeight ph = (ParagraphHeight)o;
        return this.infoField == ph.infoField && this.reserved == ph.reserved && this.dxaCol == ph.dxaCol && this.dymLineOrHeight == ph.dymLineOrHeight;
    }

    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    @Override
    public ParagraphHeight copy() {
        return new ParagraphHeight(this);
    }
}

