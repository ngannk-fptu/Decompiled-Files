/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.GeneralName
 */
package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.GeneralName;

public class SinglePubInfo
extends ASN1Object {
    public static final ASN1Integer dontCare = new ASN1Integer(0L);
    public static final ASN1Integer x500 = new ASN1Integer(1L);
    public static final ASN1Integer web = new ASN1Integer(2L);
    public static final ASN1Integer ldap = new ASN1Integer(3L);
    private ASN1Integer pubMethod;
    private GeneralName pubLocation;

    private SinglePubInfo(ASN1Sequence seq) {
        this.pubMethod = ASN1Integer.getInstance((Object)seq.getObjectAt(0));
        if (seq.size() == 2) {
            this.pubLocation = GeneralName.getInstance((Object)seq.getObjectAt(1));
        }
    }

    public static SinglePubInfo getInstance(Object o) {
        if (o instanceof SinglePubInfo) {
            return (SinglePubInfo)((Object)o);
        }
        if (o != null) {
            return new SinglePubInfo(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public SinglePubInfo(ASN1Integer pubMethod, GeneralName pubLocation) {
        this.pubMethod = pubMethod;
        this.pubLocation = pubLocation;
    }

    public ASN1Integer getPubMethod() {
        return this.pubMethod;
    }

    public GeneralName getPubLocation() {
        return this.pubLocation;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.pubMethod);
        if (this.pubLocation != null) {
            v.add((ASN1Encodable)this.pubLocation);
        }
        return new DERSequence(v);
    }
}

