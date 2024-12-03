/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.Certificate
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.asn1.isismtt.ocsp;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.util.Arrays;

public class RequestedCertificate
extends ASN1Object
implements ASN1Choice {
    public static final int certificate = -1;
    public static final int publicKeyCertificate = 0;
    public static final int attributeCertificate = 1;
    private Certificate cert;
    private byte[] publicKeyCert;
    private byte[] attributeCert;

    public static RequestedCertificate getInstance(Object obj) {
        if (obj == null || obj instanceof RequestedCertificate) {
            return (RequestedCertificate)((Object)obj);
        }
        if (obj instanceof ASN1Sequence) {
            return new RequestedCertificate(Certificate.getInstance((Object)obj));
        }
        if (obj instanceof ASN1TaggedObject) {
            return new RequestedCertificate((ASN1TaggedObject)obj);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static RequestedCertificate getInstance(ASN1TaggedObject obj, boolean explicit) {
        if (!explicit) {
            throw new IllegalArgumentException("choice item must be explicitly tagged");
        }
        return RequestedCertificate.getInstance(obj.getExplicitBaseObject());
    }

    private RequestedCertificate(ASN1TaggedObject tagged) {
        if (tagged.getTagNo() == 0) {
            this.publicKeyCert = ASN1OctetString.getInstance((ASN1TaggedObject)tagged, (boolean)true).getOctets();
        } else if (tagged.getTagNo() == 1) {
            this.attributeCert = ASN1OctetString.getInstance((ASN1TaggedObject)tagged, (boolean)true).getOctets();
        } else {
            throw new IllegalArgumentException("unknown tag number: " + tagged.getTagNo());
        }
    }

    public RequestedCertificate(Certificate certificate) {
        this.cert = certificate;
    }

    public RequestedCertificate(int type, byte[] certificateOctets) {
        this((ASN1TaggedObject)new DERTaggedObject(type, (ASN1Encodable)new DEROctetString(certificateOctets)));
    }

    public int getType() {
        if (this.cert != null) {
            return -1;
        }
        if (this.publicKeyCert != null) {
            return 0;
        }
        return 1;
    }

    public byte[] getCertificateBytes() {
        if (this.cert != null) {
            try {
                return this.cert.getEncoded();
            }
            catch (IOException e) {
                throw new IllegalStateException("can't decode certificate: " + e);
            }
        }
        if (this.publicKeyCert != null) {
            return Arrays.clone((byte[])this.publicKeyCert);
        }
        return Arrays.clone((byte[])this.attributeCert);
    }

    public ASN1Primitive toASN1Primitive() {
        if (this.publicKeyCert != null) {
            return new DERTaggedObject(0, (ASN1Encodable)new DEROctetString(this.publicKeyCert));
        }
        if (this.attributeCert != null) {
            return new DERTaggedObject(1, (ASN1Encodable)new DEROctetString(this.attributeCert));
        }
        return this.cert.toASN1Primitive();
    }
}

