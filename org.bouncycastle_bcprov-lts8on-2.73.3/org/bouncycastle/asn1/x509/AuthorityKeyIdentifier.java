/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class AuthorityKeyIdentifier
extends ASN1Object {
    ASN1OctetString keyidentifier = null;
    GeneralNames certissuer = null;
    ASN1Integer certserno = null;

    public static AuthorityKeyIdentifier getInstance(ASN1TaggedObject obj, boolean explicit) {
        return AuthorityKeyIdentifier.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static AuthorityKeyIdentifier getInstance(Object obj) {
        if (obj instanceof AuthorityKeyIdentifier) {
            return (AuthorityKeyIdentifier)obj;
        }
        if (obj != null) {
            return new AuthorityKeyIdentifier(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static AuthorityKeyIdentifier fromExtensions(Extensions extensions) {
        return AuthorityKeyIdentifier.getInstance(Extensions.getExtensionParsedValue(extensions, Extension.authorityKeyIdentifier));
    }

    protected AuthorityKeyIdentifier(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        block5: while (e.hasMoreElements()) {
            ASN1TaggedObject o = ASN1TaggedObject.getInstance(e.nextElement());
            switch (o.getTagNo()) {
                case 0: {
                    this.keyidentifier = ASN1OctetString.getInstance(o, false);
                    continue block5;
                }
                case 1: {
                    this.certissuer = GeneralNames.getInstance(o, false);
                    continue block5;
                }
                case 2: {
                    this.certserno = ASN1Integer.getInstance(o, false);
                    continue block5;
                }
            }
            throw new IllegalArgumentException("illegal tag");
        }
    }

    public AuthorityKeyIdentifier(GeneralNames name, BigInteger serialNumber) {
        this(null, name, serialNumber);
    }

    public AuthorityKeyIdentifier(byte[] keyIdentifier) {
        this(keyIdentifier, null, null);
    }

    public AuthorityKeyIdentifier(byte[] keyIdentifier, GeneralNames name, BigInteger serialNumber) {
        this.keyidentifier = keyIdentifier != null ? new DEROctetString(Arrays.clone(keyIdentifier)) : null;
        this.certissuer = name;
        this.certserno = serialNumber != null ? new ASN1Integer(serialNumber) : null;
    }

    public byte[] getKeyIdentifier() {
        if (this.keyidentifier != null) {
            return this.keyidentifier.getOctets();
        }
        return null;
    }

    public GeneralNames getAuthorityCertIssuer() {
        return this.certissuer;
    }

    public BigInteger getAuthorityCertSerialNumber() {
        if (this.certserno != null) {
            return this.certserno.getValue();
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        if (this.keyidentifier != null) {
            v.add(new DERTaggedObject(false, 0, (ASN1Encodable)this.keyidentifier));
        }
        if (this.certissuer != null) {
            v.add(new DERTaggedObject(false, 1, (ASN1Encodable)this.certissuer));
        }
        if (this.certserno != null) {
            v.add(new DERTaggedObject(false, 2, (ASN1Encodable)this.certserno));
        }
        return new DERSequence(v);
    }

    public String toString() {
        String keyID = this.keyidentifier != null ? Hex.toHexString(this.keyidentifier.getOctets()) : "null";
        return "AuthorityKeyIdentifier: KeyID(" + keyID + ")";
    }
}

