/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.nio.charset.Charset;
import java.util.Objects;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.model.PropertyModifier;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

@Internal
public final class PieceDescriptor
implements Duplicatable {
    private final short descriptor;
    int fc;
    private final PropertyModifier prm;
    private final boolean unicode;
    private final Charset charset;

    public PieceDescriptor(PieceDescriptor other) {
        this.descriptor = other.descriptor;
        this.fc = other.fc;
        this.prm = other.prm == null ? null : other.prm.copy();
        this.unicode = other.unicode;
        this.charset = other.charset;
    }

    public PieceDescriptor(byte[] buf, int offset) {
        this(buf, offset, null);
    }

    public PieceDescriptor(byte[] buf, int offset, Charset charset) {
        this.descriptor = LittleEndian.getShort(buf, offset);
        this.fc = LittleEndian.getInt(buf, offset += 2);
        this.prm = new PropertyModifier(LittleEndian.getShort(buf, offset += 4));
        if (charset == null) {
            if ((this.fc & 0x40000000) == 0) {
                this.unicode = true;
                this.charset = null;
            } else {
                this.unicode = false;
                this.fc &= 0xBFFFFFFF;
                this.fc /= 2;
                this.charset = StringUtil.WIN_1252;
            }
        } else {
            this.unicode = charset == StringUtil.UTF16LE;
            this.charset = charset;
        }
    }

    public int getFilePosition() {
        return this.fc;
    }

    public void setFilePosition(int pos) {
        this.fc = pos;
    }

    public boolean isUnicode() {
        return this.unicode;
    }

    public int hashCode() {
        return Objects.hash(this.descriptor, this.prm, this.unicode);
    }

    public Charset getCharset() {
        return this.charset;
    }

    public PropertyModifier getPrm() {
        return this.prm;
    }

    protected byte[] toByteArray() {
        int tempFc = this.fc;
        if (!this.unicode) {
            tempFc *= 2;
            tempFc |= 0x40000000;
        }
        int offset = 0;
        byte[] buf = new byte[8];
        LittleEndian.putShort(buf, offset, this.descriptor);
        LittleEndian.putInt(buf, offset += 2, tempFc);
        LittleEndian.putShort(buf, offset += 4, this.prm.getValue());
        return buf;
    }

    public static int getSizeInBytes() {
        return 8;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        PieceDescriptor other = (PieceDescriptor)obj;
        if (this.descriptor != other.descriptor) {
            return false;
        }
        if (this.prm == null ? other.prm != null : !this.prm.equals(other.prm)) {
            return false;
        }
        return this.unicode == other.unicode;
    }

    public String toString() {
        return "PieceDescriptor (pos: " + this.getFilePosition() + "; " + (this.isUnicode() ? "unicode" : "non-unicode") + "; prm: " + this.getPrm() + ")";
    }

    @Override
    public PieceDescriptor copy() {
        return new PieceDescriptor(this);
    }
}

