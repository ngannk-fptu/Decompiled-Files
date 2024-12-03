/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.eac.ECDSAPublicKey;
import org.bouncycastle.asn1.eac.RSAPublicKey;

public abstract class PublicKeyDataObject
extends ASN1Object {
    public static PublicKeyDataObject getInstance(Object obj) {
        if (obj instanceof PublicKeyDataObject) {
            return (PublicKeyDataObject)((Object)obj);
        }
        if (obj != null) {
            ASN1Sequence seq = ASN1Sequence.getInstance((Object)obj);
            ASN1ObjectIdentifier usage = ASN1ObjectIdentifier.getInstance((Object)seq.getObjectAt(0));
            if (usage.on(EACObjectIdentifiers.id_TA_ECDSA)) {
                return new ECDSAPublicKey(seq);
            }
            return new RSAPublicKey(seq);
        }
        return null;
    }

    public abstract ASN1ObjectIdentifier getUsage();
}

