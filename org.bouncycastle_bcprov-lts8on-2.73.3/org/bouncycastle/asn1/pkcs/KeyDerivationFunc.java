/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KeyDerivationFunc
extends ASN1Object {
    private AlgorithmIdentifier algId;

    public KeyDerivationFunc(ASN1ObjectIdentifier objectId, ASN1Encodable parameters) {
        this.algId = new AlgorithmIdentifier(objectId, parameters);
    }

    private KeyDerivationFunc(ASN1Sequence seq) {
        this.algId = AlgorithmIdentifier.getInstance(seq);
    }

    public static KeyDerivationFunc getInstance(Object obj) {
        if (obj instanceof KeyDerivationFunc) {
            return (KeyDerivationFunc)obj;
        }
        if (obj != null) {
            return new KeyDerivationFunc(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1ObjectIdentifier getAlgorithm() {
        return this.algId.getAlgorithm();
    }

    public ASN1Encodable getParameters() {
        return this.algId.getParameters();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.algId.toASN1Primitive();
    }
}

