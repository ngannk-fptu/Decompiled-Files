/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class PBKDF2Params
extends ASN1Object {
    private static final AlgorithmIdentifier algid_hmacWithSHA1 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, DERNull.INSTANCE);
    private final ASN1OctetString octStr;
    private final ASN1Integer iterationCount;
    private final ASN1Integer keyLength;
    private final AlgorithmIdentifier prf;

    public static PBKDF2Params getInstance(Object obj) {
        if (obj instanceof PBKDF2Params) {
            return (PBKDF2Params)obj;
        }
        if (obj != null) {
            return new PBKDF2Params(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public PBKDF2Params(byte[] salt, int iterationCount) {
        this(salt, iterationCount, 0);
    }

    public PBKDF2Params(byte[] salt, int iterationCount, int keyLength) {
        this(salt, iterationCount, keyLength, null);
    }

    public PBKDF2Params(byte[] salt, int iterationCount, int keyLength, AlgorithmIdentifier prf) {
        this.octStr = new DEROctetString(Arrays.clone(salt));
        this.iterationCount = new ASN1Integer(iterationCount);
        this.keyLength = keyLength > 0 ? new ASN1Integer(keyLength) : null;
        this.prf = prf;
    }

    public PBKDF2Params(byte[] salt, int iterationCount, AlgorithmIdentifier prf) {
        this(salt, iterationCount, 0, prf);
    }

    private PBKDF2Params(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.octStr = (ASN1OctetString)e.nextElement();
        this.iterationCount = (ASN1Integer)e.nextElement();
        if (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (o instanceof ASN1Integer) {
                this.keyLength = ASN1Integer.getInstance(o);
                o = e.hasMoreElements() ? e.nextElement() : null;
            } else {
                this.keyLength = null;
            }
            this.prf = o != null ? AlgorithmIdentifier.getInstance(o) : null;
        } else {
            this.keyLength = null;
            this.prf = null;
        }
    }

    public byte[] getSalt() {
        return Arrays.clone(this.octStr.getOctets());
    }

    public BigInteger getIterationCount() {
        return this.iterationCount.getValue();
    }

    public BigInteger getKeyLength() {
        if (this.keyLength != null) {
            return this.keyLength.getValue();
        }
        return null;
    }

    public boolean isDefaultPrf() {
        return this.prf == null || this.prf.equals(algid_hmacWithSHA1);
    }

    public AlgorithmIdentifier getPrf() {
        if (this.prf != null) {
            return this.prf;
        }
        return algid_hmacWithSHA1;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add(this.octStr);
        v.add(this.iterationCount);
        if (this.keyLength != null) {
            v.add(this.keyLength);
        }
        if (this.prf != null && !this.prf.equals(algid_hmacWithSHA1)) {
            v.add(this.prf);
        }
        return new DERSequence(v);
    }
}

