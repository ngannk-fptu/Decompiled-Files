/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class SubjectAltPublicKeyInfo
extends ASN1Object {
    private AlgorithmIdentifier algorithm;
    private ASN1BitString subjectAltPublicKey;

    public static SubjectAltPublicKeyInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return SubjectAltPublicKeyInfo.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static SubjectAltPublicKeyInfo getInstance(Object obj) {
        if (obj instanceof SubjectAltPublicKeyInfo) {
            return (SubjectAltPublicKeyInfo)obj;
        }
        if (obj != null) {
            return new SubjectAltPublicKeyInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static SubjectAltPublicKeyInfo fromExtensions(Extensions extensions) {
        return SubjectAltPublicKeyInfo.getInstance(Extensions.getExtensionParsedValue(extensions, Extension.subjectAltPublicKeyInfo));
    }

    private SubjectAltPublicKeyInfo(ASN1Sequence s) {
        if (s.size() != 2) {
            throw new IllegalArgumentException("extension should contain only 2 elements");
        }
        this.algorithm = AlgorithmIdentifier.getInstance(s.getObjectAt(0));
        this.subjectAltPublicKey = ASN1BitString.getInstance(s.getObjectAt(1));
    }

    public SubjectAltPublicKeyInfo(AlgorithmIdentifier algorithm, ASN1BitString subjectAltPublicKey) {
        this.algorithm = algorithm;
        this.subjectAltPublicKey = subjectAltPublicKey;
    }

    public SubjectAltPublicKeyInfo(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this.algorithm = subjectPublicKeyInfo.getAlgorithm();
        this.subjectAltPublicKey = subjectPublicKeyInfo.getPublicKeyData();
    }

    public AlgorithmIdentifier getAlgorithm() {
        return this.algorithm;
    }

    public ASN1BitString getSubjectAltPublicKey() {
        return this.subjectAltPublicKey;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.algorithm);
        v.add(this.subjectAltPublicKey);
        return new DERSequence(v);
    }
}

