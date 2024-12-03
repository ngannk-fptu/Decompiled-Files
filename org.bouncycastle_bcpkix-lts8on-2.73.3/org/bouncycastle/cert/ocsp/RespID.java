/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.ocsp.ResponderID
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.cert.ocsp;

import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.ResponderID;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.operator.DigestCalculator;

public class RespID {
    public static final AlgorithmIdentifier HASH_SHA1 = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE);
    ResponderID id;

    public RespID(ResponderID id) {
        this.id = id;
    }

    public RespID(X500Name name) {
        this.id = new ResponderID(name);
    }

    public RespID(SubjectPublicKeyInfo subjectPublicKeyInfo, DigestCalculator digCalc) throws OCSPException {
        try {
            if (!digCalc.getAlgorithmIdentifier().equals((Object)HASH_SHA1)) {
                throw new IllegalArgumentException("only SHA-1 can be used with RespID - found: " + digCalc.getAlgorithmIdentifier().getAlgorithm());
            }
            OutputStream digOut = digCalc.getOutputStream();
            digOut.write(subjectPublicKeyInfo.getPublicKeyData().getBytes());
            digOut.close();
            this.id = new ResponderID((ASN1OctetString)new DEROctetString(digCalc.getDigest()));
        }
        catch (Exception e) {
            throw new OCSPException("problem creating ID: " + e, e);
        }
    }

    public ResponderID toASN1Primitive() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (!(o instanceof RespID)) {
            return false;
        }
        RespID obj = (RespID)o;
        return this.id.equals((Object)obj.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }
}

