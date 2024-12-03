/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.AttributeCertificate
 *  org.bouncycastle.asn1.x509.Certificate
 */
package org.bouncycastle.asn1.cmp;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.Certificate;

public class CMPCertificate
extends ASN1Object
implements ASN1Choice {
    private Certificate x509v3PKCert;
    private int otherTagValue;
    private ASN1Object otherCert;

    public CMPCertificate(AttributeCertificate x509v2AttrCert) {
        this(1, (ASN1Object)x509v2AttrCert);
    }

    public CMPCertificate(int type, ASN1Object otherCert) {
        this.otherTagValue = type;
        this.otherCert = otherCert;
    }

    public CMPCertificate(Certificate x509v3PKCert) {
        if (x509v3PKCert.getVersionNumber() != 3) {
            throw new IllegalArgumentException("only version 3 certificates allowed");
        }
        this.x509v3PKCert = x509v3PKCert;
    }

    public static CMPCertificate getInstance(ASN1TaggedObject ato, boolean isExplicit) {
        if (ato != null) {
            if (isExplicit) {
                return CMPCertificate.getInstance(ato.getExplicitBaseObject());
            }
            throw new IllegalArgumentException("tag must be explicit");
        }
        return null;
    }

    public static CMPCertificate getInstance(Object o) {
        if (o == null || o instanceof CMPCertificate) {
            return (CMPCertificate)((Object)o);
        }
        if (o instanceof byte[]) {
            try {
                o = ASN1Primitive.fromByteArray((byte[])((byte[])o));
            }
            catch (IOException e) {
                throw new IllegalArgumentException("Invalid encoding in CMPCertificate");
            }
        }
        if (o instanceof ASN1Sequence) {
            return new CMPCertificate(Certificate.getInstance((Object)o));
        }
        if (o instanceof ASN1TaggedObject) {
            ASN1TaggedObject taggedObject = ASN1TaggedObject.getInstance((Object)o, (int)128);
            return new CMPCertificate(taggedObject.getTagNo(), taggedObject.getBaseObject());
        }
        throw new IllegalArgumentException("Invalid object: " + o.getClass().getName());
    }

    public boolean isX509v3PKCert() {
        return this.x509v3PKCert != null;
    }

    public Certificate getX509v3PKCert() {
        return this.x509v3PKCert;
    }

    public AttributeCertificate getX509v2AttrCert() {
        return AttributeCertificate.getInstance((Object)this.otherCert);
    }

    public int getOtherCertTag() {
        return this.otherTagValue;
    }

    public ASN1Object getOtherCert() {
        return this.otherCert;
    }

    public ASN1Primitive toASN1Primitive() {
        if (this.otherCert != null) {
            return new DERTaggedObject(true, this.otherTagValue, (ASN1Encodable)this.otherCert);
        }
        return this.x509v3PKCert.toASN1Primitive();
    }
}

