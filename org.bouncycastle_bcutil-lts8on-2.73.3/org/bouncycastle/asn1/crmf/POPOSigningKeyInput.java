/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.ASN1Util
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class POPOSigningKeyInput
extends ASN1Object {
    private GeneralName sender;
    private PKMACValue publicKeyMAC;
    private SubjectPublicKeyInfo publicKey;

    private POPOSigningKeyInput(ASN1Sequence seq) {
        ASN1Encodable authInfo = seq.getObjectAt(0);
        if (authInfo instanceof ASN1TaggedObject) {
            ASN1TaggedObject tagObj = (ASN1TaggedObject)authInfo;
            this.sender = GeneralName.getInstance((Object)ASN1Util.getExplicitContextBaseObject((ASN1TaggedObject)tagObj, (int)0));
        } else {
            this.publicKeyMAC = PKMACValue.getInstance(authInfo);
        }
        this.publicKey = SubjectPublicKeyInfo.getInstance((Object)seq.getObjectAt(1));
    }

    public static POPOSigningKeyInput getInstance(Object o) {
        if (o instanceof POPOSigningKeyInput) {
            return (POPOSigningKeyInput)((Object)o);
        }
        if (o != null) {
            return new POPOSigningKeyInput(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public POPOSigningKeyInput(GeneralName sender, SubjectPublicKeyInfo spki) {
        this.sender = sender;
        this.publicKey = spki;
    }

    public POPOSigningKeyInput(PKMACValue pkmac, SubjectPublicKeyInfo spki) {
        this.publicKeyMAC = pkmac;
        this.publicKey = spki;
    }

    public GeneralName getSender() {
        return this.sender;
    }

    public PKMACValue getPublicKeyMAC() {
        return this.publicKeyMAC;
    }

    public SubjectPublicKeyInfo getPublicKey() {
        return this.publicKey;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        if (this.sender != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.sender));
        } else {
            v.add((ASN1Encodable)this.publicKeyMAC);
        }
        v.add((ASN1Encodable)this.publicKey);
        return new DERSequence(v);
    }
}

