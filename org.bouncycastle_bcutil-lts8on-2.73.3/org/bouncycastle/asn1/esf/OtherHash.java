/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.esf.OtherHashAlgAndValue;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class OtherHash
extends ASN1Object
implements ASN1Choice {
    private ASN1OctetString sha1Hash;
    private OtherHashAlgAndValue otherHash;

    public static OtherHash getInstance(Object obj) {
        if (obj instanceof OtherHash) {
            return (OtherHash)((Object)obj);
        }
        if (obj instanceof ASN1OctetString) {
            return new OtherHash((ASN1OctetString)obj);
        }
        return new OtherHash(OtherHashAlgAndValue.getInstance(obj));
    }

    private OtherHash(ASN1OctetString sha1Hash) {
        this.sha1Hash = sha1Hash;
    }

    public OtherHash(OtherHashAlgAndValue otherHash) {
        this.otherHash = otherHash;
    }

    public OtherHash(byte[] sha1Hash) {
        this.sha1Hash = new DEROctetString(sha1Hash);
    }

    public AlgorithmIdentifier getHashAlgorithm() {
        if (null == this.otherHash) {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
        }
        return this.otherHash.getHashAlgorithm();
    }

    public byte[] getHashValue() {
        if (null == this.otherHash) {
            return this.sha1Hash.getOctets();
        }
        return this.otherHash.getHashValue().getOctets();
    }

    public ASN1Primitive toASN1Primitive() {
        if (null == this.otherHash) {
            return this.sha1Hash;
        }
        return this.otherHash.toASN1Primitive();
    }
}

