/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import java.util.Objects;
import org.apache.poi.hwpf.model.Grfhic;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class LFOAbstractType {
    protected int field_1_lsid;
    protected int field_2_unused1;
    protected int field_3_unused2;
    protected byte field_4_clfolvl;
    protected byte field_5_ibstFltAutoNum;
    protected Grfhic field_6_grfhic = new Grfhic();
    protected byte field_7_unused3;

    protected LFOAbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_lsid = LittleEndian.getInt(data, 0 + offset);
        this.field_2_unused1 = LittleEndian.getInt(data, 4 + offset);
        this.field_3_unused2 = LittleEndian.getInt(data, 8 + offset);
        this.field_4_clfolvl = data[12 + offset];
        this.field_5_ibstFltAutoNum = data[13 + offset];
        this.field_6_grfhic = new Grfhic(data, 14 + offset);
        this.field_7_unused3 = data[15 + offset];
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putInt(data, 0 + offset, this.field_1_lsid);
        LittleEndian.putInt(data, 4 + offset, this.field_2_unused1);
        LittleEndian.putInt(data, 8 + offset, this.field_3_unused2);
        data[12 + offset] = this.field_4_clfolvl;
        data[13 + offset] = this.field_5_ibstFltAutoNum;
        this.field_6_grfhic.serialize(data, 14 + offset);
        data[15 + offset] = this.field_7_unused3;
    }

    public byte[] serialize() {
        byte[] result = new byte[LFOAbstractType.getSize()];
        this.serialize(result, 0);
        return result;
    }

    public static int getSize() {
        return 16;
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
        LFOAbstractType other = (LFOAbstractType)obj;
        if (this.field_1_lsid != other.field_1_lsid) {
            return false;
        }
        if (this.field_2_unused1 != other.field_2_unused1) {
            return false;
        }
        if (this.field_3_unused2 != other.field_3_unused2) {
            return false;
        }
        if (this.field_4_clfolvl != other.field_4_clfolvl) {
            return false;
        }
        if (this.field_5_ibstFltAutoNum != other.field_5_ibstFltAutoNum) {
            return false;
        }
        if (this.field_6_grfhic == null ? other.field_6_grfhic != null : !this.field_6_grfhic.equals(other.field_6_grfhic)) {
            return false;
        }
        return this.field_7_unused3 == other.field_7_unused3;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_lsid, this.field_2_unused1, this.field_3_unused2, this.field_4_clfolvl, this.field_5_ibstFltAutoNum, this.field_6_grfhic, this.field_7_unused3);
    }

    public String toString() {
        return "[LFO]\n    .lsid                 =  ( " + this.field_1_lsid + " )\n    .unused1              =  ( " + this.field_2_unused1 + " )\n    .unused2              =  ( " + this.field_3_unused2 + " )\n    .clfolvl              =  ( " + this.field_4_clfolvl + " )\n    .ibstFltAutoNum       =  ( " + this.field_5_ibstFltAutoNum + " )\n    .grfhic               =  ( " + (this.field_6_grfhic == null ? "null" : this.field_6_grfhic.toString().replace("\n", "\n    ")) + " )\n    .unused3              =  ( " + this.field_7_unused3 + " )\n[/LFO]";
    }

    @Internal
    public int getLsid() {
        return this.field_1_lsid;
    }

    @Internal
    public void setLsid(int field_1_lsid) {
        this.field_1_lsid = field_1_lsid;
    }

    @Internal
    public int getUnused1() {
        return this.field_2_unused1;
    }

    @Internal
    public void setUnused1(int field_2_unused1) {
        this.field_2_unused1 = field_2_unused1;
    }

    @Internal
    public int getUnused2() {
        return this.field_3_unused2;
    }

    @Internal
    public void setUnused2(int field_3_unused2) {
        this.field_3_unused2 = field_3_unused2;
    }

    @Internal
    public byte getClfolvl() {
        return this.field_4_clfolvl;
    }

    @Internal
    public void setClfolvl(byte field_4_clfolvl) {
        this.field_4_clfolvl = field_4_clfolvl;
    }

    @Internal
    public byte getIbstFltAutoNum() {
        return this.field_5_ibstFltAutoNum;
    }

    @Internal
    public void setIbstFltAutoNum(byte field_5_ibstFltAutoNum) {
        this.field_5_ibstFltAutoNum = field_5_ibstFltAutoNum;
    }

    @Internal
    public Grfhic getGrfhic() {
        return this.field_6_grfhic;
    }

    @Internal
    public void setGrfhic(Grfhic field_6_grfhic) {
        this.field_6_grfhic = field_6_grfhic;
    }

    @Internal
    public byte getUnused3() {
        return this.field_7_unused3;
    }

    @Internal
    public void setUnused3(byte field_7_unused3) {
        this.field_7_unused3 = field_7_unused3;
    }
}

