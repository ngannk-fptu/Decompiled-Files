/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;

@Internal
public abstract class FLDAbstractType {
    protected byte field_1_chHolder;
    private static final BitField ch = new BitField(31);
    private static final BitField reserved = new BitField(224);
    protected byte field_2_flt;
    private static final BitField fDiffer = new BitField(1);
    private static final BitField fZombieEmbed = new BitField(2);
    private static final BitField fResultDirty = new BitField(4);
    private static final BitField fResultEdited = new BitField(8);
    private static final BitField fLocked = new BitField(16);
    private static final BitField fPrivateResult = new BitField(32);
    private static final BitField fNested = new BitField(64);
    private static final BitField fHasSep = new BitField(64);

    protected void fillFields(byte[] data, int offset) {
        this.field_1_chHolder = data[0 + offset];
        this.field_2_flt = data[1 + offset];
    }

    public void serialize(byte[] data, int offset) {
        data[0 + offset] = this.field_1_chHolder;
        data[1 + offset] = this.field_2_flt;
    }

    public String toString() {
        return "[FLD]\n    .chHolder             =  (" + this.getChHolder() + " )\n         .ch                       = " + this.getCh() + "\n         .reserved                 = " + this.getReserved() + "\n    .flt                  =  (" + this.getFlt() + " )\n         .fDiffer                  = " + this.isFDiffer() + "\n         .fZombieEmbed             = " + this.isFZombieEmbed() + "\n         .fResultDirty             = " + this.isFResultDirty() + "\n         .fResultEdited            = " + this.isFResultEdited() + "\n         .fLocked                  = " + this.isFLocked() + "\n         .fPrivateResult           = " + this.isFPrivateResult() + "\n         .fNested                  = " + this.isFNested() + "\n         .fHasSep                  = " + this.isFHasSep() + "\n[/FLD]\n";
    }

    public static int getSize() {
        return 6;
    }

    public byte getChHolder() {
        return this.field_1_chHolder;
    }

    public void setChHolder(byte field_1_chHolder) {
        this.field_1_chHolder = field_1_chHolder;
    }

    public byte getFlt() {
        return this.field_2_flt;
    }

    public void setFlt(byte field_2_flt) {
        this.field_2_flt = field_2_flt;
    }

    public void setCh(byte value) {
        this.field_1_chHolder = (byte)ch.setValue(this.field_1_chHolder, value);
    }

    public byte getCh() {
        return (byte)ch.getValue(this.field_1_chHolder);
    }

    public void setReserved(byte value) {
        this.field_1_chHolder = (byte)reserved.setValue(this.field_1_chHolder, value);
    }

    public byte getReserved() {
        return (byte)reserved.getValue(this.field_1_chHolder);
    }

    public void setFDiffer(boolean value) {
        this.field_2_flt = (byte)fDiffer.setBoolean(this.field_2_flt, value);
    }

    public boolean isFDiffer() {
        return fDiffer.isSet(this.field_2_flt);
    }

    public void setFZombieEmbed(boolean value) {
        this.field_2_flt = (byte)fZombieEmbed.setBoolean(this.field_2_flt, value);
    }

    public boolean isFZombieEmbed() {
        return fZombieEmbed.isSet(this.field_2_flt);
    }

    public void setFResultDirty(boolean value) {
        this.field_2_flt = (byte)fResultDirty.setBoolean(this.field_2_flt, value);
    }

    public boolean isFResultDirty() {
        return fResultDirty.isSet(this.field_2_flt);
    }

    public void setFResultEdited(boolean value) {
        this.field_2_flt = (byte)fResultEdited.setBoolean(this.field_2_flt, value);
    }

    public boolean isFResultEdited() {
        return fResultEdited.isSet(this.field_2_flt);
    }

    public void setFLocked(boolean value) {
        this.field_2_flt = (byte)fLocked.setBoolean(this.field_2_flt, value);
    }

    public boolean isFLocked() {
        return fLocked.isSet(this.field_2_flt);
    }

    public void setFPrivateResult(boolean value) {
        this.field_2_flt = (byte)fPrivateResult.setBoolean(this.field_2_flt, value);
    }

    public boolean isFPrivateResult() {
        return fPrivateResult.isSet(this.field_2_flt);
    }

    public void setFNested(boolean value) {
        this.field_2_flt = (byte)fNested.setBoolean(this.field_2_flt, value);
    }

    public boolean isFNested() {
        return fNested.isSet(this.field_2_flt);
    }

    public void setFHasSep(boolean value) {
        this.field_2_flt = (byte)fHasSep.setBoolean(this.field_2_flt, value);
    }

    public boolean isFHasSep() {
        return fHasSep.isSet(this.field_2_flt);
    }
}

