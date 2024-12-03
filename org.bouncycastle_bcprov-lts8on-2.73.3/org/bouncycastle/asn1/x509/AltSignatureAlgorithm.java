/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;

public class AltSignatureAlgorithm
extends ASN1Object {
    private final AlgorithmIdentifier algorithm;

    public static AltSignatureAlgorithm getInstance(ASN1TaggedObject obj, boolean explicit) {
        return AltSignatureAlgorithm.getInstance(AlgorithmIdentifier.getInstance(obj, explicit));
    }

    public static AltSignatureAlgorithm getInstance(Object obj) {
        if (obj instanceof AltSignatureAlgorithm) {
            return (AltSignatureAlgorithm)obj;
        }
        if (obj != null) {
            return new AltSignatureAlgorithm(AlgorithmIdentifier.getInstance(obj));
        }
        return null;
    }

    public static AltSignatureAlgorithm fromExtensions(Extensions extensions) {
        return AltSignatureAlgorithm.getInstance(Extensions.getExtensionParsedValue(extensions, Extension.altSignatureAlgorithm));
    }

    public AltSignatureAlgorithm(AlgorithmIdentifier algorithm) {
        this.algorithm = algorithm;
    }

    public AltSignatureAlgorithm(ASN1ObjectIdentifier algorithm) {
        this(algorithm, null);
    }

    public AltSignatureAlgorithm(ASN1ObjectIdentifier algorithm, ASN1Encodable parameters) {
        this.algorithm = new AlgorithmIdentifier(algorithm, parameters);
    }

    public AlgorithmIdentifier getAlgorithm() {
        return this.algorithm;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.algorithm.toASN1Primitive();
    }
}

