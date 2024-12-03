/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.asn1.cms.ecc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class ECCCMSSharedInfo
extends ASN1Object {
    private final AlgorithmIdentifier keyInfo;
    private final byte[] entityUInfo;
    private final byte[] suppPubInfo;

    public ECCCMSSharedInfo(AlgorithmIdentifier keyInfo, byte[] entityUInfo, byte[] suppPubInfo) {
        this.keyInfo = keyInfo;
        this.entityUInfo = Arrays.clone((byte[])entityUInfo);
        this.suppPubInfo = Arrays.clone((byte[])suppPubInfo);
    }

    public ECCCMSSharedInfo(AlgorithmIdentifier keyInfo, byte[] suppPubInfo) {
        this.keyInfo = keyInfo;
        this.entityUInfo = null;
        this.suppPubInfo = Arrays.clone((byte[])suppPubInfo);
    }

    private ECCCMSSharedInfo(ASN1Sequence seq) {
        this.keyInfo = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(0));
        if (seq.size() == 2) {
            this.entityUInfo = null;
            this.suppPubInfo = ASN1OctetString.getInstance((ASN1TaggedObject)((ASN1TaggedObject)seq.getObjectAt(1)), (boolean)true).getOctets();
        } else {
            this.entityUInfo = ASN1OctetString.getInstance((ASN1TaggedObject)((ASN1TaggedObject)seq.getObjectAt(1)), (boolean)true).getOctets();
            this.suppPubInfo = ASN1OctetString.getInstance((ASN1TaggedObject)((ASN1TaggedObject)seq.getObjectAt(2)), (boolean)true).getOctets();
        }
    }

    public static ECCCMSSharedInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return ECCCMSSharedInfo.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public static ECCCMSSharedInfo getInstance(Object obj) {
        if (obj instanceof ECCCMSSharedInfo) {
            return (ECCCMSSharedInfo)((Object)obj);
        }
        if (obj != null) {
            return new ECCCMSSharedInfo(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.keyInfo);
        if (this.entityUInfo != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)new DEROctetString(this.entityUInfo)));
        }
        v.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)new DEROctetString(this.suppPubInfo)));
        return new DERSequence(v);
    }
}

