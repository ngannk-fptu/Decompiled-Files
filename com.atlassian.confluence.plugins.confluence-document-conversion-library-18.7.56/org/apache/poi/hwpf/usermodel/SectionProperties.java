/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.model.types.SEPAbstractType;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.DateAndTime;

public final class SectionProperties
extends SEPAbstractType
implements Duplicatable {
    private short field_60_rncftn;
    private short field_61_rncedn;
    private int field_62_nftn;
    private int field_63_nfcftnref = 0;
    private int field_64_nedn;
    private int field_65_nfcednref = 2;

    public SectionProperties() {
        this.field_20_brcTop = new BorderCode();
        this.field_21_brcLeft = new BorderCode();
        this.field_22_brcBottom = new BorderCode();
        this.field_23_brcRight = new BorderCode();
        this.field_26_dttmPropRMark = new DateAndTime();
    }

    public SectionProperties(SectionProperties other) {
        super(other);
        this.field_60_rncftn = other.field_60_rncftn;
        this.field_61_rncedn = other.field_61_rncedn;
        this.field_62_nftn = other.field_62_nftn;
        this.field_63_nfcftnref = other.field_63_nfcftnref;
        this.field_64_nedn = other.field_64_nedn;
        this.field_65_nfcednref = other.field_65_nfcednref;
    }

    @Override
    public SectionProperties copy() {
        return new SectionProperties(this);
    }

    public void setRncFtn(short field_60_rncftn) {
        this.field_60_rncftn = field_60_rncftn;
    }

    public short getRncFtn() {
        return this.field_60_rncftn;
    }

    public void setRncEdn(short field_61_rncedn) {
        this.field_61_rncedn = field_61_rncedn;
    }

    public short getRncEdn() {
        return this.field_61_rncedn;
    }

    public void setNFtn(int field_62_nftn) {
        this.field_62_nftn = field_62_nftn;
    }

    public int getNFtn() {
        return this.field_62_nftn;
    }

    public void setNfcFtnRef(int field_63_nfcftnref) {
        this.field_63_nfcftnref = field_63_nfcftnref;
    }

    public int getNfcFtnRef() {
        return this.field_63_nfcftnref;
    }

    public void setNEdn(int field_64_nedn) {
        this.field_64_nedn = field_64_nedn;
    }

    public int getNEdn() {
        return this.field_64_nedn;
    }

    public void setNfcEdnRef(int field_65_nfcednref) {
        this.field_65_nfcednref = field_65_nfcednref;
    }

    public int getNfcEdnRef() {
        return this.field_65_nfcednref;
    }
}

