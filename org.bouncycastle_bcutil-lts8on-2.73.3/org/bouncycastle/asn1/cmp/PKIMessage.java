/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;

public class PKIMessage
extends ASN1Object {
    private final PKIHeader header;
    private final PKIBody body;
    private final ASN1BitString protection;
    private final ASN1Sequence extraCerts;

    private PKIMessage(ASN1Sequence seq) {
        Enumeration en = seq.getObjects();
        this.header = PKIHeader.getInstance(en.nextElement());
        this.body = PKIBody.getInstance(en.nextElement());
        ASN1BitString protection = null;
        ASN1Sequence extraCerts = null;
        while (en.hasMoreElements()) {
            ASN1TaggedObject tObj = (ASN1TaggedObject)en.nextElement();
            if (tObj.getTagNo() == 0) {
                protection = ASN1BitString.getInstance((ASN1TaggedObject)tObj, (boolean)true);
                continue;
            }
            extraCerts = ASN1Sequence.getInstance((ASN1TaggedObject)tObj, (boolean)true);
        }
        this.protection = protection;
        this.extraCerts = extraCerts;
    }

    public PKIMessage(PKIHeader header, PKIBody body, ASN1BitString protection, CMPCertificate[] extraCerts) {
        this.header = header;
        this.body = body;
        this.protection = protection;
        this.extraCerts = extraCerts != null ? new DERSequence((ASN1Encodable[])extraCerts) : null;
    }

    public PKIMessage(PKIHeader header, PKIBody body, ASN1BitString protection) {
        this(header, body, protection, null);
    }

    public PKIMessage(PKIHeader header, PKIBody body) {
        this(header, body, null, null);
    }

    public static PKIMessage getInstance(Object o) {
        if (o instanceof PKIMessage) {
            return (PKIMessage)((Object)o);
        }
        if (o != null) {
            return new PKIMessage(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public PKIHeader getHeader() {
        return this.header;
    }

    public PKIBody getBody() {
        return this.body;
    }

    public ASN1BitString getProtection() {
        return this.protection;
    }

    public CMPCertificate[] getExtraCerts() {
        if (this.extraCerts == null) {
            return null;
        }
        CMPCertificate[] results = new CMPCertificate[this.extraCerts.size()];
        for (int i = 0; i < results.length; ++i) {
            results[i] = CMPCertificate.getInstance(this.extraCerts.getObjectAt(i));
        }
        return results;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add((ASN1Encodable)this.header);
        v.add((ASN1Encodable)this.body);
        this.addOptional(v, 0, (ASN1Encodable)this.protection);
        this.addOptional(v, 1, (ASN1Encodable)this.extraCerts);
        return new DERSequence(v);
    }

    private void addOptional(ASN1EncodableVector v, int tagNo, ASN1Encodable obj) {
        if (obj != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, tagNo, obj));
        }
    }
}

