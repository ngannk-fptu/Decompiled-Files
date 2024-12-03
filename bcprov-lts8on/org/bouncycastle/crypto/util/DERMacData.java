/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.crypto.util.DerUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public final class DERMacData {
    private final byte[] macData;

    private DERMacData(byte[] macData) {
        this.macData = macData;
    }

    public byte[] getMacData() {
        return Arrays.clone(this.macData);
    }

    public static final class Builder {
        private final Type type;
        private ASN1OctetString idU;
        private ASN1OctetString idV;
        private ASN1OctetString ephemDataU;
        private ASN1OctetString ephemDataV;
        private byte[] text;

        public Builder(Type type, byte[] idU, byte[] idV, byte[] ephemDataU, byte[] ephemDataV) {
            this.type = type;
            this.idU = DerUtil.getOctetString(idU);
            this.idV = DerUtil.getOctetString(idV);
            this.ephemDataU = DerUtil.getOctetString(ephemDataU);
            this.ephemDataV = DerUtil.getOctetString(ephemDataV);
        }

        public Builder withText(byte[] text) {
            this.text = DerUtil.toByteArray(new DERTaggedObject(false, 0, (ASN1Encodable)DerUtil.getOctetString(text)));
            return this;
        }

        public DERMacData build() {
            switch (this.type) {
                case UNILATERALU: 
                case BILATERALU: {
                    return new DERMacData(this.concatenate(this.type.getHeader(), DerUtil.toByteArray(this.idU), DerUtil.toByteArray(this.idV), DerUtil.toByteArray(this.ephemDataU), DerUtil.toByteArray(this.ephemDataV), this.text));
                }
                case UNILATERALV: 
                case BILATERALV: {
                    return new DERMacData(this.concatenate(this.type.getHeader(), DerUtil.toByteArray(this.idV), DerUtil.toByteArray(this.idU), DerUtil.toByteArray(this.ephemDataV), DerUtil.toByteArray(this.ephemDataU), this.text));
                }
            }
            throw new IllegalStateException("Unknown type encountered in build");
        }

        private byte[] concatenate(byte[] header, byte[] id1, byte[] id2, byte[] ed1, byte[] ed2, byte[] text) {
            return Arrays.concatenate(Arrays.concatenate(header, id1, id2), Arrays.concatenate(ed1, ed2, text));
        }
    }

    public static enum Type {
        UNILATERALU("KC_1_U"),
        UNILATERALV("KC_1_V"),
        BILATERALU("KC_2_U"),
        BILATERALV("KC_2_V");

        private final String enc;

        private Type(String enc) {
            this.enc = enc;
        }

        public byte[] getHeader() {
            return Strings.toByteArray(this.enc);
        }
    }
}

