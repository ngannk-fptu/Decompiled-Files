/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1UTF8String
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.asn1.x509.GeneralName
 */
package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.GeneralName;

public class EncKeyWithID
extends ASN1Object {
    private final PrivateKeyInfo privKeyInfo;
    private final ASN1Encodable identifier;

    public static EncKeyWithID getInstance(Object o) {
        if (o instanceof EncKeyWithID) {
            return (EncKeyWithID)((Object)o);
        }
        if (o != null) {
            return new EncKeyWithID(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private EncKeyWithID(ASN1Sequence seq) {
        this.privKeyInfo = PrivateKeyInfo.getInstance((Object)seq.getObjectAt(0));
        this.identifier = seq.size() > 1 ? (!(seq.getObjectAt(1) instanceof ASN1UTF8String) ? GeneralName.getInstance((Object)seq.getObjectAt(1)) : seq.getObjectAt(1)) : null;
    }

    public EncKeyWithID(PrivateKeyInfo privKeyInfo) {
        this.privKeyInfo = privKeyInfo;
        this.identifier = null;
    }

    public EncKeyWithID(PrivateKeyInfo privKeyInfo, ASN1UTF8String str) {
        this.privKeyInfo = privKeyInfo;
        this.identifier = str;
    }

    public EncKeyWithID(PrivateKeyInfo privKeyInfo, GeneralName generalName) {
        this.privKeyInfo = privKeyInfo;
        this.identifier = generalName;
    }

    public PrivateKeyInfo getPrivateKey() {
        return this.privKeyInfo;
    }

    public boolean hasIdentifier() {
        return this.identifier != null;
    }

    public boolean isIdentifierUTF8String() {
        return this.identifier instanceof ASN1UTF8String;
    }

    public ASN1Encodable getIdentifier() {
        return this.identifier;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.privKeyInfo);
        if (this.identifier != null) {
            v.add(this.identifier);
        }
        return new DERSequence(v);
    }
}

