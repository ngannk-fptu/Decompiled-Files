/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;

public class AuthorityInformationAccess
extends ASN1Object {
    private AccessDescription[] descriptions;

    private static AccessDescription[] copy(AccessDescription[] descriptions) {
        AccessDescription[] result = new AccessDescription[descriptions.length];
        System.arraycopy(descriptions, 0, result, 0, descriptions.length);
        return result;
    }

    public static AuthorityInformationAccess getInstance(Object obj) {
        if (obj instanceof AuthorityInformationAccess) {
            return (AuthorityInformationAccess)obj;
        }
        if (obj != null) {
            return new AuthorityInformationAccess(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static AuthorityInformationAccess fromExtensions(Extensions extensions) {
        return AuthorityInformationAccess.getInstance(Extensions.getExtensionParsedValue(extensions, Extension.authorityInfoAccess));
    }

    private AuthorityInformationAccess(ASN1Sequence seq) {
        if (seq.size() < 1) {
            throw new IllegalArgumentException("sequence may not be empty");
        }
        this.descriptions = new AccessDescription[seq.size()];
        for (int i = 0; i != seq.size(); ++i) {
            this.descriptions[i] = AccessDescription.getInstance(seq.getObjectAt(i));
        }
    }

    public AuthorityInformationAccess(AccessDescription description) {
        this.descriptions = new AccessDescription[]{description};
    }

    public AuthorityInformationAccess(AccessDescription[] descriptions) {
        this.descriptions = AuthorityInformationAccess.copy(descriptions);
    }

    public AuthorityInformationAccess(ASN1ObjectIdentifier oid, GeneralName location) {
        this(new AccessDescription(oid, location));
    }

    public AccessDescription[] getAccessDescriptions() {
        return AuthorityInformationAccess.copy(this.descriptions);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.descriptions);
    }

    public String toString() {
        return "AuthorityInformationAccess: Oid(" + this.descriptions[0].getAccessMethod().getId() + ")";
    }
}

