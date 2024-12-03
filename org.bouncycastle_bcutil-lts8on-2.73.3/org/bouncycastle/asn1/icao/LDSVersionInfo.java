/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1PrintableString
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERPrintableString
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.icao;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;

public class LDSVersionInfo
extends ASN1Object {
    private ASN1PrintableString ldsVersion;
    private ASN1PrintableString unicodeVersion;

    public LDSVersionInfo(String ldsVersion, String unicodeVersion) {
        this.ldsVersion = new DERPrintableString(ldsVersion);
        this.unicodeVersion = new DERPrintableString(unicodeVersion);
    }

    private LDSVersionInfo(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("sequence wrong size for LDSVersionInfo");
        }
        this.ldsVersion = ASN1PrintableString.getInstance((Object)seq.getObjectAt(0));
        this.unicodeVersion = ASN1PrintableString.getInstance((Object)seq.getObjectAt(1));
    }

    public static LDSVersionInfo getInstance(Object obj) {
        if (obj instanceof LDSVersionInfo) {
            return (LDSVersionInfo)((Object)obj);
        }
        if (obj != null) {
            return new LDSVersionInfo(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public String getLdsVersion() {
        return this.ldsVersion.getString();
    }

    public String getUnicodeVersion() {
        return this.unicodeVersion.getString();
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.ldsVersion);
        v.add((ASN1Encodable)this.unicodeVersion);
        return new DERSequence(v);
    }
}

