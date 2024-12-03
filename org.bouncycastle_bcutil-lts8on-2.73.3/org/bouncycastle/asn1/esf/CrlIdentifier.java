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
 *  org.bouncycastle.asn1.ASN1UTCTime
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x500.X500Name
 */
package org.bouncycastle.asn1.esf;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;

public class CrlIdentifier
extends ASN1Object {
    private X500Name crlIssuer;
    private ASN1UTCTime crlIssuedTime;
    private ASN1Integer crlNumber;

    public static CrlIdentifier getInstance(Object obj) {
        if (obj instanceof CrlIdentifier) {
            return (CrlIdentifier)((Object)obj);
        }
        if (obj != null) {
            return new CrlIdentifier(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private CrlIdentifier(ASN1Sequence seq) {
        if (seq.size() < 2 || seq.size() > 3) {
            throw new IllegalArgumentException();
        }
        this.crlIssuer = X500Name.getInstance((Object)seq.getObjectAt(0));
        this.crlIssuedTime = ASN1UTCTime.getInstance((Object)seq.getObjectAt(1));
        if (seq.size() > 2) {
            this.crlNumber = ASN1Integer.getInstance((Object)seq.getObjectAt(2));
        }
    }

    public CrlIdentifier(X500Name crlIssuer, ASN1UTCTime crlIssuedTime) {
        this(crlIssuer, crlIssuedTime, null);
    }

    public CrlIdentifier(X500Name crlIssuer, ASN1UTCTime crlIssuedTime, BigInteger crlNumber) {
        this.crlIssuer = crlIssuer;
        this.crlIssuedTime = crlIssuedTime;
        if (null != crlNumber) {
            this.crlNumber = new ASN1Integer(crlNumber);
        }
    }

    public X500Name getCrlIssuer() {
        return this.crlIssuer;
    }

    public ASN1UTCTime getCrlIssuedTime() {
        return this.crlIssuedTime;
    }

    public BigInteger getCrlNumber() {
        if (null == this.crlNumber) {
            return null;
        }
        return this.crlNumber.getValue();
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.crlIssuer.toASN1Primitive());
        v.add((ASN1Encodable)this.crlIssuedTime);
        if (null != this.crlNumber) {
            v.add((ASN1Encodable)this.crlNumber);
        }
        return new DERSequence(v);
    }
}

