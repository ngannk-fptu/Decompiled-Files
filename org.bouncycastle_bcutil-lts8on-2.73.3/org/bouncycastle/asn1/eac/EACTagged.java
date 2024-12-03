/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;

class EACTagged {
    EACTagged() {
    }

    static ASN1TaggedObject create(int eacTag, ASN1Sequence seq) {
        return new DERTaggedObject(false, 64, eacTag, (ASN1Encodable)seq);
    }

    static ASN1TaggedObject create(int eacTag, PublicKeyDataObject key) {
        return new DERTaggedObject(false, 64, eacTag, (ASN1Encodable)key);
    }

    static ASN1TaggedObject create(int eacTag, byte[] octets) {
        return new DERTaggedObject(false, 64, eacTag, (ASN1Encodable)new DEROctetString(octets));
    }
}

