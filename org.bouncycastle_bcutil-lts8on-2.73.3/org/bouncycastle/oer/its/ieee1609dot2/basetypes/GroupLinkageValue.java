/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.util.Arrays;

public class GroupLinkageValue
extends ASN1Object {
    private final ASN1OctetString jValue;
    private final ASN1OctetString value;

    private GroupLinkageValue(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.jValue = ASN1OctetString.getInstance((Object)seq.getObjectAt(0));
        this.value = ASN1OctetString.getInstance((Object)seq.getObjectAt(1));
        this.assertValues();
    }

    public GroupLinkageValue(ASN1OctetString jValue, ASN1OctetString value) {
        this.jValue = jValue;
        this.value = value;
        this.assertValues();
    }

    private void assertValues() {
        if (this.jValue == null || this.jValue.getOctets().length != 4) {
            throw new IllegalArgumentException("jValue is null or not four bytes long");
        }
        if (this.value == null || this.value.getOctets().length != 9) {
            throw new IllegalArgumentException("value is null or not nine bytes long");
        }
    }

    public static GroupLinkageValue getInstance(Object src) {
        if (src instanceof GroupLinkageValue) {
            return (GroupLinkageValue)((Object)src);
        }
        if (src != null) {
            return new GroupLinkageValue(ASN1Sequence.getInstance((Object)src));
        }
        return null;
    }

    public ASN1OctetString getJValue() {
        return this.jValue;
    }

    public ASN1OctetString getValue() {
        return this.value;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.jValue, this.value});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ASN1OctetString jValue;
        private ASN1OctetString value;

        public Builder setJValue(ASN1OctetString jValue) {
            this.jValue = jValue;
            return this;
        }

        public Builder setJValue(byte[] jValue) {
            return this.setJValue((ASN1OctetString)new DEROctetString(Arrays.clone((byte[])jValue)));
        }

        public Builder setValue(ASN1OctetString value) {
            this.value = value;
            return this;
        }

        public Builder setValue(byte[] value) {
            return this.setValue((ASN1OctetString)new DEROctetString(Arrays.clone((byte[])value)));
        }

        public GroupLinkageValue createGroupLinkageValue() {
            return new GroupLinkageValue(this.jValue, this.value);
        }
    }
}

