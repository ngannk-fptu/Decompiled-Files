/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;

public class ServiceLocator
extends ASN1Object {
    private final X500Name issuer;
    private final AuthorityInformationAccess locator;

    private ServiceLocator(ASN1Sequence sequence) {
        this.issuer = X500Name.getInstance(sequence.getObjectAt(0));
        this.locator = sequence.size() == 2 ? AuthorityInformationAccess.getInstance(sequence.getObjectAt(1)) : null;
    }

    public static ServiceLocator getInstance(Object obj) {
        if (obj instanceof ServiceLocator) {
            return (ServiceLocator)obj;
        }
        if (obj != null) {
            return new ServiceLocator(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    public AuthorityInformationAccess getLocator() {
        return this.locator;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.issuer);
        if (this.locator != null) {
            v.add(this.locator);
        }
        return new DERSequence(v);
    }
}

